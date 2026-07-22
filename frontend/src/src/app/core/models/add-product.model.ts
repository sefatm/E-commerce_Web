export interface AddProduct {
  id?: number;
  name: string;
  price: number;
  salePrice?: number;
  stock?: number;
  sku?: string;
  categoryId?: number;
  brandId?: number;
  sellerId?: number;
  description: string;
  status?: string;
  approvalStatus?: string;
  rejectionReason?: string;
  originArea?: string;
  artisanStory?: string;
  craftProcess?: string;
  preOrderAvailable?: boolean;
  estimatedProductionDays?: number;
  isFeatured?: boolean;
  isOnSale?: boolean;
  image?: string;
  createdAt?: string;
}
