import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { SidebarService } from 'src/app/core/services/sidebar.service';

@Component({
  selector: 'app-main-layout',
  templateUrl: './main-layout.component.html',
  styleUrls: ['./main-layout.component.css']
})
export class MainLayoutComponent implements OnInit, OnDestroy {

  sidebarCollapsed = false;
  private sub!: Subscription;

  constructor(private sidebarService: SidebarService) {}

  ngOnInit(): void {
    this.sub = this.sidebarService.isCollapsed$.subscribe(val => {
      this.sidebarCollapsed = val;
    });
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

}
