package com.u8.server.service;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Objects;

/**
 * @author kris
 * @create 2018-01-04 10:02
 */
public class StringUtilsTest {

    @Ignore
    @Test
    public void testObjects() {
        int testStr = 10001;
        boolean stringResult = Objects.equals(10001, testStr);
        System.out.println(stringResult);
    }
}
