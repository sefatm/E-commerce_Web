import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Customer } from 'src/app/models/add-customer.model';
import { CustomerService } from 'src/app/services/add-customer.service';

@Component({
  selector: 'app-customer-list',
  templateUrl: './customer-list.component.html',
  styleUrls: ['./customer-list.component.css']
})
export class CustomerListComponent implements OnInit {

  customers: Customer[] = [];
  filteredCustomers: Customer[] = [];
  isLoading = false;
  successMsg = '';
  errorMsg = '';

  searchKeyword = '';
  selectedType   = 'ALL';
  selectedStatus = 'ALL';

  constructor(
    private customerService: CustomerService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.searchKeyword = params['q'] || '';
      this.applyFilter();
    });
    this.loadCustomers();
  }

  loadCustomers(): void {
    this.isLoading = true;
    this.customerService.getCustomers().subscribe({
      next: (res) => {
        this.customers = res;
        this.applyFilter();
        this.isLoading = false;
      },
      error: () => {
        this.errorMsg = 'Failed to load customers. Please check connection.';
        this.isLoading = false;
        setTimeout(() => this.errorMsg = '', 4000);
      }
    });
  }

  applyFilter(): void {
    let result = [...this.customers];
    if (this.selectedType !== 'ALL')
      result = result.filter(c => c.type === this.selectedType.toLowerCase());
    if (this.selectedStatus !== 'ALL')
      result = result.filter(c => c.status === this.selectedStatus.toLowerCase());
    if (this.searchKeyword.trim()) {
      const kw = this.searchKeyword.toLowerCase();
      result = result.filter(c =>
        c.name?.toLowerCase().includes(kw) ||
        c.email?.toLowerCase().includes(kw) ||
        c.phone?.includes(kw) ||
        c.address?.toLowerCase().includes(kw) ||
        c.district?.toLowerCase().includes(kw) ||
        c.upazila?.toLowerCase().includes(kw)
      );
    }
    this.filteredCustomers = result;
  }

  deleteCustomer(id: number): void {
    if (!confirm('Are you sure you want to delete this customer?')) return;
    this.customerService.deleteCustomer(id).subscribe({
      next: () => {
        this.successMsg = 'Customer deleted successfully.';
        this.loadCustomers();
        setTimeout(() => this.successMsg = '', 3000);
      },
      error: () => {
        this.errorMsg = 'Delete failed. Please try again.';
        setTimeout(() => this.errorMsg = '', 4000);
      }
    });
  }

  getTotalCount():  number { return this.customers.length; }
  getActiveCount(): number { return this.customers.filter(c => c.status === 'active').length; }
  getVipCount():    number { return this.customers.filter(c => c.type === 'vip').length; }
}
