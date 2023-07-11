import mongoose, { Model, model } from "mongoose";
import { ITask } from "../interfaces/Task";

const taskSchema = new mongoose.Schema({
  sectionId: {
    type: mongoose.Types.ObjectId,
    required: true,
  },
  name: {
    type: String,
    require: true,
  },
  description: {
    type: String,
    require: true,
  },
  startDate: {
    type: Date,
    required: true,
  },
  deadline: {
    type: Date,
    required: true,
  },
  isDone:{
    type:Boolean,
    default:false
  },
  assignedMembers: {
    type:[Number],
    default:[]
  },
});

const Task: Model<ITask> = model<ITask>('Task', taskSchema);


export default Task;
