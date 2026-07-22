export interface Coupon {
  id?: number;
  code: string;
  discountType: 'PERCENTAGE' | 'FLAT';
  discountValue: number;
  minOrderAmount?: number;
  maxDiscountAmount?: number;
  usageLimit?: number;
  usedCount?: number;
  startDate?: string;
  endDate?: string;
  status?: string;
  description?: string;
  createdAt?: string;
}

export interface CouponValidateResponse {
  valid: boolean;
  couponId?: number;
  discountAmount?: number;
  finalTotal?: number;
  message: string;
}
