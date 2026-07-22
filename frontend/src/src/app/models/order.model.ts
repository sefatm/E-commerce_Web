export interface OrderItem {
  id?: number;
  productId: number;
  productName: string;
  productImage?: string;
  unitPrice: number;
  quantity: number;
  totalPrice?: number;
}

export interface Order {
  id?: number;
  orderCode?: string;
  customerName: string;
  customerPhone: string;
  customerEmail?: string;
  shippingAddress: string;
  status?: string;
  paymentMethod: string;
  paymentStatus?: string;
  subtotal?: number;
  discountAmount?: number;
  totalAmount?: number;
  couponCode?: string;
  orderNote?: string;
  orderDate?: string;
  deliveredDate?: string;
  items: OrderItem[];
}

export interface OrderAnalytics {
  totalOrders: number;
  pendingOrders: number;
  acceptedOrders: number;
  processingOrders: number;
  shippedOrders: number;
  deliveredOrders: number;
  cancelledOrders: number;
  totalRevenue: number;
  revenueThisMonth: number;
  monthlyRevenue: [number, number][];
}

export interface OrderReturn {
  id?: number;
  order?: Order;
  productName: string;
  reason: string;
  status?: string;
  requestDate?: string;
  resolvedDate?: string;
  adminNote?: string;
  refundAmount?: number;
  refundStatus?: string;
  refundDate?: string;
  refundMethod?: string;
  refundAccountNumber?: string;
  refundAccountName?: string;
  refundBankName?: string;
  refundBranch?: string;
  transactionRef?: string;
}
