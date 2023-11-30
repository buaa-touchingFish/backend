package com.touchfish.Tool;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Result<T> {
    private Integer code;
    private String message;
    private T data;

    public static<T> Result<T> ok(String message,T data){
        return new Result<T>(200,message,data);
    }

    public static<T> Result<T> ok(String message){
        return new Result<T>(200,message,null);
    }

    public static<T> Result<T> fail(String message){
        return new Result<T>(400,message,null);
    }

    public static<T> Result<T> fail(String message,Integer code) {return  new Result<T>(code,message,null);}
}
