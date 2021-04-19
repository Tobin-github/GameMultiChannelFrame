package com.u8.sdk.analytics;

import com.u8.sdk.U8SDK;
import com.u8.sdk.UserExtraData;
import com.u8.sdk.utils.GUtils;
import com.u8.sdk.verify.UToken;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;


public class UDAgent {

	private static UDAgent instance;
	
	private UDAgent(){
		
	}
	
	public static UDAgent getInstance(){
		if(instance == null){
			instance = new UDAgent();
		}
		return instance;
	}
	
	/**
	 * 统计功能初始化，上报设备信息
	 * @param context
	 */
	public void init(final Activity context){
		
		try{
			final UDevice device = UDManager.getInstance().collectDeviceInfo(context, U8SDK.getInstance().getAppID(), U8SDK.getInstance().getCurrChannel());
			if(device == null){
				Log.e("U8SDK", "collect device info failed");
				return;
			}
			
			SubmitTask task = new SubmitTask(new ISubmitListener() {
				
				@Override
				public void run() {
					UDManager.getInstance().submitDeviceInfo(context, U8SDK.getInstance().getAnalyticsURL(), U8SDK.getInstance().getAppKey(), device);
				}
			});
			task.execute();
		}catch(Exception e){
			Log.e("U8SDK", "submit device info failed.\n"+e.getMessage());
			e.printStackTrace();
		}
		

		
	}
	
	/**
	 * 上报用户信息到U8Server，供数据统计使用
	 * @param context
	 * @param user
	 */
	public void submitUserInfo(final Activity context, final UserExtraData user){
		
		try{
			
			UToken token = U8SDK.getInstance().getUToken();
			if(token == null){
				Log.e("U8SDK", "utoken is null. submit user info failed.");
				return;
			}
			
			final UUserLog log = new UUserLog();
			boolean sendable = false;
			switch(user.getDataType()){
			case UserExtraData.TYPE_CREATE_ROLE:
				log.setOpType(UUserLog.OP_CREATE_ROLE);
				sendable = true;
				break;
			case UserExtraData.TYPE_ENTER_GAME:
				sendable = true;
				log.setOpType(UUserLog.OP_ENTER_GAME);
				break;
			case UserExtraData.TYPE_LEVEL_UP:
				sendable = true;
				log.setOpType(UUserLog.OP_LEVEL_UP);
				break;
			case UserExtraData.TYPE_EXIT_GAME:
				sendable = true;
				log.setOpType(UUserLog.OP_EXIT);
				break;
			}			
			
			if(sendable){
				log.setUserID(token.getUserID());
				log.setAppID(U8SDK.getInstance().getAppID());
				log.setChannelID(U8SDK.getInstance().getCurrChannel());
				log.setServerID(user.getServerID()+"");
				log.setServerName(user.getServerName());
				log.setRoleID(user.getRoleID());
				log.setRoleName(user.getRoleName());
				log.setRoleLevel(user.getRoleLevel());
				log.setDeviceID(GUtils.getDeviceID(context));

				
				SubmitTask task = new SubmitTask(new ISubmitListener() {
					
					@Override
					public void run() {
						UDManager.getInstance().submitUserInfo(context, U8SDK.getInstance().getAnalyticsURL(), U8SDK.getInstance().getAppKey(), log);
					}
				});
				task.execute();	
			}

		}catch(Exception e){
			Log.e("U8SDK", "submit user info failed.\n"+e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	
	class SubmitTask extends AsyncTask<Void, Void, Void>{

		private ISubmitListener runListener;
		
		public SubmitTask(ISubmitListener run){
			this.runListener = run;
		}
		
		@Override
		protected Void doInBackground(Void... arg) {

			if(this.runListener != null){
				this.runListener.run();
			}
			
			return null;
		}
	
	}
	
	interface ISubmitListener {
		void run();
	}
	
}
