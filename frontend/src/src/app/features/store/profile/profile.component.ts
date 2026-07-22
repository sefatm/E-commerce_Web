import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/core/services/auth.service';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {

  user: any = null;
  profile = {
    name: '',
    email: '',
    phone: '',
    address: '',
    password: ''
  };

  selectedFile: File | null = null;
  previewUrl = '';
  successMsg = '';
  errorMsg = '';
  isSaving = false;
  showSiteHeader = true;
  cancelRoute = '/';

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    const currentPath = this.router.url.split('?')[0].split('#')[0];
    const isInsidePanel = currentPath.startsWith('/seller') || currentPath.startsWith('/admin');
    this.showSiteHeader = !isInsidePanel;
    this.cancelRoute = currentPath.startsWith('/seller') ? '/seller/dashboard' : '/';

    this.user = this.authService.getUser();
    if (!this.user?.id) {
      this.router.navigate(['/login']);
      return;
    }

    this.patchForm(this.user);
    this.authService.getProfile(this.user.id).subscribe({
      next: (res) => {
        if (res.success && res.user) {
          this.authService.setUser(res.user);
          this.user = res.user;
          this.patchForm(res.user);
        }
      }
    });
  }

  patchForm(user: any): void {
    this.profile = {
      name: user?.name || '',
      email: user?.email || '',
      phone: user?.phone || '',
      address: user?.address || '',
      password: ''
    };
    this.previewUrl = this.photoUrl(user?.profileImage);
  }

  onPhotoSelect(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files && input.files.length ? input.files[0] : null;
    if (!file) return;

    if (!file.type.startsWith('image/')) {
      this.errorMsg = 'Please select an image file.';
      return;
    }

    this.selectedFile = file;
    const reader = new FileReader();
    reader.onload = () => this.previewUrl = reader.result as string;
    reader.readAsDataURL(file);
  }

  saveProfile(): void {
    if (!this.profile.name.trim() || !this.profile.email.trim()) {
      this.errorMsg = 'Name and email are required.';
      return;
    }

    const formData = new FormData();
    formData.append('name', this.profile.name.trim());
    formData.append('email', this.profile.email.trim());
    formData.append('phone', this.profile.phone || '');
    formData.append('address', this.profile.address || '');
    formData.append('password', this.profile.password || '');
    if (this.selectedFile) formData.append('photo', this.selectedFile);

    this.isSaving = true;
    this.errorMsg = '';
    this.successMsg = '';

    this.authService.updateProfile(this.user.id, formData).subscribe({
      next: (res) => {
        this.isSaving = false;
        if (res.success && res.user) {
          this.user = res.user;
          this.selectedFile = null;
          this.profile.password = '';
          this.previewUrl = this.photoUrl(res.user.profileImage);
          this.successMsg = 'Profile updated successfully.';
        } else {
          this.errorMsg = res.message || 'Profile update failed.';
        }
      },
      error: (err) => {
        this.isSaving = false;
        this.errorMsg = err?.error?.message || 'Profile update failed.';
      }
    });
  }

  photoUrl(image: string | null | undefined): string {
    if (!image) return '';
    if (image.startsWith('http')) return image;
    return `${environment.apiUrl}/uploads/${image}`;
  }

  initials(): string {
    const name = this.profile.name || this.user?.name || 'U';
    return name.substring(0, 1).toUpperCase();
  }
}
