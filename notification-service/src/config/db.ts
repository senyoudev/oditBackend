import { createConnection } from "typeorm";
import { Notification } from "./Notification";

const SPRING_HOSTNAME = process.env.SPRING_HOSTNAME || "localhost";

export const connection = createConnection({
  type: "postgres",
  host: SPRING_HOSTNAME,
  port: 5433, //5432 // default port of postgres
  username: "postgres", // our created username, you can have your own user name
  password: "password", // our created username, you can have your own password
  database: "notifications", // our created database name, you can have your own
  entities: [Notification],
  synchronize: true,
  logging: false,
})
  .then(() => {
    console.log("Database connection established");
    // Start your application
  })
  .catch((error) => {
    console.error("Error connecting to the database:", error);
  });
