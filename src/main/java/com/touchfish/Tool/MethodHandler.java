package com.touchfish.Tool;

import cn.hutool.json.JSONUtil;
import com.touchfish.Po.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.lang.reflect.Method;


public class MethodHandler  implements HandlerInterceptor {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String username = null;
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        // 判断接口是否有Log注解
        LoginCheck logMethod = method.getAnnotation(LoginCheck.class);
        boolean flag = true;
        boolean nolog = true;
        if(null != logMethod){
            nolog = false;
            String jwt = request.getHeader("Authorization").substring(7);
//            System.out.println(jwt);
            if (jwt == null) {
                flag = false;
            }else{
                try {
                    username = JWT.extractUsername(jwt);
                }catch (Exception e){
                    flag = false;
                }
            }
        }
        if (!flag) {
            response.setCharacterEncoding("utf-8");
            String msg  = JSONUtil.toJsonStr(Result.fail("请重新登录"));
            response.setStatus(403);
            response.getWriter().print(msg);
            return false;
        }
        if (!nolog){
            String jsonstr = stringRedisTemplate.opsForValue().get(RedisKey.JWT_KEY+username);
            User nowUser = JSONUtil.toBean(jsonstr, User.class);
            UserContext.setUser(nowUser);
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        UserContext.clearUser();
    }
}
