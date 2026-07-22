import { Component, OnInit } from '@angular/core';
import { ShippingService } from 'src/app/services/shipping.service';


@Component({
  selector: 'app-delivery-methods',
  templateUrl: './delivery-methods.component.html',
  styleUrls: ['./delivery-methods.component.css']
})
export class DeliveryMethodsComponent implements OnInit {

  methods: any[] = [];
  filteredMethods: any[] = [];
  zones: any[] = [];
  isLoading = false;
  isSubmitting = false;
  showForm = false;
  editId: number | null = null;
  selectedZoneId: any = '';
  selectedFilterZone: number | null = null;

  form = {
    name: '', carrier: '', description: '',
    charge: 0, freeShippingAbove: null as number | null,
    estimatedDays: '', type: 'flat_rate', status: 'active'
  };

  constructor(private shippingService: ShippingService) {}

  ngOnInit(): void {
    this.loadZones();
    this.loadMethods();
  }

  loadZones(): void {
    this.shippingService.getZones().subscribe({
      next: (res: any) => { this.zones = res?.data ?? res; }
    });
  }

  loadMethods(): void {
    this.isLoading = true;
    this.shippingService.getMethods().subscribe({
      next: (res: any) => {
        this.methods = res?.data ?? res;
        this.applyZoneFilter();
        this.isLoading = false;
      },
      error: () => { this.isLoading = false; }
    });
  }

  filterByZone(zoneId: number | null): void {
    this.selectedFilterZone = zoneId;
    this.applyZoneFilter();
  }

  applyZoneFilter(): void {
    if (this.selectedFilterZone === null) {
      this.filteredMethods = [...this.methods];
    } else {
      this.filteredMethods = this.methods.filter(m => m.zone?.id === this.selectedFilterZone);
    }
  }

  toggleForm(): void {
    this.showForm = !this.showForm;
    if (!this.showForm) this.cancelForm();
  }

  startEdit(method: any): void {
    this.editId = method.id;
    this.selectedZoneId = method.zone?.id || '';
    this.form = {
      name: method.name,
      carrier: method.carrier || '',
      description: method.description || '',
      charge: method.charge,
      freeShippingAbove: method.freeShippingAbove || null,
      estimatedDays: method.estimatedDays || '',
      type: method.type || 'flat_rate',
      status: method.status
    };
    this.showForm = true;
  }

  cancelForm(): void {
    this.showForm = false;
    this.editId = null;
    this.selectedZoneId = '';
    this.form = {
      name: '', carrier: '', description: '',
      charge: 0, freeShippingAbove: null,
      estimatedDays: '', type: 'flat_rate', status: 'active'
    };
  }

  onSubmit(): void {
    if (!this.selectedZoneId) return;
    this.isSubmitting = true;

    const call = this.editId
      ? this.shippingService.updateMethod(this.editId, { ...this.form, zone: { id: this.selectedZoneId } })
      : this.shippingService.createMethod(this.form, this.selectedZoneId);

    call.subscribe({
      next: () => { this.loadMethods(); this.cancelForm(); this.isSubmitting = false; },
      error: () => { this.isSubmitting = false; }
    });
  }

  deleteMethod(id: number): void {
    if (!confirm('এই delivery method টি delete করবেন?')) return;
    this.shippingService.deleteMethod(id).subscribe({ next: () => this.loadMethods() });
  }
}
