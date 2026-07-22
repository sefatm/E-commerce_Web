export interface Seller {
  id?: number;
  name: string;
  shopName?: string;
  user?: any;
  nidNo?: string;
  phone?: string;
  email?: string;
  address?: string;
  district?: string;
  artisanStory?: string;
  craftProcess?: string;
  productCategory?: string;
  businessType?: string;
  paymentMethod?: string;
  paymentNumber?: string;
  profilePhoto?: string;
  nidFrontImage?: string;
  nidBackImage?: string;
  verified?: boolean;
  rating?: number;
  reviewCount?: number;
  commissionRate?: number;
  status?: 'PENDING' | 'APPROVED' | 'REJECTED' | string;
  rejectionReason?: string;
  createdAt?: string;
}
