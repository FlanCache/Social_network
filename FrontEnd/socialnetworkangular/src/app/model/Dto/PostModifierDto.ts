import {PostStatus} from "../PostStatus";

export class PostModifierDto {
  id!:number;
  content!:string;
  postStatus!: PostStatus;
  img!: string
}
