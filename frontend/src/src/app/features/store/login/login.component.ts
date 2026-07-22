import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from 'src/app/core/services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

  user = {
    email: '',
    password: ''
  };

  showPass = false;
  isLoading = false;
  errorMsg = '';

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private authService: AuthService
  ) {}

  onSubmit(): void {
    this.errorMsg = '';

    if (!this.user.email.trim()) {
      this.errorMsg = 'Email is required';
      return;
    }
    if (!this.user.password.trim()) {
      this.errorMsg = 'Password is required';
      return;
    }

    this.isLoading = true;

    this.authService.login({ email: this.user.email, password: this.user.password }).subscribe({
      next: (res) => {
        this.isLoading = false;
        if (res.success) {
          const returnUrl = this.route.snapshot.queryParamMap.get('returnUrl');
          if (returnUrl && returnUrl.startsWith('/') && !this.authService.isAdmin() && !this.authService.isSeller()) {
            this.router.navigateByUrl(returnUrl);
          } else {
            this.router.navigate([this.authService.getHomeRouteForCurrentUser()]);
          }
        } else {
          this.errorMsg = res.message ?? 'Login failed. Please try again.';
        }
      },
      error: () => {
        this.isLoading = false;
        this.errorMsg = 'Invalid email or password.';
      }
    });
  }

  togglePassword(): void {
    this.showPass = !this.showPass;
  }

  goRegister(): void {
    const returnUrl = this.route.snapshot.queryParamMap.get('returnUrl');
    this.router.navigate(['/register'], { queryParams: returnUrl ? { returnUrl } : {} });
  }

  forgotPassword(): void {
    this.router.navigate(['/forgot-password']);
  }
}
