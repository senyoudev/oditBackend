import mongoose from "mongoose";
import { ITask } from "./../../interfaces/Task";
import connectDb, { closeConn } from "../../config/connectDb";
import Task from "../../models/Task";

describe("Task", () => {
  beforeAll(async () => {
    connectDb();
  });

  afterAll(async () => {
    closeConn();
  });

  //Insert
  it("task should inserted", async () => {
    const id = new mongoose.Types.ObjectId("64b0891db1e1594c4538d080");
    const task = await Task.create({
      _id: id,
      sectionId: "64b08735f153aa71c194a67d",
      name: "Task5",
      description: "desc 1",
      startDate: new Date("2023-07-06"),
      deadline: new Date("2023-07-14"),
      assignedMembers: [],
    });

    const instertedTask = await Task.findById(id);
    expect(instertedTask?.equals(task)).toBeTruthy();
  });

  it("task should added to section", async () => {
  });

  //Update
  it("task should existed", async () => {
  });
  it("task should updated", async () => {
  });

  //Delete

  it("task should deleted", async () => {
  });
  it("task should removed from section", async () => {
  });

  //Assign
  it("members should assigned to task", async () => {
  });

  //Make as done
  it("task should be as done", async () => {
  });
});


