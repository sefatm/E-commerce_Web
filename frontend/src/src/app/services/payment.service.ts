import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({ providedIn: 'root' })
export class PaymentService {

  private base = `${environment.apiUrl}/payment`;

  constructor(private http: HttpClient) {}

  /**
   * SSLCommerz payment session শুরু করো — gatewayUrl ফেরত আসবে,
   * যেখানে user-কে redirect করতে হবে।
   */
  initiate(orderCode: string): Observable<{ success: boolean; gatewayUrl?: string; message?: string }> {
    return this.http.post<{ success: boolean; gatewayUrl?: string; message?: string }>(
      `${this.base}/initiate`, { orderCode }
    );
  }
}
