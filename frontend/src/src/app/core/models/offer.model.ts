export interface Offer {
  id?: number;
  title: string;
  description?: string;
  offerType: 'BANNER' | 'FLASH_SALE' | 'SEASONAL';
  discountPercentage: number;
  productId?: number;
  categoryId?: number;
  startDate?: string;
  endDate?: string;
  status?: string;
  bannerImage?: string;
  createdAt?: string;
}
