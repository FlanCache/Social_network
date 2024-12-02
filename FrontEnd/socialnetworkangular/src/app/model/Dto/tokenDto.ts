import {User} from "../User";

export class TokenDto {
  id!:number;
  token!:string;
  user!:User
}
