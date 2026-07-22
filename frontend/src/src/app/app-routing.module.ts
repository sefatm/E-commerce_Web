import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { MainLayoutComponent } from './shared/layout/main-layout/main-layout.component';
import { DashboardComponent } from './features/admin/dashboard/dashboard.component';
import { AddProductComponent } from './features/admin/products/add-product/add-product.component';
import { ProductListComponent } from './features/admin/products/product-list/product-list.component';
import { OrderListComponent } from './features/admin/orders/order-list/order-list.component';
import { OrderDetailsComponent } from './features/admin/orders/order-details/order-details.component';
import { OrderTrackingComponent } from './features/admin/orders/order-tracking/order-tracking.component';
import { OrderPaymentComponent } from './features/admin/orders/order-payment/order-payment.component';
import { OrderReturnComponent } from './features/admin/orders/order-return/order-return.component';
import { OrderInvoiceComponent } from './features/admin/orders/order-invoice/order-invoice.component';
import { OrderScanComponent } from './features/admin/orders/order-scan/order-scan.component';
import { OrderAnalyticsComponent } from './features/admin/orders/order-analytics/order-analytics.component';
import { AddCustomerComponent } from './features/admin/customers/add-customer/add-customer.component';
import { CustomerListComponent } from './features/admin/customers/customer-list/customer-list.component';
import { CategoryComponent } from './features/admin/products/category/category.component';
import { BrandComponent } from './features/admin/products/brand/brand.component';
import { AttributesComponent } from './features/admin/products/attributes/attributes.component';
import { VariantsComponent } from './features/admin/products/variants/variants.component';
import { InventoryComponent } from './features/admin/products/inventory/inventory.component';
import { ReviewComponent } from './features/admin/products/review/review.component';

import { HomeComponent } from './features/store/home/home.component';
import { LoginComponent } from './features/store/login/login.component';
import { ForgotPasswordComponent } from './features/store/forgot-password/forgot-password.component';
import { CheckoutComponent } from './features/store/checkout/checkout.component';
import { OfferComponent } from './features/admin/coupons/offer/offer.component';
import { CouponComponent } from './features/admin/coupons/coupon/coupon.component';
import { ShippingZonesComponent } from './features/admin/shipping/shipping-zones/shipping-zones.component';
import { DeliveryMethodsComponent } from './features/admin/shipping/delivery-methods/delivery-methods.component';
import { ShipmentTrackingComponent } from './features/admin/shipping/shipment-tracking/shipment-tracking.component';
import { SalesReportComponent } from './features/admin/reports/sales-report/sales-report.component';
import { RevenueReportComponent } from './features/admin/reports/revenue-report/revenue-report.component';
import { ProductReportComponent } from './features/admin/reports/product-report/product-report.component';
import { CustomerReportComponent } from './features/admin/reports/customer-report/customer-report.component';
import { RegisterComponent } from './features/store/register/register.component';
import { SettingsComponent } from './features/admin/settings/settings/settings.component';
import { CreateAdminComponent } from './features/admin/users/create-admin/create-admin.component';
import { MyOrdersComponent } from './features/store/my-orders/my-orders.component';
import { ProfileComponent } from './features/store/profile/profile.component';
import { WishlistComponent } from './features/store/wishlist/wishlist.component';
import { SellerApplyComponent } from './features/store/seller-apply/seller-apply.component';
import { SellerApprovalsComponent } from './features/admin/sellers/seller-approvals/seller-approvals.component';
import { SellerDashboardComponent } from './features/admin/sellers/seller-dashboard/seller-dashboard.component';
import { SellerOrdersComponent } from './features/admin/sellers/seller-orders/seller-orders.component';
import { SellerWithdrawalsComponent } from './features/admin/sellers/seller-withdrawals/seller-withdrawals.component';
import { ProductApprovalsComponent } from './features/admin/products/product-approvals/product-approvals.component';
import { CommissionListComponent } from './features/admin/commissions/commission-list/commission-list.component';
import { ProductDetailComponent } from './features/store/product-detail/product-detail.component';
import { PaymentResultComponent } from './features/store/payment-result/payment-result.component';
import { SellerCommissionsComponent } from './features/admin/sellers/seller-commissions/seller-commissions.component';
import { AdminGuard, AuthGuard } from './core/guards/auth.guard';


