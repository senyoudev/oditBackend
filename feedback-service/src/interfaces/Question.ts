import mongoose from "mongoose";

export interface IQuestion {
  value: string;
  options: Array<{ _id: mongoose.Types.ObjectId; value: string }>;
}
