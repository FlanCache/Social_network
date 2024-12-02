import { User } from "./User";

export class Friend{
  id!: number;
  relationCreateTime!: string | null;
  userReceiverId!: number;            
  userSenderId!: number;               
  relationShip!: 'STRANGER' | 'SENDING' | 'FRIEND'; 
}