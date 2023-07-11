import amqp from "amqplib";

//Todo: create interface for message argument
export const sendNotification = async (message) => {
    const queue = process.env.QUEUE || "notification.queue";

    let connection;
    try {
      connection = await amqp.connect("amqp://localhost");
      const channel = await connection.createChannel();

      await channel.assertQueue(queue, { durable: false });
      channel.sendToQueue(queue, Buffer.from(JSON.stringify(message)));
      await channel.close();
    } catch (err) {
      console.warn(err);
    } finally {
      if (connection) await connection.close();
    }
};
