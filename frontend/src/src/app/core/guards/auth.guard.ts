import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { AuthService } from 'src/app/core/services/auth.service';

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean | UrlTree {
    if (!this.authService.isLoggedIn()) {
      const returnUrl = state.url || '/';
      return this.router.createUrlTree(['/login'], { queryParams: { returnUrl } });
    }

    const roles = route.data?.['roles'] as string[] | undefined;
    if (!roles?.length || this.authService.hasAnyRole(roles)) {
      return true;
    }

    return this.router.createUrlTree([this.authService.getHomeRouteForCurrentUser()]);
  }
}

@Injectable({ providedIn: 'root' })
export class AdminGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(): boolean | UrlTree {
    if (this.authService.isAdmin()) return true;
    if (!this.authService.isLoggedIn()) return this.router.createUrlTree(['/login']);
    return this.router.createUrlTree([this.authService.getHomeRouteForCurrentUser()]);
  }
}
