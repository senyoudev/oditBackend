import { Request, Response } from "express";
import asyncHandler from "express-async-handler";
import mongoose from "mongoose";
import UserFeedback from "../models/UserFeedback";
import { IUserFeedback } from "../interfaces/UserFeedback";
import Question from "../models/Question";

// @desc    Get User Feedbacks
// @route   GET /api/v1/feedbacks
// @access  Private
const getUserFeedbacks = asyncHandler(async (req: Request, res: Response) => {
  const { userId } = req.query;
  const feedbacks = await UserFeedback.find({ userId });

  res.json(feedbacks);
});

// @desc    Get Feedback By Id
// @route   GET /api/v1/feedbacks/:feedbackId
// @access  Private
const getFeedbackById = asyncHandler(async (req: Request, res: Response) => {
  const { feedbackId } = req.params;
  const feedback = await UserFeedback.findById(feedbackId);

  if (!feedback) {
    res.status(404);
    throw Error("feedback not found");
  }

  res.json(feedback);
});

// @desc    Create a Feedback
// @route   POST /api/v1/feedbacks
// @access  Private
const createFeedback = asyncHandler(async (req: Request, res: Response) => {
  const { userId } = req.query;
  const { questionId, optionId } = req.body;
  if (!optionId || !questionId) {
    res.status(400);
    throw Error("optionId and questionId are required");
  }

  if (
    !mongoose.Types.ObjectId.isValid(questionId) ||
    !mongoose.Types.ObjectId.isValid(optionId)
  ) {
    res.status(400);
    throw Error("optionId and questionId format not correct");
  }

  const question = await Question.findById(questionId);
  const option = question?.options.find((option) => {
    return (option._id = optionId);
  });
  if (!option) {
    res.status(400);
    throw Error("question not contient this option");
  }

  const feedback: IUserFeedback = await UserFeedback.create({
    userId,
    questionId,
    optionId,
  });

  res.status(201).json(feedback);
});

// @desc    Update a Feedback
// @route   PUT /api/v1/Feedbacks/:feedbackId
// @access  Private
const updateFeedback = asyncHandler(async (req: Request, res: Response) => {
  const { feedbackId } = req.params;
  const { questionId, optionId } = req.body;

  if (!optionId || !questionId) {
    res.status(400);
    throw Error("optionId and questionId are required");
  }

  if (
    !mongoose.Types.ObjectId.isValid(questionId) ||
    !mongoose.Types.ObjectId.isValid(optionId)
  ) {
    res.status(400);
    throw Error("optionId and questionId format not correct");
  }

  const question = await Question.findById(questionId);
  const option = question?.options.find((option) => {
    return (option._id = optionId);
  });

  if (!option) {
    res.status(400);
    throw Error("question not contient this option");
  }

  const feedback = await UserFeedback.findById(feedbackId);
  if (feedback) {
    feedback.questionId = questionId;
    feedback.optionId = optionId;

    const updatedFeedback = await feedback.save();
    res.json(updatedFeedback);
  } else {
    res.status(404);
    throw Error("Feedback not found");
  }
});

// @desc    Delete a Feedback
// @route   POST /api/v1/Feedbacks/:feedbackId
// @access  Private
const deleteFeedback = asyncHandler(async (req: Request, res: Response) => {
  const { feedbackId } = req.params;

  if (!mongoose.Types.ObjectId.isValid(feedbackId)) {
    res.status(400);
    throw Error("feedbackId format not correct");
  }

  const feedback = await UserFeedback.findByIdAndDelete(feedbackId);

  if (!feedback) {
    res.status(404);
    throw Error("feedback not found");
  }

  res.json({ message: "feedback deleted" });
});

export { getUserFeedbacks,getFeedbackById, createFeedback, updateFeedback, deleteFeedback };
