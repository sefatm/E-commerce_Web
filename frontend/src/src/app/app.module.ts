import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { SidebarComponent } from './shared/layout/sidebar/sidebar.component';
import { NavbarComponent } from './shared/layout/navbar/navbar.component';
import { FooterComponent } from './shared/layout/footer/footer.component';
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
import { OrderAnalyticsComponent } from './features/admin/orders/order-analytics/order-analytics.component';
import { AddCustomerComponent } from './features/admin/customers/add-customer/add-customer.component';
import { CustomerListComponent } from './features/admin/customers/customer-list/customer-list.component';
import { CategoryComponent } from './features/admin/products/category/category.component';
import { BrandComponent } from './features/admin/products/brand/brand.component';
import { AttributesComponent } from './features/admin/products/attributes/attributes.component';
import { VariantsComponent } from './features/admin/products/variants/variants.component';
import { InventoryComponent } from './features/admin/products/inventory/inventory.component';
import { ReviewComponent } from './features/admin/products/review/review.component';
import { SiteHeaderComponent } from './shared/components/site-header/site-header.component';
import { CategoryNavComponent } from './shared/components/category-nav/category-nav.component';
import { ProductCardComponent } from './shared/components/product-card/product-card.component';
import { CartModalComponent } from './shared/components/cart-modal/cart-modal.component';
import { HomeComponent } from './features/store/home/home.component';
import { LoginComponent } from './features/store/login/login.component';
import { ForgotPasswordComponent } from './features/store/forgot-password/forgot-password.component';
import { CheckoutComponent } from './features/store/checkout/checkout.component';
import { CouponComponent } from './features/admin/coupons/coupon/coupon.component';
import { OfferComponent } from './features/admin/coupons/offer/offer.component';
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
import { SellerCommissionsComponent } from './features/admin/sellers/seller-commissions/seller-commissions.component';
import { JwtInterceptor } from './core/interceptors/jwt.interceptor';
import { PaymentResultComponent } from './features/store/payment-result/payment-result.component';
import { OrderScanComponent } from './features/admin/orders/order-scan/order-scan.component';


@NgModule({
  declarations: [
    AppComponent,
    
    SidebarComponent,
    NavbarComponent,
    FooterComponent,
    MainLayoutComponent,
    DashboardComponent,
    AddProductComponent,
    ProductListComponent,
    OrderListComponent,
    OrderDetailsComponent,
    OrderTrackingComponent,
    OrderPaymentComponent,
    OrderReturnComponent,
    OrderInvoiceComponent,
    OrderAnalyticsComponent,
    AddCustomerComponent,
    CustomerListComponent,
    CategoryComponent,
    BrandComponent,
    AttributesComponent,
    VariantsComponent,
    InventoryComponent,
    ReviewComponent,
    SiteHeaderComponent,
    CategoryNavComponent,
    ProductCardComponent,
    CartModalComponent,
    HomeComponent,
    LoginComponent,
    ForgotPasswordComponent,
    CheckoutComponent,
    CouponComponent,
    OfferComponent,
    ShippingZonesComponent,
    DeliveryMethodsComponent,
    ShipmentTrackingComponent,
    SalesReportComponent,
    RevenueReportComponent,
    ProductReportComponent,
    CustomerReportComponent,
    RegisterComponent,
    SettingsComponent,
    CreateAdminComponent,
    MyOrdersComponent,
    ProfileComponent,
    WishlistComponent,
    SellerApplyComponent,
    SellerApprovalsComponent,
    SellerDashboardComponent,
    SellerOrdersComponent,
    SellerWithdrawalsComponent,
    ProductApprovalsComponent,
    CommissionListComponent,
    ProductDetailComponent,
    SellerCommissionsComponent,
    PaymentResultComponent,
    OrderScanComponent,

  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    HttpClientModule,
    FormsModule
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
