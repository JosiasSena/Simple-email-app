package josiassena.simpleemailapp.utils;

import android.content.Context;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class Email {
    private final Context context;
    private final String toEmail;
    private final String subject;
    private final String messageBody;
    private List<String> attachment_PathList = new ArrayList<>();
    private final String fromEmail;
    private final String password;

   public Email(Context context, String fromEmail, String password, String toEmail, String subject, String messageBody) {
       this.context = context;
       this.toEmail = toEmail;
       this.subject = subject;
       this.messageBody = messageBody;
       this.fromEmail = fromEmail;
       this.password = password;
    }

    public void send(){
        Session session = createSessionObject();

        try {
            Message message = createMessage(toEmail, subject, messageBody, session);
            new SendEmail(context, message).execute();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private Message createMessage(String email, String subject, String messageBody, Session session)
            throws MessagingException {

        Multipart multipart = new MimeMultipart("mixed");

        /** Define message */
        BodyPart messageBodyPart = new MimeBodyPart();
        MimeMessage message = new MimeMessage(session);

        message.setFrom(new InternetAddress(fromEmail));
        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(email));
        message.setSubject(subject);
        message.setSentDate(new Date());
        message.setText(messageBody);
        messageBodyPart.setContent(messageBody, "text/html");

        multipart.addBodyPart(messageBodyPart);

        /** Handle Attachments */
        for (String str : attachment_PathList) {
            BodyPart attachmentBodyPart = new MimeBodyPart();
            FileDataSource source = new FileDataSource(str);
            attachmentBodyPart.setDataHandler(new DataHandler(source));
            attachmentBodyPart.setFileName(source.getName());
            multipart.addBodyPart(attachmentBodyPart);
        }

        message.setContent(multipart);
        return message;
    }

    private Session createSessionObject() {
        Properties properties = new Properties();
        properties.put("mail.smtp.port", "587"); // 465, 587
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.debug", "true");

        return Session.getInstance(properties, new EmailAuthenticator(fromEmail, password));
    }

    public void setAttachment_PathList(List<String> attachment_PathList) {
        this.attachment_PathList = attachment_PathList;
    }
}
