import { Router } from "express";
import zipkin_config from "../zipkin";


const router = Router();
router.use(zipkin_config);


export default router;
