import { Component, OnInit } from '@angular/core';
import { ProductService } from 'src/app/services/add-product.service';

@Component({
  selector: 'app-product-approvals',
  templateUrl: './product-approvals.component.html',
  styleUrls: ['./product-approvals.component.css']
})
export class ProductApprovalsComponent implements OnInit {
  products: any[] = [];
  allProducts: any[] = [];
  isLoading = false;
  successMsg = '';
  errorMsg = '';
  selectedProduct: any = null;
  rejectReason = '';
  decisionError = '';
  isSubmitting = false;
  activeTab: 'PENDING' | 'APPROVED' | 'REJECTED' = 'PENDING';
  counts = { pending: 0, approved: 0, rejected: 0 };

  constructor(private productService: ProductService) {}

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(): void {
    this.isLoading = true;
    this.errorMsg = '';

    const request$ = this.activeTab === 'PENDING'
      ? this.productService.getPendingProducts()
      : this.activeTab === 'REJECTED'
        ? this.productService.getRejectedProducts()
        : this.productService.getProducts();

    request$.subscribe({
      next: (res: any) => {
        const data = this.normalizeProducts(res);
        this.products = this.activeTab === 'APPROVED'
          ? data.filter(p => this.productStatus(p) === 'APPROVED')
          : data;
        this.refreshCounts();
        this.isLoading = false;
      },
      error: () => {
        this.errorMsg = 'Products load failed.';
        this.isLoading = false;
      }
    });
  }

  switchTab(tab: 'PENDING' | 'APPROVED' | 'REJECTED'): void {
    if (this.activeTab === tab) return;
    this.activeTab = tab;
    this.closeReview();
    this.loadProducts();
  }

  approve(id: number): void {
    this.decisionError = '';
    this.errorMsg = '';
    this.isSubmitting = true;
    this.productService.approveProduct(id).subscribe({
      next: () => {
        this.isSubmitting = false;
        this.successMsg = 'Product approved.';
        this.closeReview();
        this.loadProducts();
        setTimeout(() => this.successMsg = '', 2500);
      },
      error: () => {
        this.isSubmitting = false;
        this.errorMsg = 'Approve failed.';
      }
    });
  }

  reject(id: number): void {
    if (!this.rejectReason.trim()) {
      this.decisionError = 'Reject reason is required.';
      return;
    }
    this.decisionError = '';
    this.errorMsg = '';
    this.isSubmitting = true;
    this.productService.rejectProduct(id, this.rejectReason.trim()).subscribe({
      next: () => {
        this.isSubmitting = false;
        this.successMsg = 'Product rejected.';
        this.closeReview();
        this.loadProducts();
        setTimeout(() => this.successMsg = '', 2500);
      },
      error: () => {
        this.isSubmitting = false;
        this.errorMsg = 'Reject failed.';
      }
    });
  }

  imageUrl(product: any): string {
    return product.image ? 'http://localhost:8080/uploads/' + product.image : 'assets/placeholder.png';
  }

  openReview(product: any): void {
    this.selectedProduct = product;
    this.rejectReason = '';
    this.decisionError = '';
    this.errorMsg = '';
  }

  closeReview(): void {
    this.selectedProduct = null;
    this.rejectReason = '';
    this.decisionError = '';
  }

  sellerName(product: any): string {
    return product?.seller?.shopName || product?.seller?.name || 'Seller not assigned';
  }

  stockLabel(product: any): string {
    const stock = product?.stock ?? 0;
    return stock > 0 ? `${stock} in stock` : 'Out of stock';
  }

  productStatus(product: any): string {
    return (product?.approvalStatus || 'PENDING').toString().trim().toUpperCase();
  }

  statusClass(product: any): string {
    return this.productStatus(product).toLowerCase();
  }

  productReadiness(product: any): number {
    const checks = [
      !!product?.name,
      !!product?.description,
      !!product?.category,
      !!product?.seller,
      !!product?.brand,
      !!product?.image,
      !!product?.artisanStory,
      !!product?.craftProcess,
      !!product?.sku,
      (product?.stock ?? 0) > 0
    ];
    const completed = checks.filter(Boolean).length;
    return Math.round((completed / checks.length) * 100);
  }

  reviewPriority(product: any): string {
    const readiness = this.productReadiness(product);
    if (readiness < 60) return 'Needs info';
    if (!product?.seller || !product?.category || !product?.image) return 'Check required';
    return 'Ready to review';
  }

  sellerLocation(product: any): string {
    return product?.seller?.address || product?.seller?.district || product?.originArea || 'Location not set';
  }

  formatDate(value: any): string {
    if (!value) return 'Not available';
    const date = new Date(value);
    return isNaN(date.getTime()) ? 'Not available' : date.toLocaleDateString();
  }

  isPending(product: any): boolean {
    return this.productStatus(product) === 'PENDING';
  }

  pageSubtitle(): string {
    if (this.activeTab === 'PENDING') return `${this.products.length} products waiting for admin review`;
    if (this.activeTab === 'APPROVED') return `${this.products.length} products already approved`;
    return `${this.products.length} rejected products`;
  }

  emptyMessage(): string {
    if (this.activeTab === 'PENDING') return 'No pending products. Newly added products will appear here before going live.';
    if (this.activeTab === 'APPROVED') return 'No approved products found.';
    return 'No rejected products found.';
  }

  private normalizeProducts(res: any): any[] {
    const data = res?.data ?? res?.value ?? res;
    return Array.isArray(data) ? data : [];
  }

  private refreshCounts(): void {
    this.productService.getProducts().subscribe({
      next: (res: any) => {
        this.allProducts = this.normalizeProducts(res);
        this.counts = {
          pending: this.allProducts.filter(p => this.productStatus(p) === 'PENDING').length,
          approved: this.allProducts.filter(p => this.productStatus(p) === 'APPROVED').length,
          rejected: this.allProducts.filter(p => this.productStatus(p) === 'REJECTED').length
        };
      },
      error: () => {}
    });
  }
}
