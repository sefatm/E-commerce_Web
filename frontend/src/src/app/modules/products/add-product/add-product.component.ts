import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { ProductService } from 'src/app/services/add-product.service';
import { CategoryService } from 'src/app/services/category.service';
import { BrandService } from 'src/app/services/brand.service';
import { SellerService } from 'src/app/services/seller.service';
import { AuthService } from 'src/app/services/auth.service';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-add-product',
  templateUrl: './add-product.component.html',
  styleUrls: ['./add-product.component.css']
})
export class AddProductComponent implements OnInit {

  product = {
    name: '',
    price: null as number | null,
    salePrice: null as number | null,
    stock: null as number | null,
    sku: '',
    categoryId: '' as any,
    brandId: '' as any,
    sellerId: '' as any,
    originArea: '',
    artisanStory: '',
    craftProcess: '',
    preOrderAvailable: false,
    estimatedProductionDays: null as number | null,
    description: '',
    status: 'active',
    isFeatured: false,
    isOnSale: false,
  };

  previewUrl: string | ArrayBuffer | null = null;
  selectedFile: File | null = null;
  isSubmitting = false;
  successMsg = '';
  errorMsg = '';
  editMode = false;
  editId: number | null = null;

  categories: any[] = [];
  brands: any[] = [];
  sellers: any[] = [];
  isSellerView = false;
  private readonly uploadBase = `${environment.apiUrl}/uploads/`;

  constructor(
    private productService: ProductService,
    private categoryService: CategoryService,
    private brandService: BrandService,
    private sellerService: SellerService,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) { }

