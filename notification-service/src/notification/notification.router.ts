import { Router } from "express";
import zipkin_config from "../zipkin";
import { SendEmail } from './notification.controller'


const router = Router();
router.use(zipkin_config);

router.post("/send-email",SendEmail );

export default router;
