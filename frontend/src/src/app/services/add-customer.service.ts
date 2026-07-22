import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Customer } from '../models/add-customer.model';
import { environment } from 'src/environments/environment';

@Injectable({ providedIn: 'root' })
export class CustomerService {

  
  private apiUrl = `${environment.apiUrl}/customer`;

  constructor(private http: HttpClient) {}

  getCustomers(): Observable<Customer[]> {
    return this.http.get<Customer[]>(`${this.apiUrl}/getall`);
  }

  getCustomerById(id: number): Observable<Customer> {
    return this.http.get<Customer>(`${this.apiUrl}/${id}`);
  }

  createCustomer(data: Customer): Observable<any> {
    return this.http.post(`${this.apiUrl}/create`, data, { responseType: 'text' });
  }

  updateCustomer(id: number, data: Customer): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}`, data, { responseType: 'text' });
  }

  deleteCustomer(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`, { responseType: 'text' });
  }
}
