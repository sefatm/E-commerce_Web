import { Component, OnInit } from '@angular/core';
import { OrderAnalytics } from 'src/app/core/models/order.model';
import { OrderService } from 'src/app/core/services/order.service';


@Component({
  selector: 'app-order-analytics',
  templateUrl: './order-analytics.component.html',
  styleUrls: ['./order-analytics.component.css']
})
export class OrderAnalyticsComponent implements OnInit {

  analytics: OrderAnalytics | null = null;
  isLoading = true;

  monthNames = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];

  constructor(private orderService: OrderService) {}

  ngOnInit(): void {
    this.orderService.getAnalytics().subscribe({
      next: (data) => { this.analytics = data; this.isLoading = false; },
      error: () => this.isLoading = false
    });
  }

  getMonthLabel(monthNum: number): string {
    return this.monthNames[monthNum - 1] || '';
  }

  getMaxRevenue(): number {
    if (!this.analytics?.monthlyRevenue?.length) return 1;
    return Math.max(...this.analytics.monthlyRevenue.map(m => m[1]));
  }

  getBarHeight(val: number): number {
    return Math.round((val / this.getMaxRevenue()) * 140);
  }

  getDeliveryRate(): number {
    if (!this.analytics || !this.analytics.totalOrders) return 0;
    return Math.round((this.analytics.deliveredOrders / this.analytics.totalOrders) * 100);
  }

  getStatusPercent(count: number): number {
    if (!this.analytics || !this.analytics.totalOrders) return 0;
    return Math.round((count / this.analytics.totalOrders) * 100);
  }
}
