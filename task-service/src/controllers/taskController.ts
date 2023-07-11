import { Request, Response } from 'express';
import asyncHandler from 'express-async-handler';
import Task from '../models/Task';
import Section from '../models/Section';
import { ITask  } from '../interfaces/Task';


// @desc    Create a task
// @route   POST /api/v1/tasks/
// @access  Private
const createTask = asyncHandler(async (req: Request, res: Response) => {
  const { sectionId, name, description, startDate, deadline, assignedMembers } = req.body;

  
  const sectionExists = await Section.exists({ _id: sectionId });
  if (!sectionExists) {
    res.status(404);
    throw new Error('Section not found');
  }

  if (startDate >= deadline) {
    res.status(400);
    throw new Error('Start date must be before the deadline');
  }

  const task: ITask = await Task.create({
    sectionId,
    name,
    description,
    startDate,
    deadline,
    assignedMembers,
  });


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
    res.status(404);
    throw new Error('Task not found');
  }

  if (startDate >= deadline) {
    res.status(400);
    throw new Error('Start date must be before the deadline');
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
    res.status(404);
    throw new Error('Task not found');
  }

  res.status(201).json(updatedTask);
});
// @desc    Get a task By Id
// @route   GET /api/v1/tasks/:taskId
// @access  Private
const getTaskById = asyncHandler(async (req: Request, res: Response) => {
  const { taskId } = req.params;

  const task: ITask | null = await Task.findById(taskId);

  if (!task) {
    res.status(404);
    throw new Error('Task not found');
  }

  res.json(task);
});

// @desc    Get The Tasks of a section
// @route   GET /api/v1/tasks/:sectionId
// @access  Private
const getSectionTasks = asyncHandler(async (req: Request, res: Response) => {
  const { sectionId } = req.params;

  const tasks: ITask[] = await Task.find({ sectionId });

  res.json(tasks);
});

// @desc    Delete a task By Id
// @route   DELETE /api/v1/tasks/:taskId
// @access  Private
const deleteTask = asyncHandler(async (req: Request, res: Response) => {
  const { taskId } = req.params;

  const task = await Task.findByIdAndDelete(taskId);

  if (!task) {
    res.status(404);
    throw new Error('Task not found');
  }

  res.status(201).json({ message: 'Task deleted' });
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
    res.status(404);
    throw new Error('Task not found');
  }

  res.json(updatedTask);
});
// @desc    Make a task done
// @route   Put /api/v1/tasks/:taskId/done
// @access  Private
const markTaskDone = asyncHandler(async (req: Request, res: Response) => {
  const { taskId } = req.params;

  const task: ITask | null = await Task.findByIdAndUpdate(taskId, { isDone: true });

  if (!task) {
    res.status(404);
    throw new Error('Task not found');
  }

  res.status(201).json(task);
});
export {
    createTask,
    updateTask,
    getTaskById,
    getSectionTasks,
    deleteTask,
    markTaskDone,
    assignTask

}