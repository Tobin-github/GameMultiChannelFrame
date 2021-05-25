package com.u8.server.web.pay.sdk;

import com.u8.server.common.UActionSupport;
import com.u8.server.constants.PayState;
import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.log.Log;
import com.u8.server.service.UOrderManager;
import com.u8.server.utils.EncryptUtils;
import com.u8.server.web.pay.SendAgent;
import net.sf.json.JSONObject;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.Date;

/**
 * 免商店（蜗牛）支付回调处理类
 * Created by ant on 2016/1/21.
 */
@Controller
@Namespace("/pay/mianshangdian")
public class MSDPayCallbackAction extends UActionSupport{

    private String AppId  				;		//应用 ID
    private String Act  				;		//1
    private String ProductName  		;		//游戏名称，encode
    private String ConsumeStreamId  	;		//消费流水号，encode
    private String CooOrderSerial  		;		//商户订单号，encode
    private String Uin   				;		//蜗牛账号 ID
    private String GoodsId  			;		//商品  ID
    private String GoodsInfo  			;		//商品名称（比如点数） ，encode
    private String GoodsCount  			;		//商品数量
    private String OriginalMoney  		;		//原始总价(格式：0.00)  蜗牛币
    private String OrderMoney  			;		//实际总价(格式：0.00)  游戏点数
    private String Note  				;		//即支付描述（客户端 API  参数中的 payDesc 字段）
    private String PayStatus  			;		//支付状态：0=失败，1=成功
    private String CreateTime  			;		//创建时间(yyyy-MM-dd HH:mm:ss)  ，encode
    private String Sign  				;		//以上参数的  MD5  值，其中  AppKey  为平台分配的应用密钥

    @Autowired
    private UOrderManager orderManager;


    @Action("payCallback")
    public void payCallback(){
        try{

            long orderID = Long.parseLong(CooOrderSerial);

            UOrder order = orderManager.getOrder(orderID);

            if(order == null){
                Log.d("The order is null");
                this.renderState(false);
                return;
            }

            UChannel channel = order.getChannel();
            if(channel == null){
                Log.d("the channel is null.");
                this.renderState(false);
                return;
            }

            if(order.getState() > PayState.STATE_PAYING) {
                Log.d("The state of the order is complete. The state is " + order.getState());
                this.renderState(true);
                return;
            }

            if(!isSignOK(channel)){
                Log.d("The sign verify failed.sign:%s;appKey:%s;orderID:%s", Sign, channel.getCpPayKey(), CooOrderSerial);
                this.renderState(false);
                return;
            }

            if("1".equals(this.PayStatus)){

                int moneyInt = (int)(Float.valueOf(OrderMoney) * 100);  //以分为单位

                order.setRealMoney(moneyInt);
                order.setSdkOrderTime(CreateTime);
                order.setCompleteTime(new Date());
                order.setChannelOrderID(ConsumeStreamId);
                order.setState(PayState.STATE_SUC);

                orderManager.saveOrder(order);

                SendAgent.sendCallbackToServer(this.orderManager, order);

            }else{
                order.setChannelOrderID(ConsumeStreamId);
                order.setState(PayState.STATE_FAILED);
                orderManager.saveOrder(order);
            }

            renderState(true);

        }catch(Exception e){
            try {
                renderState(false);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    private boolean isSignOK(UChannel channel){

        String signTxt = String.format("%s%s%s%s%s%s%s%s%s%.2f%.2f%s%s%s%s", AppId, Act, ProductName, ConsumeStreamId,
                CooOrderSerial, Uin,GoodsId, GoodsInfo, GoodsCount,
                Float.valueOf(OriginalMoney), Float.valueOf(OrderMoney), Note, PayStatus,
                CreateTime, channel.getCpAppKey());


        Log.d("sign txt:"+signTxt);

        String md5 = EncryptUtils.md5(signTxt).toLowerCase();

        Log.d("md5:"+md5);

        return md5.equalsIgnoreCase(this.Sign);
    }

    private void renderState(boolean suc) throws IOException {

        JSONObject json = new JSONObject();
        if(suc){
            json.put("ErrorCode", 1);
            json.put("ErrorDesc","接收成功");
        }else{
            json.put("ErrorCode", 0);
            json.put("ErrorDesc","接收失败");
        }

        renderJson(json.toString());
    }

    public String getAppId() {
        return AppId;
    }

    public void setAppId(String appId) {
        AppId = appId;
    }

    public String getAct() {
        return Act;
    }

    public void setAct(String act) {
        Act = act;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getConsumeStreamId() {
        return ConsumeStreamId;
    }

    public void setConsumeStreamId(String consumeStreamId) {
        ConsumeStreamId = consumeStreamId;
    }

    public String getCooOrderSerial() {
        return CooOrderSerial;
    }

    public void setCooOrderSerial(String cooOrderSerial) {
        CooOrderSerial = cooOrderSerial;
    }

    public String getUin() {
        return Uin;
    }

    public void setUin(String uin) {
        Uin = uin;
    }

    public String getGoodsId() {
        return GoodsId;
    }

    public void setGoodsId(String goodsId) {
        GoodsId = goodsId;
    }

    public String getGoodsInfo() {
        return GoodsInfo;
    }

    public void setGoodsInfo(String goodsInfo) {
        GoodsInfo = goodsInfo;
    }

    public String getGoodsCount() {
        return GoodsCount;
    }

    public void setGoodsCount(String goodsCount) {
        GoodsCount = goodsCount;
    }

    public String getOriginalMoney() {
        return OriginalMoney;
    }

    public void setOriginalMoney(String originalMoney) {
        OriginalMoney = originalMoney;
    }

    public String getOrderMoney() {
        return OrderMoney;
    }

    public void setOrderMoney(String orderMoney) {
        OrderMoney = orderMoney;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        Note = note;
    }

    public String getPayStatus() {
        return PayStatus;
    }

    public void setPayStatus(String payStatus) {
        PayStatus = payStatus;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public String getSign() {
        return Sign;
    }

    public void setSign(String sign) {
        Sign = sign;
    }
}
