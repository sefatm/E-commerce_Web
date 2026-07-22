import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CartService } from 'src/app/services/cart.service';
import { AuthService } from 'src/app/services/auth.service';
import { WishlistService } from 'src/app/services/wishlist.service';
import { Router } from '@angular/router';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-product-card',
  templateUrl: './product-card.component.html',
  styleUrls: ['./product-card.component.css']
})
export class ProductCardComponent {

  @Input() product: any;
  @Input() index!: number;
  @Output() addedToCart = new EventEmitter<void>();
  @Output() wishlistChanged = new EventEmitter<string>();

  readonly BASE_URL = `${environment.apiUrl}/uploads/`;
  readonly PLACEHOLDER = 'https://placehold.co/400x300?text=No+Image';

  constructor(
    private cartService: CartService,
    private authService: AuthService,
    public wishlistService: WishlistService,
    private router: Router
  ) {}


  getImageUrl(image: string | null | undefined): string {
    if (!image || image.trim() === '') return this.PLACEHOLDER;
    if (image.startsWith('http')) return image;
    if (image.startsWith('assets/') || image.startsWith('/') || image.startsWith('data:')) return image;
    return this.BASE_URL + image;
  }

  viewProduct(): void {
    if (this.product?.id) {
      this.router.navigate(['/product', this.product.id]);
    }
  }

  addToCart(): void {
    this.cartService.addToCart(this.product);
    this.addedToCart.emit();
  }

  isWishlisted(): boolean {
    return this.wishlistService.isWishlisted(this.product?.id);
  }

  toggleWishlist(event: Event): void {
    event.stopPropagation();
    const user = this.authService.getUser();
    if (!user?.id) {
      this.router.navigate(['/login']);
      return;
    }

    const wasSaved = this.isWishlisted();
    this.wishlistService.toggle(user.id, this.product.id).subscribe({
      next: () => this.wishlistChanged.emit(wasSaved ? 'Removed from wishlist' : 'Saved to wishlist'),
      error: () => this.wishlistChanged.emit('Wishlist update failed. Please try again.')
    });
  }
}
