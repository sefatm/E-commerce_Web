import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({ providedIn: 'root' })

export class InventoryService {

  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  
  getInventory(): Observable<any> {
    return this.http.get(`${this.apiUrl}/product/getall`);
  }

  updateStock(productId: number, stock: number): Observable<any> {
    return this.http.patch(`${this.apiUrl}/product/${productId}/stock`, { stock });
  }
}
