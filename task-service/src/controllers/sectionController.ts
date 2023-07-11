import { Request, Response } from "express";
import asyncHandler from "express-async-handler";
import Section from "../models/Section";
import { ISection } from "../interfaces/Section";

// @desc    Get room sections
// @route   POST /api/v1/sections/
// @access  Private
const getRoomSections = asyncHandler(async (req: Request, res: Response) => {
  const { roomId } = req.query;
  try {
    const sections = await Section.find({ roomId });

    res.json(sections);
  } catch (e) {
    res.status(404).send({ error: "room not found" });
  }
});

// @desc    Get section
// @route   POST /api/v1/sections/
// @access  Private
const getSection = asyncHandler(async (req: Request, res: Response) => {
  const { sectionId } = req.params;

  try {
    const section = await Section.findById(sectionId).populate("tasks");
    if (!section) res.status(404).send({ error: "section not found" });
    res.json(section);
  } catch (e) {
    res.status(400).send({ error: "id format not correct" });
  }
});

// @desc    Create a section
// @route   POST /api/v1/sections/
// @access  Private
const createSection = asyncHandler(async (req: Request, res: Response) => {
  const { roomId, name } = req.body;
  try {
    const section: ISection = await Section.create({
      roomId,
      name,
    });

    res.status(201).json(section);
  } catch {
    res.status(400).send({ error: "roomId and name is required" });
  }
});

// @desc    Update a section
// @route   POST /api/v1/sections/
// @access  Private
const updateSection = asyncHandler(async (req: Request, res: Response) => {
  const { name } = req.body;
  const { sectionId } = req.params;

  try {
    const section = await Section.findById(sectionId);
    if (section) {
      section.name = name;

      const updatedSection = await section.save();
      res.json(updatedSection);
    } else {
      res.status(404).send({ error: "section not found" });
    }
  } catch {
    res.status(400).send({ error: "roomId and name is required" });
  }
});

// @desc    Delete a section
// @route   POST /api/v1/sections/
// @access  Private
const deleteSection = asyncHandler(async (req: Request, res: Response) => {
  const { sectionId } = req.params;

  try {
    await Section.findByIdAndDelete(sectionId);

    //Todo remove tasks
    res.json({ message: "section deleted" });
  } catch (e) {
    res.status(404).send({ error: "section not found" });
  }
});

export {
  getRoomSections,
  getSection,
  createSection,
  updateSection,
  deleteSection,
};
