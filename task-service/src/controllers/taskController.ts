import { pushNotification } from "./../fcm";
import { Request, Response } from "express";
import asyncHandler from "express-async-handler";
import Task from "../models/Task";
import Section from "../models/Section";
import { ITask } from "../interfaces/Task";
import eurekaClient from "../eureka";
import axios from "axios";
import admin from "firebase-admin";

// @desc    Get Project Tasks
// @route   GET /api/v1/user-tasks/:projectId
// @access  Private
const getProjectTasks = asyncHandler(async (req: Request, res: Response) => {
  const { projectId } = req.params;
  const { userId } = req.query;
  const instance = eurekaClient.getInstancesByAppId("ROOM")[0];
  if (instance) {
    const { hostName, port } = instance;

    //data has type boolean
    const { data } = await axios.get(
      `http://${hostName}:${port.$}/api/v1/rooms?projectId=${projectId}&userId=${userId}`
    );
    const tasks = [];
    for (let i = 0; i < data.length; i++) {
      const sections = await Section.find({ roomId: data[i].id }).populate({
        path: "tasks",
      });
      console.log(sections);
      for (let j = 0; j < sections.length; j++) {
        tasks.push(...sections[j].tasks);
      }
    }
    res.json(tasks);
  } else {
    res.status(500).send("Server Error");
  }
});
// @desc    Get User Tasks
// @route   GET /api/v1/user-tasks
// @access  Private
const getUserTasks = asyncHandler(async (req: Request, res: Response) => {
  const { userId } = req.query;
  const projectInstance = eurekaClient.getInstancesByAppId("PROJECT")[0];

  if (projectInstance) {
    const { hostName, port } = projectInstance;

    //data has type boolean
    const { data } = await axios.get(
      `http://${hostName}:${port.$}/api/v1/projects?userId=${userId}`
    );

    const roomInstance = eurekaClient.getInstancesByAppId("ROOM")[0];
    const tasks = [];

    for (let i = 0; i < data.length; i++) {
      const membersId = await axios.get(
        `http://${roomInstance.hostName}:${roomInstance.port.$}/api/v1/room-members/memberId?projectId=${data[i].id}&userId=${userId}`
      );

      const membersIdMapping = membersId.data.map((item: any) => {
        return item.id;
      });

      const sections = await Section.find({}).populate({
        path: "tasks",
        match: { assignedMembers: { $in: [membersIdMapping] } },
      });
      for (let k = 0; k < sections.length; k++) {
        for (let j = 0; j < sections[k].tasks.length; j++) {
          tasks.push({
            ...sections[k].tasks[j]._doc,
            projectName: data[i].title,
          });
        }
      }
    }
    res.send(tasks);
  } else {
    res.status(500).send("Server Error");
  }
});

// @desc    Get a task By Id
// @route   GET /api/v1/tasks/:taskId
// @access  Private
const getTaskById = asyncHandler(async (req: Request, res: Response) => {
  const { taskId } = req.params;

  try {
    const task: ITask | null = await Task.findById(taskId);

    if (!task) {
      res.status(404).send({ error: "Task not found" });
    }

    res.json(task);
  } catch (e) {
    res.status(400).send({ error: "id format not correct" });
  }
});

// @desc    Create a task
// @route   POST /api/v1/tasks/
// @access  Private
const createTask = asyncHandler(async (req: Request, res: Response) => {
  const { sectionId, name, description, startDate, deadline, assignedMembers } =
    req.body;

  const sectionExists = await Section.exists({ _id: sectionId });
  if (!sectionExists) {
    res.status(404).send({ error: "section not found" });
  }

  if (startDate >= deadline) {
    res.status(400).send({ error: "Start date must be before the deadline" });
  }

  const task: ITask = await Task.create({
    sectionId,
    name,
    description,
    startDate,
    deadline,
    assignedMembers,
  });

  //Add task to section
  await Section.findByIdAndUpdate(
    sectionId,
    {
      $push: { tasks: task._id },
    },
    { new: true }
  );

  res.status(201).json(task);
});

