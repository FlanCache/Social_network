import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { GetUserDto } from 'src/app/model/Dto/GetUserDto';
import { User } from 'src/app/model/User';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = 'http://localhost:8080/api/v1/user';
  private friendUrl = 'http://localhost:8080/api/v1/friends'; 


  constructor(
    private httpClient: HttpClient,
  ) { }

  //Tìm user bằng id
  findUserById(id: number): Observable<any> {
    const headers = this.getHeaders();
    return this.httpClient.get<any>(`${this.apiUrl}/findUserById/` + id, { headers })
  }
  //Tìm user bằng email
  findUserByEmail(userEmail: string): Observable<any> {
    const headers = this.getHeaders();
    return this.httpClient.get<any>(`${this.apiUrl}/findUserByEmail/` + userEmail, { headers })
  }
  // getByNewFeed(page:number,size:number):Observable<GetPostDto>{
  //   const token = localStorage.getItem('authenticationToken');
  //   const headers = new HttpHeaders({
  //     'Authorization': `Bearer ${token}`,
  //     'Content-Type': 'application/json'
  //   });
  //   return this.HttpClient.get<GetPostDto>(`${this.apiUrl}/timeLine`,{params,headers});
  // }

  
  // Lấy token từ localStorage
  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('authenticationToken');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }

  // Lấy danh sách ID bạn bè của user
  getFriendListId(userId: number): Observable<number[]> {
    const headers = this.getHeaders();
    return this.httpClient.get<number[]>(`${this.friendUrl}/FriendListId/${userId}`, { headers });
  }
  getUserInfo(): Observable<GetUserDto> {
    const headers = this.getHeaders();
    return this.httpClient.get<GetUserDto>(`${this.apiUrl}/info`, { headers });
  }
  // Lấy danh sách yêu cầu kết bạn (friendship)
  getFriendship(): Observable<any[]> {
    const headers = this.getHeaders();
    return this.httpClient.get<any[]>(`${this.friendUrl}/friendship`, { headers });
  }

  // Gửi yêu cầu kết bạn
  addFriend(userReceiverId: number): Observable<any> {
    console.log("rev" + userReceiverId);

    const headers = this.getHeaders();
    return this.httpClient.post(`${this.friendUrl}/addFriend/${userReceiverId}`, {}, { headers });
  }

  // Chấp nhận yêu cầu kết bạn
  acceptFriendRequest(userSenderId: number): Observable<any> {
    const headers = this.getHeaders();
    return this.httpClient.put(`${this.friendUrl}/acceptFriend/${userSenderId}`, {}, { headers });
  }

  // Hủy yêu cầu kết bạn
  declineFriendRequest(userSenderId: number): Observable<any> {
    const headers = this.getHeaders();
    return this.httpClient.put(`${this.friendUrl}/declineFriend/${userSenderId}`, {}, { headers });
  }
}
