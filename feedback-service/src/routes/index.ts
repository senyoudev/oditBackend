import { Router } from "express";
import taskRoutes from "./userFeedbackRoutes";
import zipkinMiddleware from "../zipkin";
import sectionRoutes from "./questionRoutes";

const baseRoutes = Router();

baseRoutes
  .use(zipkinMiddleware)
  .use("/questions", sectionRoutes)
  .use("/feedbacks", taskRoutes);

export default baseRoutes;
