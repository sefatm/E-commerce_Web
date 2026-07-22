import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/services/auth.service';

type Step = 'email' | 'otp' | 'reset' | 'done';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css']
})
export class ForgotPasswordComponent {

  step: Step = 'email';

  email = '';
  otp = '';
  newPassword = '';
  confirmPassword = '';

  isLoading = false;
  errorMsg = '';
  infoMsg = '';

  // resend cooldown
  resendCooldown = 0;
  private cooldownTimer: any;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  // ── Step 1: Send OTP ─────────────────────────────────────────
  sendOtp(): void {
    this.errorMsg = '';
    this.infoMsg = '';

    if (!this.email.trim() || !this.email.includes('@')) {
      this.errorMsg = 'Please enter a valid email address.';
      return;
    }

    this.isLoading = true;
    this.authService.forgotPassword(this.email.trim().toLowerCase()).subscribe({
      next: (res) => {
        this.isLoading = false;
        if (res.success) {
          this.step = 'otp';
          this.infoMsg = res.message || 'If an account exists, a code has been sent to your email.';
          this.startCooldown();
        } else {
          this.errorMsg = res.message || 'Something went wrong. Please try again.';
        }
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMsg = err?.error?.message || 'Something went wrong. Please try again.';
      }
    });
  }

  resendOtp(): void {
    if (this.resendCooldown > 0) return;
    this.sendOtp();
  }

  private startCooldown(): void {
    this.resendCooldown = 60;
    clearInterval(this.cooldownTimer);
    this.cooldownTimer = setInterval(() => {
      this.resendCooldown--;
      if (this.resendCooldown <= 0) clearInterval(this.cooldownTimer);
    }, 1000);
  }

  // ── Step 2: Verify OTP ───────────────────────────────────────
  verifyOtp(): void {
    this.errorMsg = '';

    if (!this.otp.trim() || this.otp.trim().length !== 6) {
      this.errorMsg = 'Please enter the 6-digit code sent to your email.';
      return;
    }

    this.isLoading = true;
    this.authService.verifyOtp(this.email, this.otp.trim()).subscribe({
      next: (res) => {
        this.isLoading = false;
        if (res.success) {
          this.step = 'reset';
          this.infoMsg = '';
        } else {
          this.errorMsg = res.message || 'Invalid or expired code.';
        }
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMsg = err?.error?.message || 'Invalid or expired code.';
      }
    });
  }

  changeEmail(): void {
    this.step = 'email';
    this.otp = '';
    this.errorMsg = '';
    this.infoMsg = '';
    clearInterval(this.cooldownTimer);
    this.resendCooldown = 0;
  }

  // ── Step 3: Reset Password ───────────────────────────────────
  resetPassword(): void {
    this.errorMsg = '';

    if (this.newPassword.length < 6) {
      this.errorMsg = 'Password must be at least 6 characters.';
      return;
    }
    if (this.newPassword !== this.confirmPassword) {
      this.errorMsg = 'Passwords do not match.';
      return;
    }

    this.isLoading = true;
    this.authService.resetPassword(this.email, this.otp.trim(), this.newPassword).subscribe({
      next: (res) => {
        this.isLoading = false;
        if (res.success) {
          this.step = 'done';
        } else {
          this.errorMsg = res.message || 'Could not reset password. Please try again.';
        }
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMsg = err?.error?.message || 'Could not reset password. Please try again.';
      }
    });
  }

  goToLogin(): void {
    this.router.navigate(['/login']);
  }
}
