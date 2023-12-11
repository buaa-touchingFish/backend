//package com.touchfish.Tool;
//
//import cn.hutool.json.JSONArray;
//import cn.hutool.json.JSONObject;
//import cn.hutool.json.JSONUtil;
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.net.URL;
//import java.net.URLConnection;
//
//public class HttpApi {
//
//    /**
//     * 向指定URL发送GET方法的请求
//     *
//     * @param url   发送请求的URL
//     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
//     * @return URL 所代表远程资源的响应结果
//     */
//    public static String sendGet(String url, String param) {
//        String result = "";
//        BufferedReader in = null;
//        try {
//            String urlNameString = url + "?" + param;
//            URL realUrl = new URL(urlNameString);
//            // 打开和URL之间的连接
//            URLConnection connection = realUrl.openConnection();
//            // 设置通用的请求属性
//            connection.setRequestProperty("Content-type", "application/json; charset=UTF-8");
//            // 建立实际的连接
//            connection.connect();
//            // 定义 BufferedReader输入流来读取URL的响应
//            in = new BufferedReader(new InputStreamReader(
//                    connection.getInputStream()));
//            String line;
//            while ((line = in.readLine()) != null) {
//                result += line;
//            }
//            result = new String(result.getBytes(), "UTF-8");
//        } catch (Exception e) {
//            System.out.println("发送GET请求出现异常！" + e);
//            e.printStackTrace();
//        } finally {
//            // 使用finally块来关闭输入流
//            try {
//                if (in != null) {
//                    in.close();
//                }
//            } catch (Exception e2) {
//                e2.printStackTrace();
//            }
//        }
////        System.out.println("返回的结果：" +result);
//        return result;
//    }
//
//    public static String formatJson(String jsonString) {
//        // 使用正则表达式替换掉不必要的空格和缩进
////        jsonString = jsonString.replaceAll("\\{", "\\{\n");
////        jsonString = jsonString.replaceAll("\\}", "\n\\}");
////        jsonString = jsonString.replaceAll("\\[", "\\[\n");
////        jsonString = jsonString.replaceAll("\\]", "\n\\]");
//        jsonString = jsonString.replaceAll(",", ",\n\n");
////        jsonString = jsonString.replaceAll("\":\\{", "\":\\{\n");
////        jsonString = jsonString.replaceAll("\":\\[", "\":\\[\n");
//        jsonString = jsonString.replaceAll("\",\"", "\",\n\"");
////        jsonString = jsonString.replaceAll("\":\"", "\":\n\"");
//        return jsonString;
//    }
//
//
//
//    public static void main(String[] args) {
//        String str =sendGet("https://api.openalex.org/authors","per-page=50&page=1");
//        JSONObject jsonObject = JSONUtil.parseObj(str);
//        JSONArray res_array = new JSONArray(jsonObject.getStr("results"));
//
//        String formattedJsonString = formatJson(res_array.get(1).toString());
//
//        System.out.println(formattedJsonString);
//
//        System.out.println("\n\n\n\n\n\n\n");
//
//    }
//}
