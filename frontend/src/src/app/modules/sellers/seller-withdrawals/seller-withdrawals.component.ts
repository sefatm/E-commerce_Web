import { Component, OnInit } from '@angular/core';
import { SellerWithdrawService } from 'src/app/services/seller-withdraw.service';

@Component({
  selector: 'app-seller-withdrawals',
  templateUrl: './seller-withdrawals.component.html',
  styleUrls: ['./seller-withdrawals.component.css']
})
export class SellerWithdrawalsComponent implements OnInit {
  withdrawals: any[] = [];
  filtered: any[] = [];
  status = '';
  isLoading = false;
  message = '';
  approveTarget: any = null;
  approveData = { transactionRef: '', note: '' };

  constructor(private withdrawService: SellerWithdrawService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.isLoading = true;
    this.withdrawService.getAll().subscribe({
      next: (data) => {
        this.withdrawals = data || [];
        this.applyFilter();
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
        this.message = 'Withdraw requests load failed.';
      }
    });
  }

  applyFilter(): void {
    this.filtered = this.status
      ? this.withdrawals.filter(w => w.status === this.status)
      : [...this.withdrawals];
  }

  openApprove(withdraw: any): void {
    this.approveTarget = withdraw;
    this.approveData = { transactionRef: '', note: '' };
  }

  approve(): void {
    if (!this.approveTarget?.id || !this.approveData.transactionRef.trim()) return;
    this.withdrawService.approve(this.approveTarget.id, this.approveData.transactionRef, this.approveData.note).subscribe({
      next: () => {
        this.message = 'Withdraw marked as paid.';
        this.approveTarget = null;
        this.load();
      }
    });
  }

  reject(withdraw: any): void {
    if (!confirm('Reject this withdraw request?')) return;
    this.withdrawService.reject(withdraw.id, 'Rejected by admin').subscribe({
      next: () => {
        this.message = 'Withdraw rejected.';
        this.load();
      }
    });
  }

  delete(withdraw: any): void {
    if (!confirm('Delete this withdraw request?')) return;
    this.withdrawService.delete(withdraw.id).subscribe({
      next: () => {
        this.message = 'Withdraw deleted.';
        this.load();
      }
    });
  }
}
