import { Component, OnInit } from '@angular/core';
import { ItemService } from '../../services/item.service';
import { LostFoundItem, ItemType } from '../../models/item.model';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-my-items',
  templateUrl: './my-items.component.html',
  styleUrls: ['./my-items.component.css']
})
export class MyItemsComponent implements OnInit {
  items: LostFoundItem[] = [];
  loading = true;
  page = 0;
  size = 10;
  totalPages = 0;
  totalElements = 0;

  constructor(
    private itemService: ItemService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadItems();
  }

  loadItems(): void {
    this.loading = true;
    this.itemService.getMyItems(this.page, this.size).subscribe({
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

  goToPage(page: number): void {
    this.page = page;
    this.loadItems();
  }

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

  canDelete(status: string): boolean {
    return status === 'PENDING' || status === 'REJECTED';
  }

  deleteItem(id: number): void {
    if (confirm('确定要删除这条信息吗？')) {
      this.itemService.deleteItem(id).subscribe({
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
