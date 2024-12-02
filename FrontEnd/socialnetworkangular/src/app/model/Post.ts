import {User} from "./User";
import {PostStatus} from "./PostStatus";
import { Image } from "./Image";
import { Comment } from "./Comment";
import { Like } from "./Like";

export class Post{
  postId: number;
  postUserId: number;
  content: string;
  postImages:Image[];
  postComments: Comment[];
  like: number;
  liked:boolean;
  postCreateTime:string;
  postDeleteFlag:number;
  user:User;
}
