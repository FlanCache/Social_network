import {Injectable} from '@angular/core';
import {HttpClient,HttpHeaders} from "@angular/common/http";
import {RegisterPayload} from "./auth/register-payload";
import {map, Observable} from "rxjs";
import {LoginPayload} from "./auth/login-payload";
import jwt_decode from "jwt-decode";
import {JwtAuthResponse} from "./auth/jwt-auth-response";
import { User } from './model/User';
import { GetUserDto } from './model/Dto/GetUserDto';
import { UserService } from './service/user/user.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private url = "http://localhost:8080/api/v1/auth/";
  private apiUrl = 'http://localhost:8080/api/v1/user';
  jwtAuthResponse: JwtAuthResponse
 

  constructor(private httpClient: HttpClient,
                      userService: UserService
  ) {
  }



  register(registerPayload: RegisterPayload): Observable<any> {
    return this.httpClient.post(this.url + "signup", registerPayload)
  }


   jwtBody = {
    sub: '',
    iat: '',
    exp: ''
  }
  jwt={
    token:''
  }
  user: User;
  
  login(loginPayload: LoginPayload): Observable<any> {
    
    return this.httpClient.post<any>(this.url + "login", loginPayload, {responseType: "text" as "json"})
      .pipe(map(data => {
        const jsonString = JSON.parse(data);

        this.jwtBody = jwt_decode(data);
        localStorage.setItem('authenticationToken', jsonString.token);
        localStorage.setItem('username', this.jwtBody.sub);
        
        const token = localStorage.getItem('authenticationToken');
        const headers = new HttpHeaders({
          'Authorization': `Bearer ${token}`
        });
        this.getUserInfo().subscribe(
          data => {
            console.log('User info:', data.data);
            // Lưu thông tin người dùng vào localStorage
            const currentUser=JSON.stringify(data.data);
            localStorage.setItem('currentUser', currentUser);
          },
          error => {
            console.error('Failed to fetch user info:', error);
          }
        );
        return data;
      }));
  }

  getUserInfo(): Observable<GetUserDto> {
    const token = localStorage.getItem('authenticationToken');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
    return this.httpClient.get<GetUserDto>(`${this.apiUrl}/info`,{headers});
  }
  
  logout() {
    const token = this.jwtAuthResponse
    console.log("logout success")
    return this.httpClient.post("http://localhost:8080/api/v1/auth/logout", token)
  }
}
