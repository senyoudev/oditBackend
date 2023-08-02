import mongoose from "mongoose";
import * as dotenv from "dotenv";
dotenv.config();

const CONNECTION_URL = process.env.CONNECTION_URL || "";
let _mongoose: typeof mongoose;
const connectDb = async () => {
  try {
    _mongoose = await mongoose.connect(CONNECTION_URL, {
      //@ts-ignore
      useUnifiedTopology: true,
      useNewUrlParser: true,
    });

    console.log(`MongoDB connected:${_mongoose.connection.host}`);
  } catch (err) {
    console.log(`Error:${err.message}`);
    process.exit(1);
  }
};

export const closeConn = async () => {
  await _mongoose.connection.close();
};

export default connectDb;
