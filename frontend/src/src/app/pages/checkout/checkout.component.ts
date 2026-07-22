import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import { Order, OrderItem } from 'src/app/models/order.model';
import { CartService } from 'src/app/services/cart.service';
import { CouponService } from 'src/app/services/coupon.service';
import { OrderService } from 'src/app/services/order.service';
import { AuthService } from 'src/app/services/auth.service';
import { PaymentService } from 'src/app/services/payment.service';

@Component({
  selector: 'app-checkout',
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.css']
})
export class CheckoutComponent {

  cartItems: any[] = [];
  totalPrice = 0;

  couponCode = '';
  couponApplied = false;
  couponMessage = '';
  couponMessageType: 'success' | 'error' | '' = '';
  discountAmount = 0;
  finalTotal = 0;
  appliedCouponId: number | null = null;
  isValidating = false;

  shippingInfo = { name: '', address: '', phone: '', email: '', payment: 'cod', note: '' };

  isPlacingOrder = false;
  orderPlaced = false;
  placedOrderCode = '';
  firstPlacedOrderCode = '';
  orderMessage = '';
  orderMessageType: 'success' | 'error' | '' = '';

  isRedirectingToPayment = false;
  paymentError = '';

  constructor(
    private cartService: CartService,
    private orderService: OrderService,
    private couponService: CouponService,
    private authService: AuthService,
    private paymentService: PaymentService,
    private router: Router
  ) {
    this.cartService.currentCartItems.subscribe(items => {
      this.cartItems = items;
      this.totalPrice = this.cartService.getTotalPrice();
      this.finalTotal = this.totalPrice;
      if (this.couponApplied) this.removeCoupon();
    });
    this.prefillFromLoggedInUser();
  }

  private prefillFromLoggedInUser(): void {
    const user = this.authService.getUser();
    if (!user) return;
    this.shippingInfo = {
      ...this.shippingInfo,
      name: user.name || '',
      email: user.email || '',
      phone: user.phone || '',
      address: user.address || ''
    };
  }

  applyCoupon(): void {
    if (!this.couponCode.trim()) return;
    this.isValidating = true;
    this.couponService.validate(this.couponCode.trim().toUpperCase(), this.totalPrice).subscribe({
      next: (res) => {
        this.isValidating = false;
        if (res.valid) {
          this.couponApplied = true;
          this.discountAmount = res.discountAmount ?? 0;
          this.finalTotal = res.finalTotal ?? this.totalPrice;
          this.appliedCouponId = res.couponId ?? null;
          this.couponMessage = res.message;
          this.couponMessageType = 'success';
        } else {
          this.couponApplied = false;
          this.discountAmount = 0;
          this.finalTotal = this.totalPrice;
          this.couponMessage = res.message;
          this.couponMessageType = 'error';
        }
      },
      error: () => {
        this.isValidating = false;
        this.couponMessage = 'Could not validate coupon.';
        this.couponMessageType = 'error';
      }
    });
  }

  removeCoupon(): void {
    this.couponApplied = false;
    this.couponCode = '';
    this.discountAmount = 0;
    this.finalTotal = this.totalPrice;
    this.appliedCouponId = null;
    this.couponMessage = '';
    this.couponMessageType = '';
  }

  placeOrder(shipForm?: any): void {
    this.orderMessage = '';
    this.orderMessageType = '';

    if (this.cartItems.length === 0) {
      this.orderMessage = 'Your cart is empty. Please add products before placing an order.';
      this.orderMessageType = 'error';
      return;
    }

    if (!this.shippingInfo.name || !this.shippingInfo.address || !this.shippingInfo.phone) {
      this.orderMessage = 'Please fill in all required shipping details.';
      this.orderMessageType = 'error';
      return;
    }

    const orders = this.buildSellerWiseOrders();
    if (orders.length === 0) {
      this.orderMessage = 'No valid products found in cart.';
      this.orderMessageType = 'error';
      return;
    }

    this.isPlacingOrder = true;

    forkJoin(orders.map(order => this.orderService.placeOrder(order))).subscribe({
      next: (savedOrders) => {
        if (this.couponApplied && this.appliedCouponId) {
          this.couponService.markUsed(this.appliedCouponId).subscribe();
        }

        this.cartService.clearCart();

        const codes = savedOrders.map(order => order.orderCode).filter((code): code is string => !!code);
        this.placedOrderCode = codes.join(', ');
        this.firstPlacedOrderCode = codes[0] || '';

        const isOnlinePayment = this.shippingInfo.payment === 'bkash' || this.shippingInfo.payment === 'nagad';

        if (isOnlinePayment && this.firstPlacedOrderCode) {
          // ── Online payment: SSLCommerz gateway-এ redirect করো ──
          this.isPlacingOrder = false;
          this.isRedirectingToPayment = true;
          this.paymentService.initiate(this.firstPlacedOrderCode).subscribe({
            next: (res) => {
              if (res.success && res.gatewayUrl) {
                window.location.href = res.gatewayUrl; // SSLCommerz gateway page-এ redirect
              } else {
                this.isRedirectingToPayment = false;
                this.paymentError = res.message || 'Could not start online payment. You can pay on delivery instead.';
                this.finalizeOrderSuccess(shipForm, codes);
              }
            },
            error: () => {
              this.isRedirectingToPayment = false;
              this.paymentError = 'Could not connect to payment gateway. Your order is saved — you can pay on delivery.';
              this.finalizeOrderSuccess(shipForm, codes);
            }
          });
          return;
        }

        // ── COD: directly show success ──
        this.isPlacingOrder = false;
        this.finalizeOrderSuccess(shipForm, codes);
      },
      error: () => {
        this.isPlacingOrder = false;
        this.orderMessage = 'Failed to place order. Please try again.';
        this.orderMessageType = 'error';
      }
    });
  }

