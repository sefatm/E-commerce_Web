import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SidebarService {
  private collapsedSubject = new BehaviorSubject<boolean>(false);
  isCollapsed$ = this.collapsedSubject.asObservable();

  toggle(): void {
    this.collapsedSubject.next(!this.collapsedSubject.value);
  }

  setCollapsed(val: boolean): void {
    this.collapsedSubject.next(val);
  }

  get isCollapsed(): boolean {
    return this.collapsedSubject.value;
  }
}
