package com.touchfish.Tool;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RedisKey {
    public static final  String CATPTCHA_KEY = "register:captcha:";
    public static final  String JWT_KEY = "user:jwt:";

    public static final String PAPER_KEY = "paper:";
    public static final String AUTHOR_KEY = "author:";
    public static final String SEARCH_KEY = "search:";

    public static final String SUM_LOOK_KEY = "sumlook:";

    public static final String COLLECT_CNT_KEY = "collect_cnt:";

    public static final String GOOD_CNT_KEY = "good_cnt:";

    public static final String BROWSE_CNT_KEY = "browse_cnt:";

    public static final String LOGIN_KEY = "login:";


    public  static String getEveryDayKey(){
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(date);
    }


}
