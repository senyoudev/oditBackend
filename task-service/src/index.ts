import express, { Request, Response } from "express";
import * as dotenv from "dotenv";
import cors from "cors";
import bodyParser from "body-parser";
import path from "path";
import baseRoutes from "./routes";
import http from "http";
import { Server } from "socket.io";
import eurekaClient, { startEureka } from "./eureka";
import admin from "firebase-admin";

dotenv.config({ path: path.join(__dirname, ".env") });
import connectDb from "./config/connectDb";

connectDb();
const app = express();
const port = process.env.PORT || 4001;

app
  .use(
    cors({
      origin: "*",
      optionsSuccessStatus: 200,
    })
  )
  .use(bodyParser.json({ limit: "30mb" }))
  .use(bodyParser.urlencoded({ limit: "30mb", extended: true }))
  .use("/api/v1", baseRoutes);

app.get("/", (req: Request, res: Response) => {
  res.send("Task Api Is running");
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

server
  .listen(port, async () => {
    console.log(
      `Server running at http://localhost:${port} on mode ${process.env.NODE_ENV}`
    );
    await startEureka();
  })
  .on("close", () => {
    eurekaClient.stop(() => {
      process.exit();
    });
  });

const initFirebaseAdmin = () => {
  const serviceAccount = require("../odit-app-b4ee5-firebase-adminsdk-9ugws-9bc5a09380.json");
  admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
  });
};

initFirebaseAdmin();
