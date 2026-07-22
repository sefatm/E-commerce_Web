import { Component, OnInit } from '@angular/core';
import { ReportService } from 'src/app/services/report.service';
import { RevenueReport } from 'src/app/models/report.model';

@Component({
  selector: 'app-revenue-report',
  templateUrl: './revenue-report.component.html',
  styleUrls: ['./revenue-report.component.css']
})
export class RevenueReportComponent implements OnInit {

  report: RevenueReport | null = null;
  isLoading = false;
  errorMsg = '';

  constructor(private reportService: ReportService) {}

  ngOnInit(): void { this.loadReport(); }

  loadReport(): void {
    this.isLoading = true;
    this.reportService.getRevenueReport().subscribe({
      next: (data) => { this.report = data; this.isLoading = false; },
      error: () => { this.errorMsg = 'Failed to load revenue report.'; this.isLoading = false; }
    });
  }

  getMaxRevenue(): number {
    if (!this.report?.monthly?.length) return 1;
    return Math.max(...this.report.monthly.map(m => m.totalRevenue));
  }

  getBarHeight(val: number): number {
    return Math.round((val / this.getMaxRevenue()) * 160);
  }

  getGrowthClass(): string {
    if (!this.report) return '';
    return this.report.growthPercent >= 0 ? 'growth-up' : 'growth-down';
  }

  printReport(): void { window.print(); }

  exportCsv(): void {
    if (!this.report) return;

    const rows = [
      ['Revenue Report'],
      [],
      ['Total Revenue', this.report.totalRevenue],
      ['Revenue This Month', this.report.revenueThisMonth],
      ['Revenue Last Month', this.report.revenueLastMonth],
      ['Growth Percent', this.report.growthPercent],
      [],
      ['SL', 'Month', 'Year', 'Orders', 'Revenue', 'Average Order Value'],
      ...this.report.monthly.map((item, index) => [
        index + 1,
        item.month,
        item.year,
        item.orderCount,
        item.totalRevenue,
        item.orderCount > 0 ? item.totalRevenue / item.orderCount : 0
      ])
    ];

    this.downloadCsv(rows, 'rural-mart-revenue-report.csv');
  }

  private downloadCsv(rows: unknown[][], filename: string): void {
    const csv = rows.map(row => row.map(value => this.escapeCsv(value)).join(',')).join('\r\n');
    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    link.click();
    URL.revokeObjectURL(url);
  }

  private escapeCsv(value: unknown): string {
    const text = value === null || value === undefined ? '' : String(value);
    return `"${text.replace(/"/g, '""')}"`;
  }
}
