import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({ providedIn: 'root' })
export class BrandService {

  private apiUrl = `${environment.apiUrl}/brand`;

  constructor(private http: HttpClient) {}

  getAll(search?: string): Observable<any> {
    const params = search ? `?search=${search}` : '';
    return this.http.get(`${this.apiUrl}/getall${params}`);
  }

  getById(id: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/${id}`);
  }

  create(brand: { name: string; logo?: string; description?: string }): Observable<any> {
    return this.http.post(`${this.apiUrl}/create`, brand);
  }

  update(id: number, brand: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}`, brand);
  }

  delete(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }
}
