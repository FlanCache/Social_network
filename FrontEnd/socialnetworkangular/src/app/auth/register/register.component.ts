import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from "@angular/forms";
import { RegisterPayload } from "../register-payload";
import { AuthService } from "../../auth.service";
import { Router } from "@angular/router";

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {
  registerForm: FormGroup
  registerPayload: RegisterPayload
  signupErr: boolean;
  errorSignupMessage: string;
  constructor(private authService: AuthService, private router: Router) {
    this.registerForm = new FormGroup({
      fullName: new FormControl(),
      //dateOfBirth: new FormControl(),
      //mobile: new FormControl(),
      email: new FormControl(),
      //username: new FormControl(),
      password: new FormControl(),
      confirmPassword: new FormControl()
    }),
      this.registerPayload = {

        //dateOfBirth: new Date(),
        email: '',
        password: '',
        fullName: ''

      }
  }

  ngOnInit(): void {
  }


  signup() {
    //this.registerPayload.dateOfBirth = this.registerForm.get("dateOfBirth")?.value;
    this.registerPayload.email = this.registerForm.get("email")?.value;
    //this.registerPayload.mobile = this.registerForm.get("mobile")?.value;
    //this.registerPayload.username = this.registerForm.get("username")?.value;
    this.registerPayload.password = this.registerForm.get("password")?.value;
    this.registerPayload.fullName = this.registerForm.get("fullName")?.value;
    if (this.registerForm.get("password")?.value != this.registerForm.get("confirmPassword")?.value) {
      this.signupErr = true;
      this.errorSignupMessage = 'Password confirm not match';
    }
    else {
      this.authService.register(this.registerPayload).subscribe(data => {
        console.log('registed success'),
          console.log(this.registerPayload);
        this.signupErr = false;

         this.router.navigateByUrl('/register-success')
      }, error => {
        // Parse chuỗi JSON trong error.error thành đối tượng
        const errorResponse = error// JSON.parse(error.error);
        console.log(errorResponse);

        // Kiểm tra và lấy message từ đối tượng đã parse
        if (errorResponse.message) {
          this.signupErr = true;
          this.errorSignupMessage = errorResponse.error.message;  // Lấy message từ backend
        } else {
          this.errorSignupMessage = 'An unknown error occurred.';
        }

        this.signupErr = true;
      })
    }
  }

}
