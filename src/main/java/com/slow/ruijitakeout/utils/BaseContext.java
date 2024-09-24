package com.slow.ruijitakeout.utils;

/**
 * 基于threadLocal封装工具类,用户保存和获取当前登录用户id
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 设置线程id
     *
     * @param id
     */
    public static void setCurrentId(long id) {
        threadLocal.set(id);
    }
    /**
     * 获取线程id
     */
    public static Long getCurrentId() {
        return threadLocal.get();
    }
}
