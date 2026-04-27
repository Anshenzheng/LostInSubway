import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ItemService } from '../../services/item.service';
import { ClaimService } from '../../services/claim.service';
import { AuthService } from '../../services/auth.service';
import { LostFoundItem, ItemType } from '../../models/item.model';
import { ClaimRequest } from '../../models/claim.model';

@Component({
  selector: 'app-item-detail',
  templateUrl: './item-detail.component.html',
  styleUrls: ['./item-detail.component.css']
})
export class ItemDetailComponent implements OnInit {
  item: LostFoundItem | null = null;
  loading = true;
  error = '';
  claimForm!: FormGroup;
  claimLoading = false;
  claimSuccess = '';
  claimError = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private formBuilder: FormBuilder,
    private itemService: ItemService,
    private claimService: ClaimService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    this.claimForm = this.formBuilder.group({
      claimReason: ['', [Validators.required, Validators.maxLength(2000)]],
      proofImages: ['']
    });

    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadItem(+id);
    }
  }

  loadItem(id: number): void {
    this.loading = true;
    this.itemService.getItemById(id).subscribe({
      next: (response) => {
        if (response.success) {
          this.item = response.data;
        } else {
          this.error = response.message || '加载失败';
        }
        this.loading = false;
      },
      error: () => {
        this.error = '加载失败，请稍后重试';
        this.loading = false;
      }
    });
  }

  get f() { return this.claimForm.controls; }

  getItemTypeText(type: ItemType): string {
    return type === ItemType.LOST ? '寻物启事' : '招领启事';
  }

  getStatusClass(status: string): string {
    const statusMap: { [key: string]: string } = {
      'PENDING': 'status-pending',
      'APPROVED': 'status-approved',
      'REJECTED': 'status-rejected',
      'CLAIMED': 'status-claimed',
      'RETURNED': 'status-returned'
    };
    return statusMap[status] || 'status-pending';
  }

  getStatusText(status: string): string {
    const statusMap: { [key: string]: string } = {
      'PENDING': '待审核',
      'APPROVED': '已通过',
      'REJECTED': '已拒绝',
      'CLAIMED': '已认领',
      'RETURNED': '已归还'
    };
    return statusMap[status] || '未知';
  }

  canClaim(): boolean {
    if (!this.authService.isLoggedIn || !this.item) {
      return false;
    }
    // 自己发布的不能认领
    if (this.item.publisherId === this.authService.userId) {
      return false;
    }
    // 只有已通过状态的可以认领
    return this.item.status === 'APPROVED';
  }

  submitClaim(): void {
    if (this.claimForm.invalid || !this.item) {
      return;
    }

    this.claimLoading = true;
    this.claimSuccess = '';
    this.claimError = '';

    const request: ClaimRequest = {
      itemId: this.item.id,
      claimReason: this.f['claimReason'].value,
      proofImages: this.f['proofImages'].value || undefined
    };

    this.claimService.createClaim(request).subscribe({
      next: (response) => {
        if (response.success) {
          this.claimSuccess = '认领申请已提交，请等待管理员审核';
          this.claimForm.reset();
        } else {
          this.claimError = response.message || '提交失败';
        }
        this.claimLoading = false;
      },
      error: (error) => {
        this.claimError = error.error?.message || '提交失败，请稍后重试';
        this.claimLoading = false;
      }
    });
  }
}
