import { Component } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, FormGroupName } from "@angular/forms";
import { LoginPayload } from "../login-payload";
import { AuthService } from "../../auth.service";
import { Router } from "@angular/router";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  loginForm: FormGroup
  loginPayload: LoginPayload
  loginError: boolean = false;  // Biến theo dõi lỗi
  errorMessage: string;
  constructor(private authService: AuthService, private router: Router) {
    this.loginForm = new FormGroup({
      email: new FormControl,
      password: new FormControl
    }),
      this.loginPayload = {
        email: '',
        password: ''
      }
  }
  login() {
    this.loginPayload.email = this.loginForm.get("email")?.value;
    this.loginPayload.password = this.loginForm.get("password")?.value;
    
    this.authService.login(this.loginPayload).subscribe(data => {
      console.log('login success');
      this.loginError = false;
      this.authService.getUserInfo().subscribe(
        data => {
          console.log('User info:', data.data);
          // Lưu thông tin người dùng vào localStorage
          const currentUser=JSON.stringify(data.data);
          localStorage.setItem('currentUser', currentUser);
          this.router.navigateByUrl("/feed").then(()=>
            window.location.reload()
        )
        },
        error => {
          console.error('Failed to fetch user info:', error);
        }
      );
     
    }, error => {
      
        // Parse chuỗi JSON trong error.error thành đối tượng
        const errorResponse = JSON.parse(error.error);
        console.log(errorResponse);

        // Kiểm tra và lấy message từ đối tượng đã parse
        if (errorResponse.message) {
          this.loginError = true;
          this.errorMessage = errorResponse.message;  // Lấy message từ backend
        } else {
          this.errorMessage = 'An unknown error occurred.';
        }
    
      this.loginError = true;
    })
  }
}
