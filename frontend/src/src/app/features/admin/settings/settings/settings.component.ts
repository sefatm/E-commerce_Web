import { Component, OnInit } from '@angular/core';
import { environment } from 'src/environments/environment';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.css']
})
export class SettingsComponent implements OnInit {

  activeTab = 'site';

  successMsg = '';
  errorMsg   = '';
  isLoading  = false;
  isSaving   = false;

  site = {
    siteName:    'Rural Mart',
    siteTagline: 'Your one-stop online shopping destination in Bangladesh.',
    contactEmail:'support@ruralmart.com.bd',
    contactPhone:'+880 1700-000000',
    address:     'Dhaka, Bangladesh',
    currency:    'BDT',
    timezone:    'Asia/Dhaka',
    maintenanceMode: false
  };

  profile = {
    name:  'Admin',
    email: 'admin@ruralmart.com.bd',
    phone: '',
    profileImage: ''
  };
  selectedProfilePhoto: File | null = null;
  profilePhotoPreview = '';
  passwordForm = {
    currentPassword: '',
    newPassword:     '',
    confirmPassword: ''
  };
  showCurrentPass = false;
  showNewPass     = false;

  payment = {
    codEnabled:     true,
    bkashEnabled:   false,
    bkashNumber:    '',
    nagadEnabled:   false,
    nagadNumber:    '',
    bankEnabled:    false,
    bankName:       '',
    bankAccount:    '',
    bankBranch:     ''
  };

  shipping = {
    freeShippingEnabled:   true,
    freeShippingThreshold: 1000,
    defaultShippingFee:    60,
    expressFee:            120,
    dhakaFee:              50,
    outsideDhakaFee:       100
  };

  notification = {
    emailOnNewOrder:     true,
    emailOnOrderStatus:  true,
    emailOnLowStock:     true,
    lowStockThreshold:   5,
    smsOnNewOrder:       false,
    smsNumber:           ''
  };

  private readonly API = `${environment.apiUrl}/settings`;

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadSettings();
  }

  loadSettings(): void {
    this.isLoading = true;
    this.http.get<any>(`${this.API}/all`).subscribe({
      next: (res) => {
        if (res?.site)         this.site         = { ...this.site,         ...res.site         };
        if (res?.profile) {
          this.profile = { ...this.profile, ...res.profile };
          this.profilePhotoPreview = this.profilePhotoUrl(this.profile.profileImage);
        }
        if (res?.payment)      this.payment      = { ...this.payment,      ...res.payment      };
        if (res?.shipping)     this.shipping     = { ...this.shipping,     ...res.shipping     };
        if (res?.notification) this.notification = { ...this.notification, ...res.notification };
        this.isLoading = false;
      },
      error: () => { this.isLoading = false; }
    });
  }

  setTab(tab: string): void {
    this.activeTab = tab;
    this.clearMsgs();
  }


  saveSite(): void {
    this.saveSection('site', this.site);
  }

  saveProfile(): void {
    if (!this.profile.name || !this.profile.email) {
      this.errorMsg = 'Name and email are required.';
      return;
    }

    if (this.selectedProfilePhoto) {
      this.uploadProfilePhotoAndSave();
      return;
    }

    this.saveSection('profile', this.profile, true);
  }

  onProfilePhotoSelect(event: any): void {
    const file = event.target.files && event.target.files[0];
    if (!file) return;

    if (!file.type.startsWith('image/')) {
      this.errorMsg = 'Please select a valid image file.';
      return;
    }

    this.selectedProfilePhoto = file;
    const reader = new FileReader();
    reader.onload = () => {
      this.profilePhotoPreview = reader.result as string;
    };
    reader.readAsDataURL(file);
  }

  profilePhotoUrl(image: string | null | undefined): string {
    if (!image) return '';
    if (image.startsWith('http') || image.startsWith('data:')) return image;
    return `${environment.apiUrl}/uploads/${image}`;
  }

  adminInitials(): string {
    const name = this.profile.name || 'Admin';
    return name.split(' ').filter(Boolean).slice(0, 2).map(part => part[0]).join('').toUpperCase();
  }

  private uploadProfilePhotoAndSave(): void {
    this.clearMsgs();
    this.isSaving = true;

    const formData = new FormData();
    formData.append('photo', this.selectedProfilePhoto as File);

    this.http.post<any>(`${this.API}/profile-photo`, formData).subscribe({
      next: (res) => {
        this.profile.profileImage = res.profileImage || this.profile.profileImage;
        this.profilePhotoPreview = this.profilePhotoUrl(this.profile.profileImage);
        this.selectedProfilePhoto = null;
        this.saveSection('profile', this.profile, true);
      },
      error: (err) => {
        this.errorMsg = err?.error?.message || 'Profile photo upload failed.';
        this.isSaving = false;
      }
    });
  }

  changePassword(): void {
    this.clearMsgs();
    if (!this.passwordForm.currentPassword) { this.errorMsg = 'Current password is required.'; return; }
    if (this.passwordForm.newPassword.length < 6) { this.errorMsg = 'New password must be at least 6 characters.'; return; }
    if (this.passwordForm.newPassword !== this.passwordForm.confirmPassword) { this.errorMsg = 'Passwords do not match.'; return; }

    this.isSaving = true;
    this.http.post<any>(`${this.API}/change-password`, this.passwordForm).subscribe({
      next: (res) => {
        if (res?.success) {
          this.successMsg = res.message || '✅ Password changed successfully!';
          this.passwordForm = { currentPassword: '', newPassword: '', confirmPassword: '' };
          this.autoHide();
        } else {
          this.errorMsg = res?.message || '❌ Current password is incorrect.';
        }
        this.isSaving = false;
      },
      error: (err) => {
        this.errorMsg = err?.error?.message || '❌ Password change failed.';
        this.isSaving = false;
      }
    });
  }

  savePayment(): void {
    this.saveSection('payment', this.payment);
  }

  saveShipping(): void {
    if (this.shipping.freeShippingThreshold < 0 || this.shipping.defaultShippingFee < 0) {
      this.errorMsg = 'Shipping fees cannot be negative.';
      return;
    }
    this.saveSection('shipping', this.shipping);
  }

  saveNotification(): void {
    this.saveSection('notification', this.notification);
  }

  private saveSection(section: string, data: any, notifyProfileUpdate: boolean = false): void {
    this.clearMsgs();
    this.isSaving = true;
    this.http.post(`${this.API}/save`, { section, data }).subscribe({
      next: () => {
        this.successMsg = '✅ Settings saved successfully!';
        this.isSaving = false;
        this.notifyAdminProfileIfNeeded(notifyProfileUpdate);
        this.autoHide();
      },
      error: () => {
        this.errorMsg = '❌ Failed to save. Please try again.';
        this.isSaving = false;
      }
    });
  }

  private notifyAdminProfileIfNeeded(shouldNotify: boolean): void {
    if (shouldNotify) {
      window.dispatchEvent(new CustomEvent('adminProfileUpdated'));
    }
  }

  private clearMsgs(): void {
    this.successMsg = '';
    this.errorMsg   = '';
  }

  private autoHide(): void {
    setTimeout(() => this.clearMsgs(), 3500);
  }
}