const routes: Routes = [

  
  { path: '', component: HomeComponent },
  { path: 'product/:id', component: ProductDetailComponent },
  { path: 'payment/success', component: PaymentResultComponent },
  { path: 'payment/fail', component: PaymentResultComponent },
  { path: 'payment/cancel', component: PaymentResultComponent },
  { path: 'login', component: LoginComponent },
  { path: 'forgot-password', component: ForgotPasswordComponent },
  { path: 'register', component: RegisterComponent},
  { path: 'checkout', component: CheckoutComponent, canActivate: [AuthGuard] },
  { path: 'my-orders', component: MyOrdersComponent, canActivate: [AuthGuard] },
  { path: 'profile', component: ProfileComponent, canActivate: [AuthGuard] },
  { path: 'wishlist', component: WishlistComponent, canActivate: [AuthGuard] },
  { path: 'seller/apply', component: SellerApplyComponent, canActivate: [AuthGuard] },
  {
    path: 'seller',
    component: MainLayoutComponent,
    canActivate: [AuthGuard],
    data: { roles: ['seller', 'vendor', 'admin', 'manager', 'staff'] },
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: SellerDashboardComponent },
      { path: 'orders', component: SellerOrdersComponent },
      { path: 'add-product', component: AddProductComponent },
      { path: 'edit/:id', component: AddProductComponent },
      { path: 'products', component: ProductListComponent },
      { path: 'inventory', component: InventoryComponent },
      { path: 'commissions', component: SellerCommissionsComponent },
      { path: 'withdrawals', component: SellerWithdrawalsComponent },
      { path: 'settings', component: ProfileComponent },
    ]
  },

  { path: 'admin/orders/invoice/:id', component: OrderInvoiceComponent, canActivate: [AdminGuard] },
  { path: 'admin/orders/scan/:id', component: OrderScanComponent, canActivate: [AuthGuard] },

  {
    path: 'admin', component: MainLayoutComponent, canActivate: [AdminGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: DashboardComponent },

      { path: 'add', redirectTo: 'list', pathMatch: 'full' },
      { path: 'edit/:id', component: AddProductComponent },
      { path: 'list', component: ProductListComponent },
      { path: 'products/approvals', component: ProductApprovalsComponent },

      { path: 'orders/list', component: OrderListComponent },
      { path: 'orders/details/:id', component: OrderDetailsComponent },
      { path: 'orders/tracking/:id', component: OrderTrackingComponent },
      { path: 'orders/payments', component: OrderPaymentComponent },
      { path: 'orders/returns', component: OrderReturnComponent },
      
      { path: 'orders/analytics', component: OrderAnalyticsComponent },

      { path: 'customers/add', component: AddCustomerComponent },
      { path: 'customers/list', component: CustomerListComponent },
      { path: 'categories', component: CategoryComponent},
      { path: 'brands', component: BrandComponent},

      { path: 'attributes', component: AttributesComponent },
      { path: 'variants', component: VariantsComponent },
      { path: 'inventory', redirectTo: 'list', pathMatch: 'full' },
      { path: 'reviews', component: ReviewComponent },

      { path: 'coupons', component: CouponComponent },
      { path: 'offers',  component: OfferComponent  },

      { path: 'shipping-zones', component: ShippingZonesComponent },
      { path: 'delivery-methods', component: DeliveryMethodsComponent },
      { path: 'tracking', component: ShipmentTrackingComponent },
      { path: 'sellers/approvals', component: SellerApprovalsComponent },
      { path: 'sellers/dashboard', redirectTo: 'sellers/approvals', pathMatch: 'full' },
      { path: 'sellers/withdrawals', component: SellerWithdrawalsComponent },
      { path: 'commissions', component: CommissionListComponent },

      { path: 'reports/sales',    component: SalesReportComponent    },
      { path: 'reports/revenue',  component: RevenueReportComponent  },
      { path: 'reports/products', component: ProductReportComponent  },
      { path: 'reports/customers',component: CustomerReportComponent },

      { path: 'settings/general',  component: SettingsComponent },
      { path: 'users/create-admin', component: CreateAdminComponent },
      

    ]
  },

  { path: '**', redirectTo: '' } 
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