  private finalizeOrderSuccess(shipForm: any, codes: string[]): void {
    this.resetCheckoutData();
    if (shipForm) {
      shipForm.resetForm(this.shippingInfo);
    }
    this.orderPlaced = true;
    this.orderMessage = this.placedOrderCode
      ? 'Order placed successfully. Your order code' + (codes.length > 1 ? 's are ' : ' is ') + this.placedOrderCode + '.'
      : 'Order placed successfully.';
    this.orderMessageType = 'success';
    setTimeout(() => window.scrollTo({ top: 0, behavior: 'smooth' }), 0);
  }

  private buildSellerWiseOrders(): Order[] {
    const groups: { [key: string]: any[] } = {};
    this.cartItems.forEach(item => {
      const key = item.seller?.id ? String(item.seller.id) : 'product-' + item.id;
      if (!groups[key]) groups[key] = [];
      groups[key].push(item);
    });

    const keys = Object.keys(groups);
    const totalBeforeDiscount = this.cartItems.reduce((sum, item) => {
      return sum + (this.effectivePrice(item) * item.quantity);
    }, 0);

    let allocatedDiscount = 0;
    return keys.map((key, index) => {
      const items = this.toOrderItems(groups[key]);
      const groupSubtotal = items.reduce((sum, item) => sum + (item.totalPrice || 0), 0);
      let discountShare = 0;

      if (this.discountAmount > 0 && totalBeforeDiscount > 0) {
        discountShare = index === keys.length - 1
          ? this.discountAmount - allocatedDiscount
          : Number(((groupSubtotal / totalBeforeDiscount) * this.discountAmount).toFixed(2));
        allocatedDiscount += discountShare;
      }

      return {
        customerName: this.shippingInfo.name,
        customerPhone: this.shippingInfo.phone,
        customerEmail: this.shippingInfo.email,
        shippingAddress: this.shippingInfo.address,
        paymentMethod: this.shippingInfo.payment,
        orderNote: this.shippingInfo.note,
        discountAmount: discountShare,
        couponCode: this.couponApplied ? this.couponCode : undefined,
        items
      };
    });
  }

  private toOrderItems(cartItems: any[]): OrderItem[] {
    return cartItems.map(item => {
      const effectivePrice = this.effectivePrice(item);
      return {
        productId: item.id,
        productName: item.name,
        productImage: item.image,
        unitPrice: effectivePrice,
        quantity: item.quantity,
        totalPrice: effectivePrice * item.quantity
      };
    });
  }

  effectivePrice(item: any): number {
    return (item.isOnSale && item.salePrice && item.salePrice < item.price)
      ? item.salePrice
      : item.price;
  }

  itemLineTotal(item: any): number {
    return this.effectivePrice(item) * (Number(item.quantity) || 0);
  }

  private resetCheckoutData(): void {
    this.shippingInfo = { name: '', address: '', phone: '', email: '', payment: 'cod', note: '' };
    this.couponCode = '';
    this.couponApplied = false;
    this.couponMessage = '';
    this.couponMessageType = '';
    this.discountAmount = 0;
    this.appliedCouponId = null;
    this.totalPrice = this.cartService.getTotalPrice();
    this.finalTotal = this.totalPrice;
    this.firstPlacedOrderCode = '';
  }

  goHome(): void { this.router.navigate(['/']); }
}
