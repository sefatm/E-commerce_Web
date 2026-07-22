import { Component, OnInit } from '@angular/core';
import { CommissionService } from 'src/app/services/commission.service';

@Component({
  selector: 'app-commission-list',
  templateUrl: './commission-list.component.html',
  styleUrls: ['./commission-list.component.css']
})
export class CommissionListComponent implements OnInit {
  commissions: any[] = [];
  filtered: any[] = [];
  summary: any = null;
  statusFilter = 'all';
  isLoading = false;
  successMsg = '';
  errorMsg = '';

  constructor(private commissionService: CommissionService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.isLoading = true;
    this.errorMsg = '';
    this.commissionService.getAll().subscribe({
      next: (data: any[]) => {
        this.commissions = data || [];
        this.applyFilter();
        this.isLoading = false;
      },
      error: () => {
        this.errorMsg = 'Commission records load failed.';
        this.isLoading = false;
      }
    });

    this.commissionService.getSummary().subscribe({
      next: (summary: any) => this.summary = summary
    });
  }

  applyFilter(): void {
    this.filtered = this.statusFilter === 'all'
      ? [...this.commissions]
      : this.commissions.filter(c => c.status === this.statusFilter);
  }

  markPayable(id: number): void {
    this.commissionService.markPayable(id).subscribe({
      next: () => {
        this.successMsg = 'Commission marked payable.';
        this.load();
        setTimeout(() => this.successMsg = '', 2500);
      },
      error: () => this.errorMsg = 'Could not mark payable.'
    });
  }

  markPaid(id: number): void {
    this.commissionService.markPaid(id).subscribe({
      next: () => {
        this.successMsg = 'Commission marked paid.';
        this.load();
        setTimeout(() => this.successMsg = '', 2500);
      },
      error: () => this.errorMsg = 'Could not mark paid.'
    });
  }
}
