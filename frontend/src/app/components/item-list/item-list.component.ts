import { Component, OnInit } from '@angular/core';
import { ItemService } from '../../services/item.service';
import { LostFoundItem, ItemType } from '../../models/item.model';
import { PageResponse } from '../../models/common.model';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-item-list',
  templateUrl: './item-list.component.html',
  styleUrls: ['./item-list.component.css']
})
export class ItemListComponent implements OnInit {
  items: LostFoundItem[] = [];
  loading = true;
  itemType: ItemType | undefined;
  page = 0;
  size = 10;
  totalPages = 0;
  totalElements = 0;

  constructor(
    private itemService: ItemService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.route.data.subscribe(data => {
      this.itemType = data['itemType'];
      this.loadItems();
    });
  }

  loadItems(): void {
    this.loading = true;
    this.itemService.getApprovedItems(this.itemType, this.page, this.size).subscribe({
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

  getPageTitle(): string {
    if (this.itemType === ItemType.LOST) {
      return '寻物启事';
    } else if (this.itemType === ItemType.FOUND) {
      return '招领启事';
    }
    return '全部失物招领';
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
