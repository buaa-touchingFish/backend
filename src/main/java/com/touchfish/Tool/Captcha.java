package com.touchfish.Tool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import java.util.Random;
@Component
public class Captcha {
    @Autowired
    private  JavaMailSenderImpl mailSender;

    public String sendCaptcha(String email){ //发送验证码并返回产生的验证码
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("1274975655@qq.com");
        mailMessage.setSubject("航专摸鱼小分队");
        mailMessage.setTo(email);
        String code = createCode(5);
        mailMessage.setText(code);
        mailSender.send(mailMessage);
        return code;
    }

    private  String createCode(int n) {
        String codes="";
        Random r=new Random();
        //2.定义一个for循环，随机n次，随机生成字符
        for(int i=0;i<n;i++) {
            //3.生成随机字符，可能是数字，大写字母，小写字母
            int num=r.nextInt(3);
            switch(num) {
                case 0:
                    //数字:0-9
                    codes+=r.nextInt(10);
                    break;
                case 1:
                    //大写字母:A(65)-Z(65+25)
                    char ch1=(char)(r.nextInt(26)+65);
                    codes+=ch1;
                    break;
                case 2:
                    //小写字母:a(97)-z(97+25)
                    char ch2=(char)(r.nextInt(26)+97);
                    codes+=ch2;
                    break;
            }
        }
        return codes;
    }


}
