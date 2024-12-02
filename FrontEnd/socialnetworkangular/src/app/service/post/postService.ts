import { Injectable } from "@angular/core";
import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { Observable } from "rxjs";
import { Post } from "../../model/Post";
import { GetPostDto } from "src/app/model/Dto/GetPostDto";
import { UpPostDto } from "src/app/model/Dto/UpPostDto";

@Injectable({
  providedIn: 'root'
})
export class PostService {
  private apiUrl = 'http://localhost:8080/api/v1/post';
  private apiLikeUrl = 'http://localhost:8080/api/v1/like';
  constructor(private HttpClient: HttpClient) { }

  findAllByUser_Id(id: number): Observable<Post[]> {
    return this.HttpClient.get<Post[]>('http://localhost:8080/post/' + id)
  }

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('authenticationToken');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }

  save(): Observable<any> {
    const headers = this.getHeaders();
    return this.HttpClient.post<any>(`${this.apiUrl}/upPost`, { headers });
  }

  delete(id: number): Observable<any> {
    const headers = this.getHeaders();
    return this.HttpClient.delete(`${this.apiUrl}/deletePost/${id}`, { headers });
  }

  findById(id: number): Observable<any> {
    const headers = this.getHeaders();
    return this.HttpClient.get<Post>(`${this.apiUrl}/${id}`, { headers });
  }

  getByNewFeed(page: number, size: number): Observable<GetPostDto> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
      const headers = this.getHeaders();
    return this.HttpClient.get<GetPostDto>(`${this.apiUrl}/timeLine`, { params, headers });
  }
  getNewfeedById(page: number, size: number, userId: number): Observable<GetPostDto> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
      const headers = this.getHeaders();
    return this.HttpClient.get<GetPostDto>(`${this.apiUrl}/selfTimeLine/${userId}`, { params, headers });
  }

  listUserLikedPost(postId: number): Observable<boolean> {
    const headers = this.getHeaders();
    return this.HttpClient.get<boolean>(`${this.apiLikeUrl}/users/${postId}`, { headers });
  }
  
  hasUserLikedPost(postId: number, userId: number): Observable<boolean> {
    const headers = this.getHeaders();
    return this.HttpClient.get<boolean>(`http://localhost:8080/api/v1/like/liked/${postId}/${userId}`, { headers });
  }

}
