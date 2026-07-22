import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Order } from 'src/app/core/models/order.model';
import { OrderService } from 'src/app/core/services/order.service';

@Component({
  selector: 'app-order-invoice',
  templateUrl: './order-invoice.component.html',
  styleUrls: ['./order-invoice.component.css']
})
export class OrderInvoiceComponent implements OnInit {
  order: Order | null = null;
  isLoading = true;
  today = new Date();
  qrDataUrl = '';
  barcodeImageUrl = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private orderService: OrderService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!Number.isFinite(id) || id <= 0) {
      this.isLoading = false;
      return;
    }

    this.orderService.getById(id).subscribe({
      next: data => {
        this.order = data;
        this.generateCodeUrls();
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      }
    });
  }

  private generateCodeUrls(): void {
    if (!this.order?.id) {
      return;
    }

    const token = this.order.scanToken || '';
    const scanUrl = `${window.location.origin}/admin/orders/scan/${this.order.id}${
      token ? '?token=' + encodeURIComponent(token) : ''
    }`;
    const invoiceValue = this.order.invoiceNo || this.order.orderCode || String(this.order.id);

    // Image APIs remove the qrcode/jsbarcode npm dependency and therefore work
    // with the existing Angular 13 setup without module-resolution errors.
    this.qrDataUrl =
      'https://api.qrserver.com/v1/create-qr-code/?size=220x220&margin=8&data=' +
      encodeURIComponent(scanUrl);

    this.barcodeImageUrl =
      'https://bwipjs-api.metafloor.com/?bcid=code128&scale=2&height=10&includetext&text=' +
      encodeURIComponent(invoiceValue);
  }

  printInvoice(): void {
    window.print();
  }

  goBack(): void {
    this.router.navigate(['/admin/orders/details', this.order?.id]);
  }
}
