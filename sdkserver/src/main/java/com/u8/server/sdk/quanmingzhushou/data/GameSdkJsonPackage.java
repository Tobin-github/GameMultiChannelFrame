package com.u8.server.sdk.quanmingzhushou.data;

/**
 * author Administrator on 2015-09-21.
 * 公用外层报文包类
 */
public class GameSdkJsonPackage {

    protected String id;            //消息唯一标识
    protected String ver;           //报文版本号
    protected String devCode;       //游戏厂商编码
    protected String actionId;      //行为标识
    protected String data;          //数据内容
    protected String dataStr;       //解密后的报文
    protected String sign;          //数字签名

    /**
     * 获得需要签名是的字符串
     * @return 需要签名是的字符串
     */
    public String getSignString(){
        return "actionId=" + this.actionId + "data=" + this.data + "devCode=" + this.devCode+ "id=" + this.id  + "ver=" + this.ver;
    }

    /**
     * 产生一个id
     * @return 消息id
     */
    public static String genId(){
        return String.valueOf(System.nanoTime());
    }

    /**
     * 获得消息唯一标识
     * 用于标识一支交易，上送、回应报文此域保持一致。可以使用时间戳，例如：1332406591。
     * @return 消息唯一标识
     */
    public String getId() {
        return id;
    }

    /**
     * 设置消息唯一标识
     * 用于标识一支交易，上送、回应报文此域保持一致。可以使用时间戳，例如：1332406591。
     * @param id 消息唯一标识
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获得报文版本号
     * 用于获得报文格式、获取报文加密秘钥或签名秘钥。
     * @return 报文版本号
     */
    public String getVer() {
        return ver;
    }

    /**
     * 设置报文版本号
     * 用于获得报文格式、获取报文加密秘钥或签名秘钥。
     * @param ver 报文版本号
     */
    public void setVer(String ver) {
        this.ver = ver;
    }

    /**
     * 获得游戏厂商编码
     * 全民为开发商分配的标识编码。不同开发商使用不同秘钥。
     * @return 游戏厂商编码
     */
    public String getDevCode() {
        return devCode;
    }

    /**
     * 设置游戏厂商编码
     * 全民为开发商分配的标识编码。不同开发商使用不同秘钥。
     * @param devCode 游戏厂商编码
     */
    public void setDevCode(String devCode) {
        this.devCode = devCode;
    }

    /**
     * 获得行为标识
     * 用于告知对方消息类型。
     * @return 行为标识
     */
    public String getActionId() {
        return actionId;
    }

    /**
     * 设置行为标识
     * 用于告知对方消息类型。
     * @param actionId 行为标识
     */
    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    /**
     * 获得数据内容
     * @return 数据内容
     */
    public String getData() {
        return data;
    }

    /**
     * 设置数据内容
     * @param data 数据内容
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * 获得解密后的报文
     * @return 解密后的报文
     */
    public String getSign() {
        return sign;
    }

    /**
     * 设置解密后的报文
     * @param sign 解密后的报文
     */
    public void setSign(String sign) {
        this.sign = sign;
    }

    /**
     * 获得数字签名
     * @return 数字签名
     */
    public String getDataStr() {
        return dataStr;
    }

    /**
     * 设置数字签名
     * @param dataStr 数字签名
     */
    public void setDataStr(String dataStr) {
        this.dataStr = dataStr;
    }
}
