import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/services/auth.service';
import { CartService } from 'src/app/services/cart.service';
import { WishlistService } from 'src/app/services/wishlist.service';

@Component({
  selector: 'app-wishlist',
  templateUrl: './wishlist.component.html',
  styleUrls: ['./wishlist.component.css']
})
export class WishlistComponent implements OnInit {

  user: any = null;
  items: any[] = [];
  isLoading = false;
  message = '';

  readonly imageBase = 'http://localhost:8080/uploads/';
  readonly placeholder = 'https://placehold.co/360x260?text=No+Image';

  constructor(
    private authService: AuthService,
    private wishlistService: WishlistService,
    private cartService: CartService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.user = this.authService.getUser();
    if (!this.user?.id) {
      this.router.navigate(['/login']);
      return;
    }

    this.wishlistService.items$.subscribe(items => this.items = items || []);
    this.load();
  }

  load(): void {
    this.isLoading = true;
    this.wishlistService.load(this.user.id).subscribe({
      next: () => this.isLoading = false,
      error: () => {
        this.isLoading = false;
        this.message = 'Wishlist load failed.';
      }
    });
  }

  imageUrl(image: string | null | undefined): string {
    if (!image) return this.placeholder;
    if (image.startsWith('http')) return image;
    return this.imageBase + image;
  }

  productOf(item: any): any {
    return item?.product || null;
  }

  removeItem(item: any, showMessage = true): void {
    const product = this.productOf(item);
    const request = product?.id
      ? this.wishlistService.remove(this.user.id, product.id)
      : this.wishlistService.removeById(this.user.id, item.id);

    request.subscribe({
      next: () => {
        if (showMessage) {
          this.message = 'Removed from wishlist.';
        }
      },
      error: () => this.message = 'Wishlist update failed.'
    });
  }

  moveToCart(item: any): void {
    const product = this.productOf(item);
    if (!product?.id) {
      this.message = 'This saved product is no longer available.';
      return;
    }

    this.message = 'Moved to cart.';
    this.cartService.addToCart(product);
    this.removeItem(item, false);
  }
}
