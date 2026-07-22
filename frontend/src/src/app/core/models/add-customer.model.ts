
export interface Customer {
  id?: number;
  name: string;
  email: string;
  phone: string;
  password?: string;     
  address?: string;
  district?: string;      
  upazila?: string;       
  postalCode?: string;     
  type: 'regular' | 'vip';
  status?: 'active' | 'inactive';
  createdAt?: string;     
}
