package com.touchfish.Controller;


import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qiniu.util.Auth;
import com.touchfish.Dao.AuthorMapper;
import com.touchfish.Dto.*;
import com.touchfish.Po.Author;
import com.touchfish.Po.User;
import com.touchfish.Service.impl.AuthorImpl;
import com.touchfish.Service.impl.UserImpl;
import com.touchfish.Tool.*;
import io.lettuce.core.ScriptOutputType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cassandra.CassandraProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Tag(name = "用户相关接口")
public class UserController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private  Captcha captcha ;
    @Autowired
    private UserImpl user;


    @Autowired
    private QiNiuOssUtil qiNiuOssUtil;

    @PostMapping("/sendCaptcha")
    @Operation(summary = "发送验证码")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "验证码 \"email\":")
    public Result<String> registerSendCaptcha(@RequestBody Map<String,String> email){ //注册时发送邮箱验证码
        Integer result = user.sendCaptcha(email.get("email"));
        if (result == 0) return Result.ok("验证码已发送");
        else return Result.fail("验证码发送失败") ;
    }

    @PostMapping("/register")
    @Operation(summary = "注册")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "用户名 密码 邮箱 验证码")
    public Result<String> registerConfirm(@RequestBody RegisterInfo registerInfo){ //完成注册
        if (user.lambdaQuery().eq(User::getEmail,registerInfo.getEmail()).count()>0){
            return Result.fail("邮箱已存在");
        }
        if (user.lambdaQuery().eq(User::getUsername,registerInfo.getUsername()).count()>0){
            return Result.fail("用户名已存在");
        }
        String captcha = stringRedisTemplate.opsForValue().get(RedisKey.CATPTCHA_KEY+registerInfo.getEmail());
        assert captcha != null;
        if (!captcha.equals(registerInfo.getCaptcha())){
            return Result.fail("验证码错误");
        }
        User newUser = new User(registerInfo.getUsername(),registerInfo.getPassword(),registerInfo.getEmail(),null);

        user.save(newUser);
        return Result.ok("注册成功");
    }

    @PostMapping("/login")
    @Operation(summary = "登录")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "用户名 密码")
    public Result<String> login(@RequestBody LoginInfo loginInfo){
        if (!user.lambdaQuery().eq(User::getUsername,loginInfo.getUsername()).exists()){
            return Result.fail("用户名不存在");
        }
        User myUser = user.lambdaQuery().eq(User::getUsername,loginInfo.getUsername()).one();
        if (!myUser.getPassword().equals(loginInfo.getPassword())){
            return Result.fail("密码错误");
        }
        String name = myUser.getUsername();
        String jwtToken = JWT.generateJwtToken(name);
        String jsonstr = JSONUtil.toJsonStr(myUser);
        stringRedisTemplate.opsForValue().set(RedisKey.JWT_KEY+myUser.getUsername(),jsonstr,1,TimeUnit.DAYS);//1天过期
        return Result.ok("登录成功",jwtToken);
    }

    @PostMapping("/findpwd")
    @Operation(summary = "修改/找回密码时发送验证码")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "邮箱")
    public Result<String> findPwd(@RequestBody String email){
        email = email.replace("\"","");
        if (!user.lambdaQuery().eq(User::getEmail,email).exists()){
            return Result.fail("用户邮箱不存在");
        }
        Integer result = user.sendCaptcha(email);
        if (result == 0) return Result.ok("验证码已发送");
        else return Result.fail("验证码发送失败") ;
    }

    @PostMapping("/changepwd")
    @Operation(summary = "修改/找回密码验证码确认")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "邮箱 新密码 验证码")
    public Result<String> changePwd( @RequestBody PwdChangeInfo pwdChangeInfo){
        String captcha = stringRedisTemplate.opsForValue().get(RedisKey.CATPTCHA_KEY+pwdChangeInfo.getEmail());
        if (StrUtil.isEmpty(captcha)){
           return Result.fail("验证码已失效");
        }
        if (!captcha.equals(pwdChangeInfo.getCaptcha())){
            return Result.fail("验证码错误");
        }

        boolean update = user.lambdaUpdate().eq(User::getEmail, pwdChangeInfo.getEmail()).set(User::getPassword, pwdChangeInfo.getNew_pwd()).update();
        if (update)  return Result.ok("成功修改密码");
        else return Result.fail("修改密码失败");
    }

    @PostMapping("/clainpage1")
    @LoginCheck
    @Operation(summary = "认领学者门户发送邮箱",security = { @SecurityRequirement(name = "bearer-key") })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "要认领的学者id 邮箱")
    public Result<String>  claimHomePage1(@RequestBody ClaimInfo claimInfo){
        User now_user = UserContext.getUser();
        String email =  claimInfo.getEmail();
        email = email.replace("\"","");
        boolean isEmail = Validator.isEmail(email);
        if (!isEmail|| !captcha.isValidClaim(email)) return  Result.fail("验证码格式错误");
        Integer result = user.sendCaptcha(email);
        if (result == 0){
            return Result.ok("邮箱发送成功");
        }else {
            return Result.fail("邮箱发送失败");
        }
    }

    @PostMapping("/clainpage2")
    @LoginCheck
    @Operation(summary = "认领学者门户确认邮箱",security = { @SecurityRequirement(name = "bearer-key") })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "要认领的学者id 邮箱 验证码")
    public Result<String>  claimHomePage2(@RequestBody ClaimInfo claimInfo){
        User now_user = UserContext.getUser();
        String captcha = stringRedisTemplate.opsForValue().get(RedisKey.CATPTCHA_KEY+claimInfo.getEmail());
        if (StrUtil.isEmpty(captcha)){
            return Result.fail("验证码已失效");
        }
        if (!captcha.equals(claimInfo.getCaptcha())){
            return Result.fail("验证码错误");
        }
        boolean flag = user.lambdaUpdate().eq(User::getUsername, now_user.getUsername()).set(User::getAuthor_id, claimInfo.getAuthor_id()).update();
        if (!flag){
            return  Result.fail("数据库出错,更新数据失败");
        }
        //作者表要更新
        return Result.ok("门户认领成功");
    }

    @PostMapping("/upload")
    @LoginCheck
    @Operation(summary = "上传头像",security = { @SecurityRequirement(name = "bearer-key") })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "上传文件头像 content-type = multipart/form-data 以formdata的格式上传参数名为file 类型为file")
    public Result<String>  upLoadAvatar(@RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return Result.fail("上传失败,头像为空");
        }
        User myUser = UserContext.getUser();
        String fileName = file.getOriginalFilename();
        InputStream inputStream = file.getInputStream();
        String upload = qiNiuOssUtil.upload(inputStream, fileName);//upload为返回的图片外链地址
        myUser.setAvatar(upload);
        user.updateById(myUser);
        return Result.ok("上传成功",upload);
    }

    @PostMapping("/changeinfo")
    @LoginCheck
    @Operation(summary = "修改个人信息",security = { @SecurityRequirement(name = "bearer-key") })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "这里只能修改用户名、邮箱和电话号")
    public Result<String> changeInfo(@RequestBody UserInfo userInfo){
        User myUser = UserContext.getUser();
        if (!userInfo.getUsername().equals(myUser.getUsername())){
            if (user.lambdaQuery().eq(User::getUsername,userInfo.getUsername()).exists()){
                return Result.fail("用户名已存在");
            }else{
                myUser.setUsername(userInfo.getUsername());
            }
        }
        if (!userInfo.getPhone().equals(myUser.getPhone())){
            myUser.setPhone(userInfo.getPhone());
        }
        if (!userInfo.getEmail().equals(myUser.getEmail())){
            myUser.setEmail(userInfo.getEmail());
        }
        boolean update = user.lambdaUpdate().eq(User::getUid, myUser.getUid()).update(myUser);
        if (update) return Result.ok("修改信息成功");
        else return Result.fail("修改信息失败");
    }

}

