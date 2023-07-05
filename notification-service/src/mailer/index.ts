import nodemailer from "nodemailer";
import dotenv from "dotenv";

dotenv.config();

const user = process.env.EMAIL;
const pass = process.env.PASSWORD;
const transporter = nodemailer.createTransport({
  host: "smtp.gmail.com",
  port: 587,
  secure: false,
  auth: {
    user,
    pass,
  },
});
const send = async (
  from: string,
  to: string,
  subject: string,
  text: string
) => {
  await transporter.sendMail({ from, to, subject: subject, html: text });
};

export { send };
