import mongoose from "mongoose";

export interface ITask {
  _id: mongoose.Types.ObjectId;
  sectionId: mongoose.Types.ObjectId;
  name: string;
  description: string;
  startDate: Date;
  deadline: Date;
  isDone: boolean;
  assignedMembers: number[];
  comments: Array<{ _id:mongoose.Types.ObjectId,memberId: number; content: string }>;
}
