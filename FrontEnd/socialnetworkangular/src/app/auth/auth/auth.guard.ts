import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(private router: Router) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    // Kiểm tra token (hoặc session) trong localStorage hoặc service của bạn
    const token = localStorage.getItem('authenticationToken');
    
    if (token) {
      return true; // Cho phép vào trang
    } else {
      // Nếu không có token, chuyển hướng về trang login
      this.router.navigate(['/login']);
      return false; // Không cho phép vào trang
    }
  }
  
}
