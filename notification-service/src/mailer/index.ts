import nodemailer from "nodemailer";
import dotenv from "dotenv";

dotenv.config();

const user = process.env.EMAIL;
const pass = process.env.PASSWORD;
const transporter = nodemailer.createTransport({
  service: "gmail",
  auth: {
    user,
    pass,
  },
});
const send = async (from: string, to: string) => {
  await transporter.sendMail({ from, to, subject: "Wa hsaaan", text: "text" });
};

export { send };
