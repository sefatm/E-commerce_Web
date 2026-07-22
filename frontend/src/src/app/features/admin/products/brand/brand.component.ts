import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { BrandService } from 'src/app/core/services/brand.service';


@Component({
  selector: 'app-brand',
  templateUrl: './brand.component.html',
  styleUrls: ['./brand.component.css']
})
export class BrandComponent implements OnInit {

  brands: any[] = [];
  showForm = false;
  isLoading = false;
  isSubmitting = false;
  editId: number | null = null;
  searchTerm = '';
  successMsg = '';
  errorMsg = '';

  brandForm = { name: '', logo: '', description: '', status: 'active' };

  constructor(
    private brandService: BrandService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.searchTerm = params['q'] || '';
      this.onSearch();
    });
  }

  loadBrands(): void {
    this.isLoading = true;
    this.brandService.getAll().subscribe({
      next: (res: any) => { this.brands = res?.data ?? res; this.isLoading = false; },
      error: () => { this.errorMsg = 'Failed to load brands.'; this.isLoading = false; }
    });
  }

  onSearch(): void {
    if (!this.searchTerm.trim()) { this.loadBrands(); return; }
    this.brandService.getAll(this.searchTerm).subscribe({
      next: (res: any) => { this.brands = res?.data ?? res; }
    });
  }

  toggleForm(): void {
    this.showForm = !this.showForm;
    if (!this.showForm) this.cancelForm();
  }

  startEdit(brand: any): void {
    this.editId = brand.id;
    this.brandForm = { name: brand.name, logo: brand.logo || '', description: brand.description || '', status: brand.status || 'active' };
    this.showForm = true;
  }

  cancelForm(): void {
    this.showForm = false;
    this.editId = null;
    this.brandForm = { name: '', logo: '', description: '', status: 'active' };
  }

  onSubmit(): void {
    this.successMsg = '';
    this.errorMsg = '';
    if (!this.brandForm.name.trim()) {
      this.errorMsg = 'Brand name is required.';
      this.scrollToTop();
      return;
    }
    this.isSubmitting = true;

    const call = this.editId
      ? this.brandService.update(this.editId, this.brandForm)
      : this.brandService.create(this.brandForm);

    call.subscribe({
      next: () => {
        this.successMsg = this.editId ? 'Brand updated!' : 'Brand added successfully!';
        this.scrollToTop();
        this.loadBrands();
        this.cancelForm();
        this.isSubmitting = false;
        setTimeout(() => this.successMsg = '', 3000);
      },
      error: () => {
        this.errorMsg = 'Operation failed. Please try again.';
        this.scrollToTop();
        this.isSubmitting = false;
        setTimeout(() => this.errorMsg = '', 4000);
      }
    });
  }

  deleteBrand(id: number): void {
    if (!confirm('এই brand টি delete করবেন?')) return;
    this.brandService.delete(id).subscribe({
      next: () => { this.successMsg = 'Brand deleted.'; this.scrollToTop(); this.loadBrands(); setTimeout(() => this.successMsg = '', 3000); },
      error: () => { this.errorMsg = 'Delete failed.'; this.scrollToTop(); setTimeout(() => this.errorMsg = '', 4000); }
    });
  }

  private scrollToTop(): void {
    setTimeout(() => window.scrollTo({ top: 0, behavior: 'smooth' }), 0);
  }
}
