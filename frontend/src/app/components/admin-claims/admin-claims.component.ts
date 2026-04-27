import { Component, OnInit } from '@angular/core';
import { ClaimService } from '../../services/claim.service';
import { Claim, ClaimStatus } from '../../models/claim.model';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-admin-claims',
  templateUrl: './admin-claims.component.html',
  styleUrls: ['./admin-claims.component.css']
})
export class AdminClaimsComponent implements OnInit {
  claims: Claim[] = [];
  loading = true;
  status: ClaimStatus | undefined;
  page = 0;
  size = 10;
  totalPages = 0;
  totalElements = 0;

  constructor(
    private claimService: ClaimService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadClaims();
  }

  loadClaims(): void {
    this.loading = true;
    this.claimService.getAllClaims(this.status, this.page, this.size).subscribe({
      next: (response) => {
        if (response.success) {
          this.claims = response.data.content;
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

  onStatusChange(): void {
    this.page = 0;
    this.loadClaims();
  }

  goToPage(page: number): void {
    this.page = page;
    this.loadClaims();
  }

  getStatusClass(status: ClaimStatus): string {
    const statusMap: { [key in ClaimStatus]: string } = {
      [ClaimStatus.PENDING]: 'status-pending',
      [ClaimStatus.APPROVED]: 'status-approved',
      [ClaimStatus.REJECTED]: 'status-rejected'
    };
    return statusMap[status] || 'status-pending';
  }

  getStatusText(status: ClaimStatus): string {
    const statusMap: { [key in ClaimStatus]: string } = {
      [ClaimStatus.PENDING]: '待审核',
      [ClaimStatus.APPROVED]: '已通过',
      [ClaimStatus.REJECTED]: '已拒绝'
    };
    return statusMap[status] || '未知';
  }

  getStatusOptions(): { value: ClaimStatus | undefined; label: string }[] {
    return [
      { value: undefined, label: '全部' },
      { value: ClaimStatus.PENDING, label: '待审核' },
      { value: ClaimStatus.APPROVED, label: '已通过' },
      { value: ClaimStatus.REJECTED, label: '已拒绝' }
    ];
  }

  approveClaim(id: number): void {
    if (confirm('确定要通过这个认领申请吗？')) {
      this.claimService.approveClaim(id).subscribe({
        next: (response) => {
          if (response.success) {
            this.snackBar.open('认领通过', '关闭', { duration: 3000 });
            this.loadClaims();
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

  rejectClaim(id: number): void {
    const reason = prompt('请输入拒绝原因：');
    if (reason !== null) {
      this.claimService.rejectClaim(id, reason || undefined).subscribe({
        next: (response) => {
          if (response.success) {
            this.snackBar.open('已拒绝', '关闭', { duration: 3000 });
            this.loadClaims();
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
}
