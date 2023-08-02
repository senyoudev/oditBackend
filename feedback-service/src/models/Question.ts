import { IQuestion } from "../interfaces/Question";
import mongoose, { Model, model } from "mongoose";

const questionSchema = new mongoose.Schema({
  value: {
    type: String,
    require: true,
  },
  options: [
    {
      value: String,
    },
  ],
});

const Question: Model<IQuestion> = model<IQuestion>("Question", questionSchema);

export default Question;
