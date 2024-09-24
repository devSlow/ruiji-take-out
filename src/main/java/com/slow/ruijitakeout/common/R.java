package com.slow.ruijitakeout.common;

import lombok.Data;

/**
 * 通用返回结果类
 * @param <T>
 */

@Data
public class R<T> {
    private Integer code; // 1成功，0失败
    private String msg; //错误信息
    private T data; //数据

    public static <T> R<T> success(T data){
        R<T> r = new R<>();
        r.setCode(1);
        r.setData(data);
        return r;
    }

    public static <T> R<T> error(String msg){
        R<T> r = new R<>();
        r.setCode(0);
        r.setMsg(msg);
        return r;
    }
}
