import { Component, OnInit } from '@angular/core';
import { ClaimService } from '../../services/claim.service';
import { Claim, ClaimStatus } from '../../models/claim.model';

@Component({
  selector: 'app-my-claims',
  templateUrl: './my-claims.component.html',
  styleUrls: ['./my-claims.component.css']
})
export class MyClaimsComponent implements OnInit {
  claims: Claim[] = [];
  loading = true;
  page = 0;
  size = 10;
  totalPages = 0;
  totalElements = 0;

  constructor(private claimService: ClaimService) {}

  ngOnInit(): void {
    this.loadClaims();
  }

  loadClaims(): void {
    this.loading = true;
    this.claimService.getMyClaims(this.page, this.size).subscribe({
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
}
