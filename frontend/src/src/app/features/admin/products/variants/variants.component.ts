import { Component, OnInit } from '@angular/core';
import { VariantService } from 'src/app/core/services/variants.service';
import { ProductService } from 'src/app/core/services/product.service';

@Component({
  selector: 'app-variants',
  templateUrl: './variants.component.html',
  styleUrls: ['./variants.component.css']
})
export class VariantsComponent implements OnInit {

  products: any[] = [];
  variants: any[] = [];
  selectedProduct = '';
  loading = false;
  showAddForm = false;

  variantFormData = {
    name: '',
    sku: '',
    price: null as number | null,
    stock: null as number | null,
  };

  constructor(
    private variantService: VariantService,
    private productService: ProductService   // ✅ Fix: আগে `productService: any` ছিল
  ) {}

  ngOnInit(): void { this.loadProducts(); }

  loadProducts(): void {
    this.productService.getProducts().subscribe({
      next: (res: any) => { this.products = res?.data ?? res; },
      error: (err: any) => console.error('Product load error:', err)
    });
  }

  onProductSelect(): void {
    if (!this.selectedProduct) { this.variants = []; return; }
    this.loadVariants(this.selectedProduct);
    this.showAddForm = false;
  }

  loadVariants(productId: string): void {
    this.loading = true;
    this.variantService.getVariants(productId).subscribe({
      next: (res: any) => {
        this.variants = res?.data ?? res;
        this.loading = false;
      },
      error: (err: any) => { console.error('Variant load error:', err); this.loading = false; }
    });
  }

  addVariant(): void {
    if (!this.selectedProduct) return;
    const payload = { ...this.variantFormData, productId: this.selectedProduct };
    this.variantService.createVariant(payload).subscribe({
      next: () => {
        this.loadVariants(this.selectedProduct);
        this.showAddForm = false;
        this.resetVariantForm();
      },
      error: (err: any) => console.error('Create variant error:', err)
    });
  }

  deleteVariant(variantId: string): void {
    if (!confirm('এই variant টি delete করবেন?')) return;
    this.variantService.deleteVariant(variantId).subscribe({
      next: () => this.loadVariants(this.selectedProduct),
      error: (err: any) => console.error('Delete error:', err)
    });
  }

  getStockClass(stock: number): string {
    if (!stock || stock <= 0) return 'out';
    if (stock < 10) return 'low-stock';
    return 'in-stock';
  }

  private resetVariantForm(): void {
    this.variantFormData = { name: '', sku: '', price: null, stock: null };
  }
}
