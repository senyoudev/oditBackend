import express from "express";
import {
  createSection,
  deleteSection,
  getRoomSections,
  getSection,
  updateSection,
} from "../controllers/sectionController";

const router = express.Router();

router.get("/",getRoomSections);
router.get("/:sectionId", getSection);
router.post("/", createSection);
router.put("/:sectionId", updateSection);
router.delete("/:sectionId", deleteSection);

export default router;
