import amqp from "amqplib";
import { send } from "../mailer/index";
import dotenv from "dotenv";

dotenv.config();

// Declare the exchange, queue, and routing key
const exchange = process.env.EXCHANGE || "internal.exchange";
const queue = process.env.QUEUE || "notification.queue";
const routingKey =
  process.env.ROUTING_KEY || "internal.notification.routing-key";

const startConsumer = async () => {
  try {
    // Connect to RabbitMQ server
    const connection = await amqp.connect("amqp://localhost");
    const channel = await connection.createChannel();

    await channel.assertExchange(exchange, "topic", { durable: true })
    await channel.assertQueue(queue, { durable: false });
    await channel.bindQueue(queue, exchange!, routingKey);

    console.log("Waiting for messages...");

    // Consume messages from the queue
    channel.consume(
      queue,
      async (msg: any) => {
        if (msg) {
          const { from, to, type } = JSON.parse(msg.content.toString()) as {
            from: string;
            to: string;
            type: string;
          };

          try {
            // Send email
            await send(from, to);
            console.log(`Email sent to ${to}`);
          } catch (error) {
            console.error("Error sending email:", error);
          }

          // Acknowledge the message
          channel.ack(msg);
        }
      },
      { noAck: false } // Enable message acknowledgements
    );
  } catch (error) {
    console.error("Error starting consumer:", error);
  }
};

export { startConsumer };
