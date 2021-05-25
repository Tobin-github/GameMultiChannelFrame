package com.u8.server.constants;

/**
 * 唯一状态码
 */
public class StateCode {

    public static final int CODE_SUCCESS = 1;
    public static final int CODE_GAME_NONE = 2;
    public static final int CODE_CHANNEL_NONE = 3;
    public static final int CODE_MASTER_NONE = 4;
    public static final int CODE_AUTH_FAILED = 5;
    public static final int CODE_USER_NONE = 6;
    public static final int CODE_VERIFY_FAILED = 8;
    public static final int CODE_TOKEN_ERROR = 9;
    public static final int CODE_MONEY_ERROR = 10;
    public static final int CODE_ORDER_ERROR = 11;
    public static final int CODE_SIGN_ERROR = 12;
    public static final int CODE_PAY_CLOSED = 13;           //充值未开放
    public static final int CODE_CHANNEL_NOT_MATCH = 14;    //渠道和游戏没有对上
}
