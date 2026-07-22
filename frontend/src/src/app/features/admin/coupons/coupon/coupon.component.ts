import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Coupon } from 'src/app/core/models/coupon.model';
import { CouponService } from 'src/app/core/services/coupon.service';

@Component({
  selector: 'app-coupon',
  templateUrl: './coupon.component.html',
  styleUrls: ['./coupon.component.css']
})
export class CouponComponent implements OnInit {

  couponForm!: FormGroup;
  coupons: Coupon[] = [];
  isEditing = false;
  editingId: number | null = null;
  successMsg = '';
  errorMsg = '';
  isLoading = false;

  constructor(private fb: FormBuilder, private couponService: CouponService) {}

  ngOnInit(): void {
    this.initForm();
    this.loadCoupons();
  }

  initForm(): void {
    this.couponForm = this.fb.group({
      code:              ['', [Validators.required, Validators.minLength(3)]],
      discountType:      ['PERCENTAGE', Validators.required],
      discountValue:     ['', [Validators.required, Validators.min(1)]],
      minOrderAmount:    [''],
      maxDiscountAmount: [''],
      usageLimit:        [''],
      startDate:         [''],
      endDate:           [''],
      description:       [''],
      status:            ['ACTIVE']
    });
  }

  loadCoupons(): void {
    this.isLoading = true;
    this.couponService.getAll().subscribe({
      next: (data: any) => { this.coupons = data; this.isLoading = false; },
      error: () => {
        this.errorMsg = 'Failed to load coupons.';
        this.isLoading = false;
        setTimeout(() => this.errorMsg = '', 4000);
      }
    });
  }

  onSubmit(): void {
    if (this.couponForm.invalid) return;

  
    const couponData: Coupon = {
      ...this.couponForm.value,
      code: this.couponForm.value.code.toUpperCase().trim()
    };

    if (this.isEditing && this.editingId !== null) {
      this.couponService.update(this.editingId, couponData).subscribe({
        next: () => {
          this.successMsg = 'Coupon updated successfully!';
          this.resetForm();
          this.loadCoupons();
        },
        error: () => {
          this.errorMsg = 'Update failed.';
          setTimeout(() => this.errorMsg = '', 4000);
        }
      });
    } else {
      this.couponService.create(couponData).subscribe({
        next: () => {
          this.successMsg = 'Coupon created successfully!';
          this.resetForm();
          this.loadCoupons();
        },
        error: () => {
          this.errorMsg = 'Create failed.';
          setTimeout(() => this.errorMsg = '', 4000);
        }
      });
    }
  }

  onEdit(coupon: Coupon): void {
    this.isEditing = true;
    this.editingId = coupon.id!;
    this.couponForm.patchValue(coupon);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  onDelete(id: number): void {
    if (!confirm('Are you sure you want to delete this coupon?')) return;
    this.couponService.delete(id).subscribe({
      next: () => {
        this.successMsg = 'Coupon deleted.';
        this.loadCoupons();
        setTimeout(() => this.successMsg = '', 3000);
      },
      error: () => {
        this.errorMsg = 'Delete failed.';
        setTimeout(() => this.errorMsg = '', 4000);
      }
    });
  }


  toggleStatus(coupon: Coupon): void {
    const updated: Coupon = {
      ...coupon,
      status: coupon.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'
    };
    this.couponService.update(coupon.id!, updated).subscribe({
      next: () => this.loadCoupons(),
      error: () => {
        this.errorMsg = 'Status update failed.';
        setTimeout(() => this.errorMsg = '', 4000);
      }
    });
  }

  resetForm(): void {
    this.couponForm.reset({ discountType: 'PERCENTAGE', status: 'ACTIVE' });
    this.isEditing = false;
    this.editingId = null;
    setTimeout(() => { this.successMsg = ''; this.errorMsg = ''; }, 3000);
  }

  isExpired(endDate?: string): boolean {
    if (!endDate) return false;
    return new Date(endDate) < new Date();
  }
}
