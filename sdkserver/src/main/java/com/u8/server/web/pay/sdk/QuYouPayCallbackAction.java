package com.u8.server.web.pay.sdk;

import com.u8.server.common.UActionSupport;
import com.u8.server.constants.PayState;
import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.log.Log;
import com.u8.server.service.UOrderManager;
import com.u8.server.utils.EncryptUtils;
import net.sf.json.JSONObject;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.Date;

/**
 * 趣游SDK支付回调处理类
 * Created by ant on 2016/1/21.
 */

@Controller
@Namespace("/pay/quyou")
public class QuYouPayCallbackAction extends UActionSupport{



    @Autowired
    private UOrderManager orderManager;


//    @Action("payCallback")
//    public void payCallback(){
//        try{
//
//            long orderID = Long.parseLong(CooOrderSerial);
//
//            UOrder order = orderManager.getOrder(orderID);
//
//            if(order == null){
//                Log.d("The order is null");
//                this.renderState(false);
//                return;
//            }
//
//            UChannel channel = order.getChannel();
//            if(channel == null){
//                Log.d("the channel is null.");
//                this.renderState(false);
//                return;
//            }
//
//            if(order.getState() == PayState.STATE_COMPLETE) {
//                Log.d("The state of the order is complete. The state is " + order.getState());
//                this.renderState(true);
//                return;
//            }
//
//            if(!isSignOK(channel)){
//                Log.d("The sign verify failed.sign:%s;appKey:%s;orderID:%s", Sign, channel.getCpPayKey(), CooOrderSerial);
//                this.renderState(false);
//                return;
//            }
//
//            if("1".equals(this.PayStatus)){
//
//                int moneyInt = (int)(Float.valueOf(OrderMoney) * 100);  //以分为单位
//
//                order.setRealMoney(moneyInt);
//                order.setSdkOrderTime(CreateTime);
//                order.setCompleteTime(new Date());
//                order.setChannelOrderID(ConsumeStreamId);
//                order.setState(PayState.STATE_SUC);
//
//                orderManager.saveOrder(order);
//
//                SendAgent.sendCallbackToServer(this.orderManager, order);
//
//            }else{
//                order.setChannelOrderID(ConsumeStreamId);
//                order.setState(PayState.STATE_FAILED);
//                orderManager.saveOrder(order);
//            }
//
//            renderState(true);
//
//        }catch(Exception e){
//            try {
//                renderState(false);
//            } catch (IOException e1) {
//                e1.printStackTrace();
//            }
//            e.printStackTrace();
//        }
//    }
//
//    private boolean isSignOK(UChannel channel){
//
//        String signTxt = String.format("%s%s%s%s%s%s%s%s%s%.2f%.2f%s%s%s%s", AppId, Act, ProductName, ConsumeStreamId,
//                CooOrderSerial, Uin,GoodsId, GoodsInfo, GoodsCount,
//                Float.valueOf(OriginalMoney), Float.valueOf(OrderMoney), Note, PayStatus,
//                CreateTime, channel.getCpAppKey());
//
//
//        Log.d("sign txt:"+signTxt);
//
//        String md5 = EncryptUtils.md5(signTxt).toLowerCase();
//
//        Log.d("md5:"+md5);
//
//        return md5.equalsIgnoreCase(this.Sign);
//    }

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

}
