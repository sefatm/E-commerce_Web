import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Order, OrderAnalytics, OrderReturn } from '../models/order.model';
import { environment } from 'src/environments/environment';


@Injectable({ providedIn: 'root' })
export class OrderService {

  private base = `${environment.apiUrl}/order`;

  constructor(private http: HttpClient) {}

  placeOrder(order: Order): Observable<Order> {
    return this.http.post<Order>(`${this.base}/place`, order);
  }

  getAll(): Observable<Order[]> {
    return this.http.get<Order[]>(`${this.base}/getall`);
  }

  getById(id: number): Observable<Order> {
    return this.http.get<Order>(`${this.base}/${id}`);
  }

  getByCode(code: string): Observable<Order> {
    return this.http.get<Order>(`${this.base}/code/${code}`);
  }

  getByStatus(status: string): Observable<Order[]> {
    return this.http.get<Order[]>(`${this.base}/status/${status}`);
  }

  search(keyword: string): Observable<Order[]> {
    return this.http.get<Order[]>(`${this.base}/search/${keyword}`);
  }

  updateStatus(id: number, status: string): Observable<string> {
    return this.http.patch(`${this.base}/${id}/status`, { status }, { responseType: 'text' });
  }

  update(id: number, order: Order): Observable<string> {
    return this.http.put(`${this.base}/${id}`, order, { responseType: 'text' });
  }

  delete(id: number): Observable<string> {
    return this.http.delete(`${this.base}/${id}`, { responseType: 'text' });
  }

  getAnalytics(): Observable<OrderAnalytics> {
    return this.http.get<OrderAnalytics>(`${this.base}/analytics`);
  }

  getBySeller(sellerId: number): Observable<Order[]> {
    return this.http.get<Order[]>(`${this.base}/seller/${sellerId}`);
  }

  getSellerDashboard(sellerId: number): Observable<any> {
    return this.http.get<any>(`${this.base}/seller/${sellerId}/dashboard`);
  }

  requestReturn(orderId: number, productName: string, reason: string, refundDetails: any): Observable<string> {
    return this.http.post(`${this.base}/return/request`,
      { orderId, productName, reason, ...refundDetails }, { responseType: 'text' });
  }

  getAllReturns(): Observable<OrderReturn[]> {
    return this.http.get<OrderReturn[]>(`${this.base}/return/getall`);
  }

  getReturnsByOrder(orderId: number): Observable<OrderReturn[]> {
    return this.http.get<OrderReturn[]>(`${this.base}/return/order/${orderId}`);
  }

  approveReturn(id: number, note: string = '', refundAmount?: number, refundMethod: string = ''): Observable<string> {
    return this.http.patch(
      `${this.base}/return/${id}/approve`,
      { note, refundAmount, refundMethod },
      { responseType: 'text' }
    );
  }

  rejectReturn(id: number, note: string = ''): Observable<string> {
    return this.http.patch(`${this.base}/return/${id}/reject`, { note }, { responseType: 'text' });
  }

  scanUpdate(id: number, status: string, note: string = '', token: string = ''): Observable<string> {
    return this.http.patch(`${this.base}/${id}/scan-update${token ? '?token=' + encodeURIComponent(token) : ''}`, { status, note }, { responseType: 'text' });
  }

  markCodCollected(id: number): Observable<string> {
    return this.http.patch(`${this.base}/${id}/cod-collected`, {}, { responseType: 'text' });
  }

  verifyCodPayment(id: number): Observable<string> {
    return this.http.patch(`${this.base}/${id}/cod-verify`, {}, { responseType: 'text' });
  }

  advanceReturn(id: number, status: string, note: string = '', refundMethod: string = '', transactionRef: string = ''): Observable<string> {
    return this.http.patch(`${this.base}/return/${id}/status`, { status, note, refundMethod, transactionRef }, { responseType: 'text' });
  }

  deleteReturn(id: number): Observable<string> {
    return this.http.delete(`${this.base}/return/${id}`, { responseType: 'text' });
  }
}
