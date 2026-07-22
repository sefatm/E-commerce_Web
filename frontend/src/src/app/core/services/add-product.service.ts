import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AddProduct } from '../models/add-product.model';
import { environment } from 'src/environments/environment';

@Injectable({ providedIn: 'root' })
export class ProductService {

  private apiUrl         = `${environment.apiUrl}/product`;
  private categoryApiUrl = `${environment.apiUrl}/category`;

  constructor(private http: HttpClient) {}

  getCategories(): Observable<any> {
    return this.http.get<any>(`${this.categoryApiUrl}/getall`);
  }

  createCategory(categoryForm: { name: string; description: string; parent: string }): Observable<any> {
    return this.http.post(`${this.categoryApiUrl}/create`, categoryForm);
  }

  addProduct(data: FormData): Observable<string> {
    return this.http.post(`${this.apiUrl}/create`, data, { responseType: 'text' });
  }

  getProducts(): Observable<any> {
    return this.http.get(`${this.apiUrl}/getall`);
  }

  getPublicProducts(): Observable<any> {
    return this.http.get(`${this.apiUrl}/public`);
  }

  getPendingProducts(): Observable<any> {
    return this.http.get(`${this.apiUrl}/pending`);
  }

  getRejectedProducts(): Observable<any> {
    return this.http.get(`${this.apiUrl}/rejected`);
  }

  getProductById(id: number): Observable<AddProduct> {
    return this.http.get<AddProduct>(`${this.apiUrl}/${id}`);
  }

  getByCategory(categoryId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/by-category/${categoryId}`);
  }

  getPublicByCategory(categoryId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/public/by-category/${categoryId}`);
  }

  getBySeller(sellerId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/seller/${sellerId}`);
  }

  approveProduct(id: number): Observable<any> {
    return this.http.patch(`${this.apiUrl}/${id}/approve`, {}, { responseType: 'text' });
  }

  rejectProduct(id: number, reason = ''): Observable<any> {
    return this.http.patch(`${this.apiUrl}/${id}/reject`, { reason }, { responseType: 'text' });
  }

  updateProduct(id: number, data: FormData): Observable<string> {
    return this.http.post(`${this.apiUrl}/update/${id}`, data, { responseType: 'text' }).pipe(
      catchError(() => this.http.put(`${this.apiUrl}/${id}`, data, { responseType: 'text' }))
    );
  }

  deleteProduct(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }
}
