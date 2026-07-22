import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { OrderService } from 'src/app/services/order.service';

@Component({
  selector: 'app-payment-result',
  templateUrl: './payment-result.component.html',
  styleUrls: ['./payment-result.component.css']
})
export class PaymentResultComponent implements OnInit {

  outcome: 'success' | 'fail' | 'cancel' = 'success';
  orderCode = '';
  isLoading = true;
  order: any = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private orderService: OrderService
  ) {}

  ngOnInit(): void {
    const url = this.route.snapshot.url.map(s => s.path).join('/');
    if (url.includes('fail')) this.outcome = 'fail';
    else if (url.includes('cancel')) this.outcome = 'cancel';
    else this.outcome = 'success';

    this.orderCode = this.route.snapshot.queryParamMap.get('orderCode') || '';

    if (this.orderCode) {
      this.orderService.getByCode(this.orderCode).subscribe({
        next: (res: any) => { this.order = res; this.isLoading = false; },
        error: () => this.isLoading = false
      });
    } else {
      this.isLoading = false;
    }
  }

  get title(): string {
    switch (this.outcome) {
      case 'success': return 'Payment Successful!';
      case 'fail':    return 'Payment Failed';
      case 'cancel':  return 'Payment Cancelled';
    }
  }

  get message(): string {
    switch (this.outcome) {
      case 'success':
        return 'Your payment has been received. Thank you for shopping with Rural Mart!';
      case 'fail':
        return 'Your payment could not be processed. Your order is saved as Cash on Delivery — you can pay when the order arrives, or try paying again.';
      case 'cancel':
        return 'You cancelled the payment. Your order is still saved as Cash on Delivery.';
    }
  }

  get iconClass(): string {
    switch (this.outcome) {
      case 'success': return 'fa-solid fa-circle-check pr-icon-success';
      case 'fail':    return 'fa-solid fa-circle-xmark pr-icon-fail';
      case 'cancel':  return 'fa-solid fa-circle-exclamation pr-icon-cancel';
    }
  }

  goToOrders(): void {
    this.router.navigate(['/my-orders'], { queryParams: { code: this.orderCode } });
  }

  goHome(): void {
    this.router.navigate(['/']);
  }
}
