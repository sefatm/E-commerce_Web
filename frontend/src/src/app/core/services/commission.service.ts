import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({ providedIn: 'root' })
export class CommissionService {
  private readonly base = `${environment.apiUrl}/commission`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<any[]> {
    return this.http.get<any[]>(`${this.base}/getall`);
  }

  getSummary(): Observable<any> {
    return this.http.get<any>(`${this.base}/summary`);
  }

  getBySeller(sellerId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.base}/seller/${sellerId}`);
  }

  getSellerSummary(sellerId: number): Observable<any> {
    return this.http.get<any>(`${this.base}/seller/${sellerId}/summary`);
  }

  getByStatus(status: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.base}/status/${status}`);
  }

  markPayable(id: number): Observable<string> {
    return this.http.patch(`${this.base}/${id}/mark-payable`, {}, { responseType: 'text' });
  }

  markPaid(id: number): Observable<string> {
    return this.http.patch(`${this.base}/${id}/mark-paid`, {}, { responseType: 'text' });
  }
}
