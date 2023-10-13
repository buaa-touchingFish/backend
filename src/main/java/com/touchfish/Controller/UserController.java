package com.touchfish.Controller;


import cn.hutool.json.JSONUtil;
import com.touchfish.Dto.LoginInfo;
import com.touchfish.Dto.RegisterInfo;
import com.touchfish.Po.User;
import com.touchfish.Service.impl.UserImpl;
import com.touchfish.Tool.*;
import io.lettuce.core.ScriptOutputType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.Objects;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private  Captcha captcha ;
    @Autowired
    private UserImpl user ;

    @LoginCheck
    @PostMapping("/sendCaptcha")
    public Result registerSendCaptcha(@RequestBody String email){ //注册时发送邮箱验证码
//        if (!RegexUtil.isValidEmail(email)) return Result.fail("邮箱不合法");
//        User now = UserContext.getUser();
        email = email.replace("\"","");
        String code  = captcha.sendCaptcha(email);
        stringRedisTemplate.opsForValue().set(RedisKey.CATPTCHA_KEY+email,code,3, TimeUnit.MINUTES);
        return Result.ok("发送成功");
    }

    @PostMapping("/register")
    public Result registerConfirm(@RequestBody RegisterInfo registerInfo){ //完成注册
        if (user.lambdaQuery().eq(User::getEmail,registerInfo.getEmail()).count()>0){
            return Result.fail("邮箱已存在");
        }
        if (user.lambdaQuery().eq(User::getUsername,registerInfo.getUsername()).count()>0){
            return Result.fail("用户名已存在");
        }
        String captcha = stringRedisTemplate.opsForValue().get(RedisKey.CATPTCHA_KEY+registerInfo.getEmail());
        if (!captcha.equals(registerInfo.getCaptcha())){
            return Result.fail("验证码错误");
        }
        User newUser = new User(registerInfo.getUsername(),registerInfo.getPassword(),registerInfo.getEmail(),0);

        user.save(newUser);
        return Result.ok("注册成功");
    }

    @PostMapping("/login")
    public Result login(@RequestBody LoginInfo loginInfo){
        User myUser = user.lambdaQuery().eq(User::getUsername,loginInfo.getUsername()).one();
        if (!myUser.getPassword().equals(loginInfo.getPassword())){
            return Result.fail("密码错误");
        }
        String name = myUser.getUsername();
        String jwtToken = JWT.generateJwtToken(name);
        String jsonstr = JSONUtil.toJsonStr(myUser);
        stringRedisTemplate.opsForValue().set(RedisKey.JWT_KEY+myUser.getUsername(),jsonstr,1,TimeUnit.DAYS);//1天过期
        return Result.ok("登陆成功",jwtToken);
    }

}
