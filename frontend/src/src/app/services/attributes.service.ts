import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AttributeService {

  private apiUrl = `${environment.apiUrl}/attributes`;

  constructor(private http: HttpClient) {}

  getAttributes(): Observable<any> {
    return this.http.get(`${this.apiUrl}/getall`);
  }

  createAttribute(data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/create`, data);
  }

  deleteAttribute(id: string): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }
}
