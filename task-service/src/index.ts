import express, { Request, Response } from "express";
import * as dotenv from "dotenv";
import cors from "cors";
import bodyParser from "body-parser";
import path from "path";
import errorHandler from "./middlewares/errorMiddleware";
import baseRoutes from "./routes";
import http from "http";
import { Server } from "socket.io";
import { startEureka } from "./eureka";

dotenv.config({ path: path.join(__dirname, ".env") });
import connectDb from "./config/connectDb";

connectDb();
const app = express();
const port = process.env.PORT || 4000;
const EUREKA_ENABLED = process.env.EUREKA_ENABLED || false;

app
  .use(
    cors({
      origin: "*",
      optionsSuccessStatus: 200,
    })
  )
  .use(bodyParser.json({ limit: "30mb" }))
  .use(bodyParser.urlencoded({ limit: "30mb", extended: true }))
  .use("/api/v1", baseRoutes)
  .use(errorHandler);

app.get("/", (req: Request, res: Response) => {
  res.send("Upload Api Is running");
});

//Socket.io
const server = http.createServer(app);
const io = new Server(server, {
  maxHttpBufferSize: 1e10,
});

io.on("connect", (socket: any) => {
  socket.on("join", ({ roomId, roomMemberId }: any) => {
    socket.join(`room-${roomId}`);
    io.to(`room-${roomId}`).emit("memberOnline", { roomMemberId });
    console.log("join");
  });

  socket.on("taskDone", async ({ roomId, taskId }: any, callback: any) => {
    io.to(`room-${roomId}`).emit("done", { taskId });
    console.log("Task done");
  });

  socket.on("removeCheck", async ({ roomId, taskId }: any, callback: any) => {
    io.to(`room-${roomId}`).emit("done", { taskId });
    console.log("Task done");
  });

  socket.on(
    "taskCreated",
    async ({ roomId, sectionId }: any, callback: any) => {
      io.to(`room-${roomId}`).emit("SectionUpdated", { sectionId });
      console.log("Task done");
    }
  );

  socket.on(
    "taskRemoved",
    async ({ roomId, sectionId }: any, callback: any) => {
      io.to(`room-${roomId}`).emit("SectionUpdated", { sectionId });
      console.log("Task done");
    }
  );

  socket.on("disconnect", () => {
    console.log("disconnect");
  });
});

server.listen(port, async () => {
  console.log(
    `Server running at http://localhost:${port} on mode ${process.env.NODE_ENV}`
  );
  if (EUREKA_ENABLED) await startEureka();
});
