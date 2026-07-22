import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Offer } from '../models/offer.model';
import { environment } from 'src/environments/environment';

@Injectable({ providedIn: 'root' })
export class OfferService {

  private baseUrl = `${environment.apiUrl}/offer`;

  constructor(private http: HttpClient) {}

  create(formData: FormData): Observable<string> {
    return this.http.post(`${this.baseUrl}/create`, formData, { responseType: 'text' });
  }

  getAll(): Observable<Offer[]> {
    return this.http.get<Offer[]>(`${this.baseUrl}/getall`);
  }

  getById(id: number): Observable<Offer> {
    return this.http.get<Offer>(`${this.baseUrl}/${id}`);
  }

  update(id: number, offer: Offer): Observable<string> {
    return this.http.put(`${this.baseUrl}/${id}`, offer, { responseType: 'text' });
  }

  delete(id: number): Observable<string> {
    return this.http.delete(`${this.baseUrl}/${id}`, { responseType: 'text' });
  }

  getActiveOffers(): Observable<Offer[]> {
    return this.http.get<Offer[]>(`${this.baseUrl}/active`);
  }

  getOffersByProduct(productId: number): Observable<Offer[]> {
    return this.http.get<Offer[]>(`${this.baseUrl}/by-product/${productId}`);
  }

  getOffersByCategory(categoryId: number): Observable<Offer[]> {
    return this.http.get<Offer[]>(`${this.baseUrl}/by-category/${categoryId}`);
  }
}
