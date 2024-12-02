import { ChangeDetectorRef, Component, Input } from '@angular/core';
import { User } from '../model/User';
import { Friend } from '../model/Friend';
import { PostDto } from '../model/Dto/PostDto';
import { FriendDto } from '../model/Dto/FriendDto';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { CommentDto } from '../model/Dto/CommentDto';
import { Like } from '../model/Like';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { PostService } from '../service/post/postService';
import { UserService } from '../service/user/user.service';
import { Post } from '../model/Post';
import { forkJoin, map } from 'rxjs';
import { data, error } from 'jquery';
import { GetPostDto } from '../model/Dto/GetPostDto';
import UIkit from 'uikit';
import { UpPostDto } from '../model/Dto/UpPostDto';
import { UserToken } from '../model/UserToken';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent {
  AddFiend(arg0: number, arg1: number, arg2: string, arg3: string) {
    throw new Error('Method not implemented.');
  }
  cancelRequest(arg0: number, arg1: number) {
    throw new Error('Method not implemented.');
  }
  friendRequestDeny(arg0: number, arg1: number) {
    throw new Error('Method not implemented.');
  }
  acceptFiend(arg0: number, arg1: number, arg2: string, arg3: string) {
    throw new Error('Method not implemented.');
  }
  unFriend(arg0: number, arg1: number) {
    throw new Error('Method not implemented.');
  }
  currentUsername = Number(localStorage.getItem("username"));
  user: User;
  postToEdit: any;
  id: number | any;
  postList: PostDto;
  posts: GetPostDto;
  currentUser: User;
  postToDis: Post[];
  postToUp: UpPostDto;
  postForm!: FormGroup;
  currentFriendList: Friend[] = []; // Danh sách bạn bè của currentLogInUserId
  friendStatus: string = 'default'; // Trạng thái bạn bè: 'friend', 'waiting', 'stranger'

  //postForm: FormGroup[]|any;
  editForm: FormGroup[] | any;
  userToken: UserToken | any;
  yoursPost: Post | undefined;
  currentClickUserId: number;
  fileName: string | null = null;
  // @ts-ignore
  loggedInUser: User = JSON.parse(localStorage.getItem("currentUser"));

  currentLogInUserId = this.loggedInUser.id
  currenViewtUser: User;
  friendList: Friend[] = []
  @Input() activeFriendsId: number[] = []
  currentFriendship!: Friend;
  showModal = false;
  errorMessage: string = '';
  selectedAvatar: File | null = null;

  defaultImg = 'http://localhost:8080/default.png'
  constructor(
    private router: Router,
    private http: HttpClient,
    private route: ActivatedRoute,
    private httpClient: HttpClient,
    private cdr: ChangeDetectorRef,
    private postService: PostService,
    private userService: UserService,
    private authService:AuthService
  ) {


  }

  ngOnInit() {
    this.checkTokenAndRedirect();

    this.currentClickUserId = Number(this.route.snapshot.paramMap.get('id'));
    
    this.userService.findUserById(this.currentClickUserId).subscribe((user) => {
      this.currenViewtUser = user.data;
      if (this.currenViewtUser.address == null) {
        this.currenViewtUser.address = "Chưa cập nhật thông tin địa chỉ!"
      }
      // if (this.currenViewtUser.address == null) {
      //   this.currenViewtUser.address = "Người dùng chưa cập nhật sinh nhật!"
      // }
      if (this.currenViewtUser && this.currenViewtUser.avatar) {
        this.currenViewtUser.avatar = `http://localhost:8080/${this.currenViewtUser.avatar.replace(/\\/g, '/')}`;
      } else {
        console.log('No avatar found');
      }
    }, error => {
      console.log('Error when find user clicked by user Id', error);
    });

    // Lắng nghe sự kiện khi chọn ảnh
    const fileInput = document.getElementById('avatar-input') as HTMLInputElement;
    const previewImage = document.getElementById('avatar-preview') as HTMLImageElement;

    fileInput.addEventListener('change', (event: any) => {
      const file = event.target.files[0];
      if (file) {
        this.selectedAvatar = file; // Lưu file avatar vào biến
        const reader = new FileReader();
        reader.onload = () => {
          previewImage.src = reader.result as string; // Hiển thị ảnh xem trước
        };
        reader.readAsDataURL(file);
      }
    });
    //====================================================================================================
    this.postForm = new FormGroup({
      postContent: new FormControl("", Validators.required),
      img: new FormControl,
    })
    //====================================================================================================
    this.editForm = new FormGroup({
      postContent: new FormControl("", Validators.required),
      img: new FormControl,
    })
    //====================================================================================================
    this.showPost();
    this.checkFriendStatus();
    console.log("login:" + this.currentLogInUserId);
    console.log("click:" + this.currentClickUserId);
    // Lắng nghe sự kiện click nút đổi avatar
    const changeButton = document.getElementById('change-avatar-btn');
    changeButton?.addEventListener('click', () => this.updateAvatar());
  }


  checkTokenAndRedirect() {
    console.log("helloooo");

    const token = localStorage.getItem('authenticationToken');
    if (!token) {
      // Nếu không có token, điều hướng về trang login
      this.router.navigateByUrl('/login');
    }
  }
  showPost() {
    this.postService.getNewfeedById(0, 10, this.currentClickUserId).subscribe((data) => {
      const listPosts = data.data.listPosts;
      // Tạo các observable để lấy thông tin người dùng cho mỗi post
      const userObservables = listPosts.map(post => {
        return this.userService.findUserById(post.postUserId).pipe(
          map(user => {
            post.user = user.data;
            if (post.user.avatar) {
              post.user.avatar = `http://localhost:8080/${post.user.avatar.replace(/\\/g, '/')}`;
            }
            if (post.postImages) {
              post.postImages.forEach(image => {
                if (image.imageUrl) {

                  image.imageUrl = `http://localhost:8080/${image.imageUrl.replace(/\\/g, '/')}`;
                  console.log(image.imageUrl);

                }
              });
            }
            
            return post;
          })
        );
      });
      // Dùng forkJoin để chờ tất cả các observable hoàn thành
      forkJoin(userObservables).subscribe(postsWithUserInfo => {
        this.postToDis = postsWithUserInfo;
        console.log(this.postToDis);
        this.cdr.detectChanges();
      });
    }, error => {
      console.error('Error when fetching posts:', error);
    });

  }


  selectedFile: File | null = null;
  onFileSelected(event: any) {
    const fileNameChage = event.target.files[0];
    if (fileNameChage) {
      this.fileName = fileNameChage.name;
    } else {
      this.fileName = null;
    }
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0]; // Lưu file vào biến
      console.log(this.selectedFile); // Log kiểm tra
    }
  }

  post() {
    if (this.postForm.invalid && !this.selectedFile) {
      alert("Post must have content or Image")
      return;
    }
    const formData = new FormData();

    formData.append('postContent', this.postForm.value.postContent);
    if (this.selectedFile) {
      formData.append('file', this.selectedFile)
    }

    const imgTest = this.selectedFile;
    console.log(imgTest);
    console.log(this.postForm.value.img instanceof File);


    const token = localStorage.getItem('authenticationToken');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
    this.http.post('http://localhost:8080/api/v1/post/upPost', formData, { headers })
      .subscribe(response => {
        console.log('Post created successfully', response);
        UIkit.modal('#create-post-modal').hide();
        this.showPost();
      }, error => {
        console.error('Error creating post', error);
      });
  }

  showEdit(id: number) {
    this.id = id; // Lưu ID bài viết
    // Lấy dữ liệu bài viết cần chỉnh sửa từ API
    this.postService.findById(this.id).subscribe((response) => {
      const post = response.data;
      this.postToEdit = post;

      // Cập nhật form với dữ liệu cũ
      this.editForm.patchValue({
        postContent: post.content, // Gán nội dung bài viết vào form
      });

      // Nếu có hình ảnh, gắn vào form (chỉ để hiển thị, không cần gán vào form control)
      if (post.imageUrl) {
        this.editForm.patchValue({ file: post.imageUrl });
      }

      console.log('Post to edit:', this.postToEdit);
    }, error => {
      console.error('Error fetching post to edit:', error);
    });
  }
  edit() {
    if (this.editForm.invalid) {
      console.error('Form is invalid');
      return;
    }

    const formData = new FormData();
    formData.append('postContent', this.editForm.value.postContent);

    // Nếu người dùng chọn file mới, gắn file vào form data
    if (this.selectedFile) {
      formData.append('file', this.selectedFile);
    }

    const token = localStorage.getItem('authenticationToken');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    const postId = this.id; // Lấy ID bài viết cần sửa
    console.log('Updating post with ID:', postId);

    this.http.put(`http://localhost:8080/api/v1/post/editPost/${postId}`, formData, { headers })
      .subscribe(response => {
        console.log('Post updated successfully:', response);
        UIkit.modal('#edit-post-modal').hide();
        this.showPost();
      }, error => {
        console.error('Error updating post:', error);
      });
  }

  removeImage() {
    if (this.postToEdit) {
      this.postToEdit.imageUrl = null; // Xóa URL ảnh hiện tại trong bài viết
      this.selectedFile = null; // Xóa file đã chọn trước đó nếu có
      this.editForm.patchValue({ file: null }); // Đặt trường file trong form thành null
      console.log('Image removed successfully');
    } else {
      console.warn('No post to edit');
    }
  }

  delete(id: number) {
    this.postService.delete(id).subscribe(() => {
      alert("Delete Success")
      location.reload();
    })
  }


  checkFriendStatus(): void {
    this.userService.getFriendship().subscribe({
      next: (friendList) => {
        this.currentFriendList = friendList;

        // Tìm kiếm trong danh sách bạn bè để xác định trạng thái
        const currentFriendship = this.currentFriendList.find((friend: any) =>
          friend.userSenderId === this.currentClickUserId || friend.userReceiverId === this.currentClickUserId
        );

        if (currentFriendship) {
          if (currentFriendship.relationShip === 'FRIEND') {
            this.friendStatus = 'friend'; // Đã là bạn bè
          } else if (currentFriendship.relationShip === 'SENDING') {
            console.log("sender" + this.currentLogInUserId);

            // Xác định ai là người gửi yêu cầu
            if (currentFriendship.userSenderId === this.currentLogInUserId) {
              this.friendStatus = 'waiting'; // Đang chờ người kia chấp nhận
            } else {
              this.friendStatus = 'accept'; // Hiển thị nút "Accept" cho người dùng hiện tại
            }
          }
        } else if (this.currentClickUserId === this.currentLogInUserId) {
          this.friendStatus = 'self'; // Nếu là chính mình
        } else {
          this.friendStatus = 'stranger'; // Nếu không có quan hệ nào
        }

        console.log('Trạng thái bạn bè:', this.friendStatus);
      },
      error: (error) => {
        console.error('Lỗi khi lấy danh sách bạn bè:', error);
        this.friendStatus = 'stranger'; // Nếu không thể lấy danh sách bạn bè, coi như là người lạ
      }
    });
  }


  // Gửi yêu cầu kết bạn
  handleAddFriend(userReceiverId: number): void {
    this.userService.addFriend(userReceiverId).subscribe({
      next: (response) => {
        console.log('Yêu cầu kết bạn đã được gửi!');
        console.log(response);
        this.checkFriendStatus(); // Cập nhật lại trạng thái kết bạn sau khi gửi yêu cầu
      },
      error: (error) => {
        console.log('Không thể gửi yêu cầu kết bạn!');
        console.error(error.error.message);
        this.openErrorModal(error.error.message);
      }
    });
  }
  openErrorModal(errorMessage: string): void {
    this.errorMessage = errorMessage; // Lưu thông báo lỗi vào biến
    this.showModal = true;  // Hiển thị modal
  }

  // Hàm để đóng modal khi người dùng nhấn confirm
  closeErrorModal(): void {
    this.showModal = false; // Ẩn modal
    location.reload(); // Nếu bạn muốn load lại trang
  }
  // Chấp nhận yêu cầu kết bạn
  handleAcceptFriend(userSenderId: number): void {
    this.userService.acceptFriendRequest(userSenderId).subscribe({
      next: (response) => {
        console.log('Yêu cầu kết bạn đã được chấp nhận!');
        console.log(response);
        this.checkFriendStatus(); // Cập nhật lại trạng thái kết bạn sau khi chấp nhận
      },
      error: (error) => {
        console.log('Không thể chấp nhận yêu cầu kết bạn!');
        console.error(error.message);
        this.openErrorModal(error.error.message);
      }
    });
  }

  // Hủy yêu cầu kết bạn
  handleDeclineFriend(userSenderId: number): void {
    this.userService.declineFriendRequest(userSenderId).subscribe({
      next: (response) => {
        console.log('Yêu cầu kết bạn đã bị hủy!');
        console.log(response);
        this.checkFriendStatus(); // Cập nhật lại trạng thái kết bạn sau khi hủy
      },
      error: (error) => {
        console.log('Không thể hủy yêu cầu kết bạn!');
        console.error(error.message);
        this.openErrorModal(error.error.message);
      }
    });
  }


  updateAvatar() {
    if (!this.selectedAvatar) {
      alert('Please select an image to upload.');
      return;
    }

    const formData = new FormData();
    formData.append('file', this.selectedAvatar);   
    // Cập nhật avatar trong localStorage

    const token = localStorage.getItem('authenticationToken');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    this.http.put('http://localhost:8080/api/v1/user/avatarUpdate', formData, { headers })
      .subscribe({
        next: (response) => {
          alert('Avatar updated successfully!');
          location.reload();
        },
        error: (err) => {
          alert('Avatar updated successfully');
          this.authService.getUserInfo().subscribe(
            data => {
              
              // Lưu thông tin người dùng vào localStorage
               this.currentUser=data.data;
              this.currentUser.avatar =data.data.avatar;
              console.log(this.currentUser);
              const currentUserLC=JSON.stringify(this.currentUser);
              localStorage.setItem('currentUser', currentUserLC);
              location.reload();
            },
            error => {
              console.error('Failed to fetch user info:', error);
            }
          );
          
            
        }
      });
  }

  fowardToMainTimeLine(id: number) {
    this.currentClickUserId = id;

    this.router.navigateByUrl("/profile/" + id).then(() =>
      window.location.reload()
    )

  }
}