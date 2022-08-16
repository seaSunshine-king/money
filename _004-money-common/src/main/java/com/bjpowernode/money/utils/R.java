package com.bjpowernode.money.utils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 通用结果类，服务器响应的所以结果最终都会包装成此类型返回的前端页面
 * @param <T> 泛型，增加通用性
 */
public class R<T> implements Serializable {

    private Integer code; //编码：1成功，0和其它数字为失败

    private String msg; //错误信息

    private T data; //数据    T泛型，未指定的数据类型

    private Map map = new HashMap(); //动态数据

    //成功响应的方法
    public static <T> R<T> success(T object) {
        R<T> r = new R<T>();
        r.data = object;
        r.code = 1;
        return r;
    }

    //失败响应的方法
    public static <T> R<T> error(String msg) {
        R r = new R();
        r.msg = msg;
        r.code = 0;
        return r;
    }

    //操作map动态数据
    public R<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }

}