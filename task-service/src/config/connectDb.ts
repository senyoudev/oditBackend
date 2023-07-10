import mongoose from "mongoose";

const CONNECTION_URL = process.env.CONNECTION_URL || "";
const connectDb = async () => {
  try {
    const conn = await mongoose.connect(CONNECTION_URL, {
      //@ts-ignore
      useUnifiedTopology: true,
      useNewUrlParser: true,
    });

    console.log(`MongoDB connected:${conn.connection.host}`);
  } catch (err) {
    console.log(`Error:${err.message}`);
    process.exit(1);
  }
};

export default connectDb;
