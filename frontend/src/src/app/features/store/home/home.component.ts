import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ProductService } from 'src/app/core/services/add-product.service';
import { AuthService } from 'src/app/core/services/auth.service';
import { CartService } from 'src/app/core/services/cart.service';
import { WishlistService } from 'src/app/core/services/wishlist.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  allProducts: any[] = [];
  filteredProducts: any[] = [];

  selectedCategory: any = 'all';
  activeCategoryName = 'Featured Products';

  searchTerm = '';
  sortBy = 'default';

  showToast = false;
  toastMessage = 'Item added to cart!';
  isLoading = false;
  errorMsg = '';

  private demoProducts = [
    { id: 9001, name: 'Organic Tomato (1kg)', price: 80, salePrice: 64, isOnSale: true, stock: 120, image: 'assets/ruralmart/tomato.svg', categoryId: 'vegetables', category: { id: 'vegetables', name: 'Vegetables' } },
    { id: 9002, name: 'Premium Miniket Rice (5kg)', price: 420, salePrice: 340, isOnSale: true, stock: 75, image: 'assets/ruralmart/rice.svg', categoryId: 'rice', category: { id: 'rice', name: 'Rice & Grains' } },
    { id: 9003, name: 'Mustard Oil (1L)', price: 260, salePrice: 226, isOnSale: true, stock: 60, image: 'assets/ruralmart/oil.svg', categoryId: 'grocery', category: { id: 'grocery', name: 'Grocery' } },
    { id: 9004, name: 'Country Eggs (12 pcs)', price: 180, salePrice: 148, isOnSale: true, stock: 95, image: 'assets/ruralmart/eggs.svg', categoryId: 'dairy', category: { id: 'dairy', name: 'Dairy & Eggs' } },
    { id: 9005, name: 'Natural Honey (500g)', price: 650, salePrice: 540, isOnSale: true, stock: 38, image: 'assets/ruralmart/honey.svg', categoryId: 'organic', category: { id: 'organic', name: 'Organic Products' } },
    { id: 9006, name: 'Fresh Mango (1kg)', price: 180, stock: 45, image: 'assets/ruralmart/mango.svg', categoryId: 'fruits', category: { id: 'fruits', name: 'Fruits' } },
    { id: 9007, name: 'Green Tea (100g)', price: 220, stock: 50, image: 'assets/ruralmart/tea.svg', categoryId: 'beverages', category: { id: 'beverages', name: 'Beverages' } },
    { id: 9008, name: 'Handmade Bamboo Basket', price: 350, stock: 24, image: 'assets/ruralmart/basket.svg', categoryId: 'handmade', category: { id: 'handmade', name: 'Handmade' } }
  ];

  constructor(
    private productService: ProductService,
    private cartService: CartService,
    private wishlistService: WishlistService,
    private router: Router,
    public authService: AuthService
  ) {}

  get isAdmin(): boolean {
    return this.authService.isAdmin();
  }

  ngOnInit(): void {
    this.loadProducts();
    const user = this.authService.getUser();
    if (user?.id) {
      this.wishlistService.load(user.id).subscribe();
    }
  }

  loadProducts(): void {
    this.isLoading = true;
    this.errorMsg = '';
    this.productService.getPublicProducts().subscribe({
      next: (res: any) => {
        const products = this.normalizeProducts(res);
        this.allProducts = products.length ? products : [...this.demoProducts];
        this.applyFilters();
        this.isLoading = false;
      },
      error: () => {
        this.allProducts = [...this.demoProducts];
        this.errorMsg = '';
        this.applyFilters();
        this.isLoading = false;
      }
    });
  }

  onSearch(keyword: string): void {
    this.searchTerm = keyword.trim();
    if (this.searchTerm) {
      this.selectedCategory = 'all';
      this.activeCategoryName = 'Search Results';
    }
    this.applyFilters();
    this.scrollToProducts();
  }

  onCategorySelected(category: any): void {
    const categoryId = this.getSelectedCategoryId(category);
    this.selectedCategory = categoryId;
    this.searchTerm = '';

    if (categoryId === 'all') {
      this.activeCategoryName = 'Featured Products';
      this.errorMsg = '';
      this.applyFilters();
      return;
    }

    this.activeCategoryName = category.name ?? 'Category Products';
    this.errorMsg = '';
    this.applyFilters();
    this.scrollToProducts();
  }

  applyFilters(): void {
    let products = [...this.allProducts];

    if (this.selectedCategory !== 'all') {
      const selected = String(this.selectedCategory);
      products = products.filter((p: any) => this.getProductCategoryId(p) === selected);
    }

    if (this.searchTerm?.trim()) {
      const key = this.normalizeText(this.searchTerm);
      products = products.filter((p: any) => this.searchableText(p).includes(key));
    }

    if (this.sortBy === 'price-asc') {
      products.sort((a, b) => (a.price ?? 0) - (b.price ?? 0));
    } else if (this.sortBy === 'price-desc') {
      products.sort((a, b) => (b.price ?? 0) - (a.price ?? 0));
    } else if (this.sortBy === 'name') {
      products.sort((a, b) => (a.name || '').localeCompare(b.name || ''));
    }

    this.filteredProducts = products;
  }

  private normalizeProducts(res: any): any[] {
    const data = res?.data ?? res;
    return Array.isArray(data) ? data : [];
  }

  private getSelectedCategoryId(category: any): any {
    if (category === 'all') return 'all';
    return category?.id ?? category?._id ?? category?.categoryId ?? 'all';
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

  private searchableText(product: any): string {
    return [
      product?.name,
      product?.sku,
      product?.description,
      product?.category?.name,
      product?.brand?.name,
      product?.seller?.shopName,
      product?.seller?.name,
      product?.originArea,
      product?.artisanStory,
      product?.craftProcess
    ]
      .filter(value => value !== null && value !== undefined)
      .join(' ')
      .toLowerCase()
      .replace(/[-_]/g, ' ');
  }

  private normalizeText(value: any): string {
    return String(value || '').toLowerCase().replace(/[-_]/g, ' ').trim();
  }

  applySort(): void { this.applyFilters(); }

  onAddedToCart(): void {
    this.toastMessage = 'Item added to cart!';
    this.showToast = true;
    setTimeout(() => this.showToast = false, 2000);
  }

  onWishlistChanged(message: string): void {
    this.toastMessage = message;
    this.showToast = true;
    setTimeout(() => this.showToast = false, 2000);
  }

  scrollToProducts(): void {
    document.getElementById('products-section')?.scrollIntoView({ behavior: 'smooth' });
  }

  clearSearch(): void {
    this.searchTerm = '';
    this.selectedCategory = 'all';
    this.sortBy = 'default';
    this.activeCategoryName = 'Featured Products';
    this.applyFilters();
  }

  goToLogin(): void { this.router.navigate(['/login']); }
}
