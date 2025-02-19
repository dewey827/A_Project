//package com.project.reservation.common;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.JavaMailSenderImpl;
//
//import java.util.Properties;
//
//@Configuration
//public class MailConfig {
//
//    @Bean
//    JavaMailSender javaMailSender(){
//        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
//
//        javaMailSender.setHost("smtp.gmail.com");	//네이버의 경우 smtp.naver.com
//        javaMailSender.setUsername("dldudgus827@gmail.com");	//메일
//        javaMailSender.setPassword("ybzn zbzs wdrs dbiw");		//패스워드
//
//        javaMailSender.setPort(587);	//ssl의 경우 465? 네이버의 경우 465?
//
//        javaMailSender.setJavaMailProperties(getMailProperties());
//
//        return javaMailSender;
//    }
//
//    private Properties getMailProperties() {
//        Properties properties = new Properties();
//        properties.setProperty("mail.transport.protocol", "smtp");
//        properties.setProperty("mail.smtp.auth", "true");
//        properties.setProperty("mail.smtp.starttls.enable", "true");
//        properties.setProperty("mail.debug", "true");
//        properties.setProperty("mail.smtp.starttls.trust","smtp.gmail.com");	//네이버의 경우 stmp.naver.com 변경
//        properties.setProperty("mail.smtp.starttls.enable","true");	            //starttls <-> ssl로 변경도 확인
//        return properties;
//    }
//}
//메일링 기능 설정을 프로퍼티에 넣을지, config로 동적으로 넣을지 테스트용.