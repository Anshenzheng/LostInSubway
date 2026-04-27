import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ItemService } from '../../services/item.service';
import { LostFoundItemRequest, ItemType, SubwayLine, ItemTypeCategory } from '../../models/item.model';

@Component({
  selector: 'app-item-create',
  templateUrl: './item-create.component.html',
  styleUrls: ['./item-create.component.css']
})
export class ItemCreateComponent implements OnInit {
  itemForm!: FormGroup;
  loading = false;
  error = '';
  success = '';
  subwayLines: SubwayLine[] = [];
  itemTypes: ItemTypeCategory[] = [];

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private itemService: ItemService
  ) {}

  ngOnInit(): void {
    this.itemForm = this.formBuilder.group({
      itemType: [ItemType.LOST, Validators.required],
      title: ['', [Validators.required, Validators.maxLength(100)]],
      description: ['', [Validators.required, Validators.maxLength(2000)]],
      itemTypeId: [null],
      subwayLineId: [null],
      stationName: ['', [Validators.maxLength(100)]],
      lostFoundTime: [null],
      contactName: ['', [Validators.maxLength(50)]],
      contactPhone: ['', [Validators.maxLength(20)]],
      contactEmail: ['', [Validators.email, Validators.maxLength(100)]],
      imageUrls: ['']
    });

    this.loadSubwayLines();
    this.loadItemTypes();
  }

  loadSubwayLines(): void {
    this.itemService.getSubwayLines().subscribe({
      next: (response) => {
        if (response.success) {
          this.subwayLines = response.data;
        }
      }
    });
  }

  loadItemTypes(): void {
    this.itemService.getItemTypes().subscribe({
      next: (response) => {
        if (response.success) {
          this.itemTypes = response.data;
        }
      }
    });
  }

  get f() { return this.itemForm.controls; }

  getItemTypeOptions(): { value: ItemType; label: string }[] {
    return [
      { value: ItemType.LOST, label: '寻物启事' },
      { value: ItemType.FOUND, label: '招领启事' }
    ];
  }

  onSubmit(): void {
    if (this.itemForm.invalid) {
      return;
    }

    this.loading = true;
    this.error = '';
    this.success = '';

    const request: LostFoundItemRequest = {
      itemType: this.f['itemType'].value,
      title: this.f['title'].value,
      description: this.f['description'].value,
      itemTypeId: this.f['itemTypeId'].value || undefined,
      subwayLineId: this.f['subwayLineId'].value || undefined,
      stationName: this.f['stationName'].value || undefined,
      lostFoundTime: this.f['lostFoundTime'].value || undefined,
      contactName: this.f['contactName'].value || undefined,
      contactPhone: this.f['contactPhone'].value || undefined,
      contactEmail: this.f['contactEmail'].value || undefined,
      imageUrls: this.f['imageUrls'].value || undefined
    };

    this.itemService.createItem(request).subscribe({
      next: (response) => {
        if (response.success) {
          this.success = '发布成功！信息已提交审核，审核通过后将对公众可见。';
          setTimeout(() => {
            this.router.navigate(['/my-items']);
          }, 2000);
        } else {
          this.error = response.message || '发布失败';
        }
        this.loading = false;
      },
      error: (error) => {
        this.error = error.error?.message || '发布失败，请稍后重试';
        this.loading = false;
      }
    });
  }
}
