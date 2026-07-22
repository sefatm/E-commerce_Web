import { Component, OnInit } from '@angular/core';
import { CommissionService } from 'src/app/services/commission.service';
import { SellerWithdrawService } from 'src/app/services/seller-withdraw.service';
import { AuthService } from 'src/app/services/auth.service';
import { SellerService } from 'src/app/services/seller.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-seller-commissions',
  templateUrl: './seller-commissions.component.html',
  styleUrls: ['./seller-commissions.component.css']
})
export class SellerCommissionsComponent implements OnInit {

  commissions: any[] = [];
  filtered: any[] = [];
  summary: any = null;
  availableBalance = 0;

  statusFilter = 'all';
  isLoading = false;
  errorMsg = '';
  successMsg = '';

  sellerId: number | null = null;

  withdrawForm = {
    amount: null as number | null,
    paymentMethod: 'bkash',
    accountName: '',
    accountNumber: '',
    note: ''
  };
  showWithdrawForm = false;
  withdrawError = '';
  withdrawSuccess = '';

  constructor(
    private commissionService: CommissionService,
    private withdrawService: SellerWithdrawService,
    private authService: AuthService,
    private sellerService: SellerService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const user = this.authService.getUser();
    if (!user?.id) { this.router.navigate(['/login']); return; }

    this.sellerService.getByUser(user.id).subscribe({
      next: (res: any) => {
        const seller = res?.data ?? res;
        this.sellerId = seller?.id;
        if (this.sellerId) this.load();
        else this.errorMsg = 'Seller profile not found.';
      },
      error: () => this.errorMsg = 'Could not load seller profile.'
    });
  }

  load(): void {
    if (!this.sellerId) return;
    this.isLoading = true;
    this.errorMsg = '';

    this.commissionService.getBySeller(this.sellerId).subscribe({
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

    this.commissionService.getSellerSummary(this.sellerId).subscribe({
      next: (s: any) => this.summary = s
    });

    this.withdrawService.getAvailableBalance(this.sellerId).subscribe({
      next: (b: number) => this.availableBalance = Number(b) || 0
    });
  }

  applyFilter(): void {
    this.filtered = this.statusFilter === 'all'
      ? [...this.commissions]
      : this.commissions.filter(c => c.status === this.statusFilter);
  }

  submitWithdraw(): void {
    this.withdrawError = '';
    this.withdrawSuccess = '';

    if (!this.withdrawForm.amount || this.withdrawForm.amount <= 0) {
      this.withdrawError = 'Enter a valid amount.'; return;
    }
    if (!this.withdrawForm.accountName.trim() || !this.withdrawForm.accountNumber.trim()) {
      this.withdrawError = 'Account name and number are required.'; return;
    }
    if (this.withdrawForm.amount > this.availableBalance) {
      this.withdrawError = `Amount cannot exceed available balance (৳${this.availableBalance}).`; return;
    }

    this.withdrawService.request(this.sellerId!, this.withdrawForm).subscribe({
      next: () => {
        this.withdrawSuccess = 'Withdrawal request submitted successfully!';
        this.withdrawForm = { amount: null, paymentMethod: 'bkash', accountName: '', accountNumber: '', note: '' };
        this.showWithdrawForm = false;
        this.load();
        setTimeout(() => this.withdrawSuccess = '', 3500);
      },
      error: (err: any) => {
        this.withdrawError = typeof err?.error === 'string' ? err.error : 'Request failed.';
      }
    });
  }

  statusClass(status: string): string {
    const s = (status || '').toUpperCase();
    if (['PAID', 'PAYABLE'].includes(s)) return 'badge-green';
    if (s === 'PENDING') return 'badge-amber';
    if (s === 'REJECTED') return 'badge-red';
    return 'badge-gray';
  }
}
