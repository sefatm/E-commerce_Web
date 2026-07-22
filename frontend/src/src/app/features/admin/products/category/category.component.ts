import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CategoryService } from 'src/app/core/services/category.service';

@Component({
  selector: 'app-category',
  templateUrl: './category.component.html',
  styleUrls: ['./category.component.css']
})
export class CategoryComponent implements OnInit {

  categories: any[] = [];
  allCategories: any[] = [];
  showForm = false;
  isLoading = false;
  editingId: string | null = null;
  successMsg = '';
  errorMsg = '';
  searchTerm = '';

  categoryForm = { name: '', description: '', parent: '' };

  private catColors = ['#10b981','#3b82f6','#f59e0b','#a78bfa','#ef4444','#06b6d4','#ec4899'];

  constructor(
    private categoryService: CategoryService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.searchTerm = params['q'] || '';
      this.applySearch();
    });
    this.loadCategories();
  }

  loadCategories(): void {
    this.isLoading = true;
    this.categoryService.getCategories().subscribe({
      next: (res: any) => {
        this.allCategories = res?.data ?? res;
        this.applySearch();
        this.isLoading = false;
      },
      error: () => {
        this.errorMsg = 'Categories load failed.';
        this.isLoading = false;
        setTimeout(() => this.errorMsg = '', 4000);
      }
    });
  }

  toggleForm(): void {
    this.showForm = !this.showForm;
    if (!this.showForm) this.cancelForm();
  }

  applySearch(): void {
    const term = this.searchTerm.trim().toLowerCase();
    const source = [...this.allCategories];
    this.categories = term
      ? source.filter(c =>
          c.name?.toLowerCase().includes(term) ||
          c.description?.toLowerCase().includes(term) ||
          c.parent?.toLowerCase().includes(term)
        )
      : source;
  }

  startEdit(cat: any): void {
    this.editingId = cat._id || cat.id;
    this.categoryForm = { name: cat.name, description: cat.description || '', parent: cat.parent || '' };
    this.showForm = true;
  }

  cancelForm(): void {
    this.showForm = false;
    this.editingId = null;
    this.categoryForm = { name: '', description: '', parent: '' };
  }

  onSubmit(): void {
    this.successMsg = '';
    this.errorMsg = '';
    if (this.editingId) {
      this.categoryService.updateCategory(this.editingId, this.categoryForm).subscribe({
        next: () => {
          this.successMsg = 'Category updated successfully!';
          this.scrollToTop();
          this.loadCategories();
          this.cancelForm();
          setTimeout(() => this.successMsg = '', 3000);
        },
        error: () => {
          this.errorMsg = 'Update failed.';
          this.scrollToTop();
          setTimeout(() => this.errorMsg = '', 4000);
        }
      });
    } else {
      this.categoryService.createCategory(this.categoryForm).subscribe({
        next: () => {
          this.successMsg = 'Category created successfully!';
          this.scrollToTop();
          this.loadCategories();
          this.cancelForm();
          setTimeout(() => this.successMsg = '', 3000);
        },
        error: () => {
          this.errorMsg = 'Create failed.';
          this.scrollToTop();
          setTimeout(() => this.errorMsg = '', 4000);
        }
      });
    }
  }

  deleteCategory(id: string): void {
    if (!confirm('এই category টি delete করবেন?')) return;
    this.categoryService.deleteCategory(id).subscribe({
      next: () => {
        this.successMsg = 'Category deleted.';
        this.scrollToTop();
        this.loadCategories();
        setTimeout(() => this.successMsg = '', 3000);
      },
      error: () => {
        this.errorMsg = 'Delete failed.';
        this.scrollToTop();
        setTimeout(() => this.errorMsg = '', 4000);
      }
    });
  }

  getCatColor(index: number): string {
    return this.catColors[index % this.catColors.length];
  }

  private scrollToTop(): void {
    setTimeout(() => window.scrollTo({ top: 0, behavior: 'smooth' }), 0);
  }
}
