import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, AbstractControl } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from 'src/app/services/auth.service';


@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {

  registerForm!: FormGroup;
  isLoading  = false;
  successMsg = '';
  errorMsg   = '';
  showPass   = false;
  submitted  = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/']);
    }
  }

  ngOnInit(): void {
    this.registerForm = this.fb.group({
      name:     ['', [Validators.required, Validators.minLength(2)]],
      email:    ['', [Validators.required, Validators.email]],
      phone:    ['', [Validators.required, Validators.pattern(/^(\+8801|01)[0-9]{9}$/)]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      address:  [''],
      role:     ['customer']
    });
  }

  get f() { return this.registerForm.controls; }

  onSubmit(): void {
    this.submitted = true;
    if (this.registerForm.invalid) return;

    this.isLoading = true;
    this.errorMsg  = '';

    this.authService.register(this.registerForm.value).subscribe({
      next: (res) => {
        this.isLoading = false;
        if (res.success) {
          this.successMsg = 'Registration successful! Please login.';
          const returnUrl = this.route.snapshot.queryParamMap.get('returnUrl');
          setTimeout(() => {
            this.router.navigate(['/login'], { queryParams: returnUrl ? { returnUrl } : {} });
          }, 1200);
        } else {
          this.errorMsg = res.message || 'Registration failed.';
        }
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMsg = err.error?.message || 'Registration failed. Please try again.';
      }
    });
  }
}
