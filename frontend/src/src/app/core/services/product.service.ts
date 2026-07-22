import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({ providedIn: 'root' })
export class ProductService {

  private apiUrl = `${environment.apiUrl}/product`;

  constructor(private http: HttpClient) {}

  getProducts(): Observable<any> {
    return this.http.get(`${this.apiUrl}/public`);
  }

  getByCategory(id: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/public/by-category/${id}`);
  }

  deleteProduct(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`, { responseType: 'text' });
  }

  getInventory(): Observable<any> {
    return this.http.get(`${this.apiUrl}/getall`);
  }

  updateStock(productId: number, data: { stock: number; reason?: string }): Observable<any> {
    return this.http.patch(`${this.apiUrl}/${productId}/stock`, data);
  }
}
