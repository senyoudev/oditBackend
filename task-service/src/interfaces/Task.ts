import mongoose from "mongoose";

export interface ITask {
  _id: mongoose.Types.ObjectId;
  sectionId: mongoose.Types.ObjectId;
  name: string;
  description: string;
  startDate: Date;
  deadline: Date;
  isDone: Boolean;
  assignedMembers: number[];
}
