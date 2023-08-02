import express from "express";
import {
  addOption,
  createQuestion,
  deleteQuestion,
  getQuestionById,
  getQuestions,
  removeOption,
  updateQuestion,
} from "../controllers/questionController";

const router = express.Router();

router.get("/", getQuestions);
router.get("/:questionId", getQuestionById);
router.post("/", createQuestion);
router.put("/:questionId", updateQuestion);
router.put("/:questionId/addoption", addOption);
router.put("/:questionId/removeoption", removeOption);
router.delete("/:questionId", deleteQuestion);

export default router;
