import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { PostService } from "../service/post/postService";
import { FormControl, FormGroup, Validators } from "@angular/forms";
import { UserToken } from "../model/UserToken";
import { Post } from "../model/Post";
import { ActivatedRoute, Router } from "@angular/router";
import { PostStatus } from "../model/PostStatus";
import UIkit from 'uikit';
import { User } from '../model/User';
import { data, error } from 'jquery';
import { UserService } from '../service/user/user.service';
import { catchError, forkJoin, Observable, of } from 'rxjs';
import { PostDto } from '../model/Dto/PostDto';
import { GetPostDto } from '../model/Dto/GetPostDto';
import { map } from 'rxjs/operators';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { UpPostDto } from '../model/Dto/UpPostDto';
@Component({
  selector: 'app-feed',
  templateUrl: './feed.component.html',
  styleUrls: ['./feed.component.css']
})
export class FeedComponent {
  user: User;
  postToEdit: any;
  id: number | any;
  postList: PostDto;
  posts: GetPostDto;
  currentUser: User;
  postToDis: Post[];
  postToUp: UpPostDto;
  postForm!: FormGroup;
  //postForm: FormGroup[]|any;
  editForm: FormGroup[] | any;
  userToken: UserToken | any;
  yoursPost: Post | undefined;
  currentClickUserId: number;
  fileName: string | null = null;


  constructor(
    private router: Router,
    private http: HttpClient,
    private route: ActivatedRoute,
    private httpClient: HttpClient,
    private cdr: ChangeDetectorRef,
    private postService: PostService,
    private userService: UserService,
  ) {
  }
  ngOnInit() {

    // @ts-ignore
    //lấy id của user đang được click vào
    this.currentClickUserId = +this.route.snapshot.paramMap.get('id');

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
    this.postForm = new FormGroup({
      postContent: new FormControl("", Validators.required),
      img: new FormControl,
    })
    this.editForm = new FormGroup({
      postContent: new FormControl("", Validators.required),
      img: new FormControl,
    })

    this.showPost();
  }


  // showPost() {
  //   this.checkTokenAndRedirect();
  //   this.postService.getByNewFeed(0, 5).subscribe((data) => {
  //     const listPosts = data.data.listPosts;
  //     // Tạo các observable để lấy thông tin người dùng cho mỗi post
  //     const userObservables = listPosts.map(post => {
  //       return this.userService.findUserById(post.postUserId).pipe(
  //         map(user => {
  //           post.user = user.data;
  //           if (post.user.avatar) {
  //             post.user.avatar = `http://localhost:8080/${post.user.avatar.replace(/\\/g, '/')}`;
  //           }
  //           if (post.postImages) {
  //             post.postImages.forEach(image => {
  //               if (image.imageUrl) {
  //                 image.imageUrl = `http://localhost:8080/${image.imageUrl.replace(/\\/g, '/')}`;
  //               }
  //             });
  //           }
  //           return post;
  //         })
  //       );
  //     });
  //     // Dùng forkJoin để chờ tất cả các observable hoàn thành
  //     forkJoin(userObservables).subscribe(postsWithUserInfo => {
  //       this.postToDis = postsWithUserInfo;
  //       this.cdr.detectChanges();
  //     });
  //   }, error => {
  //     console.error('Error when fetching posts:', error);
  //   });
  // }

  showPost() {

    // Lấy danh sách bài viết
    this.postService.getByNewFeed(0, 5).subscribe((data) => {
      const listPosts = data.data.listPosts;

      // Tạo các observable để lấy thông tin người dùng và trạng thái like cho mỗi post
      const userAndLikeObservables = listPosts.map(post => {
        const userObservable = this.userService.findUserById(post.postUserId).pipe(
          map(user => {
            post.user = user.data;

            if (post.user.avatar) {
              post.user.avatar = `http://localhost:8080/${post.user.avatar.replace(/\\/g, '/')}`;
            }

            // Kiểm tra xem người dùng đã like bài viết hay chưa
            this.postService.hasUserLikedPost(post.postId, this.currentUser.id).subscribe(liked => {
              post.liked = liked;  // Cập nhật trạng thái liked cho bài viết
            });

            // Cập nhật đường dẫn hình ảnh bài viết nếu có
            if (post.postImages) {
              post.postImages.forEach(image => {
                if (image.imageUrl) {
                  image.imageUrl = `http://localhost:8080/${image.imageUrl.replace(/\\/g, '/')}`;
                }
              });
            }

            return post;
          })
        );

        return userObservable;
      });

      // Dùng forkJoin để chờ tất cả các observable hoàn thành
      forkJoin(userAndLikeObservables).subscribe(postsWithUserInfo => {
        this.postToDis = postsWithUserInfo;
        this.cdr.detectChanges();
      });
    }, error => {
      console.error('Error when fetching posts:', error);
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



  delete(id: number) {
    this.postService.delete(id).subscribe(() => {
      alert("Delete Success")
      this.showPost();
    })
  }

  fowardToMainTimeLine(id: number) {

    console.log('Navigating to profile with ID:', id);
    if (id) {
      this.router.navigateByUrl(`/profile/${id}`);
    } else {
      console.error('Invalid user ID:', id);
      this.currentClickUserId = id;
      //this.router.navigateByUrl(`/profile/${id}`);
    }

  }

  // Hàm like bài viết
  likePost(postId: number) {
    // Lấy token từ localStorage
    const token = localStorage.getItem('authenticationToken');
    if (!token) {
      alert('You must be logged in to like a post.');
      return;
    }

    // Cấu hình header với token
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    // Gọi API để like bài viết
    this.http.post(`http://localhost:8080/api/v1/like/${postId}`, {}, { headers })
    .subscribe({
      next: (response) => {
        const post = this.postToDis.find(p => p.postId === postId);
        if (post) {
          // Đảo ngược trạng thái liked và cập nhật số lượng like
          post.liked = !post.liked;
          post.like = post.liked ? post.like + 1 : post.like - 1;
        }
        console.log('Like response:', response);
      },
      error: (err) => {
        console.error('Error while liking the post:', err);
        alert(err.error.message);
        location.reload();
      }
    });
  }







}