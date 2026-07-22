export interface SalesReportItem {
  orderCode: string;
  customerName: string;
  orderDate: string;
  totalAmount: number;
  paymentMethod: string;
  status: string;
}

export interface SalesReport {
  items: SalesReportItem[];
  totalOrders: number;
  totalRevenue: number;
  averageOrderValue: number;
  dateFrom: string;
  dateTo: string;
}

export interface MonthlyRevenue {
  month: string;      
  monthNum: number;
  year: number;
  totalRevenue: number;
  orderCount: number;
}

export interface RevenueReport {
  monthly: MonthlyRevenue[];
  totalRevenue: number;
  revenueThisMonth: number;
  revenueLastMonth: number;
  growthPercent: number;
}

export interface ProductReportItem {
  productId: number;
  productName: string;
  category: string;
  totalSold: number;
  totalRevenue: number;
}

export interface ProductReport {
  items: ProductReportItem[];
  totalProductsSold: number;
  topProduct: string;
}

export interface CustomerReportItem {
  customerId: number;
  customerName: string;
  email: string;
  phone: string;
  totalOrders: number;
  totalSpent: number;
  type: string;
  joinDate: string;
}

export interface CustomerReport {
  items: CustomerReportItem[];
  totalCustomers: number;
  newThisMonth: number;
  vipCount: number;
  activeCount: number;
}

export interface DateFilter {
  from: string;
  to: string;
}
