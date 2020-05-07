package com.xiangshui.server.service;

import org.springframework.stereotype.Component;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

@Component
public class MailService {


    public static void send(
            String host, String fromAccount, String fromPassword,
            String[] toAccounts, String[] ccAccounts, String subject,
            String content, Attachment... attachments) throws MessagingException, FileNotFoundException, UnsupportedEncodingException {
        Properties properties = System.getProperties();
        properties.setProperty("mail.transport.protocol", "smtp");
        properties.setProperty("mail.smtp.host", host);
        properties.put("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.timeout","25000");
        Session session = Session.getDefaultInstance(properties, new Authenticator() {
            @Override
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromAccount, fromPassword);
            }
        });
//        session.setDebug(true);
        MimeMessage message = new MimeMessage(session);
        //头部头字段
        message.setFrom(new InternetAddress(fromAccount));
        if (toAccounts != null) {
            for (String toAccount : toAccounts) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(toAccount));
            }
        }
        if (ccAccounts != null) {
            for (String ccAccount : ccAccounts) {
                message.addRecipient(Message.RecipientType.CC, new InternetAddress(ccAccount));
            }
        }
        message.setSubject(subject);
        //创建多重消息
        Multipart multipart = new MimeMultipart();
        //设置文本消息部分
        BodyPart bodyPart = new MimeBodyPart();
        bodyPart.setContent(content, "text/html;charset=UTF-8");
        multipart.addBodyPart(bodyPart);
        //添加附件
        if (attachments != null) {
            for (Attachment attachment : attachments) {
                DataSource ds = new ByteArrayDataSource(attachment.bytes, attachment.contentType);
                MimeBodyPart mimeBodyPart = new MimeBodyPart();
                mimeBodyPart.setDataHandler(new DataHandler(ds));
                mimeBodyPart.setFileName(MimeUtility.encodeWord(attachment.fileName));
                multipart.addBodyPart(mimeBodyPart);
            }
        }
        message.setContent(multipart);
        Transport.send(message);
    }

    public static void send(String[] toAccounts, String[] ccAccounts, String subject,
                            String content, Attachment... attachments) throws FileNotFoundException, MessagingException, UnsupportedEncodingException {
        send("smtp.mxhichina.com", "technology@xiangshuispace.com", "Xiangshui2017", toAccounts, ccAccounts, subject, content, attachments);
    }

    public static class Attachment {
        private String fileName;
        private byte[] bytes;
        private String contentType;

        public Attachment(String fileName, byte[] bytes, String contentType) {
            this.fileName = fileName;
            this.bytes = bytes;
            this.contentType = contentType;
        }

        public String getFileName() {
            return fileName;
        }

        public Attachment setFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public byte[] getBytes() {
            return bytes;
        }

        public Attachment setBytes(byte[] bytes) {
            this.bytes = bytes;
            return this;
        }

        public String getContentType() {
            return contentType;
        }

        public Attachment setContentType(String contentType) {
            this.contentType = contentType;
            return this;
        }
    }


    public static void main(String[] args) throws MessagingException, IOException {
//        SpringUtils.init();
//        SpringUtils.getBean(MailService.class).test();
//        send(new String[]{"hongwei@xiangshuispace.com"}, null, "sdfse登录福建省", "slfjejgslef");

        send("smtp.mxhichina.com", "hz@xiangshuispace.com", "Xb20192021", new String[]{
                "973119204@qq.com",
                "hongwei@xiangshuispace.com",
//                "none@xiangshuispace.com",
        }, null, "test", "test", null);


    }
}
