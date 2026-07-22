import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ShippingService } from 'src/app/core/services/shipping.service';


@Component({
  selector: 'app-shipment-tracking',
  templateUrl: './shipment-tracking.component.html',
  styleUrls: ['./shipment-tracking.component.css']
})
export class ShipmentTrackingComponent implements OnInit {

  trackingList: any[] = [];
  filteredList: any[] = [];
  allMethods: any[] = [];
  stats: any = null;

  isLoading = false;
  isSubmitting = false;
  showAddForm = false;
  activeFilter = '';
  searchTerm = '';

  addForm = {
    orderId: null as number | null,
    recipientName: '',
    recipientPhone: '',
    shippingAddress: '',
    district: '',
    estimatedDelivery: '',
    methodId: '' as any
  };

  constructor(
    private shippingService: ShippingService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.searchTerm = params['q'] || '';
      this.applyFilters();
    });
    this.loadStats();
    this.loadTracking();
    this.loadMethods();
  }

  loadStats(): void {
    this.shippingService.getTrackingStats().subscribe({
      next: (res: any) => { this.stats = res?.data ?? res; }
    });
  }

  loadMethods(): void {
    this.shippingService.getMethods(undefined, 'active').subscribe({
      next: (res: any) => { this.allMethods = res?.data ?? res; }
    });
  }

  loadTracking(): void {
    this.isLoading = true;
    this.shippingService.getTrackingList().subscribe({
      next: (res: any) => {
        this.trackingList = res?.data ?? res;
        this.applyFilters();
        this.isLoading = false;
      },
      error: () => { this.isLoading = false; }
    });
  }

  filterByStatus(status: string): void {
    this.activeFilter = status;
    this.applyFilters();
  }

  applySearch(): void { this.applyFilters(); }

  applyFilters(): void {
    let result = [...this.trackingList];

    if (this.activeFilter) {
      result = result.filter(t => t.status === this.activeFilter);
    }

    if (this.searchTerm.trim()) {
      const term = this.searchTerm.toLowerCase();
      result = result.filter(t =>
        t.trackingNumber?.toLowerCase().includes(term) ||
        t.recipientName?.toLowerCase().includes(term) ||
        t.recipientPhone?.includes(term) ||
        t.district?.toLowerCase().includes(term) ||
        t.shippingAddress?.toLowerCase().includes(term) ||
        String(t.order?.orderCode || t.orderId || '').toLowerCase().includes(term)
      );
    }

    this.filteredList = result;
  }

  addShipment(): void {
    if (!this.addForm.orderId) return;
    this.isSubmitting = true;

    const payload = {
      orderId: this.addForm.orderId,
      recipientName: this.addForm.recipientName,
      recipientPhone: this.addForm.recipientPhone,
      shippingAddress: this.addForm.shippingAddress,
      district: this.addForm.district,
      estimatedDelivery: this.addForm.estimatedDelivery || null
    };

    this.shippingService.createTracking(payload, this.addForm.methodId || undefined).subscribe({
      next: () => {
        this.loadTracking();
        this.loadStats();
        this.showAddForm = false;
        this.resetAddForm();
        this.isSubmitting = false;
      },
      error: () => { this.isSubmitting = false; }
    });
  }

  onStatusChange(id: number, event: any): void {
    const newStatus = event.target.value;
    this.shippingService.updateTrackingStatus(id, newStatus).subscribe({
      next: () => {
        const item = this.trackingList.find(t => t.id === id);
        if (item) item.status = newStatus;
        this.loadStats();
        this.applyFilters();
      }
    });
  }

  deleteTracking(id: number): void {
    if (!confirm('এই shipment record টি delete করবেন?')) return;
    this.shippingService.deleteTracking(id).subscribe({
      next: () => { this.loadTracking(); this.loadStats(); }
    });
  }

  getStatusLabel(status: string): string {
    const labels: any = {
      'pending':           'Pending',
      'picked_up':         'Picked Up',
      'in_transit':        'In Transit',
      'out_for_delivery':  'Out for Delivery',
      'delivered':         'Delivered',
      'failed':            'Failed',
      'returned':          'Returned'
    };
    return labels[status] || status;
  }

  private resetAddForm(): void {
    this.addForm = {
      orderId: null, recipientName: '', recipientPhone: '',
      shippingAddress: '', district: '', estimatedDelivery: '', methodId: ''
    };
  }
}
