import { Component, OnInit } from '@angular/core';
import { ItemService } from '../../services/item.service';
import { LostFoundItem, ItemType } from '../../models/item.model';
import { PageResponse } from '../../models/common.model';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  recentItems: LostFoundItem[] = [];
  loading = true;

  constructor(private itemService: ItemService) {}

  ngOnInit(): void {
    this.loadRecentItems();
  }

  loadRecentItems(): void {
    this.itemService.getApprovedItems(undefined, 0, 6).subscribe({
      next: (response) => {
        if (response.success) {
          this.recentItems = response.data.content;
        }
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
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
}
