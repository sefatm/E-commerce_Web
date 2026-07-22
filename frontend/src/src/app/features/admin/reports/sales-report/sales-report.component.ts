import { Component, OnInit } from '@angular/core';
import { ReportService } from 'src/app/core/services/report.service';
import { SalesReport, SalesReportItem } from 'src/app/core/models/report.model';

@Component({
  selector: 'app-sales-report',
  templateUrl: './sales-report.component.html',
  styleUrls: ['./sales-report.component.css']
})
export class SalesReportComponent implements OnInit {

  report: SalesReport | null = null;

  isLoading = false;
  errorMsg = '';

  today = new Date().toISOString().split('T')[0];
  firstOfMonth = new Date(new Date().getFullYear(), new Date().getMonth(), 1)
    .toISOString().split('T')[0];

  dateFrom = this.firstOfMonth;
  dateTo = this.today;

  selectedStatus = 'ALL';
  statuses = ['ALL', 'PENDING', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED'];

  filteredItems: any[] = [];

  constructor(private reportService: ReportService) {}

  ngOnInit(): void {
    this.loadReport();
  }

  loadReport(): void {
    this.isLoading = true;
    this.errorMsg = '';

    this.reportService.getSalesReport(this.dateFrom, this.dateTo).subscribe({
      next: (data) => {
        this.report = data;
        this.applyFilters();
        this.isLoading = false;
      },
      error: () => {
        this.errorMsg = 'Failed to load sales report.';
        this.isLoading = false;
      }
    });
  }

  applyFilters(): void {
    if (!this.report) {
      this.filteredItems = [];
      return;
    }

    let items = this.report.items;

    if (this.selectedStatus !== 'ALL') {
      items = items.filter(i => i.status === this.selectedStatus);
    }

    this.filteredItems = items;
  }

  onStatusChange(status: string): void {
    this.selectedStatus = status;
    this.applyFilters();
  }

  printReport(): void {
    window.print();
  }

  exportCsv(): void {
    const rows = [
      ['Sales Report'],
      [`From: ${this.dateFrom}`, `To: ${this.dateTo}`, `Status: ${this.selectedStatus}`],
      [],
      ['SL', 'Order Code', 'Customer', 'Date', 'Payment Method', 'Amount', 'Status'],
      ...this.filteredItems.map((item: SalesReportItem, index: number) => [
        index + 1,
        item.orderCode,
        item.customerName,
        item.orderDate,
        item.paymentMethod,
        item.totalAmount,
        item.status
      ]),
      [],
      ['Total Orders', this.filteredItems.length],
      ['Total Revenue', this.totalRevenue]
    ];

    const csv = rows
      .map(row => row.map(value => this.escapeCsv(value)).join(','))
      .join('\r\n');

    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `rural-mart-sales-report-${this.dateFrom}-to-${this.dateTo}.csv`;
    link.click();
    URL.revokeObjectURL(url);
  }

  private escapeCsv(value: unknown): string {
    const text = value === null || value === undefined ? '' : String(value);
    return `"${text.replace(/"/g, '""')}"`;
  }

  get totalRevenue(): number {
    return this.filteredItems.reduce(
      (sum, item) => sum + (item.totalAmount || 0),
      0
    );
  }

  get totalRevenueAll(): number {
    return this.report?.items?.reduce(
      (sum, item) => sum + (item.totalAmount || 0),
      0
    ) || 0;
  }

  getStatusClass(s: string): string {
    const m: Record<string, string> = {
      PENDING: 'badge-pending',
      PROCESSING: 'badge-processing',
      SHIPPED: 'badge-shipped',
      DELIVERED: 'badge-delivered',
      CANCELLED: 'badge-cancelled'
    };
    return m[s] || '';
  }
}
