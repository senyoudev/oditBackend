import { Request, Response } from "express";
import asyncHandler from "express-async-handler";
import Section from "../models/Section";
import { ISection } from "../interfaces/Section";
import mongoose from "mongoose";

// @desc    Get room sections
// @route   Get /api/v1/sections
// @access  Private
const getRoomSections = asyncHandler(async (req: Request, res: Response) => {
  const { roomId } = req.query;
  const sections = await Section.find({ roomId });

  res.json(sections);
});

// @desc    Get section
// @route   GET /api/v1/sections/:sectionId
// @access  Private
const getSection = asyncHandler(async (req: Request, res: Response) => {
  const { sectionId } = req.params;
  if (!mongoose.Types.ObjectId.isValid(sectionId)) {
    res.status(400);
    throw Error("Id format not correct");
  }
  const section = await Section.findById(sectionId).populate("tasks");
  if (!section) {
    res.status(404);
    throw Error("section not found");
  }
  res.json(section);
});

// @desc    Create a section
// @route   POST /api/v1/sections
// @access  Private
const createSection = asyncHandler(async (req: Request, res: Response) => {
  const { roomId, name } = req.body;
  if (!roomId || !name) {
    res.status(400);
    throw Error("roomId and name is required");
  }
  const section: ISection = await Section.create({
    roomId,
    name,
  });

  res.status(201).json(section);
});

// @desc    Update a section
// @route   PUT /api/v1/sections/:sectionId
// @access  Private
const updateSection = asyncHandler(async (req: Request, res: Response) => {
  const { name } = req.body;
  const { sectionId } = req.params;
  if (!mongoose.Types.ObjectId.isValid(sectionId)) {
    res.status(400);
    throw Error("Id format not correct");
  }
  if (!name) {
    res.status(400);
    throw Error("name is required");
  }

  const section = await Section.findById(sectionId);
  if (section) {
    section.name = name;

    const updatedSection = await section.save();
    res.json(updatedSection);
  } else {
    res.status(404);
    throw Error("section not found");
  }
});

// @desc    Delete a section
// @route   DELETE /api/v1/sections/:sectionId
// @access  Private
const deleteSection = asyncHandler(async (req: Request, res: Response) => {
  const { sectionId } = req.params;

  if (!mongoose.Types.ObjectId.isValid(sectionId)) {
    res.status(400);
    throw Error("Id format not correct");
  }

  const section = await Section.findByIdAndDelete(sectionId);

  if (!section) {
    res.status(404);
    throw Error("section not found");
  }

  res.json({ message: "section deleted" });
});

export {
  getRoomSections,
  getSection,
  createSection,
  updateSection,
  deleteSection,
};
