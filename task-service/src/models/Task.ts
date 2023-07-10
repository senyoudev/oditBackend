import mongoose from "mongoose";

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
  assignedMembers: [Number],
});

export default taskSchema;
