import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductService } from 'src/app/services/add-product.service';
import { AuthService } from 'src/app/services/auth.service';
import { CommissionService } from 'src/app/services/commission.service';
import { OrderService } from 'src/app/services/order.service';
import { SellerService } from 'src/app/services/seller.service';
import { SellerWithdrawService } from 'src/app/services/seller-withdraw.service';

@Component({
  selector: 'app-seller-dashboard',
  templateUrl: './seller-dashboard.component.html',
  styleUrls: ['./seller-dashboard.component.css']
})
export class SellerDashboardComponent implements OnInit {
  sellers: any[] = [];
  selectedSellerId: number | null = null;
  selectedSeller: any = null;
  products: any[] = [];
  orders: any[] = [];
  stats: any = null;
  commissionSummary: any = null;
  commissions: any[] = [];
  withdrawals: any[] = [];
  availableWithdrawBalance = 0;
  withdrawForm = {
    amount: null as number | null,
    paymentMethod: 'bkash',
    accountName: '',
    accountNumber: '',
    note: ''
  };
  isLoading = false;
  errorMsg = '';
  successMsg = '';
  isAdminView = false;
  viewMode: 'overview' | 'finance' | 'withdrawals' = 'overview';
  searchTerm = '';

  constructor(
    private sellerService: SellerService,
    private productService: ProductService,
    private commissionService: CommissionService,
    private orderService: OrderService,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute,
    private withdrawService: SellerWithdrawService
  ) {}

  ngOnInit(): void {
    this.isAdminView = this.router.url.startsWith('/admin');
    this.setViewMode();
    this.route.queryParams.subscribe(params => {
      this.searchTerm = params['q'] || '';
      const changedSeller = this.selectSellerFromSearch();
      if (changedSeller) this.loadDashboard();
    });
    if (this.isAdminView) {
      this.loadSellers();
    } else {
      this.loadCurrentSeller();
    }
  }

  private setViewMode(): void {
    const url = this.router.url;
    if (url.includes('/withdrawals')) this.viewMode = 'withdrawals';
    else if (url.includes('/commissions')) this.viewMode = 'finance';
    else this.viewMode = 'overview';
  }

  showOverview(): boolean {
    return this.isAdminView || this.viewMode === 'overview';
  }

  showFinance(): boolean {
    return this.isAdminView || this.viewMode === 'finance';
  }

  showWithdrawals(): boolean {
    return this.isAdminView || this.viewMode === 'withdrawals';
  }

  pageTitle(): string {
    if (this.viewMode === 'finance') return 'Seller Commissions';
    if (this.viewMode === 'withdrawals') return 'Seller Withdrawals';
    return 'Seller Dashboard';
  }

  pageSubtitle(): string {
    if (this.viewMode === 'finance') return 'Review seller earnings, platform commission, and payable records.';
    if (this.viewMode === 'withdrawals') return 'Request payout and track withdrawal status.';
    return 'Overview of seller products, orders, stock, and estimated earnings.';
  }

  loadCurrentSeller(): void {
    const user = this.authService.getUser();
    if (!user?.id) {
      this.router.navigate(['/login']);
      return;
    }

    this.isLoading = true;
    this.sellerService.getByUser(user.id).subscribe({
      next: (res: any) => {
        const seller = res?.data ?? res;
        this.sellers = [seller];
        this.selectedSeller = seller;
        this.selectedSellerId = seller.id;

        if (seller.status !== 'APPROVED') {
          this.isLoading = false;
          this.errorMsg = seller.status === 'REJECTED'
            ? 'Your seller application was rejected. Please contact admin or apply again.'
            : 'Your seller application is pending admin approval.';
          return;
        }

        this.loadDashboard();
      },
      error: () => {
        this.isLoading = false;
        this.errorMsg = 'No seller profile found. Please apply as a seller first.';
      }
    });
  }

  loadSellers(): void {
    this.sellerService.getApproved().subscribe({
      next: (res: any) => {
        this.sellers = res?.data ?? res;
        if (this.sellers.length > 0) {
          this.selectSellerFromSearch();
          if (!this.selectedSellerId) {
            this.selectedSellerId = this.sellers[0].id;
          }
          this.loadDashboard();
        }
      },
      error: () => this.errorMsg = 'Approved sellers load failed.'
    });
  }

