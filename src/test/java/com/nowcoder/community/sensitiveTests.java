package com.nowcoder.community;

import com.nowcoder.community.util.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class sensitiveTests {

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveFilter(){

        String text = "这里可以嫖娼，可以吸毒，可以赌博，cnm!";
        text = sensitiveFilter.filter(text);
        System.out.println(text);

        text = "这里可以嫖娼，可以-吸-毒-，可以-赌/博-，cnm!";
        text = sensitiveFilter.filter(text);
        System.out.println(text);



    }

}
