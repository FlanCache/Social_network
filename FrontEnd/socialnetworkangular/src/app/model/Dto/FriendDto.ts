import { User } from "../User";

export class FriendDto{
    id              : number;
    source          : User;
    target          : User;
    relationshipType: string;
    friendshipStatus: string;
  }