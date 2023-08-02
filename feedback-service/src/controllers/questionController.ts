import { Request, Response } from "express";
import asyncHandler from "express-async-handler";
import { IQuestion } from "../interfaces/Question";
import mongoose from "mongoose";
import Question from "../models/Question";

// @desc    Get all Questions
// @route   GET /api/v1/questions
// @access  Private
const getQuestions = asyncHandler(async (req: Request, res: Response) => {
  const pageSize = 10;
  const page = Number(req.query.pageNumber) || 1;

  const count = await Question.countDocuments({});
  const questions = await Question.find({})
    .limit(pageSize)
    .skip(pageSize * (page - 1));

  res.json({ questions, page, pages: Math.ceil(count / pageSize) });
});

// @desc    Get a Question By Id
// @route   GET /api/v1/questions/:QuestionId
// @access  Private
const getQuestionById = asyncHandler(async (req: Request, res: Response) => {
  const { questionId } = req.params;

  if (!mongoose.Types.ObjectId.isValid(questionId)) {
    res.status(400);
    throw Error("id format not correct");
  }
  const question: IQuestion | null = await Question.findById(questionId);

  if (!question) {
    res.status(404);
    throw Error("question not found");
  }

  res.json(question);
});

// @desc    Create a question
// @route   POST /api/v1/questions/
// @access  Private
const createQuestion = asyncHandler(async (req: Request, res: Response) => {
  const { value } = req.body;

  if (!value) {
    res.status(400);
    throw Error("value is required");
  }
  const question: IQuestion = await Question.create({
    value
  });

  res.status(201).json(question);
});

// @desc    Update a Question
// @route   PUT /api/v1/questions/:questionId
// @access  Private
const updateQuestion = asyncHandler(async (req: Request, res: Response) => {
  const { questionId } = req.params;
  const { value } = req.body;

  if (!value) {
    res.status(400);
    throw Error("question is required");
  }

  const question = await Question.findById(questionId);
  if (!question) {
    res.status(404);
    throw Error("question not found");
  }

  question.value = value;
  const updatequestion = await question?.save();

  res.status(201).json(updatequestion);
});

// @desc    Delete a Question By Id
// @route   DELETE /api/v1/questions/:questionId
// @access  Private
const deleteQuestion = asyncHandler(async (req: Request, res: Response) => {
  const { questionId } = req.params;

  const question: IQuestion | null = await Question.findByIdAndDelete(
    questionId
  );

  if (!question) {
    res.status(404).send({ error: "question not found" });
  }

  res.json({ message: "question deleted" });
});

// @desc    Add Option for a Question
// @route   PUT /api/v1/questions/:questionId/addoption
// @access  Private
const addOption = asyncHandler(async (req: Request, res: Response) => {
  const { questionId } = req.params;
  const { value } = req.body;

  const question = await Question.findByIdAndUpdate(
    questionId,
    {
      $push: { options: { value } },
    },
    { new: true }
  );

  if (!question) {
    res.status(404).send({ error: "question not found" });
  }

  res.json(question);
});

// @desc    Remove Option From a Question
// @route   PUT /api/v1/questions/:questionId/removeoption
// @access  Private
const removeOption = asyncHandler(async (req: Request, res: Response) => {
  const { questionId } = req.params;
  const { optionId } = req.body;

  const question = await Question.findByIdAndUpdate(
    questionId,
    {
      $pull: { options: { _id: optionId } },
    },
    { new: true }
  );

  if (!question) {
    res.status(404).send({ error: "question not found" });
  }

  res.json(question);
});

export {
  getQuestions,
  getQuestionById,
  createQuestion,
  updateQuestion,
  deleteQuestion,
  addOption,
  removeOption,
};
