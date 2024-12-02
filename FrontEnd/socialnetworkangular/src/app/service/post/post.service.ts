import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Post} from "../../model/Post";

@Injectable({
  providedIn: 'root'
})
export class PostService {

  constructor(private http:HttpClient) { }

  findAllByUser_Id(id:number):Observable<Post[]> {
    return this.http.get<Post[]>('http://localhost:8080/post/'+id)
  }

  save(post:Post):Observable<any> {
    return this.http.post('http://localhost:8080/post',post)
  }

  delete(id : number){
    this.http.put('http://localhost:8080/post/delete/'+id,id)
  }
}
