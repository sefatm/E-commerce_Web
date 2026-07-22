import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

  isLoading = true;
  errorMsg  = '';

  greeting     = 'Good morning';
  currentMonth = '';
  adminName    = 'Admin';

  totalRevenue      = 0;
  revenueThisMonth  = 0;
  revenueGrowth     = 0;
  systemRevenue     = 0;
  totalOrders       = 0;
  pendingOrders     = 0;
  acceptedOrders    = 0;
  totalCustomers    = 0;
  activeProducts    = 0;

  monthlyRevenue: { month: string; totalRevenue: number; orderCount: number; pct: number }[] = [];
  maxRevenue = 1;

  orders: any[] = [];

  topProducts: any[] = [];

  orderStatuses: { label: string; count: number; pct: number; color: string }[] = [];

  private readonly ORDER_API  = `${environment.apiUrl}/order`;
  private readonly REPORT_API = `${environment.apiUrl}/report`;
  private readonly COMMISSION_API = `${environment.apiUrl}/commission`;
  private readonly SETTINGS_API = `${environment.apiUrl}/settings`;

  constructor(private http: HttpClient, private router: Router) {
    const h = new Date().getHours();
    this.greeting    = h < 12 ? 'Good morning' : h < 17 ? 'Good afternoon' : 'Good evening';
    this.currentMonth = new Date().toLocaleDateString('en-US', { month: 'long', year: 'numeric' });
  }

  ngOnInit(): void {
    this.loadAdminName();
    this.loadDashboard();
  }

  loadAdminName(): void {
    this.http.get<any>(`${this.SETTINGS_API}/all`).subscribe({
      next: (res) => {
        this.adminName = res?.profile?.name || 'Admin';
      },
      error: () => {
        this.adminName = 'Admin';
      }
    });
  }

  loadDashboard(): void {
    this.isLoading = true;
    this.errorMsg  = '';

    forkJoin({
      analytics:    this.http.get<any>(`${this.ORDER_API}/analytics`),
      revenue:      this.http.get<any>(`${this.REPORT_API}/revenue`),
      commission:   this.http.get<any>(`${this.COMMISSION_API}/summary`),
      products:     this.http.get<any>(`${this.REPORT_API}/products`),
      recentOrders: this.http.get<any[]>(`${this.ORDER_API}/getall`),
    }).subscribe({
      next: ({ analytics, revenue, commission, products, recentOrders }) => {

        this.totalRevenue     = analytics.totalRevenue     ?? 0;
        this.revenueThisMonth = analytics.revenueThisMonth ?? 0;
        this.systemRevenue    = commission?.platformCommission ?? 0;
        this.totalOrders      = analytics.totalOrders      ?? 0;
        this.pendingOrders    = analytics.pendingOrders    ?? 0;
        this.acceptedOrders   = analytics.acceptedOrders   ?? 0;
        this.revenueGrowth    = revenue.growthPercent      ?? 0;

        const monthly: any[] = revenue.monthly ?? [];
        this.maxRevenue = Math.max(...monthly.map((m: any) => m.totalRevenue ?? 0), 1);
        this.monthlyRevenue = monthly.map((m: any) => ({
          month:        m.month,
          totalRevenue: m.totalRevenue ?? 0,
          orderCount:   m.orderCount   ?? 0,
          pct:          Math.round(((m.totalRevenue ?? 0) / this.maxRevenue) * 90)
        }));

        const allOrders = Array.isArray(recentOrders) ? recentOrders : [];
        this.totalCustomers = new Set(allOrders.map((o: any) => o.customerPhone)).size;
        this.orders = allOrders.slice(0, 6).map((o: any) => ({
          id:          o.orderCode ?? `#${o.id}`,
          customer:    o.customerName  ?? 'Unknown',
          initials:    this.getInitials(o.customerName),
          avatarBg:    this.avatarColor(o.customerName),
          date:        this.formatDate(o.orderDate),
          items:       o.items?.length ?? 0,
          total:       `৳${Number(o.totalAmount ?? 0).toFixed(2)}`,
          status:      o.status ?? 'PENDING',
          statusClass: this.statusClass(o.status),
          rawId:       o.id
        }));

        const delivered  = analytics.deliveredOrders  ?? 0;
        const accepted   = analytics.acceptedOrders   ?? 0;
        const processing = analytics.processingOrders ?? 0;
        const pending    = analytics.pendingOrders    ?? 0;
        const cancelled  = analytics.cancelledOrders  ?? 0;
        const total      = Math.max(delivered + accepted + processing + pending + cancelled, 1);
        this.orderStatuses = [
          { label: 'Delivered',  count: delivered,  pct: Math.round(delivered  / total * 100), color: '#10b981' },
          { label: 'Accepted',   count: accepted,   pct: Math.round(accepted   / total * 100), color: '#6366f1' },
          { label: 'Processing', count: processing, pct: Math.round(processing / total * 100), color: '#3b82f6' },
          { label: 'Pending',    count: pending,    pct: Math.round(pending    / total * 100), color: '#f59e0b' },
          { label: 'Cancelled',  count: cancelled,  pct: Math.round(cancelled  / total * 100), color: '#ef4444' },
        ];

        const productItems: any[] = products.items ?? [];
        this.activeProducts = productItems.length;
        this.topProducts = productItems.slice(0, 5).map((p: any) => ({
          name:     p.productName,
          category: p.category ?? '—',
          sold:     p.totalSold    ?? 0,
          revenue:  p.totalRevenue ?? 0
        }));

        this.isLoading = false;
      },
      error: () => {
        this.errorMsg = 'Backend data unavailable right now, so demo dashboard data is showing. Start Spring Boot on port 8080 and click Refresh Data.';
        this.loadDemoDashboard();
        this.isLoading = false;
      }
    });
  }

  private loadDemoDashboard(): void {
    this.totalRevenue = 285430;
    this.revenueThisMonth = 78450;
    this.revenueGrowth = 18.6;
    this.systemRevenue = 14270;
    this.totalOrders = 246;
    this.pendingOrders = 18;
    this.acceptedOrders = 64;
    this.totalCustomers = 168;
    this.activeProducts = 52;

    const monthly = [
      { month: 'Jan', totalRevenue: 32000, orderCount: 28 },
      { month: 'Feb', totalRevenue: 47000, orderCount: 39 },
      { month: 'Mar', totalRevenue: 38500, orderCount: 33 },
      { month: 'Apr', totalRevenue: 62000, orderCount: 51 },
      { month: 'May', totalRevenue: 70500, orderCount: 62 },
      { month: 'Jun', totalRevenue: 78450, orderCount: 71 },
    ];
    this.maxRevenue = Math.max(...monthly.map(m => m.totalRevenue), 1);
    this.monthlyRevenue = monthly.map(m => ({
      ...m,
      pct: Math.max(8, Math.round((m.totalRevenue / this.maxRevenue) * 90))
    }));

    this.orders = [
      { id: '#RM-1028', customer: 'Sadia Akter', initials: 'SA', avatarBg: '#0e7a4d', date: 'Jun 28, 2026', items: 5, total: '৳1,450.00', status: 'DELIVERED', statusClass: 'status-delivered', rawId: 1028 },
      { id: '#RM-1027', customer: 'Rahim Uddin', initials: 'RU', avatarBg: '#f6a33b', date: 'Jun 27, 2026', items: 3, total: '৳890.00', status: 'PROCESSING', statusClass: 'status-processing', rawId: 1027 },
      { id: '#RM-1026', customer: 'Nusrat Jahan', initials: 'NJ', avatarBg: '#2563eb', date: 'Jun 26, 2026', items: 2, total: '৳650.00', status: 'PENDING', statusClass: 'status-pending', rawId: 1026 },
      { id: '#RM-1025', customer: 'Farhan Ahmed', initials: 'FA', avatarBg: '#7c3aed', date: 'Jun 25, 2026', items: 7, total: '৳2,240.00', status: 'ACCEPTED', statusClass: 'status-accepted', rawId: 1025 },
      { id: '#RM-1024', customer: 'Mim Chowdhury', initials: 'MC', avatarBg: '#dc2626', date: 'Jun 24, 2026', items: 1, total: '৳380.00', status: 'SHIPPED', statusClass: 'status-shipped', rawId: 1024 },
    ];

    this.orderStatuses = [
      { label: 'Delivered', count: 112, pct: 46, color: '#0e7a4d' },
      { label: 'Accepted', count: 64, pct: 26, color: '#4f46e5' },
      { label: 'Processing', count: 34, pct: 14, color: '#1d72c8' },
      { label: 'Pending', count: 26, pct: 10, color: '#f6a33b' },
      { label: 'Cancelled', count: 10, pct: 4, color: '#d54635' },
    ];

    this.topProducts = [
      { name: 'Organic Tomato 1kg', category: 'Vegetables', sold: 86, revenue: 6880 },
      { name: 'Natural Honey 500g', category: 'Organic', sold: 58, revenue: 37700 },
      { name: 'Premium Miniket Rice 25kg', category: 'Rice & Grains', sold: 34, revenue: 66300 },
      { name: 'Country Eggs 12 pcs', category: 'Dairy & Eggs', sold: 73, revenue: 13140 },
      { name: 'Jute Shopping Bag', category: 'Handmade', sold: 41, revenue: 11480 },
    ];
  }

  getInitials(name: string): string {
    if (!name) return 'U';
    const p = name.trim().split(' ');
    return p.length >= 2 ? (p[0][0] + p[1][0]).toUpperCase() : name.substring(0, 2).toUpperCase();
  }

  avatarColor(name: string): string {
    const colors = ['#10b981','#3b82f6','#f59e0b','#a78bfa','#ef4444','#06b6d4','#f97316'];
    return colors[(name?.charCodeAt(0) ?? 0) % colors.length];
  }

  formatDate(d: string): string {
    if (!d) return '—';
    try { return new Date(d).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' }); }
    catch { return d; }
  }

  statusClass(status: string): string {
    switch ((status ?? '').toUpperCase()) {
      case 'DELIVERED':  return 'status-delivered';
      case 'ACCEPTED':   return 'status-accepted';
      case 'PROCESSING': return 'status-processing';
      case 'SHIPPED':    return 'status-shipped';
      case 'CANCELLED':  return 'status-cancelled';
      default:           return 'status-pending';
    }
  }

  getStatusDashArray(status: { pct: number }): string {
    const circumference = 276.46;
    const length = (status.pct / 100) * circumference;
    return `${length} ${circumference - length}`;
  }

  getStatusDashOffset(index: number): number {
    const circumference = 276.46;
    const completed = this.orderStatuses
      .slice(0, index)
      .reduce((sum, status) => sum + (status.pct / 100) * circumference, 0);
    return -completed;
  }

  formatRevenue(n: number): string {
    if (n >= 100000) return '৳' + (n / 100000).toFixed(1) + 'L';
    if (n >= 1000)   return '৳' + (n / 1000).toFixed(1)   + 'k';
    return '৳' + Number(n || 0).toFixed(2);
  }

  viewOrder(order: any): void {
    this.router.navigate(['/admin/orders/details', order.rawId]);
  }
}
