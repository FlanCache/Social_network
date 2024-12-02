import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-container',
  templateUrl: './container.component.html',
  styleUrls: ['./container.component.css'],

})
export class ContainerComponent {
  isLoginOrSignupPage = false;
  constructor(private router: Router) {
    this.router.events.subscribe(() => {
      const currentUrl = this.router.url;
      // Kiểm tra nếu URL là login hoặc signup
      this.isLoginOrSignupPage = currentUrl.includes('/login') || currentUrl.includes('/register');
    });
  }
}