import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductService } from 'src/app/core/services/add-product.service';
import { AuthService } from 'src/app/core/services/auth.service';
import { SellerService } from 'src/app/core/services/seller.service';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-product-list',
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.css']
})
export class ProductListComponent implements OnInit {

  readonly imageBase = `${environment.apiUrl}/uploads/`;

  products: any[] = [];
  filteredProducts: any[] = [];
  isLoading = false;
  successMsg = '';
  errorMsg = '';
  searchTerm = '';
  statusFilter = 'all';
  approvalFilter = 'all';
  categoryFilter: any = 'all';
  isSellerView = false;
  currentSellerId: number | null = null;

  viewModalProduct: any = null;

  constructor(
    private productService: ProductService,
    private router: Router,
    private route: ActivatedRoute,
    private authService: AuthService,
    private sellerService: SellerService
  ) {}

  ngOnInit(): void {
    this.isSellerView = this.router.url.startsWith('/seller');
    this.route.queryParams.subscribe(params => {
      this.searchTerm = params['q'] || '';
      this.applyFilters();
    });
    if (this.isSellerView) {
      this.loadSellerProducts();
    } else {
      this.loadProducts();
    }
  }

  get addProductRoute(): string {
    return this.isSellerView ? '/seller/add-product' : '/admin/add';
  }

  get canAddProduct(): boolean {
    return this.isSellerView;
  }

  loadProducts(): void {
    this.isLoading = true;
    this.productService.getProducts().subscribe({
      next: (res: any) => {
        this.products = this.normalizeProducts(res);
        this.applyFilters();
        this.isLoading = false;
      },
      error: () => {
        this.errorMsg = 'Products load failed.';
        this.isLoading = false;
        setTimeout(() => this.errorMsg = '', 4000);
      }
    });
  }

  loadSellerProducts(): void {
    const user = this.authService.getUser();
    if (!user?.id) {
      this.router.navigate(['/login']);
      return;
    }

    this.isLoading = true;
    this.sellerService.getByUser(user.id).subscribe({
      next: (sellerRes: any) => {
        const seller = sellerRes?.data ?? sellerRes;
        this.currentSellerId = seller?.id ?? null;
        if (!this.currentSellerId) {
          this.products = [];
          this.filteredProducts = [];
          this.errorMsg = 'No seller profile found.';
          this.isLoading = false;
          return;
        }

        this.productService.getBySeller(this.currentSellerId).subscribe({
          next: (res: any) => {
            this.products = this.normalizeProducts(res);
            this.applyFilters();
            this.isLoading = false;
          },
          error: () => {
            this.errorMsg = 'Seller products load failed.';
            this.isLoading = false;
            setTimeout(() => this.errorMsg = '', 4000);
          }
        });
      },
      error: () => {
        this.products = [];
        this.filteredProducts = [];
        this.errorMsg = 'No seller profile found. Please apply as a seller first.';
        this.isLoading = false;
      }
    });
  }

  applyFilters(): void {
    let result = [...this.products];
    if (this.searchTerm.trim()) {
      const term = this.searchTerm.toLowerCase();
      result = result.filter(p =>
        p.name?.toLowerCase().includes(term) ||
        p.sku?.toLowerCase().includes(term) ||
        p.category?.name?.toLowerCase().includes(term) ||
        p.brand?.name?.toLowerCase().includes(term) ||
        p.seller?.shopName?.toLowerCase().includes(term) ||
        p.seller?.name?.toLowerCase().includes(term) ||
        p.originArea?.toLowerCase().includes(term) ||
        p.description?.toLowerCase().includes(term) ||
        p.artisanStory?.toLowerCase().includes(term) ||
        p.craftProcess?.toLowerCase().includes(term)
      );
    }
    if (this.statusFilter !== 'all') {
      result = result.filter(p => (p.status || 'active') === this.statusFilter);
    }
    if (this.approvalFilter !== 'all') {
      result = result.filter(p => (p.approvalStatus || 'PENDING') === this.approvalFilter);
    }
    if (this.categoryFilter !== 'all') {
      const selected = String(this.categoryFilter);
      result = result.filter(p => this.getProductCategoryId(p) === selected);
    }
    this.filteredProducts = result;
  }

  onCategorySelect(category: any): void {
    const categoryId = this.getSelectedCategoryId(category);
    this.categoryFilter = categoryId;
    this.errorMsg = '';

    if (categoryId === 'all') {
      this.applyFilters();
      return;
    }

    this.applyFilters();
  }

  private normalizeProducts(res: any): any[] {
    const data = res?.data ?? res;
    return Array.isArray(data) ? data : [];
  }

  private getSelectedCategoryId(category: any): any {
    if (category === 'all') return 'all';
    return category?.id ?? category?._id ?? category?.categoryId ?? category ?? 'all';
  }

  private getProductCategoryId(product: any): string {
    return String(
      product?.category?.id ??
      product?.category?._id ??
      product?.categoryId ??
      product?.category_id ??
      ''
    );
  }

  deleteProduct(id: number): void {
    if (!confirm('Delete this product?')) return;
    this.productService.deleteProduct(id).subscribe({
      next: () => {
        this.successMsg = 'Product deleted successfully.';
        this.loadProducts();
        setTimeout(() => this.successMsg = '', 3000);
      },
      error: () => {
        this.errorMsg = 'Delete failed. Please try again.';
        setTimeout(() => this.errorMsg = '', 4000);
      }
    });
  }

  approveProduct(id: number): void {
    if (this.isSellerView) return;
    this.productService.approveProduct(id).subscribe({
      next: () => {
        this.successMsg = 'Product approved.';
        this.loadProducts();
        setTimeout(() => this.successMsg = '', 3000);
      },
      error: () => {
        this.errorMsg = 'Approve failed.';
        setTimeout(() => this.errorMsg = '', 4000);
      }
    });
  }

  rejectProduct(id: number): void {
    if (this.isSellerView) return;
    const reason = prompt('Reject reason') || '';
    this.productService.rejectProduct(id, reason).subscribe({
      next: () => {
        this.successMsg = 'Product rejected.';
        this.loadProducts();
        setTimeout(() => this.successMsg = '', 3000);
      },
      error: () => {
        this.errorMsg = 'Reject failed.';
        setTimeout(() => this.errorMsg = '', 4000);
      }
    });
  }

  viewProduct(p: any): void {
    this.viewModalProduct = p;
  }

  closeViewModal(): void {
    this.viewModalProduct = null;
  }

  editProduct(id: number): void {
    this.router.navigate([this.isSellerView ? '/seller/edit' : '/admin/edit', id]);
  }

  getStockClass(stock: number): string {
    if (!stock || stock <= 0) return 'out';
    if (stock < 10) return 'low-stock';
    return 'in-stock';
  }

  getCategoryName(product: any): string {
   
    return product.category?.name || 'Uncategorized';
  }

  toggleSelectAll(event: any): void {
  }
}
