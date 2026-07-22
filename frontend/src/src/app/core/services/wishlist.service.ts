import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { switchMap, tap } from 'rxjs/operators';
import { environment } from 'src/environments/environment';

@Injectable({ providedIn: 'root' })
export class WishlistService {

  private readonly api = `${environment.apiUrl}/wishlist`;
  private itemsSubject = new BehaviorSubject<any[]>([]);
  items$ = this.itemsSubject.asObservable();

  constructor(private http: HttpClient) {}

  load(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.api}/user/${userId}`).pipe(
      tap(items => this.itemsSubject.next(items || []))
    );
  }

  add(userId: number, productId: number): Observable<any> {
    return this.http.post<any>(`${this.api}/user/${userId}/product/${productId}`, {}).pipe(
      tap(() => this.load(userId).subscribe())
    );
  }

  remove(userId: number, productId: number): Observable<string> {
    return this.http.delete(`${this.api}/user/${userId}/product/${productId}`, { responseType: 'text' }).pipe(
      tap(() => this.load(userId).subscribe())
    );
  }

  removeById(userId: number, wishlistId: number): Observable<string> {
    return this.http.delete(`${this.api}/${wishlistId}`, { responseType: 'text' }).pipe(
      tap(() => this.load(userId).subscribe())
    );
  }

  toggle(userId: number, productId: number): Observable<any> {
    return this.exists(userId, productId).pipe(
      switchMap(res => res.exists ? this.remove(userId, productId) : this.add(userId, productId))
    );
  }

  exists(userId: number, productId: number): Observable<{ exists: boolean }> {
    return this.http.get<{ exists: boolean }>(`${this.api}/user/${userId}/product/${productId}/exists`);
  }

  isWishlisted(productId: number): boolean {
    return this.itemsSubject.value.some(item =>
      Number(item.product?.id ?? item.productId ?? item.id) === Number(productId)
    );
  }

  currentItems(): any[] {
    return this.itemsSubject.value;
  }

  clear(): void {
    this.itemsSubject.next([]);
  }
}
