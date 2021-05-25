package com.u8.server.service;

import com.u8.server.utils.U8OrderIDHexUtils;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author kris
 * @create 2018-01-10 11:55
 */
public class OrderTranTest {

    @Ignore
    @Test
    public void orderTurnAndBack() {
        long orderId = 1470051912534458379L;
        String littleOrderId = U8OrderIDHexUtils.encode(orderId);
        System.out.println(littleOrderId);
        long realOrderId = U8OrderIDHexUtils.decode(littleOrderId);
        System.out.println(realOrderId);
    }
}
