package com.bjpowernode.money.utils;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {
    private static ExecutorService instance = null;

    public static ExecutorService getInstance(){
        if(instance==null){
            instance = Executors.newFixedThreadPool(8);
            return instance;
        }
        return instance;
    }
}
