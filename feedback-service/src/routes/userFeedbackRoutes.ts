import express from "express";
import {
  getUserFeedbacks,
  createFeedback,
  updateFeedback,
  deleteFeedback,
  getFeedbackById,
} from "../controllers/userFeedbackController";

const router = express.Router();

router.get("/", getUserFeedbacks);
router.get("/:feedbackId", getFeedbackById);
router.post("/", createFeedback);
router.put("/:feedbackId", updateFeedback);
router.delete("/:feedbackId", deleteFeedback);

export default router;