  loadDashboard(): void {
    if (!this.selectedSellerId) return;
    this.isLoading = true;
    this.errorMsg = '';
    this.selectedSeller = this.sellers.find(s => Number(s.id) === Number(this.selectedSellerId));
    if (!this.selectedSeller && this.selectedSellerId) {
      this.selectedSeller = { id: this.selectedSellerId };
    }

    this.productService.getBySeller(this.selectedSellerId).subscribe({
      next: (res: any) => this.products = res?.data ?? res,
      error: () => this.errorMsg = 'Seller products load failed.'
    });

    this.orderService.getSellerDashboard(this.selectedSellerId).subscribe({
      next: (res: any) => {
        this.stats = res;
        this.orders = res.orders || [];
        this.isLoading = false;
      },
      error: () => {
        this.errorMsg = 'Seller orders load failed.';
        this.isLoading = false;
      }
    });

    this.commissionService.getSellerSummary(this.selectedSellerId).subscribe({
      next: (summary: any) => this.commissionSummary = summary
    });

    this.commissionService.getBySeller(this.selectedSellerId).subscribe({
      next: (data: any[]) => this.commissions = data || []
    });

    this.withdrawService.getBySeller(this.selectedSellerId).subscribe({
      next: (data: any[]) => this.withdrawals = data || []
    });

    this.withdrawService.getAvailableBalance(this.selectedSellerId).subscribe({
      next: (balance: number) => this.availableWithdrawBalance = Number(balance) || 0
    });
  }

  submitWithdraw(): void {
    this.errorMsg = '';
    this.successMsg = '';

    if (!this.selectedSellerId || !this.withdrawForm.amount || this.withdrawForm.amount <= 0) {
      this.errorMsg = 'Enter a valid withdraw amount.';
      return;
    }

    if (!this.withdrawForm.accountName.trim() || !this.withdrawForm.accountNumber.trim()) {
      this.errorMsg = 'Enter account name and account number for withdraw request.';
      return;
    }

    const available = this.availableWithdrawBalance || 0;
    if (this.withdrawForm.amount > available) {
      this.errorMsg = `Withdraw amount cannot exceed available balance (Tk ${available}).`;
      return;
    }

    this.withdrawService.request(this.selectedSellerId, this.withdrawForm).subscribe({
      next: () => {
        this.successMsg = 'Withdraw request submitted.';
        this.errorMsg = '';
        this.withdrawForm = { amount: null, paymentMethod: 'bkash', accountName: '', accountNumber: '', note: '' };
        this.loadDashboard();
        setTimeout(() => this.successMsg = '', 3000);
      },
      error: (err) => {
        this.errorMsg = typeof err?.error === 'string'
          ? err.error
          : 'Withdraw request failed.';
      }
    });
  }

  updateOrderStatus(order: any, status: string): void {
    this.orderService.updateStatus(order.id, status).subscribe({
      next: () => {
        order.status = status;
        this.loadDashboard();
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

  countProducts(status: string): number {
    return this.products.filter(p => (p.approvalStatus || 'PENDING') === status).length;
  }

  payoutTotal(status: string): number {
    const target = status.toUpperCase();
    return this.withdrawals
      .filter(w => (w.status || '').toUpperCase() === target)
      .reduce((sum, w) => sum + (Number(w.amount) || 0), 0);
  }

  statusClass(status: string | null | undefined): string {
    const normalized = (status || '').toUpperCase();
    if (['APPROVED', 'PAID', 'PAYABLE', 'DELIVERED'].includes(normalized)) return 'ok';
    if (['PENDING', 'PROCESSING', 'ACCEPTED'].includes(normalized)) return 'warn';
    if (['REJECTED', 'REFUNDED', 'CANCELLED'].includes(normalized)) return 'danger';
    return '';
  }

  sellerName(): string {
    return this.selectedSeller?.shopName || this.selectedSeller?.name || 'Seller';
  }

  get filteredProducts(): any[] {
    const term = this.searchTerm.trim().toLowerCase();
    if (!term) return this.products;
    return this.products.filter(p =>
      p.name?.toLowerCase().includes(term) ||
      p.sku?.toLowerCase().includes(term) ||
      p.category?.name?.toLowerCase().includes(term) ||
      p.approvalStatus?.toLowerCase().includes(term)
    );
  }

  get filteredOrders(): any[] {
    const term = this.searchTerm.trim().toLowerCase();
    if (!term) return this.orders;
    return this.orders.filter(o =>
      o.orderCode?.toLowerCase().includes(term) ||
      o.customerName?.toLowerCase().includes(term) ||
      o.customerPhone?.includes(term) ||
      o.status?.toLowerCase().includes(term)
    );
  }

  private selectSellerFromSearch(): boolean {
    if (!this.searchTerm.trim() || this.sellers.length === 0) return false;
    const term = this.searchTerm.toLowerCase();
    const matched = this.sellers.find(s =>
      s.shopName?.toLowerCase().includes(term) ||
      s.name?.toLowerCase().includes(term) ||
      s.email?.toLowerCase().includes(term) ||
      s.phone?.includes(term) ||
      s.district?.toLowerCase().includes(term)
    );
    if (matched && Number(this.selectedSellerId) !== Number(matched.id)) {
      this.selectedSellerId = matched.id;
      return true;
    }
    return false;
  }
}