  ngOnInit(): void {
    this.isSellerView = this.router.url.startsWith('/seller');
    this.loadCategories();
    this.loadBrands();
    if (this.isSellerView) {
      this.loadCurrentSeller();
    } else {
      this.loadSellers();
    }

    const sellerId = this.route.snapshot.queryParamMap.get('sellerId');
    if (sellerId && !this.editMode) {
      this.product.sellerId = sellerId;
    }

    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.editMode = true;
      this.editId = +id;
      this.productService.getProductById(+id).subscribe({
        next: (p: any) => {
          this.product = {
            name: p.name ?? '',
            price: p.price ?? null,
            salePrice: p.salePrice ?? null,
            stock: p.stock ?? null,
            sku: p.sku ?? '',
            categoryId: p.category?.id ?? '',
            brandId: p.brand?.id ?? '',
            sellerId: p.seller?.id ?? '',
            originArea: p.originArea ?? '',
            artisanStory: p.artisanStory ?? '',
            craftProcess: p.craftProcess ?? '',
            preOrderAvailable: p.preOrderAvailable ?? false,
            estimatedProductionDays: p.estimatedProductionDays ?? null,
            description: p.description ?? '',
            status: p.status ?? 'active',
            isFeatured: p.isFeatured ?? false,
            isOnSale: p.isOnSale ?? false,
          };
          if (p.image) this.previewUrl = this.uploadBase + p.image;
        },
        error: () => { this.errorMsg = 'Product load failed.'; }
      });
    }
  }

  loadCategories(): void {
    this.categoryService.getCategories().subscribe({
      next: (res: any) => { this.categories = res?.data ?? res; },
      error: () => {
        this.errorMsg = 'Categories load failed.';
        setTimeout(() => this.errorMsg = '', 4000);
      }
    });
  }

  loadBrands(): void {
    this.brandService.getAll().subscribe({
      next: (res: any) => { this.brands = res?.data ?? res; },
      error: () => {
        this.errorMsg = 'Brands load failed.';
        setTimeout(() => this.errorMsg = '', 4000);
      }
    });
  }

  loadSellers(): void {
    this.sellerService.getApproved().subscribe({
      next: (res: any) => {
        this.sellers = this.normalizeSellerListResponse(res);
      },
      error: () => {
        this.errorMsg = 'Approved sellers load failed.';
        setTimeout(() => this.errorMsg = '', 4000);
      }
    });
  }

  loadCurrentSeller(): void {
    const user = this.authService.getUser();
    const userId = user?.id ?? user?._id;
    if (!userId) {
      this.router.navigate(['/login']);
      return;
    }

    this.sellerService.getByUser(userId).subscribe({
      next: (res: any) => {
        const seller = this.normalizeSellerResponse(res);
        this.sellers = seller ? [seller] : [];
        const sellerId = seller?.id ?? seller?._id;
        if (!this.editMode && sellerId) {
          this.product.sellerId = sellerId;
        }
      },
      error: () => {
        this.errorMsg = 'No seller profile found. Please apply as a seller first.';
        setTimeout(() => this.errorMsg = '', 5000);
      }
    });
  }

  onFileSelect(event: any): void {
    const file = event.target.files[0];
    if (!file) return;
    this.selectedFile = file;
    const reader = new FileReader();
    reader.onload = () => { this.previewUrl = reader.result; };
    reader.readAsDataURL(file);
  }

  removeImage(): void {
    this.previewUrl = null;
    this.selectedFile = null;
  }

  onSubmit(): void {
    this.successMsg = '';
    this.errorMsg = '';
    if (!this.product.name || !this.product.price || !this.product.categoryId || !this.product.sellerId) {
      this.errorMsg = 'Name, Price, Category and Seller are required.';
      this.scrollToTop();
      setTimeout(() => this.errorMsg = '', 4000);
      return;
    }

    this.isSubmitting = true;
    const formData = new FormData();

    formData.append('name', this.product.name);
    formData.append('price', String(this.product.price));
    formData.append('categoryId', String(this.product.categoryId));
    formData.append('description', this.product.description);
    formData.append('status', this.product.status);
    formData.append('originArea', this.product.originArea);
    formData.append('artisanStory', this.product.artisanStory);
    formData.append('craftProcess', this.product.craftProcess);
    formData.append('preOrderAvailable', String(this.product.preOrderAvailable));
    formData.append('isFeatured', String(this.product.isFeatured));
    formData.append('isOnSale', String(this.product.isOnSale));

    if (this.product.salePrice) formData.append('salePrice', String(this.product.salePrice));
    if (this.product.stock) formData.append('stock', String(this.product.stock));
    if (this.product.sku) formData.append('sku', this.product.sku);
    if (this.product.brandId) formData.append('brandId', String(this.product.brandId));
    if (this.product.sellerId) formData.append('sellerId', String(this.product.sellerId));
    if (this.product.estimatedProductionDays) {
      formData.append('estimatedProductionDays', String(this.product.estimatedProductionDays));
    }
    if (this.selectedFile) formData.append('image', this.selectedFile);

    const request$ = this.editMode && this.editId
      ? this.productService.updateProduct(this.editId, formData)
      : this.productService.addProduct(formData);

    request$.subscribe({
      next: () => {
        this.isSubmitting = false;
        this.successMsg = this.editMode
          ? 'Product updated successfully!'
          : 'Product added successfully. Admin approval is required before it appears on the homepage.';
        this.scrollToTop();
        if (!this.editMode) this.resetForm();
        const targetRoute = this.isSellerView
          ? '/seller/products'
          : (this.editMode ? '/admin/list' : '/admin/products/approvals');
        setTimeout(() => this.router.navigate([targetRoute]), 1500);
      },
      error: (err) => {
        const fallback = this.editMode
          ? 'Update failed. Please check category, seller, brand and product details, then try again.'
          : 'There was a problem adding the product. Please check category, seller and image, then try again.';
        this.errorMsg = this.cleanApiError(err, fallback);
        this.isSubmitting = false;
        this.scrollToTop();
        setTimeout(() => this.errorMsg = '', 4000);
      }
    });
  }

  resetForm(): void {
    this.product = {
      name: '', price: null, salePrice: null, stock: null,
      sku: '', categoryId: '', brandId: '', sellerId: '', originArea: '',
      artisanStory: '', craftProcess: '', preOrderAvailable: false,
      estimatedProductionDays: null, description: '',
      status: 'active', isFeatured: false, isOnSale: false
    };
    this.previewUrl = null;
    this.selectedFile = null;
    this.isSubmitting = false;
  }

  private normalizeSellerResponse(res: any): any {
    const seller = res?.data ?? res?.seller ?? res?.data?.seller ?? res;
    if (!seller) {
      return null;
    }

    return {
      ...seller,
      id: seller.id ?? seller._id
    };
  }

  private normalizeSellerListResponse(res: any): any[] {
    const payload = res?.data ?? res;
    const list = Array.isArray(payload)
      ? payload
      : (payload?.items ?? payload?.sellers ?? []);

    return list.map((item: any) => ({
      ...item,
      id: item.id ?? item._id
    }));
  }

  private scrollToTop(): void {
    setTimeout(() => window.scrollTo({ top: 0, behavior: 'smooth' }), 0);
  }

  private cleanApiError(err: any, fallback: string): string {
    const raw = typeof err?.error === 'string' ? err.error : err?.error?.message;
    if (!raw) return fallback;

    const noisyBackendError =
      raw.length > 220 ||
      raw.includes('org.springframework') ||
      raw.includes('"trace"') ||
      raw.includes('Internal Server Error') ||
      raw.includes('<!DOCTYPE');

    return noisyBackendError ? fallback : raw;
  }
}
