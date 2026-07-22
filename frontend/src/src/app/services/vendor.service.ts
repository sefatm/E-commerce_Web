import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({ providedIn: 'root' })
export class VendorService {

  private base = `${environment.apiUrl}/vendors`;

  constructor(private http: HttpClient) {}


  getAll(status?: string, search?: string): Observable<any> {
    let params = '';
    if (search)       params += `?search=${search}`;
    else if (status)  params += `?status=${status}`;
    return this.http.get(`${this.base}/getall${params}`);
  }

  getStats(): Observable<any> {
    return this.http.get(`${this.base}/stats`);
  }

  getById(id: number): Observable<any> {
    return this.http.get(`${this.base}/${id}`);
  }

  create(vendor: any): Observable<any> {
    return this.http.post(`${this.base}/create`, vendor);
  }

  update(id: number, vendor: any): Observable<any> {
    return this.http.put(`${this.base}/${id}`, vendor);
  }

  approve(id: number): Observable<any> {
    return this.http.patch(`${this.base}/${id}/approve`, {});
  }

  suspend(id: number, reason: string): Observable<any> {
    return this.http.patch(`${this.base}/${id}/suspend`, { reason });
  }

  reject(id: number, reason: string): Observable<any> {
    return this.http.patch(`${this.base}/${id}/reject`, { reason });
  }

  updateCommission(id: number, rate: number): Observable<any> {
    return this.http.patch(`${this.base}/${id}/commission`, { rate });
  }

  delete(id: number): Observable<any> {
    return this.http.delete(`${this.base}/${id}`);
  }


  getPayouts(vendorId?: number, status?: string): Observable<any> {
    let params = '';
    if (vendorId)    params += `?vendorId=${vendorId}`;
    else if (status) params += `?status=${status}`;
    return this.http.get(`${this.base}/payout/getall${params}`);
  }

  requestPayout(vendorId: number, amount: number, method: string, note: string): Observable<any> {
    return this.http.post(`${this.base}/payout/request`, { vendorId, amount, paymentMethod: method, note });
  }

  approvePayout(id: number, transactionRef: string, note: string): Observable<any> {
    return this.http.patch(`${this.base}/payout/${id}/approve`, { transactionRef, note });
  }

  rejectPayout(id: number, note: string): Observable<any> {
    return this.http.patch(`${this.base}/payout/${id}/reject`, { note });
  }

  deletePayout(id: number): Observable<any> {
    return this.http.delete(`${this.base}/payout/${id}`);
  }
}
