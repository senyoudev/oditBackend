import admin from "firebase-admin";
export const pushNotification = async (topic: string, title: string, body: string) => {
  await admin.messaging().sendToTopic(
    topic,
    {
      notification: {
        body,
        title,
      },
    },
    {
      contentAvailable: true,
      priority: "high",
    }
  );
};
