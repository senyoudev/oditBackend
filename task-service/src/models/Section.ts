import mongoose, { Model } from "mongoose";
import { ISection } from "src/interfaces/Section";
import Task from "./Task";

const sectionSchema = new mongoose.Schema({
  roomId: {
    type: String,
    required: true,
  },
  name: {
    type: String,
    required: true,
  },
  tasks: [
    {
      type: mongoose.Types.ObjectId,
      ref: "Task",
    },
  ],
});

sectionSchema.pre<ISection>("deleteOne", async function (next) {
  const section = this;

  try {
    // Remove all tasks associated with the section
    await Task.deleteMany({ _id: { $in: section.tasks } });
    next();
  } catch (error) {
    next(error);
  }
});

const Section: Model<ISection> = mongoose.model("Section", sectionSchema);

export default Section;
