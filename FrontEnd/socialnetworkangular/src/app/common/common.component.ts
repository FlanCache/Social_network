import { Component } from '@angular/core';
import { AuthService } from "../auth.service";
import { ActivatedRoute, Router } from "@angular/router";
import { User } from '../model/User';
import { UserService } from '../service/user/user.service';
import { debounceTime, distinctUntilChanged, filter, map, switchMap } from 'rxjs';

@Component({
  selector: 'app-common',
  templateUrl: './common.component.html',
  styleUrls: ['./common.component.css']
})
export class CommonComponent {
  constructor(private authService: AuthService,
    private router: Router,
    private userService: UserService,
    private aroute: ActivatedRoute
  ) {
    this.router.events.subscribe(() => {
      const currentUrl = this.router.url;
      // Kiểm tra nếu URL là login hoặc signup
      this.isProfilePage = currentUrl.includes('/profile');
    });
  }
  currentClickUserId: number;
  searchTerm: string = '';
  searchResults: any[] = [];
  userSearchResult: User;
  searchQuery: string = '';
  isProfilePage: boolean = false;
  friends: any[] = [];
  loading = true;
  logoutservice() {
    localStorage.clear()
    this.authService.logout()
    this.router.navigateByUrl('')

  }
  currentUser: User;
  ngOnInit() {
    this.searchMess="";
    const currentUserString = localStorage.getItem("currentUser");
    if (currentUserString) {
      this.currentUser = JSON.parse(currentUserString);

      if (this.currentUser && this.currentUser.avatar) {
        console.log(this.currentUser.avatar);
        this.currentUser.avatar = `http://localhost:8080/${this.currentUser.avatar.replace(/\\/g, '/')}`;
      } else {
        console.log('No avatar found');
      }
    }


  }
  onSearchInput(event: Event): void {
    this.searchMess = ""; // Cập nhật giá trị searchMess
  }
  loadFriendList(): void {
    this.searchMess="";
    this.loading = true; // Bật trạng thái loading

    // Lấy danh sách bạn bè (bao gồm thông tin quan hệ)
    this.userService.getFriendship().subscribe(friendList => {
      console.log(friendList); // Kiểm tra dữ liệu trả về từ backend

      // Tạo danh sách các Promise để lấy thông tin user
      const friendRequests = friendList.map((friendship: any) => {
        const friendId =
          friendship.userSenderId === this.currentUser.id
            ? friendship.userReceiverId
            : friendship.userSenderId;

        // Lấy thông tin user dựa trên friendId
        return this.userService.findUserById(friendId).toPromise().then(userDetails => {
          const user = userDetails.data;

          // Xử lý URL avatar để hiển thị chính xác
          if (user.avatar) {
            user.avatar = `http://localhost:8080/${user.avatar.replace(/\\/g, '/')}`;
          }

          // Phân loại trạng thái "sending"
          let relationshipStatus = friendship.relationShip;
          if (friendship.relationShip === 'SENDING') {
            relationshipStatus =
              friendship.userSenderId === this.currentUser.id
                ? 'YOU_SENT_REQUEST' // Bạn đã gửi yêu cầu
                : 'YOU_RECEIVED_REQUEST'; // Bạn nhận được yêu cầu
          }

          return {
            user: user, // Thông tin user sau khi xử lý avatar
            relationship: relationshipStatus // Trạng thái quan hệ đã phân loại
          };
        });
      });

      // Chờ tất cả các Promise hoàn thành
      Promise.all(friendRequests).then(friends => {
        this.friends = friends; // Cập nhật danh sách bạn bè
        this.loading = false; // Tắt trạng thái loading
      });
    });
  }


  searchMess: string = "";
  searchUser(): void {
    this.searchMess="";
    const userEmail = this.searchQuery.trim();
    if (userEmail !== '') {
      // Tiến hành gọi API nếu có email hợp lệ
      this.userService.findUserByEmail(userEmail).subscribe({

        next: (response) => {
          console.log(response);

          if (response.message !== "User not found") {
            // Cập nhật kết quả tìm kiếm
            this.userSearchResult = response.data
            this.userSearchResult.avatar = `http://localhost:8080/${response.data.avatar.replace(/\\/g, '/')}`
          } else {
            // Không có người dùng, làm trống kết quả
            this.searchMess = "Không tìm thấy người dùng"
          }
        },
        error: (err) => {
          console.error('Lỗi khi tìm kiếm người dùng:', err);
        }
      });
    }
  }



  fowardToMainTimeLine(id: number) {
    console.log(this.isProfilePage);

    if (id) {
      this.router.navigateByUrl(`/profile/${id}`).then(() =>
        window.location.reload()
      );
    } else {
      console.error('Invalid user ID:', id);
      this.currentClickUserId = id;
      //this.router.navigateByUrl(`/profile/${id}`);
    }

  }
}
