import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { InventoryService } from 'src/app/core/services/inventory.service';
import { AuthService } from 'src/app/core/services/auth.service';
import { ProductService } from 'src/app/core/services/add-product.service';
import { SellerService } from 'src/app/core/services/seller.service';

@Component({
  selector: 'app-inventory',
  templateUrl: './inventory.component.html',
  styleUrls: ['./inventory.component.css']
})
export class InventoryComponent implements OnInit {

  inventory: any[] = [];
  filteredInventory: any[] = [];
  loading = false;
  searchTerm = '';
  filterStatus = 'all';
  isSellerView = false;

  successMsg = '';
  errorMsg = '';

  constructor(
    private inventoryService: InventoryService,
    private productService: ProductService,
    private sellerService: SellerService,
    private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.isSellerView = this.router.url.startsWith('/seller');
    this.route.queryParams.subscribe(params => {
      this.searchTerm = params['q'] || '';
      this.applyFilters();
    });
    this.isSellerView ? this.loadSellerInventory() : this.loadInventory();
  }

  loadInventory(): void {
    this.loading = true;
    this.inventoryService.getInventory().subscribe({
      next: (res: any) => {
        const rawData = res?.data ?? res;
        this.inventory = this.mapInventory(rawData);
        this.applyFilters();
        this.loading = false;
      },
      error: () => {
        this.errorMsg = 'Inventory load failed. Please try again.';
        this.loading = false;
        setTimeout(() => this.errorMsg = '', 4000);
      }
    });
  }

  loadSellerInventory(): void {
    const user = this.authService.getUser();
    if (!user?.id) {
      this.router.navigate(['/login']);
      return;
    }

    this.loading = true;
    this.sellerService.getByUser(user.id).subscribe({
      next: (sellerRes: any) => {
        const seller = sellerRes?.data ?? sellerRes;
        if (!seller?.id) {
          this.inventory = [];
          this.filteredInventory = [];
          this.errorMsg = 'No seller profile found.';
          this.loading = false;
          return;
        }

        this.productService.getBySeller(seller.id).subscribe({
          next: (res: any) => {
            const rawData = res?.data ?? res;
            this.inventory = this.mapInventory(rawData);
            this.applyFilters();
            this.loading = false;
          },
          error: () => {
            this.errorMsg = 'Seller inventory load failed.';
            this.loading = false;
            setTimeout(() => this.errorMsg = '', 4000);
          }
        });
      },
      error: () => {
        this.errorMsg = 'No seller profile found. Please apply as a seller first.';
        this.loading = false;
      }
    });
  }

  private mapInventory(rawData: any): any[] {
    return (Array.isArray(rawData) ? rawData : []).map((p: any) => ({
      productId: p.id,
      productName: p.name,
      sku: p.sku || '-',
      category: p.category?.name || 'Uncategorized',
      stock: p.stock ?? 0,
      status: p.status,
      price: p.salePrice || p.price || 0
    }));
  }

  applyFilters(): void {
    let result = [...this.inventory];

    if (this.searchTerm.trim()) {
      const term = this.searchTerm.toLowerCase();
      result = result.filter(i =>
        i.productName?.toLowerCase().includes(term) ||
        i.sku?.toLowerCase().includes(term) ||
        i.category?.toLowerCase().includes(term)
      );
    }

    if (this.filterStatus !== 'all') {
      result = result.filter(i => this.getStockClass(i.stock) === this.filterStatus);
    }

    this.filteredInventory = result;
  }

  updateStock(productId: number, newStock: number): void {
    if (isNaN(newStock) || newStock < 0) {
      this.errorMsg = 'Enter a valid stock quantity (0 or more).';
      setTimeout(() => this.errorMsg = '', 3000);
      return;
    }

    this.inventoryService.updateStock(productId, newStock).subscribe({
      next: () => {
        this.successMsg = 'Stock updated successfully!';
        this.loadInventory();
        setTimeout(() => this.successMsg = '', 3000);
      },
      error: () => {
        this.errorMsg = 'Stock update failed. Please try again.';
        setTimeout(() => this.errorMsg = '', 4000);
      }
    });
  }

  getStockStatus(stock: number): string {
    if (!stock || stock <= 0) return 'Out of Stock';
    if (stock < 10) return 'Low Stock';
    return 'In Stock';
  }

  getStockClass(stock: number): string {
    if (!stock || stock <= 0) return 'out';
    if (stock < 10) return 'low-stock';
    return 'in-stock';
  }

  getCountByStatus(status: string): number {
    return this.inventory.filter(i => this.getStockClass(i.stock) === status).length;
  }

  getTotalStockUnits(): number {
    return this.inventory.reduce((total, item) => total + (Number(item.stock) || 0), 0);
  }

  getInventoryValue(): number {
    return this.inventory.reduce((total, item) => {
      const stock = Number(item.stock) || 0;
      const price = Number(item.price) || 0;
      return total + (stock * price);
    }, 0);
  }
}
