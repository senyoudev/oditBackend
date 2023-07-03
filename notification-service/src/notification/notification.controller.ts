import asyncHandler from "express-async-handler";
import { Request, Response } from "express";
import { send } from "../mailer";

// @desc    Send an email To the user
// @route   POST /api/v1/notifications/send-email
// @access  Private
const SendEmail = asyncHandler(async (req: Request, res: Response) => {
  const { from, to, subject, content } = req.body;
  try {
    await send(from, to, subject, content);
    res.status(200).json({ message: "sent" });
  } catch (error) {
    res.status(500).json({ message: error });
  }
});

export { SendEmail };
