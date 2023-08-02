import mongoose from "mongoose";

export interface IUserFeedback {
  userId: number;
  questionId: mongoose.Types.ObjectId;
  optionId: mongoose.Types.ObjectId;
}
