import { Component, OnInit } from '@angular/core';
import { environment } from 'src/environments/environment';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-create-admin',
  templateUrl: './create-admin.component.html',
  styleUrls: ['./create-admin.component.css']
})
export class CreateAdminComponent implements OnInit {

  form!: FormGroup;
  submitted  = false;
  isLoading  = false;
  successMsg = '';
  errorMsg   = '';
  showPass   = false;

  users: any[]    = [];
  loadingUsers    = true;
  deletingId: number | null = null;

  private readonly API = `${environment.apiUrl}/auth`;

  constructor(private fb: FormBuilder, private http: HttpClient) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      name:     ['', [Validators.required, Validators.minLength(2)]],
      email:    ['', [Validators.required, Validators.email]],
      phone:    ['', [Validators.required, Validators.pattern(/^(\+8801|01)[0-9]{9}$/)]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      address:  [''],
      role:     ['admin', Validators.required]
    });
    this.loadUsers();
  }

  get f() { return this.form.controls; }

  loadUsers(): void {
    this.loadingUsers = true;
    this.http.get<any[]>(`${this.API}/users`).subscribe({
      next: (res) => {
        this.users = res.filter(u => u.role !== 'customer' && u.role !== 'vendor');
        this.loadingUsers = false;
      },
      error: () => { this.loadingUsers = false; }
    });
  }

  onSubmit(): void {
    this.submitted = true;
    if (this.form.invalid) return;

    this.isLoading = true;
    this.errorMsg  = '';
    this.successMsg = '';

    this.http.post<any>(`${this.API}/register`, this.form.value).subscribe({
      next: (res) => {
        this.isLoading = false;
        if (res.success) {
          this.successMsg = `✅ ${this.form.value.role === 'admin' ? 'Admin' : 'Staff'} account তৈরি হয়েছে!`;
          this.form.reset({ role: 'admin' });
          this.submitted = false;
          this.loadUsers();
          setTimeout(() => this.successMsg = '', 4000);
        } else {
          this.errorMsg = res.message || 'Account তৈরি হয়নি।';
        }
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMsg  = err.error?.message || 'সমস্যা হয়েছে। আবার চেষ্টা করুন।';
      }
    });
  }

  deleteUser(id: number, name: string): void {
    if (!confirm(`"${name}" কে delete করবেন?`)) return;
    this.deletingId = id;
    this.http.delete<any>(`${this.API}/users/${id}`).subscribe({
      next: (res) => {
        this.deletingId = null;
        if (res?.success === false) {
          this.errorMsg = res.message || 'User delete করা যায়নি।';
          setTimeout(() => this.errorMsg = '', 5000);
          return;
        }
        this.users = this.users.filter(u => u.id !== id);
        this.successMsg = res?.message || 'User deleted successfully.';
        setTimeout(() => this.successMsg = '', 3000);
      },
      error: (err) => {
        this.deletingId = null;
        this.errorMsg = err.error?.message || 'User delete করতে সমস্যা হয়েছে।';
        setTimeout(() => this.errorMsg = '', 5000);
      }
    });
  }

  roleLabel(role: string): string {
    const map: Record<string, string> = { admin: 'Admin', staff: 'Staff', manager: 'Manager' };
    return map[role] ?? role;
  }

  roleColor(role: string): string {
    const map: Record<string, string> = { admin: '#ef4444', staff: '#3b82f6', manager: '#8b5cf6' };
    return map[role] ?? '#888';
  }
}
