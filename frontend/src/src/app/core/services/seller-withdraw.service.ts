import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({ providedIn: 'root' })
export class SellerWithdrawService {
  private readonly api = `${environment.apiUrl}/seller/withdraw`;

  constructor(private http: HttpClient) {}

  request(sellerId: number, data: any): Observable<any> {
    return this.http.post(`${this.api}/request/${sellerId}`, data);
  }

  getAll(status?: string): Observable<any[]> {
    const params = status ? `?status=${status}` : '';
    return this.http.get<any[]>(`${this.api}/getall${params}`);
  }

  getBySeller(sellerId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.api}/seller/${sellerId}`);
  }

  getAvailableBalance(sellerId: number): Observable<number> {
    return this.http.get<number>(`${this.api}/seller/${sellerId}/available`);
  }

  approve(id: number, transactionRef: string, note = ''): Observable<string> {
    return this.http.patch(`${this.api}/${id}/approve`, { transactionRef, note }, { responseType: 'text' });
  }

  reject(id: number, note = ''): Observable<string> {
    return this.http.patch(`${this.api}/${id}/reject`, { note }, { responseType: 'text' });
  }

  delete(id: number): Observable<string> {
    return this.http.delete(`${this.api}/${id}`, { responseType: 'text' });
  }
}
