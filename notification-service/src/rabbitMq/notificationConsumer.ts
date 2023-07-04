import amqp from 'amqplib';
import { send } from '../mailer/index'


const startConsumer = async () => {
  try {
    // Connect to RabbitMQ server
    const connection = await amqp.connect('amqp://localhost');
    const channel = await connection.createChannel();

    // Declare the exchange, queue, and routing key
       const exchange = 'internal.exchange';
    const queue = 'notification.queue';
    const routingKey = 'internal.notification.routing-key';

   await channel.assertExchange(exchange, 'topic', { durable: true });
    await channel.assertQueue(queue, { durable: false });
    await channel.bindQueue(queue, exchange, routingKey);

    console.log('Waiting for messages...');

   

    // Consume messages from the queue
    channel.consume(
      queue,
      async (msg) => {
        if (msg) {
          const notificationRequest = JSON.parse(msg.content.toString()) as {
            from: string;
            to: string;
            subject: string;
            text: string;
          };

          const { from, to, subject, text } = notificationRequest;

          console.log(`Received message: ${subject}`);

          try {
            // Send email
            await send(from, to, subject, text);
            console.log(`Email sent to ${to}`);
          } catch (error) {
            console.error('Error sending email:', error);
          }

          // Acknowledge the message
          channel.ack(msg);
        }
      },
      { noAck: false } // Enable message acknowledgements
    );
  } catch (error) {
    console.error('Error starting consumer:', error);
  }
};

export { startConsumer };