// @desc    Update a task
// @route   PUT /api/v1/tasks/:taskId
// @access  Private
const updateTask = asyncHandler(async (req: Request, res: Response) => {
  const { taskId } = req.params;
  const { name, description, startDate, deadline, assignedMembers } = req.body;

  const taskExists = await Task.exists({ _id: taskId });
  if (!taskExists) {
    res.status(404).send({ error: "Task not found" });
  }

  if (startDate >= deadline) {
    res.status(400).send({ error: "Start date must be before the deadline" });
  }

  const updatedTask: ITask | null = await Task.findByIdAndUpdate(
    taskId,
    {
      name,
      description,
      startDate,
      deadline,
      assignedMembers,
    },
    { new: true }
  );

  if (!updatedTask) {
    res.status(404).send({ error: "Task not found" });
  }

  res.status(201).json(updatedTask);
});

// @desc    Delete a task By Id
// @route   DELETE /api/v1/tasks/:taskId
// @access  Private
const deleteTask = asyncHandler(async (req: Request, res: Response) => {
  const { taskId } = req.params;

  const task: ITask | null = await Task.findByIdAndDelete(taskId);

  if (!task) {
    res.status(404).send({ error: "Task not found" });
  } else {
    //Remove task from section
    await Section.findByIdAndUpdate(
      task?.sectionId,
      {
        $pull: { tasks: taskId },
      },
      { new: true }
    );
  }

  res.json({ message: "Task deleted" });
});

// @desc    Assign members to a task
// @route   PUT /api/v1/tasks/:taskId/assign
// @access  Private
const assignTask = asyncHandler(async (req: Request, res: Response) => {
  const { taskId } = req.params;
  const { assignedMembers } = req.body;

  const updatedTask: ITask | null = await Task.findByIdAndUpdate(
    taskId,
    { $push: { assignedMembers: { $each: assignedMembers } } },
    { new: true }
  );

  if (!updatedTask) {
    res.status(404).send({ error: "Task not found" });
  }

  res.json(updatedTask);
});
// @desc    Make a task done
// @route   Put /api/v1/tasks/:taskId/done
// @access  Private
const markTaskDone = asyncHandler(async (req: Request, res: Response) => {
  const { taskId } = req.params;
  const { roomId } = req.query;
  const { username } = req.body;
  console.log(roomId);

  const task: ITask | null = await Task.findByIdAndUpdate(taskId, {
    isDone: true,
  });

  if (!task) {
    res.status(404).send({ error: "Task not found" });
  }

  pushNotification(
    `room-${roomId}`,
    "Task Info",
    `task completed by ${username}`
  );

  res.status(201).json("task completed successfully");
});
// @desc    Add comment in task
// @route   Put /api/v1/tasks/:taskId/comment
// @access  Private
const addComment = asyncHandler(async (req: Request, res: Response) => {
  const { taskId } = req.params;
  const { userId } = req.query;
  const { content } = req.body;
  const task: ITask | null = await Task.findByIdAndUpdate(taskId, {
    $push: { comments: { memberId: userId, content } },
  });

  if (!task) {
    res.status(404).send({ error: "Task not found" });
  }

  res.status(201).json(task);
});
// @desc    Add comment in task
// @route   Put /api/v1/tasks/:taskId/comment
// @access  Private
const deleteComment = asyncHandler(async (req: Request, res: Response) => {
  const { taskId } = req.params;
  const { commentId } = req.body;
  const task: ITask | null = await Task.findByIdAndUpdate(
    taskId,
    {
      $pull: { comments: { _id: commentId } },
    },
    { new: true }
  );

  if (!task) {
    res.status(404).send({ error: "Task not found" });
  }
  res.status(201).json(task);
});

export {
  getProjectTasks,
  getUserTasks,
  createTask,
  updateTask,
  getTaskById,
  deleteTask,
  markTaskDone,
  assignTask,
  addComment,
  deleteComment,
};
