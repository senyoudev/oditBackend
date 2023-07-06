# oditBackend
Odit - Task Management Application Backend Using Microsevices Architecture


# Microservice Application

## Overview

This Microservice Application is a project and task tracking tool that enables efficient management of projects and tasks. It provides a unique feature called "Rooms" that enhances collaboration and organization within projects. Rooms represent specific teams or departments within a project, such as Development, Marketing, or Design. Users can create multiple Rooms within a project and invite members via email.

Each Room contains tasks specific to that Room, allowing users to categorize and focus on tasks related to their respective areas of responsibility. Additionally, users can communicate and collaborate within Rooms using a messaging feature, fostering seamless communication for discussing tasks, sharing updates, and addressing project-related matters efficiently.

The Microservice Application facilitates ease of use and collaboration by incorporating the concept of Rooms. It empowers teams to streamline project management efforts, enhance communication, and effectively track tasks within dedicated areas of focus.

## Microservices

The Microservice Application is built using a microservices architecture, which allows for independent development, deployment, and scaling of individual components. The application consists of the following microservices:

o ensure data consistency.
   - Built with Node.js and TypeScript.

1. **Notification Service**
   - Handles notifications and communication within Rooms.
   - Sends email notifications to Room members for important updates and discussions.
   - Integrates with RabbitMQ for message queueing.
   - Implemented using Node.js and TypeScript.

2. **Auth Service**
   - Manages user authentication and authorization.
   - Handles user registration, login, and access control.
   - Integrates with a PostgreSQL database for user data storage.
   - Developed using Spring Boot.

Each microservice is independently deployable and can be scaled horizontally to handle increased load. They communicate with each other via RESTful APIs or message queues to ensure seamless coordination and data consistency across the application.

## Getting Started

To run the Microservice Application locally, follow the installation and usage instructions provided in each microservice's respective directory.

Please refer to the individual README files in each microservice directory for detailed instructions on installation, configuration, and usage.

## Contributing

Contributions to the Microservice Application are welcome! If you have any ideas, suggestions, or bug fixes, please open an issue or submit a pull request.

## License

This project is licensed under the [MIT License](LICENSE).

