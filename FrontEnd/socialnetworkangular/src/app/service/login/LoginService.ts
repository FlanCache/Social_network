import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Post} from "../../model/Post";
import {TokenDto} from "../../model/Dto/tokenDto";

@Injectable({
  providedIn: 'root'
})

export class LoginService {
  constructor(private http: HttpClient) {
  }

  // get user trên token
  getUser() : TokenDto{
    return JSON.parse(<string>localStorage.getItem("user"));
  }
  // get id tren token
  getId() : TokenDto {
    return JSON.parse(<string>localStorage.getItem("id"))
  }
}
