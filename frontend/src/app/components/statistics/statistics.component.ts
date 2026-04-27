import { Component, OnInit } from '@angular/core';
import { StatisticsService } from '../../services/statistics.service';
import { StatisticsDTO, LineStatistics, ItemTypeStatistics, MonthStatistics } from '../../models/common.model';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-statistics',
  templateUrl: './statistics.component.html',
  styleUrls: ['./statistics.component.css']
})
export class StatisticsComponent implements OnInit {
  statistics: StatisticsDTO | null = null;
  loading = true;
  exporting = false;

  constructor(
    private statisticsService: StatisticsService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadStatistics();
  }

  loadStatistics(): void {
    this.loading = true;
    this.statisticsService.getStatistics().subscribe({
      next: (response) => {
        if (response.success) {
          this.statistics = response.data;
        }
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  exportItems(): void {
    this.exporting = true;
    this.statisticsService.exportItems().subscribe({
      next: (blob) => {
        this.downloadFile(blob, 'items_export.xlsx');
        this.snackBar.open('导出成功', '关闭', { duration: 3000 });
        this.exporting = false;
      },
      error: () => {
        this.snackBar.open('导出失败，请稍后重试', '关闭', { duration: 3000 });
        this.exporting = false;
      }
    });
  }

  exportStatistics(): void {
    this.exporting = true;
    this.statisticsService.exportStatistics().subscribe({
      next: (blob) => {
        this.downloadFile(blob, 'statistics_export.xlsx');
        this.snackBar.open('导出成功', '关闭', { duration: 3000 });
        this.exporting = false;
      },
      error: () => {
        this.snackBar.open('导出失败，请稍后重试', '关闭', { duration: 3000 });
        this.exporting = false;
      }
    });
  }

  private downloadFile(blob: Blob, filename: string): void {
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    window.URL.revokeObjectURL(url);
  }
}
