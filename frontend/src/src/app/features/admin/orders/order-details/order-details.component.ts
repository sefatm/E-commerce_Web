import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Order } from 'src/app/core/models/order.model';
import { OrderService } from 'src/app/core/services/order.service';
import { environment } from 'src/environments/environment';


@Component({
  selector: 'app-order-details',
  templateUrl: './order-details.component.html',
  styleUrls: ['./order-details.component.css']
})
export class OrderDetailsComponent implements OnInit {

  readonly imageBase = `${environment.apiUrl}/uploads/`;

  order: Order | null = null;
  isLoading = true;
  successMsg = '';
  errorMsg = '';

  statusSteps = ['PENDING', 'ACCEPTED', 'PROCESSING', 'SHIPPED', 'DELIVERED'];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private orderService: OrderService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.orderService.getById(id).subscribe({
      next: (data) => { this.order = data; this.isLoading = false; },
      error: () => { this.errorMsg = 'Order not found.'; this.isLoading = false; }
    });
  }

  updateStatus(status: string): void {
    if (!this.order) return;
    this.errorMsg = '';
    this.orderService.updateStatus(this.order.id!, status).subscribe({
      next: () => {
        this.order!.status = status;
        if (status === 'DELIVERED') {
          this.order!.deliveredDate = new Date().toISOString().split('T')[0];
          this.order!.paymentStatus = this.isCodOrder() ? 'COD_PENDING' : 'PAID';
        }
        this.successMsg = `Status updated to ${status}`;
        setTimeout(() => this.successMsg = '', 3000);
      },
      error: (err) => {
        this.errorMsg = typeof err?.error === 'string'
          ? err.error
          : 'Order status update failed.';
      }
    });
  }

  isCodOrder(): boolean {
    return (this.order?.paymentMethod || '').toUpperCase() === 'COD';
  }

  canRecordCod(): boolean {
    if (!this.order) return false;
    const paymentStatus = (this.order.paymentStatus || '').toUpperCase();
    return this.isCodOrder()
      && (this.order.status || '').toUpperCase() === 'DELIVERED'
      && (paymentStatus === 'COD_PENDING' || paymentStatus === 'UNPAID');
  }

  canVerifyCod(): boolean {
    return this.isCodOrder()
      && (this.order?.paymentStatus || '').toUpperCase() === 'COD_COLLECTED';
  }

  recordCodCollection(): void {
    if (!this.order || !this.canRecordCod()) return;
    this.errorMsg = '';
    this.orderService.markCodCollected(this.order.id!).subscribe({
      next: (message) => {
        this.order!.paymentStatus = 'COD_COLLECTED';
        this.successMsg = message || 'COD payment collection recorded successfully.';
        setTimeout(() => this.successMsg = '', 3000);
      },
      error: (err) => {
        this.errorMsg = typeof err?.error === 'string'
          ? err.error
          : 'Could not record COD collection.';
      }
    });
  }

  verifyCodPayment(): void {
    if (!this.order || !this.canVerifyCod()) return;
    this.errorMsg = '';
    this.orderService.verifyCodPayment(this.order.id!).subscribe({
      next: (message) => {
        this.order!.paymentStatus = 'PAID';
        this.successMsg = message || 'COD payment verified successfully.';
        setTimeout(() => this.successMsg = '', 3000);
      },
      error: (err) => {
        this.errorMsg = typeof err?.error === 'string'
          ? err.error
          : 'COD payment verification failed.';
      }
    });
  }

  getPaymentStatusLabel(status?: string): string {
    const labels: Record<string, string> = {
      'UNPAID': 'Unpaid',
      'COD_PENDING': 'Cash Verification Pending',
      'COD_COLLECTED': 'Cash Collected — Verification Pending',
      'PAID': 'Paid',
      'REFUNDED': 'Refunded'
    };
    const key = (status || '').toUpperCase();
    return labels[key] || (status || 'Unknown');
  }

  getPaymentStatusClass(status?: string): string {
    const key = (status || '').toUpperCase();
    if (key === 'PAID' || key === 'REFUNDED') return 'paid';
    if (key === 'COD_COLLECTED') return 'collected';
    return 'unpaid';
  }

  getCurrentStepIndex(): number {
    return this.statusSteps.indexOf(this.order?.status || 'PENDING');
  }

  getStatusClass(status: string): string {
    const m: Record<string, string> = {
      'PENDING': 'badge-pending', 'PROCESSING': 'badge-processing',
      'SHIPPED': 'badge-shipped', 'DELIVERED': 'badge-delivered', 'CANCELLED': 'badge-cancelled'
    };
    return m[status] || '';
  }

  goToInvoice(): void {
    this.router.navigate(['/admin/orders/invoice', this.order?.id]);
  }
  goToTracking(): void {
    this.router.navigate(['/admin/orders/tracking', this.order?.id]);
  }
  goBack(): void {
    this.router.navigate(['/admin/orders/list']);
  }
}
