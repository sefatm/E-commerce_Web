import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from 'src/app/services/auth.service';
import { OrderService } from 'src/app/services/order.service';
import { SellerService } from 'src/app/services/seller.service';

@Component({
  selector: 'app-seller-orders',
  templateUrl: './seller-orders.component.html',
  styleUrls: ['./seller-orders.component.css']
})
export class SellerOrdersComponent implements OnInit {
  seller: any = null;
  sellerId: number | null = null;
  orders: any[] = [];
  filteredOrders: any[] = [];
  selectedStatus = 'ALL';
  searchTerm = '';
  isLoading = false;
  successMsg = '';
  errorMsg = '';

  statusList = ['ALL', 'PENDING', 'ACCEPTED', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED'];

  constructor(
    private authService: AuthService,
    private sellerService: SellerService,
    private orderService: OrderService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.searchTerm = params['q'] || '';
      this.applyFilter();
    });
    this.loadCurrentSeller();
  }

  loadCurrentSeller(): void {
    const user = this.authService.getUser();
    if (!user?.id) {
      this.router.navigate(['/login'], { queryParams: { returnUrl: '/seller/orders' } });
      return;
    }

    this.isLoading = true;
    this.errorMsg = '';
    this.sellerService.getByUser(user.id).subscribe({
      next: (res: any) => {
        const seller = res?.data ?? res;
        this.seller = seller;
        this.sellerId = seller?.id || null;

        if (!this.sellerId || seller.status !== 'APPROVED') {
          this.isLoading = false;
          this.errorMsg = seller?.status === 'REJECTED'
            ? 'Your seller application was rejected. Order management is unavailable.'
            : 'Your seller application is pending admin approval.';
          return;
        }

        this.loadOrders();
      },
      error: () => {
        this.isLoading = false;
        this.errorMsg = 'No seller profile found. Please apply as a seller first.';
      }
    });
  }

  loadOrders(): void {
    if (!this.sellerId) return;
    this.isLoading = true;
    this.orderService.getBySeller(this.sellerId).subscribe({
      next: (orders: any[]) => {
        this.orders = orders || [];
        this.applyFilter();
        this.isLoading = false;
      },
      error: () => {
        this.errorMsg = 'Seller orders load failed.';
        this.isLoading = false;
      }
    });
  }

  applyFilter(): void {
    let result = [...this.orders];
    if (this.selectedStatus !== 'ALL') {
      result = result.filter(order => (order.status || 'PENDING').toUpperCase() === this.selectedStatus);
    }

    const term = this.searchTerm.trim().toLowerCase();
    if (term) {
      result = result.filter(order =>
        order.orderCode?.toLowerCase().includes(term) ||
        order.customerName?.toLowerCase().includes(term) ||
        order.customerPhone?.includes(term) ||
        order.shippingAddress?.toLowerCase().includes(term) ||
        order.paymentMethod?.toLowerCase().includes(term) ||
        order.status?.toLowerCase().includes(term) ||
        order.items?.some((item: any) => item.productName?.toLowerCase().includes(term))
      );
    }

    this.filteredOrders = result;
  }

  setStatusFilter(status: string): void {
    this.selectedStatus = status;
    this.applyFilter();
  }

  updateOrderStatus(order: any, status: string): void {
    this.errorMsg = '';
    this.successMsg = '';
    this.orderService.updateStatus(order.id, status).subscribe({
      next: () => {
        this.successMsg = `Order ${order.orderCode || ''} marked as ${status}.`;
        order.status = status;
        this.loadOrders();
        setTimeout(() => this.successMsg = '', 3000);
      },
      error: (err) => {
        this.errorMsg = typeof err?.error === 'string'
          ? err.error
          : 'Order status update failed.';
      }
    });
  }

  availableOrderStatuses(order: any): string[] {
    const status = (order?.status || 'PENDING').toUpperCase();
    const flow: { [key: string]: string[] } = {
      PENDING: ['PENDING', 'ACCEPTED', 'CANCELLED'],
      ACCEPTED: ['ACCEPTED', 'PROCESSING', 'CANCELLED'],
      PROCESSING: ['PROCESSING', 'SHIPPED', 'CANCELLED'],
      SHIPPED: ['SHIPPED', 'DELIVERED'],
      DELIVERED: ['DELIVERED'],
      CANCELLED: ['CANCELLED']
    };
    return flow[status] || [status];
  }

  getNextStatus(status: string): string {
    const flow: { [key: string]: string } = {
      PENDING: 'ACCEPTED',
      ACCEPTED: 'PROCESSING',
      PROCESSING: 'SHIPPED',
      SHIPPED: 'DELIVERED'
    };
    return flow[(status || '').toUpperCase()] || '';
  }

  totalAmount(order: any): number {
    return Number(order?.totalAmount) || 0;
  }

  countByStatus(status: string): number {
    if (status === 'ALL') return this.orders.length;
    return this.orders.filter(order => (order.status || 'PENDING').toUpperCase() === status).length;
  }

  statusClass(status: string | null | undefined): string {
    const normalized = (status || '').toUpperCase();
    if (normalized === 'DELIVERED') return 'delivered';
    if (normalized === 'SHIPPED') return 'shipped';
    if (normalized === 'PROCESSING') return 'processing';
    if (normalized === 'ACCEPTED') return 'accepted';
    if (normalized === 'CANCELLED') return 'cancelled';
    return 'pending';
  }
}
