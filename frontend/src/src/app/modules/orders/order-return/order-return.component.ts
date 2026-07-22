import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { OrderReturn } from 'src/app/models/order.model';
import { OrderService } from 'src/app/services/order.service';

@Component({
  selector: 'app-order-return',
  templateUrl: './order-return.component.html',
  styleUrls: ['./order-return.component.css']
})
export class OrderReturnComponent implements OnInit {

  returns: OrderReturn[] = [];
  filteredReturns: OrderReturn[] = [];
  selectedStatus = 'ALL';
  isLoading = true;
  successMsg = '';
  errorMsg = '';
  selectedReturn: OrderReturn | null = null;
  actionMode: 'approve' | 'reject' | null = null;
  noteText = '';
  refundAmount: number | null = null;
  refundMethod = 'Original payment method';
  searchKeyword = '';
  isProcessingAction = false;

  statusList = ['ALL', 'PENDING', 'APPROVED', 'RETURN_PICKED', 'ITEM_RECEIVED', 'REFUND_INITIATED', 'REFUNDED', 'REJECTED'];

  constructor(private orderService: OrderService, private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.searchKeyword = params['q'] || '';
      this.applyFilter();
    });
    this.loadReturns();
  }

  loadReturns(): void {
    this.isLoading = true;
    this.errorMsg = '';
    this.orderService.getAllReturns().subscribe({
      next: (data) => {
        this.returns = data;
        this.applyFilter();
        this.isLoading = false;
      },
      error: () => {
        this.errorMsg = 'Return requests load failed.';
        this.isLoading = false;
      }
    });
  }

  applyFilter(): void {
    let result = this.selectedStatus === 'ALL'
      ? [...this.returns]
      : this.returns.filter(r => r.status === this.selectedStatus);

    if (this.searchKeyword.trim()) {
      const keyword = this.searchKeyword.trim().toLowerCase();
      result = result.filter(r =>
        r.order?.orderCode?.toLowerCase().includes(keyword) ||
        r.order?.customerName?.toLowerCase().includes(keyword) ||
        r.order?.customerPhone?.toLowerCase().includes(keyword) ||
        r.productName?.toLowerCase().includes(keyword) ||
        r.reason?.toLowerCase().includes(keyword) ||
        r.status?.toLowerCase().includes(keyword) ||
        r.refundStatus?.toLowerCase().includes(keyword)
      );
    }

    this.filteredReturns = result;
  }

  openApprove(ret: OrderReturn): void {
    this.selectedReturn = ret;
    this.actionMode = 'approve';
    this.noteText = '';
    this.refundAmount = ret.refundAmount || this.calculateRefundAmount(ret);
    this.refundMethod = ret.refundMethod || 'Not provided';
  }

  openReject(ret: OrderReturn): void {
    this.selectedReturn = ret;
    this.actionMode = 'reject';
    this.noteText = '';
    this.refundAmount = null;
    this.refundMethod = '';
  }

  closeModal(): void {
    this.selectedReturn = null;
    this.actionMode = null;
    this.noteText = '';
    this.refundAmount = null;
    this.refundMethod = 'Original payment method';
  }

  approve(id: number): void {
    if (!this.refundAmount || this.refundAmount <= 0) {
      this.errorMsg = 'Enter a valid refund amount.';
      return;
    }
    this.isProcessingAction = true;
    this.orderService.approveReturn(id, this.noteText, this.refundAmount, '').subscribe({
      next: () => {
        this.isProcessingAction = false;
        this.successMsg = 'Return approved. Refund will be processed after the item is received.';
        this.errorMsg = '';
        this.closeModal();
        this.loadReturns();
        setTimeout(() => this.successMsg = '', 3000);
      },
      error: (err) => {
        this.isProcessingAction = false;
        this.errorMsg = typeof err?.error === 'string' ? err.error : 'Return approve failed.';
      }
    });
  }

  reject(id: number): void {
    if (!this.noteText.trim()) {
      this.errorMsg = 'Write a rejection reason first.';
      return;
    }
    this.isProcessingAction = true;
    this.orderService.rejectReturn(id, this.noteText).subscribe({
      next: () => {
        this.isProcessingAction = false;
        this.successMsg = 'Return rejected';
        this.errorMsg = '';
        this.closeModal();
        this.loadReturns();
        setTimeout(() => this.successMsg = '', 3000);
      },
      error: (err) => {
        this.isProcessingAction = false;
        this.errorMsg = typeof err?.error === 'string' ? err.error : 'Return reject failed.';
      }
    });
  }

  advance(ret: OrderReturn, status: string): void {
    const method = ret.refundMethod || '';
    const ref = status === 'REFUNDED' ? (prompt('Enter refund transaction reference:', '') || '').trim() : '';
    if (status === 'REFUNDED' && !ref) { this.errorMsg = 'Transaction reference is required.'; return; }
    this.orderService.advanceReturn(ret.id!, status, '', method, ref).subscribe({ next: msg => { this.successMsg = msg; this.loadReturns(); }, error: err => this.errorMsg = err?.error || 'Status update failed' });
  }

  deleteReturn(id: number): void {
    if (!confirm('Delete this return request?')) return;
    this.orderService.deleteReturn(id).subscribe({
      next: () => { this.successMsg = 'Deleted'; this.errorMsg = ''; this.loadReturns(); },
      error: (err) => this.errorMsg = typeof err?.error === 'string' ? err.error : 'Delete failed.'
    });
  }

  getPendingCount(): number { return this.returns.filter(r => r.status === 'PENDING').length; }

  getApprovedCount(): number { return this.returns.filter(r => r.status === 'APPROVED').length; }

  getRejectedCount(): number { return this.returns.filter(r => r.status === 'REJECTED').length; }

  clearSearch(): void {
    this.searchKeyword = '';
    this.applyFilter();
  }

  private calculateRefundAmount(ret: OrderReturn): number {
    if (!ret.order?.items?.length) return ret.order?.totalAmount || 0;
    if (ret.productName === 'Full order') return ret.order?.totalAmount || 0;
    const item = ret.order.items.find(i =>
      i.productName?.toLowerCase() === ret.productName?.toLowerCase()
    );
    return item?.totalPrice || ((item?.unitPrice || 0) * (item?.quantity || 0)) || ret.order?.totalAmount || 0;
  }
}
