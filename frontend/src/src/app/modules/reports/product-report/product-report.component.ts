import { Component, OnInit } from '@angular/core';
import { ProductReport, ProductReportItem } from 'src/app/models/report.model';
import { ReportService } from 'src/app/services/report.service';


@Component({
  selector: 'app-product-report',
  templateUrl: './product-report.component.html',
  styleUrls: ['./product-report.component.css']
})
export class ProductReportComponent implements OnInit {

  report: ProductReport | null = null;
  isLoading = false;
  errorMsg = '';
  searchKeyword = '';
  sortBy: 'sold' | 'revenue' = 'sold';

  get filteredItems(): ProductReportItem[] {
    if (!this.report) return [];
    let items = [...this.report.items];
    if (this.searchKeyword.trim()) {
      const kw = this.searchKeyword.toLowerCase();
      items = items.filter(i =>
        i.productName.toLowerCase().includes(kw) ||
        i.category.toLowerCase().includes(kw)
      );
    }
    items.sort((a, b) =>
      this.sortBy === 'sold'
        ? b.totalSold - a.totalSold
        : b.totalRevenue - a.totalRevenue
    );
    return items;
  }

  constructor(private reportService: ReportService) {}

  ngOnInit(): void { this.loadReport(); }

  loadReport(): void {
    this.isLoading = true;
    this.reportService.getProductReport().subscribe({
      next: (data) => { this.report = data; this.isLoading = false; },
      error: () => { this.errorMsg = 'Failed to load product report.'; this.isLoading = false; }
    });
  }

  getMaxSold(): number {
    if (!this.report?.items?.length) return 1;
    return Math.max(...this.report.items.map(i => i.totalSold));
  }

  getBarWidth(sold: number): number {
    return Math.round((sold / this.getMaxSold()) * 100);
  }

  printReport(): void { window.print(); }

  exportCsv(): void {
    if (!this.report) return;

    const rows = [
      ['Product Report'],
      [`Sorted By: ${this.sortBy}`],
      [],
      ['SL', 'Product Name', 'Category', 'Units Sold', 'Revenue'],
      ...this.filteredItems.map((item, index) => [
        index + 1,
        item.productName,
        item.category,
        item.totalSold,
        item.totalRevenue
      ])
    ];

    const csv = rows.map(row => row.map(value => this.escapeCsv(value)).join(',')).join('\r\n');
    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = 'rural-mart-product-report.csv';
    link.click();
    URL.revokeObjectURL(url);
  }

  private escapeCsv(value: unknown): string {
    const text = value === null || value === undefined ? '' : String(value);
    return `"${text.replace(/"/g, '""')}"`;
  }
}
