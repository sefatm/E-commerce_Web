import { Component } from '@angular/core';
import { CartService } from 'src/app/services/cart.service';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-cart-modal',
  templateUrl: './cart-modal.component.html',
  styleUrls: ['./cart-modal.component.css']
})
export class CartModalComponent {
  cartItems: any[] = [];
  isModalOpen: boolean = false;
  totalPrice: number = 0;
  readonly placeholder = 'https://placehold.co/80x80?text=No+Image';

  constructor(
    public cartService: CartService,
    private router: Router,
    private authService: AuthService
  ) {
    this.cartService.currentCartItems.subscribe(items => {
      this.cartItems = items;
      this.totalPrice = this.cartService.getTotalPrice();
    });

    this.cartService.isCartOpen$.subscribe(isOpen => {
      this.isModalOpen = isOpen;
      if(isOpen) document.body.style.overflow = 'hidden';
      else document.body.style.overflow = 'auto';
    });
  }

  closeCart() {
    this.cartService.closeCart();
  }

  goToCheckout() {
    this.closeCart();
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/checkout']);
    } else {
      this.router.navigate(['/login'], { queryParams: { returnUrl: '/checkout' } });
    }
  }

  imageUrl(image: string | null | undefined): string {
    if (!image) return this.placeholder;
    if (image.startsWith('http')) return image;
    return 'http://localhost:8080/uploads/' + image;
  }
}
