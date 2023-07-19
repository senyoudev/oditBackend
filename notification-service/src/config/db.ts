import {createConnection} from "typeorm";
import {Notification} from "./Notification"


export const connection = createConnection({
    type: "postgres", 
    host: "localhost",
    port:  5433, // default port of postgres
    username: "postgres", // our created username, you can have your own user name
    password: "yassine1", // our created username, you can have your own password
    database: "notifications", // our created database name, you can have your own
    entities: [
               Notification
    ],
    synchronize: true,
    logging: false
})
.then(() => {
    console.log("Database connection established");
    // Start your application
  })
  .catch((error) => {
    console.error("Error connecting to the database:", error);
  });