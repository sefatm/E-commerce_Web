import { Component, OnInit } from '@angular/core';
import { VendorService } from 'src/app/services/vendor.service';


@Component({
  selector: 'app-vendor-payouts',
  templateUrl: './vendor-payouts.component.html',
  styleUrls: ['./vendor-payouts.component.css']
})
export class VendorPayoutsComponent implements OnInit {

  payouts: any[] = [];
  filteredPayouts: any[] = [];
  activeVendors: any[] = [];
  isLoading = false;
  isSubmitting = false;
  activeFilter = '';
  showRequestForm = false;

  showApproveModal = false;
  approveTargetId: number | null = null;
  approveData = { txRef: '', note: '' };

  payoutForm_data = {
    vendorId: '' as any,
    amount: null as number | null,
    method: 'bank_transfer',
    note: ''
  };

  constructor(private vendorService: VendorService) {}

  ngOnInit(): void {
    this.loadPayouts();
    this.loadActiveVendors();
  }

  loadPayouts(): void {
    this.isLoading = true;
    this.vendorService.getPayouts().subscribe({
      next: (res: any) => {
        this.payouts = res?.data ?? res;
        this.applyFilter();
        this.isLoading = false;
      },
      error: () => { this.isLoading = false; }
    });
  }

  loadActiveVendors(): void {
    this.vendorService.getAll('active').subscribe({
      next: (res: any) => { this.activeVendors = res?.data ?? res; }
    });
  }

  filterByStatus(status: string): void {
    this.activeFilter = status;
    this.applyFilter();
  }

  applyFilter(): void {
    this.filteredPayouts = this.activeFilter
      ? this.payouts.filter(p => p.status === this.activeFilter)
      : [...this.payouts];
  }

  submitPayoutRequest(): void {
    if (!this.payoutForm_data.vendorId || !this.payoutForm_data.amount) return;
    this.isSubmitting = true;
    this.vendorService.requestPayout(
      this.payoutForm_data.vendorId,
      this.payoutForm_data.amount,
      this.payoutForm_data.method,
      this.payoutForm_data.note
    ).subscribe({
      next: () => {
        this.loadPayouts();
        this.showRequestForm = false;
        this.resetPayoutForm();
        this.isSubmitting = false;
      },
      error: () => { this.isSubmitting = false; }
    });
  }

  openApproveModal(payout: any): void {
    this.approveTargetId = payout.id;
    this.approveData = { txRef: '', note: '' };
    this.showApproveModal = true;
  }

  confirmApprove(): void {
    if (!this.approveTargetId || !this.approveData.txRef) return;
    this.vendorService.approvePayout(this.approveTargetId, this.approveData.txRef, this.approveData.note).subscribe({
      next: () => {
        this.showApproveModal = false;
        this.approveTargetId = null;
        this.loadPayouts();
      }
    });
  }

  rejectPayout(id: number): void {
    if (!confirm('এই payout request টি reject করবেন?')) return;
    this.vendorService.rejectPayout(id, 'Rejected by admin').subscribe({
      next: () => this.loadPayouts()
    });
  }

  deletePayout(id: number): void {
    if (!confirm('এই payout record টি delete করবেন?')) return;
    this.vendorService.deletePayout(id).subscribe({
      next: () => this.loadPayouts()
    });
  }

  getMethodLabel(method: string): string {
    const labels: any = {
      'bank_transfer': 'Bank', 'bkash': 'bKash',
      'nagad': 'Nagad', 'cash': 'Cash'
    };
    return labels[method] || method;
  }

  private resetPayoutForm(): void {
    this.payoutForm_data = { vendorId: '', amount: null, method: 'bank_transfer', note: '' };
  }
}
