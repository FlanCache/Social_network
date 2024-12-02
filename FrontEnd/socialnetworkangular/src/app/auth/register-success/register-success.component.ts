import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";

@Component({
  selector: 'app-register-success',
  templateUrl: './register-success.component.html',
  styleUrls: ['./register-success.component.css']
})
export class RegisterSuccessComponent{
  constructor(private router: Router) {
  }

  ngOnInit() {
    setTimeout(()=>{
      this.router.navigateByUrl('/login');
    },3000)//3s
  }
}
