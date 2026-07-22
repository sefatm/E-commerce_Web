import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Order } from 'src/app/models/order.model';
import { OrderService } from 'src/app/services/order.service';

@Component({
  selector: 'app-order-tracking',
  templateUrl: './order-tracking.component.html',
  styleUrls: ['./order-tracking.component.css']
})
export class OrderTrackingComponent implements OnInit {

  order: Order | null = null;
  isLoading = true;
  errorMsg = '';

  steps = [
    { key: 'PENDING', label: 'Order Placed', icon: '1', desc: 'The order has been received' },
    { key: 'ACCEPTED', label: 'Accepted', icon: '2', desc: 'The seller accepted the order' },
    { key: 'PROCESSING', label: 'Processing', icon: '3', desc: 'The order is being prepared' },
    { key: 'SHIPPED', label: 'Shipped', icon: '4', desc: 'The order is on the way' },
    { key: 'DELIVERED', label: 'Delivered', icon: '5', desc: 'Order delivered successfully' }
  ];

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

  getCurrentIndex(): number {
    if (!this.order) return 0;
    return this.steps.findIndex(s => s.key === this.order!.status);
  }

  isStepDone(i: number): boolean { return i < this.getCurrentIndex(); }
  isStepActive(i: number): boolean { return i === this.getCurrentIndex(); }

  goBack(): void { this.router.navigate(['/admin/orders/list']); }

  goToDetails(): void {
    if (this.order) this.router.navigate(['/admin/orders/details', this.order.id]);
  }
}
