import { Injectable } from '@angular/core';
import {
  HttpRequest, HttpHandler, HttpEvent, HttpInterceptor, HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';

@Injectable()
export class JwtInterceptor implements HttpInterceptor {

  constructor(private router: Router) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = localStorage.getItem('rural_mart_token');

    // Token থাকলে প্রতিটি request-এ Authorization header যোগ করো
    if (token) {
      request = request.clone({
        setHeaders: { Authorization: `Bearer ${token}` }
      });
    }

    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          // Token expired বা invalid — logout করে login page-এ পাঠাও
          localStorage.removeItem('rural_mart_user');
          localStorage.removeItem('rural_mart_token');
          this.router.navigate(['/login'], {
            queryParams: { reason: 'session_expired' }
          });
        }
        return throwError(() => error);
      })
    );
  }
}
