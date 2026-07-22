import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ReviewService {

  private apiUrl = environment.apiUrl + '/products/reviews';

  constructor(private http: HttpClient) { }

  getProductReviews(filter: any): Observable<any> {
    let params = new HttpParams();
    if (filter.rating) params = params.set('rating', filter.rating.toString());
    if (filter.status) params = params.set('status', filter.status);
    if (filter.page)   params = params.set('page', filter.page.toString());
    if (filter.limit)  params = params.set('limit', filter.limit.toString());
    return this.http.get(this.apiUrl, { params });
  }

  // Public: get reviews for one product
  getByProduct(productId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/product/${productId}`);
  }

  // Public: get rating stats for one product
  getProductStats(productId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/product/${productId}/stats`);
  }

  // Customer: submit a review
  submitReview(customerId: number, productId: number, rating: number, comment: string): Observable<any> {
    return this.http.post(this.apiUrl, { customerId, productId, rating, comment });
  }

  deleteReview(id: string): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }
}
