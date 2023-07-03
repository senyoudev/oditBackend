import asyncHandler from "express-async-handler";
import cloudinary from "cloudinary";
import fs from "fs";

const uploadToCloudinary = asyncHandler(async (req, res) => {
  cloudinary.v2.config({
    secure: true,
    cloud_name: process.env.CLOUD_NAME,
    api_key: process.env.API_KEY,
    api_secret: process.env.API_SECRET,
  });

  const file = req.file;
  const options: any = {
    resource_type: "auto",
    use_filename: true,
    unique_filename: false,
    upload_preset: process.env.UPLOAD_PRESET,
    folder: file?.mimetype.includes("image/") ? "/images" : "/videos",
  };

  try {
    const result = await cloudinary.v2.uploader.upload(file!.path, options);
    fs.unlinkSync(file!.path);
    res
      .status(200)
      .json({ url: result.secure_url, public_id: result.public_id });
  } catch (error: any) {
    fs.unlinkSync(file!.path);
    res.status(500).json({ message: error });
  }
});

const removeImage = asyncHandler(async (req, res) => {
  cloudinary.v2.config({
    secure: true,
    cloud_name: process.env.CLOUD_NAME,
    api_key: process.env.API_KEY,
    api_secret: process.env.API_SECRET,
  });
  const {publicId,type} = req.body;
  try {
    const result = await cloudinary.v2.uploader.destroy(publicId,{resource_type: type});
    console.log(result);
    if (result.result === "ok") {
      res.status(200).json({ message: "File deleted successfully" });
    } else {
      res.status(400).json({ message: "Failed to delete the file" });
    }
  } catch (error: any) {
    res.status(500).json({ message: error });
  }
});

export { uploadToCloudinary, removeImage };
