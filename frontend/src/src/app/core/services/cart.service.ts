import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class CartService {


  private readonly CART_KEY = 'rural_mart_cart';

  public cartItems = new BehaviorSubject<any[]>(this.loadFromStorage());
  currentCartItems = this.cartItems.asObservable();

  public isCartOpen = new BehaviorSubject<boolean>(false);
  isCartOpen$ = this.isCartOpen.asObservable();

  private loadFromStorage(): any[] {
    try {
      const stored = localStorage.getItem(this.CART_KEY);
      const items = stored ? JSON.parse(stored) : [];
      return Array.isArray(items) ? items.filter(item => item && item.id && item.name) : [];
    } catch {
      return [];
    }
  }

  private saveToStorage(items: any[]): void {
    try {
      localStorage.setItem(this.CART_KEY, JSON.stringify(items));
    } catch {
    }
  }

  toggleCartModal(): void {
    this.isCartOpen.next(!this.isCartOpen.value);
  }

  openCart(): void {
    this.isCartOpen.next(true);
  }

  closeCart(): void {
    this.isCartOpen.next(false);
  }

  addToCart(product: any): void {
    if (!product || typeof product !== 'object') return;
    const productId = product.id ?? product.productId ?? product._id;
    if (!productId) return;
    product = { ...product, id: productId };
    const cart = this.cartItems.value;
    const existing = cart.find(item => item.id === product.id);
    if (existing) {
      existing.quantity += 1;
    } else {
      cart.push({ ...product, quantity: 1 });
    }
    const updated = [...cart];
    this.cartItems.next(updated);
    this.saveToStorage(updated); 
  }

  decreaseQuantity(productId: number): void {
    let cart = this.cartItems.value;
    const item = cart.find(i => i.id === productId);
    if (!item) return;
    if (item.quantity > 1) {
      item.quantity -= 1;
      const updated = [...cart];
      this.cartItems.next(updated);
      this.saveToStorage(updated); 
    } else {
      this.removeFromCart(productId);
    }
  }

  removeFromCart(productId: number): void {
    const updated = this.cartItems.value.filter(item => item.id !== productId);
    this.cartItems.next(updated);
    this.saveToStorage(updated); 
  }

  clearCart(): void {
    this.cartItems.next([]);
    localStorage.removeItem(this.CART_KEY); 
  }

  getEffectivePrice(item: any): number {
    if (item.isOnSale && item.salePrice && item.salePrice < item.price) {
      return item.salePrice;
    }
    return item.price;
  }

  getTotalPrice(): number {
    return this.cartItems.value.reduce(
      (total, item) => total + (this.getEffectivePrice(item) * item.quantity), 0
    );
  }

  getCartCount(): number {
    return this.cartItems.value.reduce((count, item) => count + item.quantity, 0);
  }
}
