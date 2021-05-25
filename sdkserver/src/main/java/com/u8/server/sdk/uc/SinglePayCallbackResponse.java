package com.u8.server.sdk.uc;

/**
 * 支付回调的响应内容类。
 */
public class SinglePayCallbackResponse {
	String sign = "";
	PayCallbackResponseData data;
	
	public String getSign(){
		return this.sign;
	}
	public void setSign(String sign){
		this.sign =sign;
	}
	
	public PayCallbackResponseData getData(){
		return this.data;
	}
	public void setData(PayCallbackResponseData data){
		this.data = data;
	}
	
	public static class PayCallbackResponseData{
		private String tradeId;
		private String tradeTime;
		private String orderId;
		private String gameId;
		private String amount;
		private String payType;
		private String attachInfo;
		private String orderStatus;
		private String failedDesc;

		public String getTradeId() {
			return tradeId;
		}

		public void setTradeId(String tradeId) {
			this.tradeId = tradeId;
		}

		public String getTradeTime() {
			return tradeTime;
		}

		public void setTradeTime(String tradeTime) {
			this.tradeTime = tradeTime;
		}

		public String getOrderId() {
			return orderId;
		}

		public void setOrderId(String orderId) {
			this.orderId = orderId;
		}

		public String getGameId() {
			return gameId;
		}

		public void setGameId(String gameId) {
			this.gameId = gameId;
		}

		public String getAmount() {
			return amount;
		}

		public void setAmount(String amount) {
			this.amount = amount;
		}

		public String getPayType() {
			return payType;
		}

		public void setPayType(String payType) {
			this.payType = payType;
		}

		public String getAttachInfo() {
			return attachInfo;
		}

		public void setAttachInfo(String attachInfo) {
			this.attachInfo = attachInfo;
		}

		public String getOrderStatus() {
			return orderStatus;
		}

		public void setOrderStatus(String orderStatus) {
			this.orderStatus = orderStatus;
		}

		public String getFailedDesc() {
			return failedDesc;
		}

		public void setFailedDesc(String failedDesc) {
			this.failedDesc = failedDesc;
		}

		@Override
		public String toString() {
			return "PayCallbackResponseData{" +
					"tradeId='" + tradeId + '\'' +
					", tradeTime='" + tradeTime + '\'' +
					", orderId='" + orderId + '\'' +
					", gameId='" + gameId + '\'' +
					", amount='" + amount + '\'' +
					", payType='" + payType + '\'' +
					", attachInfo='" + attachInfo + '\'' +
					", orderStatus='" + orderStatus + '\'' +
					", failedDesc='" + failedDesc + '\'' +
					'}';
		}
	}

	
}
