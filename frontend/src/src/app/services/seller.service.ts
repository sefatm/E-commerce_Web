import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Seller } from '../models/seller.model';
import { environment } from 'src/environments/environment';

@Injectable({ providedIn: 'root' })
export class SellerService {
  private readonly apiUrl = `${environment.apiUrl}/seller`;

  constructor(private http: HttpClient) {}

  apply(data: Seller): Observable<any> {
    return this.http.post(`${this.apiUrl}/apply`, data);
  }

  getAll(): Observable<any> {
    return this.http.get(`${this.apiUrl}/getall`);
  }

  getPending(): Observable<any> {
    return this.http.get(`${this.apiUrl}/pending`);
  }

  getApproved(): Observable<any> {
    return this.http.get(`${this.apiUrl}/approved`);
  }

  getByUser(userId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/user/${userId}`);
  }

  approve(id: number): Observable<any> {
    return this.http.patch(`${this.apiUrl}/${id}/approve`, {});
  }

  reject(id: number, reason = ''): Observable<any> {
    return this.http.patch(`${this.apiUrl}/${id}/reject`, { reason });
  }
}
