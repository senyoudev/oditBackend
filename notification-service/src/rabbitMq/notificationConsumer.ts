import { getConnection } from 'typeorm';
import amqp from "amqplib";
import { send } from "../mailer/index";
import { getEmailContent } from "../mailer/getEmailContent";
import NotificationType from "../interfaces/NotificationType";
import { Notification } from "../config/Notification";


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
          const { from, to,inviteLink,recipient,resetToken, type } = JSON.parse(msg.content.toString()) as {
            from: string;
            to: string;
            inviteLink?: string;
            recipient?: string;
            resetToken?:string;
            type: NotificationType;
          };
       
          const {subject, content} = getEmailContent(type,{inviteLink,recipient,resetToken});

          try {
            // Send email
            await send(from, to,subject,content);
            const notification = new Notification();
      notification.from = from;
      notification.to = to;
      notification.type=type;
            await getConnection().getRepository(Notification).save(notification);

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
