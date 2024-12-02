export class CommentDto{
    id :number;
    userId :number;
    content :string;
    postId :number;
    profile :string;
    firstName  :string;
    middleName :string;
    lastName   :string;
    createdAt:Date
  
  
    constructor(id: number, userId: number, content: string, postId: number, profile: string, firstName: string, middleName: string, lastName: string, createdAt: Date) {
      this.id = id;
      this.userId = userId;
      this.content = content;
      this.postId = postId;
      this.profile = profile;
      this.firstName = firstName;
      this.middleName = middleName;
      this.lastName = lastName;
      this.createdAt = createdAt;
    }
  }