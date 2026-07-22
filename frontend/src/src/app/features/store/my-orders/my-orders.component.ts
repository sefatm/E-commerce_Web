import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { OrderReturn } from 'src/app/core/models/order.model';
import { OrderService } from 'src/app/core/services/order.service';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-my-orders',
  templateUrl: './my-orders.component.html',
  styleUrls: ['./my-orders.component.css']
})
export class MyOrdersComponent implements OnInit {

  readonly imageBase = `${environment.apiUrl}/uploads/`;

  searchCode = '';
  searchPhone = '';
  isSearching = false;
  searchError = '';

  order: any = null;

  showReturnForm = false;
  selectedProduct = '';
  returnReason = '';
  refundMethod = '';
  refundAccountNumber = '';
  refundAccountName = '';
  refundBankName = '';
  refundBranch = '';
  isSubmitting = false;
  returnSuccess = '';
  returnError = '';
  orderReturns: OrderReturn[] = [];

  readonly STEPS = ['PENDING', 'ACCEPTED', 'PROCESSING', 'SHIPPED', 'DELIVERED'];
  readonly STEP_LABELS: Record<string, string> = {
    PENDING: 'Order Placed',
    ACCEPTED: 'Accepted',
    PROCESSING: 'Processing',
    SHIPPED: 'Shipped',
    DELIVERED: 'Delivered',
    CANCELLED: 'Cancelled'
  };
  readonly STEP_ICONS: Record<string, string> = {
    PENDING: 'Cart',
    ACCEPTED: 'OK',
    PROCESSING: 'Box',
    SHIPPED: 'Ship',
    DELIVERED: 'Done',
    CANCELLED: 'No'
  };

  constructor(
    private orderService: OrderService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const code = params['code'];
      if (code) {
        this.searchCode = code;
        this.trackOrder();
      }
    });
  }

  trackOrder(): void {
    const code = this.normalizeOrderCode(this.searchCode);

    if (!code) {
      this.searchError = 'Please enter your order code.';
      return;
    }

    this.isSearching = true;
    this.searchError = '';
    this.order = null;

    this.orderService.getByCode(encodeURIComponent(code)).subscribe({
      next: (res) => {
        this.isSearching = false;
        this.order = res;
        this.loadOrderReturns();
      },
      error: () => {
        this.isSearching = false;
        this.searchError = 'This order code was not found. Please check and try again.';
      }
    });
  }

  private normalizeOrderCode(value: string): string {
    return (value || '').trim().replace(/\s+/g, '').toUpperCase();
  }

  currentStepIndex(): number {
    return this.STEPS.indexOf(this.order?.status ?? '');
  }

  stepStatus(step: string): 'done' | 'active' | 'pending' {
    if (this.order?.status === 'CANCELLED') return 'pending';
    const cur = this.currentStepIndex();
    const idx = this.STEPS.indexOf(step);
    if (idx < cur) return 'done';
    if (idx === cur) return 'active';
    return 'pending';
  }

  canReturn(): boolean {
    return (this.order?.status || '').toUpperCase() === 'DELIVERED';
  }

  hasReturnRequest(productName: string): boolean {
    return this.orderReturns.some(r => r.productName === productName);
  }

  returnStatus(productName: string): string {
    const ret = this.orderReturns.find(r => r.productName === productName);
    return ret?.status || '';
  }

  loadOrderReturns(): void {
    this.orderReturns = [];
    if (!this.order?.id) return;

    this.orderService.getReturnsByOrder(this.order.id).subscribe({
      next: (data) => this.orderReturns = data || [],
      error: () => this.orderReturns = []
    });
  }

  openReturnForm(productName: string): void {
    if (!this.canReturn() || this.hasReturnRequest(productName)) return;
    this.selectedProduct = productName;
    this.showReturnForm = true;
    this.returnReason = '';
    this.refundMethod = (this.order?.paymentMethod || '').toUpperCase() === 'COD' ? 'BKASH' : 'ORIGINAL_PAYMENT';
    this.refundAccountNumber = '';
    this.refundAccountName = this.order?.customerName || '';
    this.refundBankName = '';
    this.refundBranch = '';
    this.returnSuccess = '';
    this.returnError = '';
  }

  closeReturnForm(): void {
    this.showReturnForm = false;
    this.returnReason = '';
    this.refundMethod = '';
    this.refundAccountNumber = '';
    this.refundAccountName = '';
    this.refundBankName = '';
    this.refundBranch = '';
  }

  requiresRefundAccount(): boolean {
    return this.refundMethod !== 'ORIGINAL_PAYMENT';
  }

  submitReturn(): void {
    if (!this.returnReason.trim()) {
      this.returnError = 'Please write the return reason.';
      return;
    }

    if (!this.refundMethod) { this.returnError = 'Please select a refund method.'; return; }
    if (this.requiresRefundAccount() && (!this.refundAccountNumber.trim() || !this.refundAccountName.trim())) {
      this.returnError = 'Refund account number and account holder name are required.'; return;
    }
    if (this.refundMethod === 'BANK' && !this.refundBankName.trim()) {
      this.returnError = 'Bank name is required.'; return;
    }

    this.isSubmitting = true;
    this.returnError = '';

    this.orderService.requestReturn(
      this.order.id,
      this.selectedProduct,
      this.returnReason.trim(),
      {
        refundMethod: this.refundMethod,
        refundAccountNumber: this.refundAccountNumber.trim(),
        refundAccountName: this.refundAccountName.trim(),
        refundBankName: this.refundBankName.trim(),
        refundBranch: this.refundBranch.trim()
      }
    ).subscribe({
      next: () => {
        this.isSubmitting = false;
        this.returnSuccess = 'Return request submitted successfully. Admin will review it.';
        this.showReturnForm = false;
        this.loadOrderReturns();
        setTimeout(() => this.returnSuccess = '', 5000);
      },
      error: (err) => {
        this.isSubmitting = false;
        this.returnError = typeof err?.error === 'string'
          ? err.error
          : 'Return request could not be submitted. Please try again.';
      }
    });
  }

  formatDate(d: string): string {
    if (!d) return '-';
    return new Date(d).toLocaleDateString('en-GB', { year: 'numeric', month: 'short', day: 'numeric' });
  }

  getStatusColor(status: string): string {
    const map: Record<string, string> = {
      PENDING: '#f59e0b',
      ACCEPTED: '#06b6d4',
      PROCESSING: '#3b82f6',
      SHIPPED: '#8b5cf6',
      DELIVERED: '#10b981',
      CANCELLED: '#ef4444'
    };
    return map[status] ?? '#888';
  }

  resetSearch(): void {
    this.order = null;
    this.searchCode = '';
    this.searchPhone = '';
    this.searchError = '';
    this.showReturnForm = false;
    this.orderReturns = [];
  }
}
