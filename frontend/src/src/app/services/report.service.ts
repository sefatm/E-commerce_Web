import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CustomerReport, ProductReport, RevenueReport, SalesReport } from '../models/report.model';
import { environment } from 'src/environments/environment';


@Injectable({ providedIn: 'root' })
export class ReportService {

  private baseUrl = `${environment.apiUrl}/report`;

  constructor(private http: HttpClient) {}

  getSalesReport(from: string, to: string): Observable<SalesReport> {
    const params = new HttpParams().set('from', from).set('to', to);
    return this.http.get<SalesReport>(`${this.baseUrl}/sales`, { params });
  }

  getRevenueReport(): Observable<RevenueReport> {
    return this.http.get<RevenueReport>(`${this.baseUrl}/revenue`);
  }

  getProductReport(): Observable<ProductReport> {
    return this.http.get<ProductReport>(`${this.baseUrl}/products`);
  }

  getCustomerReport(): Observable<CustomerReport> {
    return this.http.get<CustomerReport>(`${this.baseUrl}/customers`);
  }
}
