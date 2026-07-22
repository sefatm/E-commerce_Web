import { Component, OnInit } from '@angular/core';
import { Order } from 'src/app/core/models/order.model';
import { OrderService } from 'src/app/core/services/order.service';


@Component({
  selector: 'app-order-payment',
  templateUrl: './order-payment.component.html',
  styleUrls: ['./order-payment.component.css']
})
export class OrderPaymentComponent implements OnInit {

  orders: Order[] = [];
  filteredOrders: Order[] = [];
  selectedPaymentStatus = 'ALL';
  selectedMethod = 'ALL';
  isLoading = true;
  successMsg = '';

  paymentStatuses = ['ALL', 'UNPAID', 'COD_PENDING', 'COD_COLLECTED', 'PAID', 'REFUNDED'];
  paymentMethods  = ['ALL', 'COD', 'BKASH', 'NAGAD', 'ONLINE'];

  constructor(private orderService: OrderService) {}

  ngOnInit(): void { this.loadOrders(); }

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
    if (this.selectedPaymentStatus !== 'ALL')
      result = result.filter(o => o.paymentStatus === this.selectedPaymentStatus);
    if (this.selectedMethod !== 'ALL')
      result = result.filter(o => o.paymentMethod?.toUpperCase() === this.selectedMethod);
    this.filteredOrders = result;
  }

  collectCod(order: Order): void {
    this.orderService.markCodCollected(order.id!).subscribe({ next: msg => { this.successMsg = msg; this.loadOrders(); }, error: err => alert(err?.error || 'COD collection failed') });
  }

  verifyCod(order: Order): void {
    this.orderService.verifyCodPayment(order.id!).subscribe({ next: msg => { this.successMsg = msg; this.loadOrders(); }, error: err => alert(err?.error || 'COD verification failed') });
  }

  markAsPaid(order: Order): void {
    this.orderService.updateStatus(order.id!, order.status!).subscribe(); 
    order.paymentStatus = 'PAID';
    this.orderService.update(order.id!, order).subscribe({
      next: () => {
        this.successMsg = `Order ${order.orderCode} marked as PAID`;
        this.loadOrders();
        setTimeout(() => this.successMsg = '', 3000);
      }
    });
  }

  getTotalRevenue(): number {
    return this.filteredOrders
      .filter(o => o.paymentStatus === 'PAID')
      .reduce((sum, o) => sum + (o.totalAmount || 0), 0);
  }

  getPaidCount(): number {
    return this.filteredOrders.filter(o => o.paymentStatus === 'PAID').length;
  }

  getUnpaidCount(): number {
    return this.filteredOrders.filter(o => o.paymentStatus === 'UNPAID').length;
  }
}
