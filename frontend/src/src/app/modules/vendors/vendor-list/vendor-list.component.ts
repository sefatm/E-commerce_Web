import { Component, OnInit } from '@angular/core';
import { VendorService } from 'src/app/services/vendor.service';


@Component({
  selector: 'app-vendor-list',
  templateUrl: './vendor-list.component.html',
  styleUrls: ['./vendor-list.component.css']
})
export class VendorListComponent implements OnInit {

  vendors: any[] = [];
  filteredVendors: any[] = [];
  stats: any = null;
  isLoading = false;
  isSubmitting = false;
  showAddForm = false;
  activeFilter = '';
  searchTerm = '';

  selectedVendor: any = null;
  showSuspendModal = false;
  suspendTargetId: number | null = null;
  suspendReason = '';

  editingCommissionId: number | null = null;
  newCommissionRate: number = 10;

  addForm = {
    name: '', shopName: '', phone: '', email: '',
    nidNo: '', address: '', commissionRate: 10,
    shopDescription: ''
  };

  private avatarColors = ['#10b981','#3b82f6','#f59e0b','#a78bfa','#ef4444','#06b6d4','#ec4899'];

  constructor(private vendorService: VendorService) {}

  ngOnInit(): void {
    this.loadStats();
    this.loadVendors();
  }

  loadStats(): void {
    this.vendorService.getStats().subscribe({
      next: (res: any) => { this.stats = res?.data ?? res; }
    });
  }

  loadVendors(): void {
    this.isLoading = true;
    this.vendorService.getAll().subscribe({
      next: (res: any) => {
        this.vendors = res?.data ?? res;
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

  applyFilters(): void {
    let result = [...this.vendors];
    if (this.activeFilter) {
      result = result.filter(v => v.status === this.activeFilter);
    }
    if (this.searchTerm.trim()) {
      const term = this.searchTerm.toLowerCase();
      result = result.filter(v =>
        v.name?.toLowerCase().includes(term) ||
        v.shopName?.toLowerCase().includes(term) ||
        v.email?.toLowerCase().includes(term) ||
        v.phone?.includes(term)
      );
    }
    this.filteredVendors = result;
  }

  addVendor(): void {
    this.isSubmitting = true;
    this.vendorService.create(this.addForm).subscribe({
      next: () => {
        this.loadVendors();
        this.loadStats();
        this.cancelAdd();
        this.isSubmitting = false;
      },
      error: () => { this.isSubmitting = false; }
    });
  }

  cancelAdd(): void {
    this.showAddForm = false;
    this.addForm = {
      name: '', shopName: '', phone: '', email: '',
      nidNo: '', address: '', commissionRate: 10, shopDescription: ''
    };
  }

  approveVendor(id: number): void {
    this.vendorService.approve(id).subscribe({
      next: () => { this.loadVendors(); this.loadStats(); }
    });
  }

  promptSuspend(vendor: any): void {
    this.suspendTargetId = vendor.id;
    this.suspendReason = '';
    this.showSuspendModal = true;
  }

  confirmSuspend(): void {
    if (!this.suspendTargetId) return;
    this.vendorService.suspend(this.suspendTargetId, this.suspendReason).subscribe({
      next: () => {
        this.showSuspendModal = false;
        this.suspendTargetId = null;
        this.loadVendors();
        this.loadStats();
      }
    });
  }

  openCommissionEdit(vendor: any): void {
    this.editingCommissionId = vendor.id;
    this.newCommissionRate = vendor.commissionRate;
  }

  saveCommission(id: number): void {
    this.vendorService.updateCommission(id, this.newCommissionRate).subscribe({
      next: () => {
        this.editingCommissionId = null;
        this.loadVendors();
      }
    });
  }

  viewVendor(vendor: any): void {
    this.selectedVendor = vendor;
  }

  deleteVendor(id: number): void {
    if (!confirm('এই vendor টি permanently delete করবেন?')) return;
    this.vendorService.delete(id).subscribe({
      next: () => { this.loadVendors(); this.loadStats(); }
    });
  }

  getStatusLabel(status: string): string {
    const labels: any = {
      'active': 'Active', 'pending': 'Pending',
      'suspended': 'Suspended', 'rejected': 'Rejected'
    };
    return labels[status] || status;
  }

  getAvatarBg(id: number): string {
    return this.avatarColors[id % this.avatarColors.length];
  }
}
