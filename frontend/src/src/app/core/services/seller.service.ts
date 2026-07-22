import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Seller } from '../models/seller.model';
import { environment } from 'src/environments/environment';

@Injectable({ providedIn: 'root' })
export class SellerService {
  private readonly apiUrl = `${environment.apiUrl}/seller`;

  constructor(private http: HttpClient) {}

  apply(data: any): Observable<any> {
    const hasFiles = data?.profilePhotoFile || data?.nidFrontFile || data?.nidBackFile;

    if (!hasFiles) {
      return this.http.post(`${this.apiUrl}/apply`, data);
    }

    const formData = new FormData();
    Object.keys(data).forEach(key => {
      const value = data[key];
      if (value === undefined || value === null) return;
      if (key === 'user' && value?.id) {
        formData.append('userId', String(value.id));
      } else if (key === 'profilePhotoFile' || key === 'nidFrontFile' || key === 'nidBackFile') {
        formData.append(key, value);
      } else if (typeof value !== 'object') {
        formData.append(key, String(value));
      }
    });

    return this.http.post(`${this.apiUrl}/apply`, formData);
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
