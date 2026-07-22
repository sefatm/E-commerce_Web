import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductService } from 'src/app/services/add-product.service';
import { CartService } from 'src/app/services/cart.service';
import { WishlistService } from 'src/app/services/wishlist.service';
import { AuthService } from 'src/app/services/auth.service';
import { ReviewService } from 'src/app/services/review.service';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-product-detail',
  templateUrl: './product-detail.component.html',
  styleUrls: ['./product-detail.component.css']
})
export class ProductDetailComponent implements OnInit {

  product: any = null;
  isLoading = true;
  errorMsg = '';
  showToast = false;
  toastMessage = '';
  quantity = 1;
  activeTab: 'description' | 'artisan' | 'craft' | 'reviews' = 'description';

  // Reviews
  reviews: any[] = [];
  reviewStats: any = null;
  reviewsLoading = false;
  newRating = 0;
  hoverRating = 0;
  newComment = '';
  reviewSubmitting = false;
  reviewError = '';
  reviewSuccess = '';

  readonly BASE_URL = `${environment.apiUrl}/uploads/`;
  readonly PLACEHOLDER = 'https://placehold.co/600x450?text=No+Image';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private productService: ProductService,
    private cartService: CartService,
    public wishlistService: WishlistService,
    private authService: AuthService,
    private reviewService: ReviewService
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = Number(params.get('id'));
      if (!id) { this.router.navigate(['/']); return; }
      this.loadProduct(id);
    });
  }

  loadProduct(id: number): void {
    this.isLoading = true;
    this.errorMsg = '';
    this.productService.getProductById(id).subscribe({
      next: (res: any) => {
        this.product = res?.data ?? res;
        this.isLoading = false;
        this.loadReviews();
      },
      error: () => {
        this.errorMsg = 'Product not found.';
        this.isLoading = false;
      }
    });
  }

  loadReviews(): void {
    if (!this.product?.id) return;
    this.reviewsLoading = true;
    this.reviewService.getByProduct(this.product.id).subscribe({
      next: (data: any) => {
        this.reviews = Array.isArray(data) ? data : (data?.data ?? []);
        this.reviewsLoading = false;
      },
      error: () => this.reviewsLoading = false
    });
    this.reviewService.getProductStats(this.product.id).subscribe({
      next: (s: any) => this.reviewStats = s
    });
  }

  getStars(rating: number): boolean[] {
    return [1,2,3,4,5].map(i => i <= rating);
  }

  setRating(r: number): void { this.newRating = r; }
  setHover(r: number): void  { this.hoverRating = r; }

  submitReview(): void {
    const user = this.authService.getUser();
    if (!user?.id) { this.router.navigate(['/login']); return; }
    if (this.newRating === 0) { this.reviewError = 'Please select a star rating.'; return; }
    this.reviewSubmitting = true;
    this.reviewError = '';
    this.reviewSuccess = '';
    this.reviewService.submitReview(user.id, this.product.id, this.newRating, this.newComment).subscribe({
      next: () => {
        this.reviewSuccess = 'Your review has been submitted!';
        this.newRating = 0;
        this.newComment = '';
        this.reviewSubmitting = false;
        this.loadReviews();
        setTimeout(() => this.reviewSuccess = '', 3000);
      },
      error: (err: any) => {
        this.reviewError = err?.error?.error ?? 'Submission failed. Please try again.';
        this.reviewSubmitting = false;
      }
    });
  }

  getImageUrl(image: string | null | undefined): string {
    if (!image || image.trim() === '') return this.PLACEHOLDER;
    if (image.startsWith('http')) return image;
    return this.BASE_URL + image;
  }

  get effectivePrice(): number {
    if (this.product?.isOnSale && this.product?.salePrice && this.product.salePrice < this.product.price)
      return this.product.salePrice;
    return this.product?.price ?? 0;
  }

  get discountPercent(): number {
    if (!this.product?.isOnSale || !this.product?.salePrice) return 0;
    return Math.round(100 - (this.product.salePrice / this.product.price) * 100);
  }

  get inStock(): boolean { return (this.product?.stock ?? 0) > 0; }

  increaseQty(): void { if (this.quantity < (this.product?.stock ?? 1)) this.quantity++; }
  decreaseQty(): void { if (this.quantity > 1) this.quantity--; }

  addToCart(): void {
    if (!this.inStock) return;
    for (let i = 0; i < this.quantity; i++) this.cartService.addToCart(this.product);
    this.showNotification(`${this.quantity} item(s) added to cart!`);
  }

  isWishlisted(): boolean { return this.wishlistService.isWishlisted(this.product?.id); }

  toggleWishlist(): void {
    const user = this.authService.getUser();
    if (!user?.id) { this.router.navigate(['/login']); return; }
    const wasSaved = this.isWishlisted();
    this.wishlistService.toggle(user.id, this.product.id).subscribe({
      next: () => this.showNotification(wasSaved ? 'Removed from wishlist' : 'Saved to wishlist!')
    });
  }

  showNotification(msg: string): void {
    this.toastMessage = msg;
    this.showToast = true;
    setTimeout(() => this.showToast = false, 2500);
  }

  goBack(): void { this.router.navigate(['/']); }
}
