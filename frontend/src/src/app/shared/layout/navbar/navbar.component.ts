import { Component, OnDestroy, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router, NavigationEnd } from '@angular/router';
import { forkJoin, Subscription } from 'rxjs';
import { filter } from 'rxjs/operators';
import { SidebarService } from 'src/app/core/services/sidebar.service';
import { AuthService } from 'src/app/core/services/auth.service';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit, OnDestroy {

  showProfile = false;
  showNotif = false;
  searchQuery = '';
  searchFocused = false;
  pageTitle = 'Dashboard';
  adminName = 'Admin User';
  adminEmail = 'admin@ruralmart.com.bd';
  adminPhoto = '';
  adminRole = 'Admin';
  siteName = 'Rural Mart';
  notifications: { title: string; detail: string; route: string; level: 'warn' | 'info' | 'success' }[] = [];
  unreadCount = 0;
  private routeSub?: Subscription;
  private readonly apiUrl = environment.apiUrl;

  private titleMap: { [key: string]: string } = {
    '/admin/dashboard':        'Dashboard',
    '/admin/list':             'Product List',
    '/admin/add':              'Add Product',
    '/admin/products/approvals': 'Product Approvals',
    '/admin/categories':       'Categories',
    '/admin/brands':           'Brands',
    '/admin/attributes':       'Attributes',
    '/admin/variants':         'Variants',
    '/admin/reviews':          'Reviews',
    '/admin/orders/list':      'Order List',
    '/admin/orders/payments':  'Payments',
    '/admin/orders/returns':   'Returns & Refunds',
    '/admin/orders/analytics': 'Order Analytics',
    '/admin/customers/list':   'Customer List',
    '/admin/customers/add':    'Add Customer',
    '/admin/sellers/approvals': 'Seller Approvals',
    '/admin/sellers/withdrawals': 'Seller Withdrawals',
    '/admin/settings/general': 'Settings',
    '/admin/reports/sales': 'Sales Report',
    '/admin/reports/revenue': 'Revenue Report',
    '/admin/reports/products': 'Product Report',
    '/admin/reports/customers': 'Customer Report',
    '/seller/dashboard': 'Seller Dashboard',
    '/seller/orders': 'Seller Orders',
    '/seller/products': 'My Products',
    '/seller/add-product': 'Add Product',
    '/seller/inventory': 'Inventory',
    '/seller/settings': 'Profile Settings',
  };

  constructor(
    private router: Router,
    private sidebarService: SidebarService,
    private authService: AuthService,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.updateTitle(this.router.url);
    this.loadNavbarData();
    window.addEventListener('adminProfileUpdated', this.adminProfileUpdatedHandler);

    this.routeSub = this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe((event: any) => {
        this.updateTitle(event.urlAfterRedirects || event.url);
        this.closeAll();
      });
  }

  ngOnDestroy(): void {
    window.removeEventListener('adminProfileUpdated', this.adminProfileUpdatedHandler);
    this.routeSub?.unsubscribe();
  }

  private adminProfileUpdatedHandler = () => {
    this.loadNavbarData();
  };

  private loadNavbarData(): void {
    this.loadAdminProfile();
    this.loadNotifications();
  }

  private loadAdminProfile(): void {
    const user = this.authService.getUser();
    this.adminRole = this.router.url.startsWith('/admin')
      ? 'Admin'
      : (user?.role ? this.toTitleCase(user.role) : 'Admin');

    if (this.authService.isSeller()) {
      this.adminName = user?.name || 'Seller';
      this.adminEmail = user?.email || '';
      this.adminPhoto = this.profilePhotoUrl(user?.profileImage);
      return;
    }

    this.http.get<any>(`${this.apiUrl}/settings/all`).subscribe({
      next: (res) => {
        const site = res?.site || {};
        const profile = res?.profile || {};
        this.siteName = site.siteName || 'Rural Mart';
        this.adminName = profile.name || 'Admin User';
        this.adminEmail = profile.email || 'admin@ruralmart.com.bd';
        this.adminPhoto = this.profilePhotoUrl(profile.profileImage);
      },
      error: () => {}
    });
  }

  private loadNotifications(): void {
    forkJoin({
      analytics: this.http.get<any>(`${this.apiUrl}/order/analytics`),
      pendingSellers: this.http.get<any>(`${this.apiUrl}/seller/pending`),
      pendingProducts: this.http.get<any[]>(`${this.apiUrl}/product/pending`),
    }).subscribe({
      next: ({ analytics, pendingSellers, pendingProducts }) => {
        const sellers = pendingSellers?.data ?? pendingSellers ?? [];
        const products = Array.isArray(pendingProducts) ? pendingProducts : [];
        const pendingOrders = analytics?.pendingOrders ?? 0;
        const acceptedOrders = analytics?.acceptedOrders ?? 0;
        const processingOrders = analytics?.processingOrders ?? 0;

        this.notifications = [
          {
            title: `${pendingOrders} pending order${pendingOrders === 1 ? '' : 's'}`,
            detail: pendingOrders > 0 ? 'Need admin or seller action' : 'No pending orders right now',
            route: '/admin/orders/list',
            level: pendingOrders > 0 ? 'warn' : 'success'
          },
          {
            title: `${acceptedOrders} accepted order${acceptedOrders === 1 ? '' : 's'}`,
            detail: acceptedOrders > 0 ? 'Ready to move into processing' : 'No accepted orders waiting',
            route: '/admin/orders/list',
            level: 'info'
          },
          {
            title: `${products.length} product approval${products.length === 1 ? '' : 's'}`,
            detail: products.length > 0 ? 'Review artisan product submissions' : 'Product approval queue is clear',
            route: '/admin/products/approvals',
            level: products.length > 0 ? 'warn' : 'success'
          },
          {
            title: `${sellers.length} seller application${sellers.length === 1 ? '' : 's'}`,
            detail: sellers.length > 0 ? 'Review seller onboarding requests' : 'Seller queue is clear',
            route: '/admin/sellers/approvals',
            level: sellers.length > 0 ? 'warn' : 'success'
          },
          {
            title: `${processingOrders} processing order${processingOrders === 1 ? '' : 's'}`,
            detail: processingOrders > 0 ? 'Orders currently being prepared' : 'No processing orders',
            route: '/admin/orders/list',
            level: 'info'
          }
        ];

        this.unreadCount = pendingOrders + acceptedOrders + products.length + sellers.length;
      },
      error: () => {
        this.notifications = [{
          title: 'Dashboard data unavailable',
          detail: 'Check backend server connection',
          route: '/admin/dashboard',
          level: 'warn'
        }];
        this.unreadCount = 1;
      }
    });
  }

  profilePhotoUrl(image: string | null | undefined): string {
    if (!image) return '';
    if (image.startsWith('http') || image.startsWith('data:')) return image;
    return `${this.apiUrl}/uploads/` + image;
  }

  adminInitials(): string {
    const name = this.adminName || 'Admin';
    return name.split(' ').filter(Boolean).slice(0, 2).map(part => part[0]).join('').toUpperCase();
  }

  goToNotification(item: { route: string }): void {
    this.closeAll();
    this.router.navigate([item.route]);
  }

  refreshNotifications(): void {
    this.loadNotifications();
  }

  search(): void {
    const query = this.searchQuery.trim();
    if (!query) return;
    const normalized = query.toLowerCase();
    const route = this.resolveSearchRoute(normalized);
    this.closeAll();

    if (route.exact) {
      this.router.navigate([route.path]);
      return;
    }

    this.router.navigate([route.path], { queryParams: { q: query } });
  }

  private resolveSearchRoute(query: string): { path: string; exact: boolean } {
    const currentPath = this.router.url.split('?')[0].split('#')[0];
    const moduleRoutes: { keys: string[]; path: string }[] = [
      { keys: ['dashboard', 'home'], path: '/admin/dashboard' },
      { keys: ['product', 'products', 'product list', 'catalog'], path: '/admin/list' },
      { keys: ['add product', 'create product', 'new product'], path: '/admin/add' },
      { keys: ['product approval', 'product approvals', 'pending products'], path: '/admin/products/approvals' },
      { keys: ['order', 'orders', 'order list', 'sales order'], path: this.authService.isSeller() ? '/seller/orders' : '/admin/orders/list' },
      { keys: ['return', 'returns', 'refund', 'refunds'], path: '/admin/orders/returns' },
      { keys: ['payment', 'payments'], path: '/admin/orders/payments' },
      { keys: ['customer', 'customers', 'buyer', 'buyers'], path: '/admin/customers/list' },
      { keys: ['seller', 'sellers', 'vendor', 'vendors', 'seller list', 'vendor list'], path: '/admin/sellers/approvals' },
      { keys: ['seller approval', 'seller approvals', 'pending sellers', 'seller application', 'seller applications'], path: '/admin/sellers/approvals' },
      { keys: ['seller dashboard', 'vendor dashboard'], path: '/admin/sellers/approvals' },
      { keys: ['withdraw', 'withdrawals', 'payout', 'payouts'], path: '/admin/sellers/withdrawals' },
      { keys: ['category', 'categories'], path: '/admin/categories' },
      { keys: ['brand', 'brands'], path: '/admin/brands' },
      { keys: ['coupon', 'coupons'], path: '/admin/coupons' },
      { keys: ['offer', 'offers'], path: '/admin/offers' },
      { keys: ['shipping', 'shipment'], path: '/admin/tracking' },
      { keys: ['settings', 'profile'], path: '/admin/settings/general' },
      { keys: ['commission', 'commissions', 'profit'], path: '/admin/commissions' },
      { keys: ['sales report', 'sales reports'], path: '/admin/reports/sales' },
      { keys: ['revenue report', 'revenue reports'], path: '/admin/reports/revenue' },
      { keys: ['product report', 'product reports'], path: '/admin/reports/products' },
      { keys: ['customer report', 'customer reports'], path: '/admin/reports/customers' },
      { keys: ['report', 'reports'], path: '/admin/reports/sales' },
      { keys: ['inventory', 'stock'], path: this.authService.isSeller() ? '/seller/inventory' : '/admin/list' },
      { keys: ['review', 'reviews', 'rating', 'ratings'], path: '/admin/reviews' }
    ];

    const exactMatch = moduleRoutes.find(item => item.keys.includes(query));
    if (exactMatch) return { path: exactMatch.path, exact: true };

    if (this.isOrderCodeSearch(query)) {
      return {
        path: this.authService.isSeller()
          ? '/seller/orders'
          : (currentPath === '/admin/orders/returns' ? '/admin/orders/returns' : '/admin/orders/list'),
        exact: false
      };
    }

    if (this.isProductFieldSearch(query)) {
      return { path: '/admin/list', exact: false };
    }

    if (this.isSearchableListPath(currentPath)) {
      return { path: currentPath, exact: false };
    }

    if (query.startsWith('ord-') || query.startsWith('#ord') || query.includes('order')) {
      return { path: this.authService.isSeller() ? '/seller/orders' : '/admin/orders/list', exact: false };
    }
    if (query.includes('@') || query.includes('customer') || query.includes('buyer')) {
      return { path: '/admin/customers/list', exact: false };
    }
    if (query.includes('seller') || query.includes('vendor') || query.includes('artisan')) {
      return { path: '/admin/sellers/approvals', exact: false };
    }
    if (query.includes('brand')) return { path: '/admin/brands', exact: false };
    if (query.includes('categor')) return { path: '/admin/categories', exact: false };
    if (query.includes('shipping') || query.includes('shipment') || query.includes('tracking')) {
      return { path: '/admin/tracking', exact: false };
    }
    if (query.includes('stock') || query.includes('inventory')) {
      return { path: this.authService.isSeller() ? '/seller/inventory' : '/admin/list', exact: false };
    }

    if (query.includes('order')) return { path: this.authService.isSeller() ? '/seller/orders' : '/admin/orders/list', exact: false };
    if (query.includes('customer') || query.includes('buyer')) return { path: '/admin/customers/list', exact: false };
    if (query.includes('product approval') || query.includes('pending product')) return { path: '/admin/products/approvals', exact: true };
    if (query.includes('product') || query.includes('catalog')) return { path: '/admin/list', exact: false };
    if (query.includes('seller approval') || query.includes('seller application') || query.includes('pending seller')) {
      return { path: '/admin/sellers/approvals', exact: true };
    }
    if (query.includes('seller') || query.includes('vendor')) return { path: '/admin/sellers/approvals', exact: true };
    if (query.includes('brand')) return { path: '/admin/brands', exact: true };
    if (query.includes('categor')) return { path: '/admin/categories', exact: true };

    return { path: '/admin/list', exact: false };
  }

  private isOrderCodeSearch(query: string): boolean {
    return /^(#?ord-|rural-mart-\d{8}-\d+|rural-mart-\d+)/i.test(query.trim());
  }

  private isProductFieldSearch(query: string): boolean {
    return [
      'sku',
      'origin',
      'origin area',
      'artisan story',
      'craft process',
      'process',
      'story'
    ].some(key => query.includes(key));
  }

  private isSearchableListPath(path: string): boolean {
    return [
      '/admin/list',
      '/admin/orders/list',
      '/admin/orders/returns',
      '/seller/orders',
      '/admin/customers/list',
      '/admin/sellers/approvals',
      '/admin/categories',
      '/admin/brands',
      '/seller/inventory',
      '/seller/products',
      '/admin/reviews',
      '/admin/tracking'
    ].includes(path);
  }

  private toTitleCase(value: string): string {
    return value.charAt(0).toUpperCase() + value.slice(1).toLowerCase();
  }

  private updateTitle(url: string): void {
    const cleanUrl = url.split('?')[0].split('#')[0];
    const match = Object.keys(this.titleMap).find(key => cleanUrl.startsWith(key));
    this.pageTitle = match ? this.titleMap[match] : 'Admin Panel';
  }

  toggleProfile(): void {
    this.showProfile = !this.showProfile;
    this.showNotif = false;
  }

  toggleNotif(): void {
    this.showNotif = !this.showNotif;
    this.showProfile = false;
  }

  closeAll(): void {
    this.showProfile = false;
    this.showNotif = false;
  }

  toggleSidebar(): void {
    this.sidebarService.toggle();
  }

  goToProfile(): void {
    this.closeAll();
    this.router.navigate([this.authService.isSeller() ? '/seller/settings' : '/admin/settings/general']);
  }

  goToSettings(): void {
    this.closeAll();
    this.router.navigate(['/admin/settings/general']);
  }

  logout(): void {
    this.closeAll();
    this.authService.logout();
    this.router.navigate(['/']);
  }
}
