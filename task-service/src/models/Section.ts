import mongoose, { Model } from "mongoose";
import {taskSchema} from "./Task";
import { ISection } from "../interfaces/Section";

const sectionSchema = new mongoose.Schema({
  roomId: {
    type: String,
    required: true,
  },
  name: {
    type: String,
    required: true,
  },
  tasks:[taskSchema]
});

const Section = mongoose.model("Section", sectionSchema);


export default Section;
