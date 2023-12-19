package com.touchfish.Tool;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class QiNiuOssUtil {


    /**
     * 存储空间名
     */
    private static final String BUCKET = "touchingfish";
    /**
     * accessKey和secretKey
     */
    private static final String ACCESS_KEY = "6VPU2lyFULQxExcLBlSofqDy9CV5AKINuS8TawG2";
    private static final String SECRET_KEY = "wzg2rtEdXzuaiR7m4zBXek_HVlK_TUfvu2-ZeO8u";
    /**
     * 外网访问地址(内置域名有效期只有30天)
     */
    private static final String BASE_URL = "s5usfv19s.hb-bkt.clouddn.com/";

    /**
     * 上传管理器
     */
    private UploadManager upload;
    /**
     * 桶管理器（存储空间管理器）
     */
    private BucketManager bucket;

    public QiNiuOssUtil() {
        //创建配置对象
        Configuration cfg = new Configuration();
        //创建上传管理器
        upload = new UploadManager(cfg);
        //创建存储空间管理器
        bucket = new BucketManager(getAuth(), cfg);
    }

    /**
     * 返回认证器（包含的访问密钥）
     */
    private Auth getAuth() {
        return Auth.create(ACCESS_KEY, SECRET_KEY);
    }

    /**
     * 获取令牌对象（服务器返回的授权信息）
     */
    private String getToken() {
        return getAuth().uploadToken(BUCKET);
    }

    /**
     * 上传文件
     */
    public String upload(InputStream is, String key) throws QiniuException {
        //上传流
        Response response = upload.put(is, key, getToken(), null, null);
        //解析返回结果
        DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
        //将文件的访问地址返回
        return BASE_URL + putRet.key;
    }

}
