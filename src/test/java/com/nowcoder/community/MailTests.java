package com.nowcoder.community;


import com.nowcoder.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;

import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTests {
    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testText(){
        mailClient.sendMail("hemaolin96@qq.com", "TEST", "Welcome to new WORLD!");

    }

    @Test
    public void testHtml(){
        Context context = new Context();
        context.setVariable("username", "hemaolin");
        String content = templateEngine.process("/mail/demo", context);
        mailClient.sendMail("hemaolin96@qq.com", "TEST", content);

    }


}
