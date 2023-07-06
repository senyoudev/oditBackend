import NotificationType from "../interfaces/NotificationType";
import fs from 'fs'

export const getEmailContent = (notificationType: NotificationType, dynamicData: any): { subject: string, content: string } => {
  let templateFilePath;
  switch (notificationType) {
    case NotificationType.INVITATION:
      templateFilePath = "src/Templates/invitationEmail.json";
      
      break;
    case NotificationType.ACCEPT_NOTIF:
      templateFilePath = "src/Templates/acceptInvitation.json";
      break;
    case NotificationType.DENY_NOTIF:
      templateFilePath = "src/Templates/denyInvitation.json";
      break;
    case NotificationType.REGISTRATION_NOTIF:
      templateFilePath = "src/Templates/registration.json";
      break;
    case NotificationType.PASSWORD_RESET:
        templateFilePath = "src/Templates/resetEmail.json";
        break;
    default:
      throw new Error("Invalid notification type");
  }

  const templateData = fs.readFileSync(templateFilePath, "utf8");
  const template = JSON.parse(templateData);

  const { subject, content } = template;
  const { recipient, inviteLink,resetToken } = dynamicData;

  // Replace placeholders in the template with dynamic data
  const replacedContent = content
    .replace("{recipient}", recipient || "Odit User")
    .replace("{invitationLink}", inviteLink)
    .replace("{resetPassword}", resetToken);

  return { subject, content: replacedContent };
};