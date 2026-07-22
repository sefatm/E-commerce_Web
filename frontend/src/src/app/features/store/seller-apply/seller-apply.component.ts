import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/core/services/auth.service';
import { SellerService } from 'src/app/core/services/seller.service';

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

  profilePhotoFile: File | null = null;
  nidFrontFile: File | null = null;
  nidBackFile: File | null = null;

  profilePhotoName = '';
  nidFrontName = '';
  nidBackName = '';

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
      nidNo: ['', [Validators.required, Validators.minLength(10)]],
      district: ['', [Validators.required]],
      address: [user?.address || '', [Validators.required]],
      productCategory: ['', [Validators.required]],
      businessType: ['Individual Seller', [Validators.required]],
      paymentMethod: ['bKash'],
      paymentNumber: ['', [Validators.required]],
      artisanStory: ['', [Validators.required, Validators.minLength(20)]],
      craftProcess: ['', [Validators.required, Validators.minLength(20)]]
    });
  }

  get f() { return this.sellerForm.controls; }

  onFileChange(event: Event, type: 'profile' | 'nidFront' | 'nidBack'): void {
    const input = event.target as HTMLInputElement;
    const file = input.files && input.files.length ? input.files[0] : null;
    if (!file) return;

    const allowed = ['image/jpeg', 'image/png', 'image/jpg', 'application/pdf'];
    if (!allowed.includes(file.type)) {
      this.errorMsg = 'Only JPG, PNG or PDF file is allowed.';
      input.value = '';
      return;
    }

    if (file.size > 3 * 1024 * 1024) {
      this.errorMsg = 'File size must be less than 3MB.';
      input.value = '';
      return;
    }

    this.errorMsg = '';
    if (type === 'profile') {
      this.profilePhotoFile = file;
      this.profilePhotoName = file.name;
    } else if (type === 'nidFront') {
      this.nidFrontFile = file;
      this.nidFrontName = file.name;
    } else {
      this.nidBackFile = file;
      this.nidBackName = file.name;
    }
  }

  submit(): void {
    this.submitted = true;
    this.successMsg = '';

    if (this.sellerForm.invalid || !this.profilePhotoFile || !this.nidFrontFile) {
      this.errorMsg = 'Please complete all seller information and upload profile photo + NID front image.';
      this.scrollToTop();
      return;
    }

    const user = this.authService.getUser();
    const payload = {
      ...this.sellerForm.value,
      user: user?.id ? { id: user.id } : null,
      profilePhotoFile: this.profilePhotoFile,
      nidFrontFile: this.nidFrontFile,
      nidBackFile: this.nidBackFile
    };

    this.isLoading = true;
    this.errorMsg = '';

    this.sellerService.apply(payload).subscribe({
      next: () => {
        this.isLoading = false;
        this.successMsg = 'Seller application submitted. Admin will review your documents soon.';
        this.scrollToTop();
        setTimeout(() => this.router.navigate(['/']), 1400);
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
      ? 'Application failed. Please check required information and uploaded files.'
      : raw;
  }

  private scrollToTop(): void {
    setTimeout(() => window.scrollTo({ top: 0, behavior: 'smooth' }), 0);
  }
}
