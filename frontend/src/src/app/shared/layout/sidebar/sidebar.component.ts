import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { Subscription } from 'rxjs';
import { SidebarService } from 'src/app/core/services/sidebar.service';
import { AuthService } from 'src/app/core/services/auth.service';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent implements OnInit, OnDestroy {

  openMenu: string | null = null;
  isCollapsed = false;
  private sub!: Subscription;

  private routeMenuMap: { [key: string]: string } = {
    '/admin/list':          'products',
    '/admin/products/approvals': 'products',
    '/admin/add':           'products',
    '/admin/categories':    'products',
    '/admin/brands':        'products',
    '/admin/attributes':    'products',
    '/admin/variants':      'products',
    '/admin/reviews':       'products',
    '/admin/orders/list':   'orders',
    '/admin/orders/payments':  'orders',
    '/admin/commissions': 'orders',
    '/admin/orders/returns':   'orders',
    '/admin/orders/analytics': 'orders',
    '/admin/customers/list':  'customers',
    '/admin/customers/add':   'customers',
    '/admin/sellers/approvals': 'sellers',
    '/admin/sellers/withdrawals': 'sellers',
    '/admin/vendors/list': 'sellers',
    '/admin/vendors/payouts': 'sellers',
    '/admin/reports/sales': 'reports',
    '/admin/reports/revenue': 'reports',
    '/admin/reports/products': 'reports',
    '/admin/reports/customers': 'reports',
    '/seller/dashboard': 'seller-dashboard',
    '/seller/orders': 'seller-dashboard',
    '/seller/add-product': 'seller-products',
    '/seller/products': 'seller-products',
    '/seller/inventory': 'seller-products',
    '/seller/commissions': 'seller-money',
    '/seller/withdrawals': 'seller-money',
    '/seller/settings': 'seller-settings',
  };

  constructor(
    private router: Router,
    private sidebarService: SidebarService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.sub = this.sidebarService.isCollapsed$.subscribe(val => {
      this.isCollapsed = val;
    });

    this.setMenuFromRoute(this.router.url);

    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe((event: any) => {
        this.setMenuFromRoute(event.urlAfterRedirects || event.url);
      });
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  private setMenuFromRoute(url: string): void {
    const cleanUrl = url.split('?')[0].split('#')[0];
    const match = Object.keys(this.routeMenuMap).find(key => cleanUrl.startsWith(key));
    if (match) {
      this.openMenu = this.routeMenuMap[match];
    }
  }

  toggle(menu: string): void {
    this.openMenu = this.openMenu === menu ? null : menu;
  }

  isAdmin(): boolean {
    return this.authService.isAdmin();
  }

  isSeller(): boolean {
    return this.authService.isSeller() && !this.isAdmin();
  }

  userName(): string {
    return this.authService.getUser()?.name || (this.isSeller() ? 'Seller' : 'Admin');
  }

  roleLabel(): string {
    return this.isSeller() ? 'Seller' : 'Admin';
  }

  userInitials(): string {
    const name = this.userName();
    return name.split(' ').filter(Boolean).slice(0, 2).map(part => part[0]).join('').toUpperCase() || 'AD';
  }
}
