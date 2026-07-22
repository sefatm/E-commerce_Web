import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Order } from 'src/app/models/order.model';
import { OrderService } from 'src/app/services/order.service';


@Component({
  selector: 'app-order-details',
  templateUrl: './order-details.component.html',
  styleUrls: ['./order-details.component.css']
})
export class OrderDetailsComponent implements OnInit {

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
          this.order!.paymentStatus = 'PAID';
          this.order!.deliveredDate = new Date().toISOString().split('T')[0];
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
