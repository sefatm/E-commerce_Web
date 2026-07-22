import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({ providedIn: 'root' })
export class FinanceService {
  private readonly api = `${environment.apiUrl}/finance`;
  constructor(private http: HttpClient) {}
  getWallet(sellerId: number): Observable<any> {
    return this.http.get<any>(`${this.api}/seller/${sellerId}/wallet`);
  }
  getTransactions(sellerId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.api}/seller/${sellerId}/transactions`);
  }
}
