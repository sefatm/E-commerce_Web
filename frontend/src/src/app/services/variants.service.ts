import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class VariantService {

  private apiUrl = `${environment.apiUrl}/variants`;

  constructor(private http: HttpClient) {}

  getVariants(productId: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/product/${productId}`);
  }

  createVariant(data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/create`, data);
  }

  deleteVariant(variantId: string): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${variantId}`);
  }

  updateVariant(id: string, data: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}`, data);
  }
}
