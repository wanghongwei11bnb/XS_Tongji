package com.xiangshui.server.service;

import com.xiangshui.util.spring.SpringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.StringWriter;

@Component
public class MailService {


    public JavaMailSender createMailSender(String hort, int port, String username, String password) {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(hort);
        javaMailSender.setPort(port);
        javaMailSender.setUsername(username);
        javaMailSender.setPassword(password);
        return javaMailSender;
    }

    public JavaMailSender createMailSender(String username, String password) {
        return createMailSender("smtp.mxhichina.com", 25, username, password);
    }

    public JavaMailSender createMailSender() {
        return createMailSender("technology@xiangshuispace.com", "Xiangshui2017");
    }


    public void send(String from, String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        createMailSender().send(message);
    }

    public void sendHtml(String to, String subject, String text) throws MessagingException {
        sendHtml("technology@xiangshuispace.com", to, subject, text);
    }


    public void sendHtml(String from, String to, String subject, String text) throws MessagingException {
        JavaMailSender javaMailSender = createMailSender();
        MimeMessage mailMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage);
        messageHelper.setFrom(from);
        messageHelper.setTo(to);
        messageHelper.setSubject(subject);
        messageHelper.setText(text, true);
        javaMailSender.send(mailMessage);
    }


    public void send(String to, String subject, String text) {
        send("technology@xiangshuispace.com", to, subject, text);
    }


    public void test() throws MessagingException {
        sendHtml("hongwei@xiangshuispace.com", "测试", "<div class='alert'>123123</div>");
    }

    public static void main(String[] args) throws MessagingException {
        SpringUtils.init();
        SpringUtils.getBean(MailService.class).test();
    }
}
