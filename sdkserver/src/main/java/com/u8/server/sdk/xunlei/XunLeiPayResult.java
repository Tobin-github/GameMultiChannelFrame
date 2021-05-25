package com.u8.server.sdk.xunlei;

/**
 * Created by ant on 2016/12/7.
 */
public class XunLeiPayResult {

    private XunLeiOrder order_info;
    private String sign;
    private String sign_type;


    public static class XunLeiOrder{

        private String body             ; //str 商品详细信息 畅玩包
        private String channel_pay_uid  ; //str ⽀付渠道的⽀付⽤户id，如微信id和⽀付宝id
        private String order_id         ; //str 迅雷的订单id，后台可查询到的流⽔id
        private String channel_trade_no ; //str⽀付渠道的订单id，即第三⽅的订单id, 如⽀付宝订单id 和微信订单id
        private String server_id        ; //str 游戏服务器id,暂不⽤
        private String create_time      ; //str 创建时间
        private String pay_channel      ; //str ⽀付渠道
        private String pay_time         ; //str ⽀付到账时间
        private String game_id          ; //int 游戏id 44
        private String subject          ; //str ⽀付商品标题
        private String pay_uid          ; //str ⽀付者的迅雷uid
        private String total_amount     ; //str ⽀付的商品总价, 两位⼩数。单位元
        private String notify_status    ; //str
        private String product_id       ; //str 透传的产品信息id或游戏⽅的特定订单信息，可供游戏 商⾃⾏扩展改字段
        private String pay_status       ; //str ⽀付通知处理状态 ，仅success成功的订单才会通知。 success

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public String getChannel_pay_uid() {
            return channel_pay_uid;
        }

        public void setChannel_pay_uid(String channel_pay_uid) {
            this.channel_pay_uid = channel_pay_uid;
        }

        public String getOrder_id() {
            return order_id;
        }

        public void setOrder_id(String order_id) {
            this.order_id = order_id;
        }

        public String getChannel_trade_no() {
            return channel_trade_no;
        }

        public void setChannel_trade_no(String channel_trade_no) {
            this.channel_trade_no = channel_trade_no;
        }

        public String getServer_id() {
            return server_id;
        }

        public void setServer_id(String server_id) {
            this.server_id = server_id;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public String getPay_channel() {
            return pay_channel;
        }

        public void setPay_channel(String pay_channel) {
            this.pay_channel = pay_channel;
        }

        public String getPay_time() {
            return pay_time;
        }

        public void setPay_time(String pay_time) {
            this.pay_time = pay_time;
        }

        public String getGame_id() {
            return game_id;
        }

        public void setGame_id(String game_id) {
            this.game_id = game_id;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getPay_uid() {
            return pay_uid;
        }

        public void setPay_uid(String pay_uid) {
            this.pay_uid = pay_uid;
        }

        public String getTotal_amount() {
            return total_amount;
        }

        public void setTotal_amount(String total_amount) {
            this.total_amount = total_amount;
        }

        public String getNotify_status() {
            return notify_status;
        }

        public void setNotify_status(String notify_status) {
            this.notify_status = notify_status;
        }

        public String getProduct_id() {
            return product_id;
        }

        public void setProduct_id(String product_id) {
            this.product_id = product_id;
        }

        public String getPay_status() {
            return pay_status;
        }

        public void setPay_status(String pay_status) {
            this.pay_status = pay_status;
        }
    }


    public XunLeiOrder getOrder_info() {
        return order_info;
    }

    public void setOrder_info(XunLeiOrder order_info) {
        this.order_info = order_info;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getSign_type() {
        return sign_type;
    }

    public void setSign_type(String sign_type) {
        this.sign_type = sign_type;
    }
}
