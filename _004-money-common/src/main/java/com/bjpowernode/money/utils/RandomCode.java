package com.bjpowernode.money.utils;

import java.util.Random;

public class RandomCode {

    public static String randomCode(int num){
        Random r = new Random();
        String code = "";
        for(int i=0; i<num; i++){
            code += r.nextInt(10);
        }
        return code;
    }
}
