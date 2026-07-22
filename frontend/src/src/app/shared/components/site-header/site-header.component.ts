import { Component, EventEmitter, HostListener, Output } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/core/services/auth.service';
import { CartService } from 'src/app/core/services/cart.service';
import { CategoryService } from 'src/app/core/services/category.service';
import { WishlistService } from 'src/app/core/services/wishlist.service';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-site-header',
  templateUrl: './site-header.component.html',
  styleUrls: ['./site-header.component.css']
})
export class SiteHeaderComponent {

  cartCount = 0;
  wishlistCount = 0;
  showUserMenu = false;
  showCategoryMenu = false;
  categories: any[] = [];
  activeCategoryId: any = 'all';
  isLoggedIn = false;
  isAdmin = false;
  isSeller = false;
  userName = 'Account';
  userPhoto = '';

  @Output() searchEvent = new EventEmitter<string>();
  @Output() categorySelected = new EventEmitter<any>();

  private readonly fallbackCategories = [
    { id: 'vegetables', name: 'Vegetables', icon: '🥬' },
    { id: 'fruits', name: 'Fruits', icon: '🍎' },
    { id: 'rice', name: 'Rice & Grains', icon: '🍚' },
    { id: 'dairy', name: 'Dairy & Eggs', icon: '🥛' },
    { id: 'grocery', name: 'Grocery', icon: '🛒' },
    { id: 'organic', name: 'Organic Products', icon: '🌿' },
    { id: 'beverages', name: 'Beverages', icon: '🍵' },
    { id: 'handmade', name: 'Handmade', icon: '🧺' }
  ];

  constructor(
    public cartService: CartService,
    public authService: AuthService,
    public router: Router,
    private wishlistService: WishlistService,
    private categoryService: CategoryService
  ) {
    this.cartService.currentCartItems.subscribe(() => {
      this.cartCount = this.cartService.getCartCount();
    });

    this.authService.currentUser$.subscribe(user => {
      this.isLoggedIn = !!user;
      this.isAdmin = this.authService.isAdmin();
      this.isSeller = this.authService.isSeller();
      this.userName = user?.name ?? 'Account';
      this.userPhoto = this.photoUrl(user?.profileImage);

      if (user?.id) {
        this.wishlistService.load(user.id).subscribe();
      } else {
        this.wishlistService.clear();
      }
    });

    this.wishlistService.items$.subscribe(items => {
      this.wishlistCount = items?.length || 0;
    });

    this.loadCategories();
  }

  @HostListener('document:click')
  closeOpenMenus(): void {
    this.showCategoryMenu = false;
    this.showUserMenu = false;
  }

  loadCategories(): void {
    this.categoryService.getCategories().subscribe({
      next: (res: any) => {
        const data = res?.data ?? res?.content ?? res;
        this.categories = Array.isArray(data) && data.length
          ? data.filter((category: any) => category?.status !== 'INACTIVE')
          : this.fallbackCategories;
      },
      error: () => {
        this.categories = this.fallbackCategories;
      }
    });
  }

  toggleCategoryMenu(event: Event): void {
    event.stopPropagation();
    this.showCategoryMenu = !this.showCategoryMenu;
    this.showUserMenu = false;
  }

  chooseCategory(category: any, event?: Event): void {
    event?.stopPropagation();
    this.activeCategoryId = category === 'all'
      ? 'all'
      : (category?.id ?? category?._id ?? category?.categoryId);
    this.categorySelected.emit(category);
    this.showCategoryMenu = false;
  }

  categoryIcon(category: any): string {
    if (category?.icon) return category.icon;

    const name = String(category?.name || '').toLowerCase();
    if (name.includes('vegetable')) return '🥬';
    if (name.includes('fruit')) return '🍎';
    if (name.includes('rice') || name.includes('grain')) return '🍚';
    if (name.includes('dairy') || name.includes('egg')) return '🥛';
    if (name.includes('organic')) return '🌿';
    if (name.includes('tea') || name.includes('beverage')) return '🍵';
    if (name.includes('hand') || name.includes('craft')) return '🧺';
    return '🌱';
  }

  onSearchInput(event: Event): void {
    const value = (event.target as HTMLInputElement).value;
    this.searchEvent.emit(value);
  }

  onSearchClick(value: string): void {
    this.searchEvent.emit(value.trim());
  }

  openCart(): void {
    this.cartService.openCart();
  }

  goToLogin(): void {
    this.router.navigateByUrl('/login');
  }

  toggleUserMenu(event?: Event): void {
    event?.stopPropagation();
    this.showUserMenu = !this.showUserMenu;
    this.showCategoryMenu = false;
  }

  logout(): void {
    this.authService.logout();
    this.showUserMenu = false;
    this.router.navigateByUrl('/');
  }

  photoUrl(image: string | null | undefined): string {
    if (!image) return '';
    if (image.startsWith('http')) return image;
    return `${environment.apiUrl}/uploads/${image}`;
  }

  userInitial(): string {
    return (this.userName || 'A').substring(0, 1).toUpperCase();
  }
}
