import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ReviewService } from 'src/app/services/review.service';

@Component({
  selector: 'app-review',
  templateUrl: './review.component.html',
  styleUrls: ['./review.component.css']
})
export class ReviewComponent implements OnInit {

  reviews: any[] = [];
  filteredReviews: any[] = [];
  loading = false;
  filterRating = 0;
  searchTerm = '';
  sortBy = 'newest';

  constructor(
    private reviewService: ReviewService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.searchTerm = params['q'] || '';
      this.applyFilters();
    });
    this.loadReviews();
  }

  loadReviews(): void {
    this.loading = true;
    this.reviewService.getProductReviews({
      rating: this.filterRating,
      status: 'all',
      page: 1,
      limit: 100
    }).subscribe({
      next: (res: any) => {
        this.reviews = res?.data ?? res;
        this.applyFilters();
        this.loading = false;
      },
      error: (err: any) => { console.error(err); this.loading = false; }
    });
  }

  onFilterRating(rating: number): void {
    this.filterRating = rating;
    this.loadReviews();
  }

  applyFilters(): void {
    let result = [...this.reviews];

    if (this.searchTerm.trim()) {
      const term = this.searchTerm.toLowerCase();
      result = result.filter(r =>
        r.customerName?.toLowerCase().includes(term) ||
        r.productName?.toLowerCase().includes(term) ||
        r.comment?.toLowerCase().includes(term)
      );
    }

    switch (this.sortBy) {
      case 'newest':  result.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()); break;
      case 'oldest':  result.sort((a, b) => new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime()); break;
      case 'highest': result.sort((a, b) => b.rating - a.rating); break;
      case 'lowest':  result.sort((a, b) => a.rating - b.rating); break;
    }

    this.filteredReviews = result;
  }

  deleteReview(id: string): void {
    if (!confirm('এই review টি delete করবেন?')) return;
    this.reviewService.deleteReview(id).subscribe({
      next: () => this.loadReviews(),
      error: (err: any) => console.error(err)
    });
  }

  getStarsArray(count: number): number[] {
    return Array(Math.max(0, count)).fill(0);
  }
}
