import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ItemService } from '../../services/item.service';
import { LostFoundItem, ItemType, ItemStatus } from '../../models/item.model';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-admin-items',
  templateUrl: './admin-items.component.html',
  styleUrls: ['./admin-items.component.css']
})
export class AdminItemsComponent implements OnInit {
  items: LostFoundItem[] = [];
  loading = true;
  filterForm!: FormGroup;
  page = 0;
  size = 10;
  totalPages = 0;
  totalElements = 0;

  constructor(
    private formBuilder: FormBuilder,
    private itemService: ItemService,
    private snackBar: MatSnackBar,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.filterForm = this.formBuilder.group({
      status: [ItemStatus.PENDING],
      itemType: [null],
      subwayLineId: [null],
      itemTypeId: [null],
      keyword: ['']
    });

    this.loadItems();
  }

  loadItems(): void {
    this.loading = true;
    const filters = this.filterForm.value;
    
    this.itemService.getAllItems(
      filters.status,
      filters.itemType,
      filters.subwayLineId,
      filters.itemTypeId,
      filters.keyword,
      this.page,
      this.size
    ).subscribe({
      next: (response) => {
        if (response.success) {
          this.items = response.data.content;
          this.totalPages = response.data.totalPages;
          this.totalElements = response.data.totalElements;
        }
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  onSearch(): void {
    this.page = 0;
    this.loadItems();
  }

  onReset(): void {
    this.filterForm.reset({
      status: ItemStatus.PENDING,
      itemType: null,
      subwayLineId: null,
      itemTypeId: null,
      keyword: ''
    });
    this.page = 0;
    this.loadItems();
  }

  goToPage(page: number): void {
    this.page = page;
    this.loadItems();
  }

  getItemTypeText(type: ItemType): string {
    return type === ItemType.LOST ? '寻物启事' : '招领启事';
  }

  getStatusClass(status: ItemStatus): string {
    const statusMap: { [key in ItemStatus]: string } = {
      [ItemStatus.PENDING]: 'status-pending',
      [ItemStatus.APPROVED]: 'status-approved',
      [ItemStatus.REJECTED]: 'status-rejected',
      [ItemStatus.CLAIMED]: 'status-claimed',
      [ItemStatus.RETURNED]: 'status-returned'
    };
    return statusMap[status] || 'status-pending';
  }

  getStatusText(status: ItemStatus): string {
    const statusMap: { [key in ItemStatus]: string } = {
      [ItemStatus.PENDING]: '待审核',
      [ItemStatus.APPROVED]: '已通过',
      [ItemStatus.REJECTED]: '已拒绝',
      [ItemStatus.CLAIMED]: '已认领',
      [ItemStatus.RETURNED]: '已归还'
    };
    return statusMap[status] || '未知';
  }

  getItemTypeOptions(): { value: ItemType | null; label: string }[] {
    return [
      { value: null, label: '全部' },
      { value: ItemType.LOST, label: '寻物启事' },
      { value: ItemType.FOUND, label: '招领启事' }
    ];
  }

  getStatusOptions(): { value: ItemStatus | null; label: string }[] {
    return [
      { value: null, label: '全部' },
      { value: ItemStatus.PENDING, label: '待审核' },
      { value: ItemStatus.APPROVED, label: '已通过' },
      { value: ItemStatus.REJECTED, label: '已拒绝' },
      { value: ItemStatus.CLAIMED, label: '已认领' },
      { value: ItemStatus.RETURNED, label: '已归还' }
    ];
  }

  approveItem(id: number): void {
    if (confirm('确定要审核通过这条信息吗？')) {
      this.itemService.approveItem(id).subscribe({
        next: (response) => {
          if (response.success) {
            this.snackBar.open('审核通过', '关闭', { duration: 3000 });
            this.loadItems();
          } else {
            this.snackBar.open(response.message || '操作失败', '关闭', { duration: 3000 });
          }
        },
        error: () => {
          this.snackBar.open('操作失败，请稍后重试', '关闭', { duration: 3000 });
        }
      });
    }
  }

  rejectItem(id: number): void {
    const reason = prompt('请输入拒绝原因：');
    if (reason !== null) {
      this.itemService.rejectItem(id, reason || undefined).subscribe({
        next: (response) => {
          if (response.success) {
            this.snackBar.open('已拒绝', '关闭', { duration: 3000 });
            this.loadItems();
          } else {
            this.snackBar.open(response.message || '操作失败', '关闭', { duration: 3000 });
          }
        },
        error: () => {
          this.snackBar.open('操作失败，请稍后重试', '关闭', { duration: 3000 });
        }
      });
    }
  }

  markAsReturned(id: number): void {
    if (confirm('确定要标记为已归还吗？')) {
      this.itemService.markAsReturned(id).subscribe({
        next: (response) => {
          if (response.success) {
            this.snackBar.open('已标记为已归还', '关闭', { duration: 3000 });
            this.loadItems();
          } else {
            this.snackBar.open(response.message || '操作失败', '关闭', { duration: 3000 });
          }
        },
        error: () => {
          this.snackBar.open('操作失败，请稍后重试', '关闭', { duration: 3000 });
        }
      });
    }
  }

  deleteItem(id: number): void {
    if (confirm('确定要删除这条信息吗？此操作不可恢复。')) {
      this.itemService.adminDeleteItem(id).subscribe({
        next: (response) => {
          if (response.success) {
            this.snackBar.open('删除成功', '关闭', { duration: 3000 });
            this.loadItems();
          } else {
            this.snackBar.open(response.message || '删除失败', '关闭', { duration: 3000 });
          }
        },
        error: () => {
          this.snackBar.open('删除失败，请稍后重试', '关闭', { duration: 3000 });
        }
      });
    }
  }
}
