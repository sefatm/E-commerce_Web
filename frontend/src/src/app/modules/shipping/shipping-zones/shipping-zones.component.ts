import { Component, OnInit } from '@angular/core';
import { ShippingService } from 'src/app/services/shipping.service';

@Component({
  selector: 'app-shipping-zones',
  templateUrl: './shipping-zones.component.html',
  styleUrls: ['./shipping-zones.component.css']
})
export class ShippingZonesComponent implements OnInit {

  zones: any[] = [];
  isLoading = false;
  isSubmitting = false;
  showForm = false;
  editId: number | null = null;

  form = { name: '', description: '', regions: '', status: 'active' };

  constructor(private shippingService: ShippingService) {}

  ngOnInit(): void { this.loadZones(); }

  loadZones(): void {
    this.isLoading = true;
    this.shippingService.getZones().subscribe({
      next: (res: any) => { this.zones = res?.data ?? res; this.isLoading = false; },
      error: () => { this.isLoading = false; }
    });
  }

  toggleForm(): void { this.showForm = !this.showForm; if (!this.showForm) this.cancelForm(); }

  startEdit(zone: any): void {
    this.editId = zone.id;
    this.form = { name: zone.name, description: zone.description || '', regions: zone.regions || '', status: zone.status };
    this.showForm = true;
  }

  cancelForm(): void {
    this.showForm = false; this.editId = null;
    this.form = { name: '', description: '', regions: '', status: 'active' };
  }

  onSubmit(): void {
    this.isSubmitting = true;
    const call = this.editId
      ? this.shippingService.updateZone(this.editId, this.form)
      : this.shippingService.createZone(this.form);

    call.subscribe({
      next: () => { this.loadZones(); this.cancelForm(); this.isSubmitting = false; },
      error: () => { this.isSubmitting = false; }
    });
  }

  deleteZone(id: number): void {
    if (!confirm('এই zone টি delete করবেন? এর সব delivery methods-ও delete হবে।')) return;
    this.shippingService.deleteZone(id).subscribe({ next: () => this.loadZones() });
  }
}
