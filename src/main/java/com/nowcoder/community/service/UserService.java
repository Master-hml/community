package com.nowcoder.community.service;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommnunityUtil;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;

@Service
public class UserService implements CommunityConstant {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    public User findUserById(int id){

        return userMapper.selectById(id);
    }

    public Map<String, Object> register(User user){
        Map<String, Object> map = new HashMap<>();

        if(user == null){
            throw new IllegalArgumentException("参数不能为空！");
        }else if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMSG", "账号不能为空！");
            return map;
        }else if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMSG", "密码不能为空！");
            return map;
        }else if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMSG", "邮箱不能为空！");
            return map;
        }

        //验证账号
        User u = userMapper.selectByName(user.getUsername());
        if(u != null){
            map.put("usernameMSG", "该账号已存在！");
            return map;
        }
        //验证邮箱
        u = userMapper.selectByEmail(user.getEmail());
        if(u != null){
            map.put("emailMSG", "该邮箱已注册！");
            return map;
        }

        //注册账户
        user.setSalt(CommnunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommnunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0); //类型，普通用户
        user.setStatus(0); //激活状态
        user.setActivationCode(CommnunityUtil.generateUUID());
        user.setCreateTime(new Date());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        userMapper.insertUser(user);

        // 激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        String url = domain + contextPath + "/activation/" + user.getId() + user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号", content);


        return map;
    }

    public int activation(int userId, String code){
        User user = userMapper.selectById(userId);
        if(user.getStatus() == 1){
            return ACTIVATION_REPEAT;
        }else if(user.getActivationCode().equals(code)){
            return ACTIVATION_SUCCESS;
        }else{
            return ACTIVATION_FAILURE;
        }
    }

    public Map<String, Object> login(String username, String password, int expiredSeconds){

        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if(StringUtils.isBlank(username)){
            map.put("usernameMSG", "账号不能为空！");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("passwordMSG", "密码不能为空！");
            return map;
        }

        //验证账号
        User user = userMapper.selectByName(username);
        if(user == null){
            map.put("usernameMSG", "该账号不存在！");
            return map;
        }
        //验证状态
        if(user.getStatus() == 0){
            map.put("usernameMSG", "该账号未激活！");
            return map;
        }
        // 验证密码
        password = CommnunityUtil.md5(password + user.getSalt());
        if(!user.getPassword().equals(password)){
            map.put("passwordMSG", "密码不正确！");
            return map;
        }

        // 生成凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommnunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds));
        loginTicketMapper.insertLoginTicket(loginTicket);

        map.put("ticket", loginTicket.getTicket());
        return map;

    }

    public void logout(String ticket){
        loginTicketMapper.updateStatus(ticket, 1);
    }

    public LoginTicket findLoginTicket(String ticket){
        return loginTicketMapper.selectByTicket(ticket);
    }

    public int updateHeaderUrl(int userId, String headerUrl){
        return userMapper.updateHeader(userId, headerUrl);
    }

    public Map<String, Object> updatePassword(User user, String prePwd, String newPwd, String confirmPwd){

        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if(StringUtils.isBlank(prePwd)){
            map.put("prePwdMSG", "密码不能为空！");
            return map;
        }
        if(StringUtils.isBlank(newPwd)){
            map.put("newPwdMSG", "密码不能为空！");
            return map;
        }
        if(StringUtils.isBlank(confirmPwd)){
            map.put("confirmPwdMSG", "密码不能为空！");
            return map;
        }

        // 验证密码
        prePwd = CommnunityUtil.md5(prePwd + user.getSalt());
        if(!user.getPassword().equals(prePwd)){
            map.put("prePwdMSG", "密码不正确！");
            return map;
        }

        // 验证确认密码
        if(!newPwd.equals(confirmPwd)){
            map.put("confirmPwdMSG", "两次输入的密码不一致!！");
            return map;
        }

        userMapper.updatePassword(user.getId(), CommnunityUtil.md5(newPwd + user.getSalt()));
        return map;

    }

    public User findUserByName(String username){
        return userMapper.selectByName(username);
    }


}
