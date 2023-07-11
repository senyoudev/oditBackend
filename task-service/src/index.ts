import express, { Request, Response } from "express";
import * as dotenv from "dotenv";
import cors from "cors";
import bodyParser from "body-parser";
import path from "path";
import errorHandler from './middlewares/errorMiddleware';

dotenv.config({ path: path.join(__dirname, ".env") });
import connectDb from "./config/connectDb";

connectDb();
const app = express();
const port = process.env.PORT || 4000;

app
  .use(
    cors({
      origin: "*",
      optionsSuccessStatus: 200,
    })
  )
  .use(bodyParser.json({ limit: "30mb" }))
  .use(bodyParser.urlencoded({ limit: "30mb", extended: true }))
  .use(errorHandler);


app.get("/", (req: Request, res: Response) => {
  res.send("Upload Api Is running");
});

app.listen(port, async () => {
  console.log(
    `Server running at http://localhost:${port} on mode ${process.env.NODE_ENV}`
  );
  //await startEureka();
});
