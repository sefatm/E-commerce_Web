import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, of } from 'rxjs';
import { tap } from 'rxjs/operators';
import { AuthResponse, LoginRequest, RegisterRequest } from '../models/auth.model';
import { environment } from 'src/environments/environment';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private apiUrl = `${environment.apiUrl}/auth`;

  private currentUserSubject = new BehaviorSubject<any>(this.getSavedUser());
  currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) { }


  login(data: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, data).pipe(
      tap(res => {
        if (res.success && res.user) {
          const normalized = this.normalizeUser(res.user);
          localStorage.setItem(environment.auth.userKey, JSON.stringify(normalized));
          if (res.token) localStorage.setItem(environment.auth.tokenKey, res.token);
          this.currentUserSubject.next(normalized);
        }
      })
    );
  }

  register(data: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, data);
  }

  // ── Forgot Password (OTP via email) ──────────────────────────
  forgotPassword(email: string): Observable<{ success: boolean; message: string }> {
    return this.http.post<{ success: boolean; message: string }>(`${this.apiUrl}/forgot-password`, { email });
  }

  verifyOtp(email: string, otp: string): Observable<{ success: boolean; message: string }> {
    return this.http.post<{ success: boolean; message: string }>(`${this.apiUrl}/verify-otp`, { email, otp });
  }

  resetPassword(email: string, otp: string, newPassword: string): Observable<{ success: boolean; message: string }> {
    return this.http.post<{ success: boolean; message: string }>(`${this.apiUrl}/reset-password`, { email, otp, newPassword });
  }

  getProfile(id: number): Observable<AuthResponse> {
    return this.http.get<AuthResponse>(`${this.apiUrl}/profile/${id}`);
  }

  updateProfile(id: number, formData: FormData): Observable<AuthResponse> {
    return this.http.put<AuthResponse>(`${this.apiUrl}/profile/${id}`, formData).pipe(
      tap(res => {
        if (res.success && res.user) {
          this.setUser(res.user);
        }
      })
    );
  }

  setUser(user: any): void {
    const normalized = this.normalizeUser(user);
    localStorage.setItem(environment.auth.userKey, JSON.stringify(normalized));
    this.currentUserSubject.next(normalized);
  }

  logout(): void {
    localStorage.removeItem(environment.auth.userKey);
    localStorage.removeItem(environment.auth.tokenKey);
    this.currentUserSubject.next(null);
  }


  isLoggedIn(): boolean {
    return !!localStorage.getItem(environment.auth.userKey);
  }

  getUser(): any {
    return this.getSavedUser();
  }

  getToken(): string | null {
    return localStorage.getItem(environment.auth.tokenKey);
  }

  isAdmin(): boolean {
    return this.hasAnyRole(['admin', 'manager', 'staff']);
  }

  isSeller(): boolean {
    return this.hasAnyRole(['seller', 'vendor']);
  }

  isCustomer(): boolean {
    return this.hasAnyRole(['customer']);
  }

  hasAnyRole(roles: string[]): boolean {
    const userRole = this.normalizeRole(this.getSavedUser()?.role);
    return roles.map(role => this.normalizeRole(role)).includes(userRole);
  }

  getHomeRouteForCurrentUser(): string {
    if (this.isAdmin()) return '/admin/dashboard';
    if (this.isSeller()) return '/seller/dashboard';
    return '/';
  }

  private normalizeRole(role: string | null | undefined): string {
    return (role || '').trim().toLowerCase();
  }

  private getSavedUser(): any {
    const raw = localStorage.getItem(environment.auth.userKey);
    return raw ? this.normalizeUser(JSON.parse(raw)) : null;
  }

  private normalizeUser(user: any): any {
    if (!user) {
      return null;
    }

    return {
      ...user,
      id: user.id ?? user._id,
      role: user.role,
    };
  }
}
