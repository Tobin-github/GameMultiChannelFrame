package com.u8.sdk.wxapi;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import com.u8.sdk.WXSDK;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


public class WXEntryActivity extends Activity implements IWXAPIEventHandler{
		
    private IWXAPI api;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);      
        
        Log.e("u8sdk", "WXentry oncreate "+WXSDK.APP_ID);
    	api = WXAPIFactory.createWXAPI(this, WXSDK.APP_ID, false);

    	api.handleIntent(getIntent(), this);
   
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		setIntent(intent);
        api.handleIntent(intent, this);
	}


	@Override
	public void onReq(BaseReq req) {
		switch (req.getType()) {
		case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
			break;
		case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
			break;
		default:
			break;
		}
	}

	public void onResp(BaseResp resp) {
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			SendAuth.Resp re = (SendAuth.Resp) resp;
			WXSDK.onLoginResult(re.code);
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			WXSDK.onResult(5, "user cancel");
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			WXSDK.onResult(5, "login failed");
			break;
		case BaseResp.ErrCode.ERR_UNSUPPORT:
			WXSDK.onResult(5, "login failed");
			break;
		default:
			break;
		}
		this.finish();
	}
}