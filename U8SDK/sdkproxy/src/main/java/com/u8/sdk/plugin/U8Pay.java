package com.u8.sdk.plugin;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.Toast;

import com.u8.sdk.IPay;
import com.u8.sdk.PayParams;
import com.u8.sdk.SDKTools;
import com.u8.sdk.U8SDK;
import com.u8.sdk.base.PluginFactory;
import com.u8.sdk.impl.SimpleDefaultPay;
import com.u8.sdk.log.Log;
import com.u8.sdk.verify.U8Proxy;
import com.u8.sdk.verify.UOrder;

/***
 * 支付插件
 * @author xiaohei
 *
 */
public class U8Pay{
	
	private static U8Pay instance;
	
	private IPay payPlugin;
	
	private U8Pay(){
		
	}
	
	public static U8Pay getInstance(){
		if(instance == null){
			instance = new U8Pay();
		}
		return instance;
	}
	
	public void init(){
		this.payPlugin = (IPay)PluginFactory.getInstance().initPlugin(IPay.PLUGIN_TYPE);
		if(this.payPlugin == null){
			this.payPlugin = new SimpleDefaultPay();
		}
	}
	
	public boolean isSupport(String method){
		if(this.payPlugin == null){
			return false;
		}
		
		return this.payPlugin.isSupportMethod(method);
	}
	
	/***
	 * 支付接口（弹出支付界面）
	 * @param data
	 */
	public void pay(PayParams data){
		if(this.payPlugin == null){
			return;
		}
		
		Log.d("U8SDK", "****PayParams Print Begin****");
		Log.d("U8SDK", "productId="+data.getProductId());
		Log.d("U8SDK", "productName="+data.getProductName());
		Log.d("U8SDK", "productDesc="+data.getProductDesc());
		Log.d("U8SDK", "price="+data.getPrice());
		Log.d("U8SDK", "coinNum="+data.getCoinNum());
		Log.d("U8SDK", "serverId="+data.getServerId());
		Log.d("U8SDK", "serverName="+data.getServerName());
		Log.d("U8SDK", "roleId="+data.getRoleId());
		Log.d("U8SDK", "roleName="+data.getRoleName());
		Log.d("U8SDK", "roleLevel="+data.getRoleLevel());
		Log.d("U8SDK", "vip="+data.getVip());
		Log.d("U8SDK", "orderID="+data.getOrderID());
		Log.d("U8SDK", "extension="+data.getExtension());
		Log.d("U8SDK", "****PayParams Print End****");		
		
		if(U8SDK.getInstance().isGetOrder()){
			
			startOrderTask(data);
		}else{
			this.payPlugin.pay(data);
		}

	}
	
	//默认的AsyncTask的执行顺序可能会有些影响，导致队列中的任务并不能被及时执行
	private void startOrderTask(PayParams data){
		GetOrderTask authTask = new GetOrderTask(data);
        if (Build.VERSION.SDK_INT >= 11) //Build.VERSION_CODES.HONEYCOMB
        {
        	authTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        else
        {
        	authTask.execute();
        }
	}
	
	class GetOrderTask extends AsyncTask<Void, Void, UOrder>{

		private PayParams data;
		
		private ProgressDialog processTip;
		
		public GetOrderTask(PayParams data){
			this.data = data;
		}

		protected void onPreExecute(){
			processTip = SDKTools.showProgressTip(U8SDK.getInstance().getContext(), "正在启动支付，请稍后...");
		}
		
		@Override
		protected UOrder doInBackground(Void... args) {
			Log.d("U8SDK", "begin to get order id from u8server...");
			UOrder result = U8Proxy.getOrder(data);
			
			return result;
		}
		
		protected void onPostExecute(UOrder order){
			
			SDKTools.hideProgressTip(processTip);
			
			if(order == null){
				Log.e("U8SDK", "get order from u8server failed.");
				Toast.makeText(U8SDK.getInstance().getContext(), "获取订单号失败", Toast.LENGTH_SHORT);
				return;
			}
			
			data.setOrderID(order.getOrder());
			data.setExtension(order.getExtension());
			payPlugin.pay(data);
		}
		
	}
}
