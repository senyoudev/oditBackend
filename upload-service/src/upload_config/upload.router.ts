import { Router } from "express";
import multer from "multer";
import zipkin_config from "../zipkin";
import { uploadToCloudinary, removeImage } from "./upload.contoller";

var storage = multer.diskStorage({
  destination: function (req, file, cb) {
    cb(null, "./src/uploads");
  },
  filename: function (req, file, cb) {
    cb(null, file.originalname);
  },
});
var upload = multer({ storage: storage });

const router = Router();
router.use(zipkin_config);

router.post("/upload", upload.single("file"), uploadToCloudinary);
router.delete("/remove", removeImage);

export default router;
