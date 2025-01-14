package com.kkyu.utils;


import com.kkyu.common.utils.MD5;
import org.junit.jupiter.api.Test;

public class Md5Test {

    @Test
    public void testMd5() {
        System.out.println(MD5.encrypt("123456"));
    }
}
