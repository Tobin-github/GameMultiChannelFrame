package com.u8.server.sdk.coolpad;

/**
 * 酷派支付结果
 * Created by ant on 2015/9/15.
 */
public class CoolPadPayResult {

    private String exorderno	;			//商户订单号	String(64)	可选	商户订单号
    private String transid		;			//交易流水号	String(32)	必填	计费支付平台的交易流水号
    private String appid		;			//游戏id	String(20)	必填	平台为商户应用分配的唯一代码
    private String waresid		;			//商品编码	integer	必填	平台为应用内需计费商品分配的编码
    private String feetype		;			//计费方式	integer	必填	计费方式，具体定义见附录
    private String money		;			//交易金额	int(10)	是	本次交易的金额，单位：分
    private String count	    ;			//购买数量	int(10)	是	本次购买的商品数量
    private String result		;			//交易结果	integer	必填	交易结果：0–交易成功1–交易失败
    private String transtype	;           //交易类型	int(2)	是	交易类型：0 – 交易；1 – 冲正
    private String transtime	;			//交易完成时间	String(20)	必填
    private String cpprivate	;			//商户私有信息	String(64)	可选	商户私有信息
    private String paytype		;			//支付方式	integer	可选	支付方式，具体定义见附录

    public String getTranstype() {
        return transtype;
    }

    public void setTranstype(String transtype) {
        this.transtype = transtype;
    }



    public String getTransid() {
        return transid;
    }

    public void setTransid(String transid) {
        this.transid = transid;
    }


    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getWaresid() {
        return waresid;
    }

    public void setWaresid(String waresid) {
        this.waresid = waresid;
    }

    public String getFeetype() {
        return feetype;
    }

    public void setFeetype(String feetype) {
        this.feetype = feetype;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }


    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getTranstime() {
        return transtime;
    }

    public void setTranstime(String transtime) {
        this.transtime = transtime;
    }

    public String getCpprivate() {
        return cpprivate;
    }

    public void setCpprivate(String cpprivate) {
        this.cpprivate = cpprivate;
    }

    public String getPaytype() {
        return paytype;
    }

    public void setPaytype(String paytype) {
        this.paytype = paytype;
    }

    public String getExorderno() {
        return exorderno;
    }

    public void setExorderno(String exorderno) {
        this.exorderno = exorderno;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
