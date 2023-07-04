import express, { Request, Response } from 'express';
import * as dotenv from 'dotenv';
import cors from 'cors';
import bodyParser from 'body-parser';
import router from './upload_config/upload.router';
dotenv.config();
import path from 'path';
import eurekaClient from './eureka';


dotenv.config({ path: path.join(__dirname, '.env') });

const app = express();
const port = process.env.PORT || 4000;

app
  .use(
    cors({
      origin: '*',
      optionsSuccessStatus: 200,
    })
  )
  //@ts-ignore
  .use(bodyParser.json({ limit: '30mb', extended: true }))
  .use(bodyParser.urlencoded({ limit: '30mb', extended: true }))
  .use('/api/v1/files', router);

app.get('/', (req: Request, res: Response) => {
  res.send('Upload Api Is running');
});

app.listen(port, () => {
  console.log(
    `Server running at http://localhost:${port} on mode ${process.env.NODE_ENV}`
  );
});

async function start() {
  eurekaClient.logger.level('debug');

  eurekaClient.start((error: any) => {
    console.log(error || 'user service registered');
  });

  eurekaClient.on('deregistered', () => {
    console.log('after deregistered');
    process.exit();
  });
}

start().catch(console.error);
