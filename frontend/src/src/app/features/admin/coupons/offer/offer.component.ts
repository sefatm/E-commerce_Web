import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Offer } from 'src/app/core/models/offer.model';
import { ProductService } from 'src/app/core/services/add-product.service';
import { OfferService } from 'src/app/core/services/offer.service';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-offer',
  templateUrl: './offer.component.html',
  styleUrls: ['./offer.component.css']
})
export class OfferComponent implements OnInit {

  offerForm!: FormGroup;
  offers: Offer[] = [];
  products: any[] = [];
  categories: any[] = [];
  selectedFile: File | null = null;
  previewUrl: string | null = null;

  isEditing = false;
  editingId: number | null = null;
  successMsg = '';
  errorMsg = '';

  constructor(
    private fb: FormBuilder,
    private offerService: OfferService,
    private productService: ProductService
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.loadOffers();
    this.loadProducts();
    this.loadCategories();
  }

  initForm(): void {
    this.offerForm = this.fb.group({
      title:              ['', Validators.required],
      description:        [''],
      offerType:          ['BANNER', Validators.required],
      discountPercentage: ['', [Validators.required, Validators.min(1), Validators.max(100)]],
      productId:          [''],
      categoryId:         [''],
      startDate:          [''],
      endDate:            [''],
      status:             ['ACTIVE']
    });
  }

  loadOffers(): void {
    this.offerService.getAll().subscribe({
      next: (data) => this.offers = data,
      error: () => {
        this.errorMsg = 'Failed to load offers.';
        setTimeout(() => this.errorMsg = '', 4000);
      }
    });
  }

  
  loadProducts(): void {
    this.productService.getProducts().subscribe({
      next: (data) => this.products = data,
      error: () => console.warn('Products could not be loaded.')
    });
  }

  loadCategories(): void {
    this.productService.getCategories().subscribe({
      next: (data) => this.categories = data,
      error: () => console.warn('Categories could not be loaded.')
    });
  }

  onFileSelect(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      this.selectedFile = input.files[0];
      const reader = new FileReader();
      reader.onload = (e) => this.previewUrl = e.target?.result as string;
      reader.readAsDataURL(this.selectedFile);
    }
  }

  onSubmit(): void {
    if (this.offerForm.invalid) return;

    const val = this.offerForm.value;

    if (!this.isEditing) {
      const formData = new FormData();
      formData.append('title', val.title);
      formData.append('description', val.description || '');
      formData.append('offerType', val.offerType);
   
      formData.append('discountPercentage', val.discountPercentage.toString());
     
      if (val.productId  && val.productId  !== '') formData.append('productId',  val.productId.toString());
      if (val.categoryId && val.categoryId !== '') formData.append('categoryId', val.categoryId.toString());
      if (val.startDate) formData.append('startDate', val.startDate);
      if (val.endDate)   formData.append('endDate',   val.endDate);
      formData.append('status', val.status);
      if (this.selectedFile) formData.append('bannerImage', this.selectedFile);

      this.offerService.create(formData).subscribe({
        next: () => {
          this.successMsg = 'Offer created successfully!';
          this.resetForm();
          this.loadOffers();
        },
        error: () => {
          this.errorMsg = 'Create failed.';
          setTimeout(() => this.errorMsg = '', 4000);
        }
      });

    } else if (this.isEditing && this.editingId !== null) {
      const offerObj: Offer = {
        id:                 this.editingId,
        title:              val.title,
        description:        val.description,
        offerType:          val.offerType,
        discountPercentage: Number(val.discountPercentage),
        productId:          val.productId  ? Number(val.productId)  : undefined,
        categoryId:         val.categoryId ? Number(val.categoryId) : undefined,
        startDate:          val.startDate  || undefined,
        endDate:            val.endDate    || undefined,
        status:             val.status
      };
      this.offerService.update(this.editingId, offerObj).subscribe({
        next: () => {
          this.successMsg = 'Offer updated!';
          this.resetForm();
          this.loadOffers();
        },
        error: () => {
          this.errorMsg = 'Update failed.';
          setTimeout(() => this.errorMsg = '', 4000);
        }
      });
    }
  }

  onEdit(offer: Offer): void {
    this.isEditing = true;
    this.editingId = offer.id!;
    this.offerForm.patchValue(offer);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  onDelete(id: number): void {
    if (!confirm('Delete this offer?')) return;
    this.offerService.delete(id).subscribe({
      next: () => {
        this.successMsg = 'Offer deleted.';
        this.loadOffers();
        setTimeout(() => this.successMsg = '', 3000);
      },
      error: () => {
        this.errorMsg = 'Delete failed.';
        setTimeout(() => this.errorMsg = '', 4000);
      }
    });
  }

  toggleStatus(offer: Offer): void {
    const updated: Offer = { ...offer, status: offer.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE' };
    this.offerService.update(offer.id!, updated).subscribe({
      next: () => this.loadOffers(),
      error: () => {
        this.errorMsg = 'Status update failed.';
        setTimeout(() => this.errorMsg = '', 4000);
      }
    });
  }

  resetForm(): void {
    this.offerForm.reset({ offerType: 'BANNER', status: 'ACTIVE' });
    this.isEditing = false;
    this.editingId = null;
    this.selectedFile = null;
    this.previewUrl = null;
    setTimeout(() => { this.successMsg = ''; this.errorMsg = ''; }, 3000);
  }

  isExpired(endDate?: string): boolean {
    if (!endDate) return false;
    return new Date(endDate) < new Date();
  }

  getImageUrl(filename: string): string {
    return `${environment.apiUrl}/uploads/offers/${filename}`;
  }
}
