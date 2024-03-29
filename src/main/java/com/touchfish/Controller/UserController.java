package com.touchfish.Controller;


import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.touchfish.Dto.*;
import com.touchfish.Po.*;
import com.touchfish.Service.impl.*;
import com.touchfish.Tool.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
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
    private PaperAppealImpl paperAppeal;
    @Autowired
    private PaperImpl paper;

    @Autowired
    private AuthorImpl authorImpl;
    @Autowired
    private ClaimRequestImpl claimRequestImpl;
    @Autowired
    private QiNiuOssUtil qiNiuOssUtil;




    private static String parsePwd(String pwd) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, IOException, InvalidKeySpecException, InvalidKeyException {
        return RSAUtils.decryptByPrivateKey(pwd);
    }


    private static String getTimeNow(){
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(date);
    }

    @GetMapping("/getInfo")
    @LoginCheck
    @Operation(summary = "获取个人信息",security = { @SecurityRequirement(name = "bearer-key") })
    public  Result<UserInfo> getUserInfo(){
        User myUser = UserContext.getUser();
        User one = user.lambdaQuery().eq(User::getUid, myUser.getUid()).one();
        String author_name = null;
        if (!StrUtil.isEmpty(one.getAuthor_id())){
            author_name = authorImpl.getById(one.getAuthor_id()).getDisplay_name();
        }
        UserInfo info = new UserInfo(one.getUsername(),one.getEmail(),one.getPhone(),one.getAvatar(),one.getUid(),one.getAuthor_id(),author_name);
        return Result.ok("成功获取个人信息",info);
    }

    @PostMapping("/getava")
    @Operation(summary = "获取个人头像" )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "用户id \"id\":")
    public  Result<String> getUserAva(@RequestBody Map<String,Integer> mp){
        Integer uid = mp.get("id");
        User byId = user.getById(uid);
        if (byId == null){
            return Result.fail("用户不存在");
        }
        return Result.ok("成功获取头像",byId.getAvatar());
    }

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
    public Result<String> registerConfirm(@RequestBody RegisterInfo registerInfo) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, IOException, InvalidKeySpecException, InvalidKeyException { //完成注册
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
        User newUser = new User(registerInfo.getUsername(),parsePwd(registerInfo.getPassword()),registerInfo.getEmail(),null);

        user.save(newUser);
        return Result.ok("注册成功");
    }

    @PostMapping("/login")
    @Operation(summary = "登录")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "用户名 密码")
    public Result<RegisterSuccess> login(@RequestBody LoginInfo loginInfo) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, IOException, InvalidKeySpecException, InvalidKeyException {
        if (!user.lambdaQuery().eq(User::getUsername,loginInfo.getUsername()).exists()){
            return Result.fail("用户名不存在");
        }
        User myUser = user.lambdaQuery().eq(User::getUsername,loginInfo.getUsername()).one();
        if (!myUser.getPassword().equals(parsePwd(loginInfo.getPassword()))){
            return Result.fail("密码错误");
        }
        String name = myUser.getUsername();
        String jwtToken = JWT.generateJwtToken(name);
        String jsonstr = JSONUtil.toJsonStr(myUser);
        stringRedisTemplate.opsForValue().set(RedisKey.JWT_KEY+myUser.getUsername(),jsonstr,1,TimeUnit.DAYS);//1天过期

        stringRedisTemplate.opsForValue().increment(RedisKey.LOGIN_KEY+RedisKey.getEveryDayKey(),1);

        return Result.ok("登录成功",new RegisterSuccess(jwtToken, myUser.getUid(),myUser.getUsername(),myUser.getEmail(),myUser.getPhone(),myUser.getAvatar(),myUser.getAuthor_id()));

    }

    @PostMapping("/outlogin")
    @LoginCheck
    @Operation(summary = "退出登录",security = { @SecurityRequirement(name = "bearer-key") })
    public Result<String> outLogin(){
        User myUser = UserContext.getUser();
        stringRedisTemplate.delete(RedisKey.JWT_KEY+myUser.getUsername());
        return Result.ok("退出登录");
    }


    @PostMapping("/findpwd")
    @Operation(summary = "找回密码时发送验证码")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "验证码 \"email\":")
    public Result<String> findPwd(@RequestBody Map<String,String> json){
        String email = json.get("email");
        email = email.replace("\"","");
        if (!user.lambdaQuery().eq(User::getEmail,email).exists()){
            return Result.fail("用户邮箱不存在");
        }
        Integer result = user.sendCaptcha(email);
        if (result == 0) return Result.ok("验证码已发送");
        else return Result.fail("验证码发送失败") ;
    }

    @PostMapping("/changepwd")
    @Operation(summary = "找回密码验证码确认")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "邮箱 新密码 验证码")
    public Result<String> changePwd( @RequestBody PwdChangeInfo pwdChangeInfo) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, IOException, InvalidKeySpecException, InvalidKeyException {
        String captcha = stringRedisTemplate.opsForValue().get(RedisKey.CATPTCHA_KEY+pwdChangeInfo.getEmail());
        if (StrUtil.isEmpty(captcha)){
            return Result.fail("验证码已失效");
        }
        if (!captcha.equals(pwdChangeInfo.getCaptcha())){
            return Result.fail("验证码错误");
        }

        boolean update = user.lambdaUpdate().eq(User::getEmail, pwdChangeInfo.getEmail()).set(User::getPassword, parsePwd(pwdChangeInfo.getNew_pwd())).update();
        if (update)  return Result.ok("成功修改密码");
        else return Result.fail("修改密码失败");
    }

    @PostMapping("/changepwd1")
    @LoginCheck
    @Operation(summary = "修改密码",security = { @SecurityRequirement(name = "bearer-key") })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "\"oldpwd\": ,\"newpwd:\"")
    public Result<String> changePwd1(@RequestBody Map<String,String> mp) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, IOException, InvalidKeySpecException, InvalidKeyException {
        String oldPwd = parsePwd(mp.get("oldpwd"));
        String newPwd = parsePwd(mp.get("newpwd"));
        User myUser = UserContext.getUser();
        User newUser = user.lambdaQuery().eq(User::getUid,myUser.getUid()).one();
        if (newUser.getPassword().equals(oldPwd)){
            myUser.setPassword(newPwd);
            newUser.setPassword(newPwd);
            boolean flag = user.updateById(newUser);
            if (flag) return Result.ok("修改密码成功");
            else return Result.fail("修改密码失败");
        }
        return Result.fail("原密码错误");
    }


    @PostMapping("/claimcaptcha1")
    @LoginCheck
    @Operation(summary = "认领学者门户发送邮箱",security = { @SecurityRequirement(name = "bearer-key") })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "邮箱 \"email\":")
    public Result<String>  claimHomeCaptcha1(@RequestBody Map<String,String> mp){
        User now_user = UserContext.getUser();
        String email =  mp.get("email");
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

    @PostMapping("/claimcaptcha2")
    @LoginCheck
    @Operation(summary = "认领学者门户确认邮箱并发送身份证照片",security = { @SecurityRequirement(name = "bearer-key") })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "要认领的学者id 邮箱 验证码  照片")
    public Result<String>  claimHomeCaptcha2(@RequestParam(value = "file", required = false) MultipartFile file,@RequestParam(value = "email") String email,@RequestParam(value = "id") String id,@RequestParam(value = "captcha") String mycaptcha) throws IOException {
        User now_user = UserContext.getUser();
        if (user.lambdaQuery().eq(User::getUid,now_user.getUid()).one().getAuthor_id() != null){
            return Result.fail("你已认领过门户");
        }
        Author one = authorImpl.lambdaQuery().eq(Author::getId, id).one();
        if (one.getClaim_uid() != null){
            return Result.fail("该学者门户已被认领");
        }
        String captcha = stringRedisTemplate.opsForValue().get(RedisKey.CATPTCHA_KEY+email);
        if (StrUtil.isEmpty(captcha)){
            return Result.fail("验证码已失效");
        }
        if (!captcha.equals(mycaptcha)){
            return Result.fail("验证码错误");
        }
        String fileName = file.getOriginalFilename();
        InputStream inputStream = file.getInputStream();
        String upload = qiNiuOssUtil.upload(inputStream, fileName);//upload为返回的图片外链地址
        ClaimRequest claimRequest = new ClaimRequest(now_user.getUid(),getTimeNow(),id,upload);
        boolean save = true;
        if (claimRequestImpl.lambdaQuery().eq(ClaimRequest::getApplicant_id,now_user.getUid()).eq(ClaimRequest::getStatus,0).exists()){
            return  Result.fail("您之前的申请尚未处理");
        }else{
            save = claimRequestImpl.save(claimRequest);
            //作者表要更新
            if (save){
                return Result.ok("等待管理员审核");
            }else{
                return Result.fail("网络错误，请稍后再试");
            }
        }


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
        String fileName = file.getOriginalFilename()+getTimeNow();
        InputStream inputStream = file.getInputStream();
        String upload = qiNiuOssUtil.upload(inputStream, fileName);//upload为返回的图片外链地址
        User newUser = user.getById(myUser.getUid());
        newUser.setAvatar(upload);
        myUser.setAvatar(upload);
        user.saveOrUpdate(newUser);
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
        myUser.setAvatar(userInfo.getAvatar());
        boolean update = user.lambdaUpdate().eq(User::getUid, myUser.getUid()).update(myUser);
        if (update) return Result.ok("修改信息成功");
        else return Result.fail("修改信息失败");
    }

    @PostMapping("/create/appeal")
    @LoginCheck
    @Operation(summary = "申诉文章",security = { @SecurityRequirement(name = "bearer-key") })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "申诉理由 申诉的文章id")
    public Result<String> createAppeal(@RequestBody AppealInfo appealInfo){
        Paper targetPaper = paper.getBaseMapper().selectById(appealInfo.getPaper_id());
        if(!targetPaper.getIs_active()){
            return Result.fail("该文章已经申诉下架", 402);
        }

        PaperAppeal existAppeal = paperAppeal.lambdaQuery().ne(PaperAppeal::getStatus, -1)
                .eq(PaperAppeal::getPaper_id, appealInfo.getPaper_id())
                .eq(PaperAppeal::getApplicant_id, UserContext.getUser().getUid()).one();

        if(existAppeal != null){
            if(existAppeal.getStatus() == 0)
                return Result.fail("之前提交的申诉尚未处理", 401);
            else
                return Result.fail("您之前的申诉已经通过，但文章被恢复", 403);
        }

        paperAppeal.save(new PaperAppeal(UserContext.getUser().getUid(), getTimeNow(),
                appealInfo.getPaper_id(), appealInfo.getContent()));

        return Result.ok("创建申诉成功");
    }


}

