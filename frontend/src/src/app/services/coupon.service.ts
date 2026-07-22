import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Coupon, CouponValidateResponse } from '../models/coupon.model';
import { environment } from 'src/environments/environment';

@Injectable({ providedIn: 'root' })
export class CouponService {

  private baseUrl = `${environment.apiUrl}/coupon`;

  constructor(private http: HttpClient) {}

  create(coupon: Coupon): Observable<string> {
    return this.http.post(`${this.baseUrl}/create`, coupon, { responseType: 'text' });
  }

  getAll(): Observable<Coupon[]> {
    return this.http.get<Coupon[]>(`${this.baseUrl}/getall`);
  }

  getById(id: number): Observable<Coupon> {
    return this.http.get<Coupon>(`${this.baseUrl}/${id}`);
  }

  update(id: number, coupon: Coupon): Observable<string> {
    return this.http.put(`${this.baseUrl}/${id}`, coupon, { responseType: 'text' });
  }

  delete(id: number): Observable<string> {
    return this.http.delete(`${this.baseUrl}/${id}`, { responseType: 'text' });
  }

  validate(code: string, orderTotal: number): Observable<CouponValidateResponse> {
    return this.http.post<CouponValidateResponse>(`${this.baseUrl}/validate`, { code, orderTotal });
  }

  markUsed(couponId: number): Observable<string> {
    return this.http.post(`${this.baseUrl}/mark-used/${couponId}`, {}, { responseType: 'text' });
  }
}
