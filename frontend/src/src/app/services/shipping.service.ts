
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({ providedIn: 'root' })
export class ShippingService {

  private base = `${environment.apiUrl}/shipping`;

  constructor(private http: HttpClient) {}

  getZones(status?: string): Observable<any> {
    const params = status ? `?status=${status}` : '';
    return this.http.get(`${this.base}/zones${params}`);
  }

  getZoneById(id: number): Observable<any> {
    return this.http.get(`${this.base}/zones/${id}`);
  }

  createZone(zone: any): Observable<any> {
    return this.http.post(`${this.base}/zones`, zone);
  }

  updateZone(id: number, zone: any): Observable<any> {
    return this.http.put(`${this.base}/zones/${id}`, zone);
  }

  deleteZone(id: number): Observable<any> {
    return this.http.delete(`${this.base}/zones/${id}`);
  }

  getMethods(zoneId?: number, status?: string): Observable<any> {
    let params = '';
    if (zoneId) params += `?zoneId=${zoneId}`;
    if (status) params += `${params ? '&' : '?'}status=${status}`;
    return this.http.get(`${this.base}/methods${params}`);
  }

  getAvailableMethods(district: string): Observable<any> {
    return this.http.get(`${this.base}/methods/available?district=${district}`);
  }

  createMethod(method: any, zoneId: number): Observable<any> {
    return this.http.post(`${this.base}/methods?zoneId=${zoneId}`, method);
  }

  updateMethod(id: number, method: any): Observable<any> {
    return this.http.put(`${this.base}/methods/${id}`, method);
  }

  deleteMethod(id: number): Observable<any> {
    return this.http.delete(`${this.base}/methods/${id}`);
  }

  getTrackingList(status?: string, search?: string): Observable<any> {
    let params = '';
    if (status) params += `?status=${status}`;
    if (search) params += `${params ? '&' : '?'}search=${search}`;
    return this.http.get(`${this.base}/tracking${params}`);
  }

  getTrackingStats(): Observable<any> {
    return this.http.get(`${this.base}/tracking/stats`);
  }

  getTrackingById(id: number): Observable<any> {
    return this.http.get(`${this.base}/tracking/${id}`);
  }

  getTrackingByOrder(orderId: number): Observable<any> {
    return this.http.get(`${this.base}/tracking/order/${orderId}`);
  }

  trackByNumber(trackingNumber: string): Observable<any> {
    return this.http.get(`${this.base}/tracking/track/${trackingNumber}`);
  }

  createTracking(tracking: any, methodId?: number): Observable<any> {
    const params = methodId ? `?methodId=${methodId}` : '';
    return this.http.post(`${this.base}/tracking${params}`, tracking);
  }

  updateTrackingStatus(id: number, status: string): Observable<any> {
    return this.http.patch(`${this.base}/tracking/${id}/status`, { status });
  }

  updateTracking(id: number, tracking: any): Observable<any> {
    return this.http.put(`${this.base}/tracking/${id}`, tracking);
  }

  deleteTracking(id: number): Observable<any> {
    return this.http.delete(`${this.base}/tracking/${id}`);
  }
}
