package com.banktree.banktree.config;


import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MailComponents {
    private final JavaMailSender javaMailSender;

    public boolean sendMail(String email, String subject, String text){
        boolean result = false;


        MimeMessagePreparator msg = new MimeMessagePreparator() {
            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper mimeMessageHelper =
                        new MimeMessageHelper(mimeMessage, true, "UTF-8");
                mimeMessageHelper.setTo(email);
                mimeMessageHelper.setSubject(subject);
                mimeMessageHelper.setText(text, true);
            }
        };

        try{
            javaMailSender.send(msg);
            result = true;
            System.out.println("이메일 전송 성공");
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("이메일 전송 실패");
        }
        return result;
    }
}
