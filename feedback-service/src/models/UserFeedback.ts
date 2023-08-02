import { IUserFeedback } from "../interfaces/UserFeedback";
import mongoose, { Model, model } from "mongoose";

const userFeedbackSchema = new mongoose.Schema({
  userId: {
    type: Number,
    require: true,
  },
  questionId: {
    type: mongoose.Types.ObjectId,
    require: true,
  },
  optionId: {
    type: mongoose.Types.ObjectId,
    require: true,
  },
});

const UserFeedback: Model<IUserFeedback> = model<IUserFeedback>(
  "UserFeedback",
  userFeedbackSchema
);

export default UserFeedback;
