import { Component, OnInit } from '@angular/core';
import { CustomerReport, CustomerReportItem } from 'src/app/core/models/report.model';
import { ReportService } from 'src/app/core/services/report.service';


@Component({
  selector: 'app-customer-report',
  templateUrl: './customer-report.component.html',
  styleUrls: ['./customer-report.component.css']
})
export class CustomerReportComponent implements OnInit {

  report: CustomerReport | null = null;
  isLoading = false;
  errorMsg = '';
  searchKeyword = '';
  selectedType   = 'ALL';
  sortBy: 'spent' | 'orders' = 'spent';

  get filteredItems(): CustomerReportItem[] {
    if (!this.report) return [];
    let items = [...this.report.items];
    if (this.selectedType !== 'ALL')
      items = items.filter(i => i.type === this.selectedType.toLowerCase());
    if (this.searchKeyword.trim()) {
      const kw = this.searchKeyword.toLowerCase();
      items = items.filter(i =>
        i.customerName.toLowerCase().includes(kw) ||
        i.email.toLowerCase().includes(kw)
      );
    }
    items.sort((a, b) =>
      this.sortBy === 'spent'
        ? b.totalSpent - a.totalSpent
        : b.totalOrders - a.totalOrders
    );
    return items;
  }

  constructor(private reportService: ReportService) {}

  ngOnInit(): void { this.loadReport(); }

  loadReport(): void {
    this.isLoading = true;
    this.reportService.getCustomerReport().subscribe({
      next: (data) => { this.report = data; this.isLoading = false; },
      error: () => { this.errorMsg = 'Failed to load customer report.'; this.isLoading = false; }
    });
  }

  printReport(): void { window.print(); }

  exportCsv(): void {
    if (!this.report) return;

    const rows = [
      ['Customer Report'],
      [`Type: ${this.selectedType}`, `Sorted By: ${this.sortBy}`],
      [],
      ['SL', 'Name', 'Email', 'Phone', 'Type', 'Orders', 'Total Spent', 'Joined'],
      ...this.filteredItems.map((item, index) => [
        index + 1,
        item.customerName,
        item.email,
        item.phone,
        item.type,
        item.totalOrders,
        item.totalSpent,
        item.joinDate
      ])
    ];

    const csv = rows.map(row => row.map(value => this.escapeCsv(value)).join(',')).join('\r\n');
    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = 'rural-mart-customer-report.csv';
    link.click();
    URL.revokeObjectURL(url);
  }

  private escapeCsv(value: unknown): string {
    const text = value === null || value === undefined ? '' : String(value);
    return `"${text.replace(/"/g, '""')}"`;
  }
}
