import express, { Request, Response } from 'express';
import * as dotenv from "dotenv";
import cors from "cors";
import bodyParser from "body-parser";
dotenv.config();
import path from 'path';
import router from './notification/notification.router'

dotenv.config({ path: path.join(__dirname, '.env') });

const app = express();
const port = process.env.PORT || 4000;

app.use(
  cors({
    origin: '*',
    optionsSuccessStatus: 200,
  })
)
.use(bodyParser.json({ limit: "30mb" }))
.use(bodyParser.urlencoded({ limit: "30mb", extended: true }))
.use("/api/v1/notifications", router)


app.get('/', (req: Request, res: Response) => {
  res.send('Upload Api Is running');
});

app.listen(port, () => {
  console.log(
    `Server running at http://localhost:${port} on mode ${process.env.NODE_ENV}`
  );
});



