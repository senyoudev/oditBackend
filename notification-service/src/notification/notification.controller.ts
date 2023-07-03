import asyncHandler from "express-async-handler";
import { Request, Response } from 'express';

// @desc    Send an email To the user
// @route   POST /api/v1/notifications/send-email
// @access  Private
const SendEmail = asyncHandler(async (req: Request, res: Response) => {
  res.status(200).json({ message:"sent" });
});


export {
    SendEmail
}