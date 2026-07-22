import { Injectable } from '@angular/core';
import {
  HttpRequest, HttpHandler, HttpEvent, HttpInterceptor, HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';
import { environment } from 'src/environments/environment';

@Injectable()
export class JwtInterceptor implements HttpInterceptor {

  constructor(private router: Router) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = localStorage.getItem(environment.auth.tokenKey);

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
          localStorage.removeItem(environment.auth.userKey);
          localStorage.removeItem(environment.auth.tokenKey);
          this.router.navigate(['/login'], {
            queryParams: { reason: 'session_expired' }
          });
        }
        return throwError(() => error);
      })
    );
  }
}
