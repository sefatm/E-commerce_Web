export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  name: string;
  email: string;
  phone: string;
  password: string;
  address?: string;
  role?: string;
  profileImage?: string;
}

export interface AuthResponse {
  success: boolean;
  message: string;
  token?: string;
  user?: {
    id: number;
    name: string;
    email: string;
    phone: string;
    address?: string;
    role: string;
    userCode: string;
    profileImage?: string;
  };
}
