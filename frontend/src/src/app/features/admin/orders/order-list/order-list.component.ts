import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Order } from 'src/app/core/models/order.model';
import { OrderService } from 'src/app/core/services/order.service';


@Component({
  selector: 'app-order-list',
  templateUrl: './order-list.component.html',
  styleUrls: ['./order-list.component.css']
})
export class OrderListComponent implements OnInit {

  orders: Order[] = [];
  filteredOrders: Order[] = [];
  searchKeyword = '';
  selectedStatus = 'ALL';
  isLoading = true;
  successMsg = '';
  errorMsg = '';

  statusList = ['ALL', 'PENDING', 'ACCEPTED', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED'];

  constructor(
    private orderService: OrderService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.searchKeyword = params['q'] || '';
      this.applyFilter();
    });
    this.loadOrders();
  }

  loadOrders(): void {
    this.isLoading = true;
    this.orderService.getAll().subscribe({
      next: (data) => {
        this.orders = data;
        this.applyFilter();
        this.isLoading = false;
      },
      error: () => this.isLoading = false
    });
  }

  applyFilter(): void {
    let result = [...this.orders];
    if (this.selectedStatus !== 'ALL') {
      result = result.filter(o => o.status === this.selectedStatus);
    }
    if (this.searchKeyword.trim()) {
      const kw = this.searchKeyword.toLowerCase();
      result = result.filter(o =>
        o.customerName?.toLowerCase().includes(kw) ||
        o.customerPhone?.includes(kw) ||
        o.status?.toLowerCase().includes(kw) ||
        o.paymentMethod?.toLowerCase().includes(kw) ||
        o.shippingAddress?.toLowerCase().includes(kw) ||
        (o.orderCode && o.orderCode.toLowerCase().includes(kw))
      );
    }
    this.filteredOrders = result;
  }

  onSearch(): void { this.applyFilter(); }

  onStatusFilter(status: string): void {
    this.selectedStatus = status;
    this.applyFilter();
  }

  updateStatus(id: number, status: string): void {
    this.orderService.updateStatus(id, status).subscribe({
      next: () => {
        this.successMsg = `Order status updated to ${status}`;
        this.loadOrders();
        setTimeout(() => this.successMsg = '', 3000);
      }
    });
  }

  deleteOrder(id: number): void {
    if (!confirm('Delete this order?')) return;
    this.errorMsg = '';
    this.orderService.delete(id).subscribe({
      next: () => {
        this.successMsg = 'Order deleted';
        this.loadOrders();
        setTimeout(() => this.successMsg = '', 3000);
      },
      error: (err) => {
        this.errorMsg = typeof err?.error === 'string'
          ? err.error
          : 'Order delete failed. Related records may still be linked.';
      }
    });
  }

  viewDetails(id: number): void {
    this.router.navigate(['/admin/orders/details', id]);
  }

  trackOrder(id: number): void {
    this.router.navigate(['/admin/orders/tracking', id]);
  }

  getStatusClass(status: string): string {
    const map: Record<string, string> = {
      'PENDING': 'badge-pending',
      'ACCEPTED': 'badge-accepted',
      'PROCESSING': 'badge-processing',
      'SHIPPED': 'badge-shipped',
      'DELIVERED': 'badge-delivered',
      'CANCELLED': 'badge-cancelled'
    };
    return map[status] || 'badge-pending';
  }

  getNextStatus(current: string): string {
    const flow: Record<string, string> = {
      'PENDING': 'ACCEPTED',
      'ACCEPTED': 'PROCESSING',
      'PROCESSING': 'SHIPPED',
      'SHIPPED': 'DELIVERED'
    };
    return flow[current] || '';
  }
}
