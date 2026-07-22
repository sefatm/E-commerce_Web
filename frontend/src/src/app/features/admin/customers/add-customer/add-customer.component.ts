import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { CustomerService } from 'src/app/core/services/add-customer.service';


@Component({
  selector: 'app-add-customer',
  templateUrl: './add-customer.component.html',
  styleUrls: ['./add-customer.component.css']
})
export class AddCustomerComponent implements OnInit {

  customerForm!: FormGroup;
  submitted = false;
  isLoading = false;
  successMsg = '';
  errorMsg = '';

  districts: string[] = [
    'Dhaka', 'Chattogram', 'Khulna', 'Rajshahi',
    'Sylhet', 'Barishal', 'Rangpur', 'Mymensingh'
  ];

  allUpazilas: any = {
    Dhaka:      ['Dhamrai', 'Dohar', 'Keraniganj', 'Nawabganj', 'Savar'],
    Chattogram: ['Anwara', 'Boalkhali', 'Chandanaish', 'Fatikchhari', 'Hathazari'],
    Khulna:     ['Dumuria', 'Dacope', 'Batiaghata', 'Dighalia', 'Koyra'],
    Rajshahi:   ['Bagha', 'Bagmara', 'Charghat', 'Durgapur', 'Godagari'],
    Sylhet:     ['Balaganj', 'Beanibazar', 'Bishwanath', 'Companiganj', 'Fenchuganj'],
    Barishal:   ['Agailjhara', 'Bakerganj', 'Babuganj', 'Banaripara', 'Gaurnadi'],
    Rangpur:    ['Badarganj', 'Gangachara', 'Kaunia', 'Mithapukur', 'Pirgachha'],
    Mymensingh: ['Bhaluka', 'Dhobaura', 'Fulbaria', 'Gaffargaon', 'Gauripur']
  };

  upazilas: string[] = [];

  constructor(
    private fb: FormBuilder,
    private customerService: CustomerService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.customerForm = this.fb.group({
      name:       ['', Validators.required],
      email:      ['', [Validators.required, Validators.email]],
      phone:      ['', [Validators.required, Validators.pattern(/^(\+8801|01)[0-9]{9}$/)]],
      password:   ['', [Validators.required, Validators.minLength(6)]],
      address:    ['', Validators.required],
      district:   ['', Validators.required],
      upazila:    ['', Validators.required],
      postalCode: [''],
      type:       ['regular'],
      status:     ['active']
    });

    this.customerForm.get('district')?.valueChanges.subscribe(district => {
      this.upazilas = this.allUpazilas[district] || [];
      this.customerForm.get('upazila')?.setValue('');
    });
  }

  get f() { return this.customerForm.controls; }

  onSubmit(): void {
    this.successMsg = '';
    this.errorMsg = '';
    this.submitted = true;
    if (this.customerForm.invalid) {
      this.errorMsg = 'Please fill in all required customer information correctly.';
      this.scrollToTop();
      return;
    }

    this.isLoading = true;
    this.customerService.createCustomer(this.customerForm.value).subscribe({
      next: () => {
        this.isLoading = false;
        this.successMsg = '✅ Customer added successfully!';
        this.scrollToTop();
        this.submitted = false;
        this.customerForm.reset({ type: 'regular', status: 'active' });
      
        setTimeout(() => this.router.navigate(['/admin/customers/list']), 1500);
      },
      error: () => {
        this.isLoading = false;
        this.errorMsg = '❌ Failed to add customer. Please try again.';
        this.scrollToTop();
        setTimeout(() => this.errorMsg = '', 4000);
      }
    });
  }

  private scrollToTop(): void {
    setTimeout(() => window.scrollTo({ top: 0, behavior: 'smooth' }), 0);
  }
}
