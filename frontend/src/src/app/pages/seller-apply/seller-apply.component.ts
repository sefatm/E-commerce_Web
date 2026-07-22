import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/services/auth.service';
import { SellerService } from 'src/app/services/seller.service';

@Component({
  selector: 'app-seller-apply',
  templateUrl: './seller-apply.component.html',
  styleUrls: ['./seller-apply.component.css']
})
export class SellerApplyComponent implements OnInit {
  sellerForm!: FormGroup;
  submitted = false;
  isLoading = false;
  successMsg = '';
  errorMsg = '';

  constructor(
    private fb: FormBuilder,
    private sellerService: SellerService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const user = this.authService.getUser();
    this.sellerForm = this.fb.group({
      name: [user?.name || '', [Validators.required, Validators.minLength(2)]],
      shopName: ['', [Validators.required, Validators.minLength(2)]],
      email: [user?.email || '', [Validators.required, Validators.email]],
      phone: [user?.phone || '', [Validators.required]],
      nidNo: ['', [Validators.required]],
      district: ['', [Validators.required]],
      address: [user?.address || '', [Validators.required]],
      artisanStory: ['', [Validators.required, Validators.minLength(20)]],
      craftProcess: ['', [Validators.required, Validators.minLength(20)]]
    });
  }

  get f() { return this.sellerForm.controls; }

  submit(): void {
    this.submitted = true;
    if (this.sellerForm.invalid) {
      this.errorMsg = 'Please complete all seller information correctly.';
      this.successMsg = '';
      this.scrollToTop();
      return;
    }

    const user = this.authService.getUser();
    const payload = {
      ...this.sellerForm.value,
      user: user?.id ? { id: user.id } : null
    };

    this.isLoading = true;
    this.errorMsg = '';
    this.successMsg = '';

    this.sellerService.apply(payload).subscribe({
      next: () => {
        this.isLoading = false;
        this.successMsg = 'Seller application submitted. Admin will review it soon.';
        this.scrollToTop();
        setTimeout(() => this.router.navigate(['/admin/sellers/approvals']), 1200);
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMsg = this.cleanApiError(err);
        this.scrollToTop();
      }
    });
  }

  private cleanApiError(err: any): string {
    const raw = err?.error?.message || err?.error;
    if (!raw || typeof raw !== 'string') {
      return 'Application failed. Please check the seller information and try again.';
    }

    const noisyBackendError =
      raw.length > 180 ||
      raw.includes('org.hibernate') ||
      raw.includes('org.springframework') ||
      raw.includes('could not execute statement') ||
      raw.includes('Internal Server Error') ||
      raw.includes('"trace"');

    return noisyBackendError
      ? 'Application failed. Please shorten any very long text and try again.'
      : raw;
  }

  private scrollToTop(): void {
    setTimeout(() => window.scrollTo({ top: 0, behavior: 'smooth' }), 0);
  }
}
