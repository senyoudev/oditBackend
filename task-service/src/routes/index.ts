import { Router } from "express";
import taskRoutes from "./taskRoutes";
import zipkinMiddleware from "../zipkin";
import sectionRoutes from "./sectionRoutes";
import userTasksRoutes from "./userTasks";
import checkRoomMember from "../middlewares/checkRoomMember";

const baseRoutes = Router();

baseRoutes
  .use(zipkinMiddleware)
  .use("/user-tasks", userTasksRoutes)
  .use(checkRoomMember)
  .use("/sections", sectionRoutes)
  .use("/tasks", taskRoutes);

export default baseRoutes;
