import { Component, OnInit } from '@angular/core';
import { SellerService } from 'src/app/core/services/seller.service';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-seller-approvals',
  templateUrl: './seller-approvals.component.html',
  styleUrls: ['./seller-approvals.component.css']
})
export class SellerApprovalsComponent implements OnInit {
  sellers: any[] = [];
  isLoading = false;
  isSubmitting = false;
  successMsg = '';
  errorMsg = '';
  rejectTarget: any = null;
  rejectReason = '';
  rejectError = '';

  constructor(private sellerService: SellerService) {}

  ngOnInit(): void {
    this.loadPending();
  }

  loadPending(): void {
    this.isLoading = true;
    this.errorMsg = '';

    this.sellerService.getPending().subscribe({
      next: (res: any) => {
        const data = res?.data ?? res;
        this.sellers = Array.isArray(data) ? data : [];
        this.isLoading = false;
      },
      error: () => {
        this.errorMsg = 'Pending sellers load failed.';
        this.sellers = [];
        this.isLoading = false;
      }
    });
  }

  approve(id: number): void {
    this.isSubmitting = true;
    this.errorMsg = '';
    this.sellerService.approve(id).subscribe({
      next: () => {
        this.isSubmitting = false;
        this.successMsg = 'Seller approved.';
        this.loadPending();
        setTimeout(() => this.successMsg = '', 2500);
      },
      error: () => {
        this.isSubmitting = false;
        this.errorMsg = 'Approve failed.';
      }
    });
  }

  openReject(seller: any): void {
    this.rejectTarget = seller;
    this.rejectReason = '';
    this.rejectError = '';
    this.errorMsg = '';
  }

  closeReject(): void {
    this.rejectTarget = null;
    this.rejectReason = '';
    this.rejectError = '';
  }

  reject(): void {
    if (!this.rejectTarget?.id) return;
    if (!this.rejectReason.trim()) {
      this.rejectError = 'Reject reason is required for admin record.';
      return;
    }

    this.isSubmitting = true;
    this.rejectError = '';
    this.errorMsg = '';
    this.sellerService.reject(this.rejectTarget.id, this.rejectReason.trim()).subscribe({
      next: () => {
        this.isSubmitting = false;
        this.successMsg = 'Seller rejected.';
        this.closeReject();
        this.loadPending();
        setTimeout(() => this.successMsg = '', 2500);
      },
      error: () => {
        this.isSubmitting = false;
        this.errorMsg = 'Reject failed.';
      }
    });
  }

  fileUrl(path?: string): string {
    if (!path) return '';
    if (path.startsWith('http://') || path.startsWith('https://')) return path;

    const cleanPath = path
      .replace(/^\/+/, '')
      .replace(/^uploads\//, '');

    return `${environment.apiUrl}/uploads/${cleanPath}`;
  }

  isPdf(path?: string): boolean {
    return !!path && path.toLowerCase().endsWith('.pdf');
  }

  hasAnyDocument(seller: any): boolean {
    return !!(seller?.profilePhoto || seller?.nidFrontImage || seller?.nidBackImage);
  }

  ownerName(seller: any): string {
    return seller?.name || seller?.user?.name || seller?.user?.fullName || 'N/A';
  }

  trackBySellerId(index: number, seller: any): any {
    return seller?.id ?? index;
  }
}
