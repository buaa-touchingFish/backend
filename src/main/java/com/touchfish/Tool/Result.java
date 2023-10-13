package com.touchfish.Tool;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Result {
    private Integer code;
    private String message;
    private Object data;

    public static Result ok(String message,Object data){
        return new Result(200,message,data);
    }

    public static Result ok(String message){
        return new Result(200,message,null);
    }

    public static Result fail(String message){
        return new Result(400,message,null);
    }

    public static Result fail(String message,Integer code) {return  new Result(code,message,null);}
}
