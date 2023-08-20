import express from "express";
import { getProjectTasks, getUserTasks } from "../controllers/taskController";

const router = express.Router();

router.get("/", getUserTasks);
router.get("/:projectId", getProjectTasks);

export default router;
