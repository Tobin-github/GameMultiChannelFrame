package com.u8.sdk.wxapi;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.u8.sdk.SDK;



public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler{
	
    private IWXAPI api;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("u8sdk", "WXPayentry oncreate "+SDK.APP_ID);
    	api = WXAPIFactory.createWXAPI(this, SDK.APP_ID);
        api.handleIntent(getIntent(), this);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	public void onReq(BaseReq req) {
	}

	public void onResp(BaseResp resp) {
		Log.d("wxsdk", "onPayFinish, errCode = " + resp.errCode);
		if(resp.getType()==ConstantsAPI.COMMAND_PAY_BY_WX){
			if(resp.errCode == 0){
				SDK.onResult(10, "pay success");
			}else{
				SDK.onResult(11, "pay failed:"+resp.errCode);
			}
		}
		this.finish();
	}
}