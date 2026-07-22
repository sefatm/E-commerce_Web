import {
  Component,
  Output,
  EventEmitter,
  OnInit,
  Input,
  AfterViewInit,
  ElementRef,
  ViewChild
} from '@angular/core';

import { CategoryService } from 'src/app/services/category.service';

@Component({
  selector: 'app-category-nav',
  templateUrl: './category-nav.component.html',
  styleUrls: ['./category-nav.component.css']
})
export class CategoryNavComponent implements OnInit, AfterViewInit {

  categories: any[] = [];
  loadError = false;

  private fallbackCategories = [
    { id: 'vegetables', name: 'Vegetables', icon: '🥬' },
    { id: 'fruits', name: 'Fruits', icon: '🍎' },
    { id: 'rice', name: 'Rice & Grains', icon: '🍚' },
    { id: 'dairy', name: 'Dairy & Eggs', icon: '🥛' },
    { id: 'grocery', name: 'Grocery', icon: '🛒' },
    { id: 'organic', name: 'Organic', icon: '🌿' },
    { id: 'beverages', name: 'Beverages', icon: '🍵' },
    { id: 'home', name: 'Home & Kitchen', icon: '🏡' },
    { id: 'handmade', name: 'Handmade', icon: '🧺' }
  ];

  @Input() activeId: any = 'all';
  @Output() categorySelected = new EventEmitter<any>();

  @ViewChild('scrollContainer', { static: false }) scrollContainer!: ElementRef;

  constructor(private categoryService: CategoryService) {}

  ngOnInit(): void {
    this.loadCategories();
  }

  ngAfterViewInit(): void {
    this.enableDragScroll();
  }

  loadCategories(): void {
    this.loadError = false;
    this.categoryService.getCategories().subscribe({
      next: (res: any) => {
        const data = res?.data ?? res;
        this.categories = Array.isArray(data) && data.length ? data : this.fallbackCategories;
        this.loadError = false;
      },
      error: () => {
        this.categories = this.fallbackCategories;
        this.loadError = false;
      }
    });
  }

  selectCategory(category: any): void {
    this.categorySelected.emit(category);
  }

  getIcon(cat: any): string {
    return cat?.icon || this.iconFromName(cat?.name);
  }

  private iconFromName(name: string = ''): string {
    const key = name.toLowerCase();
    if (key.includes('vegetable')) return '🥬';
    if (key.includes('fruit')) return '🍎';
    if (key.includes('rice') || key.includes('grain')) return '🍚';
    if (key.includes('dairy') || key.includes('egg')) return '🥛';
    if (key.includes('organic')) return '🌿';
    if (key.includes('beverage') || key.includes('tea')) return '🍵';
    if (key.includes('home') || key.includes('kitchen')) return '🏡';
    if (key.includes('hand')) return '🧺';
    return '🌱';
  }

  enableDragScroll(): void {
    setTimeout(() => {
      if (!this.scrollContainer?.nativeElement) return;
      const slider = this.scrollContainer.nativeElement;
      let isDown = false;
      let startX = 0;
      let scrollLeft = 0;

      slider.addEventListener('mousedown', (e: MouseEvent) => {
        isDown = true;
        slider.classList.add('dragging');
        startX = e.pageX - slider.offsetLeft;
        scrollLeft = slider.scrollLeft;
      });

      window.addEventListener('mouseup', () => {
        isDown = false;
        slider.classList.remove('dragging');
      });

      window.addEventListener('mousemove', (e: MouseEvent) => {
        if (!isDown) return;
        e.preventDefault();
        const x = e.pageX - slider.offsetLeft;
        const walk = (x - startX) * 1.5;
        slider.scrollLeft = scrollLeft - walk;
      });
    });
  }
}
