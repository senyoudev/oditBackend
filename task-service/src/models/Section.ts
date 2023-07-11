import mongoose, { Model } from "mongoose";
import { ISection } from "src/interfaces/Section";

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

const Section: Model<ISection> = mongoose.model("Section", sectionSchema);

export default Section;
