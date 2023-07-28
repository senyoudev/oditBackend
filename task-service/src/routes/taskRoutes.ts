import express from "express";
import {
  addComment,
  assignTask,
  createTask,
  deleteComment,
  deleteTask,
  getTaskById,
  markTaskDone,
  updateTask,
} from "../controllers/taskController";

const router = express.Router();

router.get("/:taskId", getTaskById);
router.post("/", createTask);
router.put("/:taskId", updateTask);
router.delete("/:taskId", deleteTask);
router.put("/:taskId/assign", assignTask);
router.put("/:taskId/done", markTaskDone);
router.put("/:taskId/addcomment", addComment);
router.put("/:taskId/deletecomment", deleteComment);

export default router;
