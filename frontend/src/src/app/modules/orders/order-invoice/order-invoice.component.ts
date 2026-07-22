import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Order } from 'src/app/models/order.model';
import { OrderService } from 'src/app/services/order.service';


@Component({
  selector: 'app-order-invoice',
  templateUrl: './order-invoice.component.html',
  styleUrls: ['./order-invoice.component.css']
})
export class OrderInvoiceComponent implements OnInit {

  order: Order | null = null;
  isLoading = true;
  today = new Date();

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private orderService: OrderService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.orderService.getById(id).subscribe({
      next: (data) => { this.order = data; this.isLoading = false; },
      error: () => this.isLoading = false
    });
  }

  printInvoice(): void { window.print(); }

  goBack(): void { this.router.navigate(['/admin/orders/details', this.order?.id]); }
}
