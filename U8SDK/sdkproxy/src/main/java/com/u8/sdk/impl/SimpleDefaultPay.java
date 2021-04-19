package com.u8.sdk.impl;

import android.widget.Toast;

import com.u8.sdk.IPay;
import com.u8.sdk.PayParams;
import com.u8.sdk.U8SDK;

public class SimpleDefaultPay implements IPay{

	@Override
	public boolean isSupportMethod(String methodName) {
		return true;
	}

	@Override
	public void pay(PayParams data) {
		Toast.makeText(U8SDK.getInstance().getContext(), "调用[支付接口]接口成功，PayParams中的参数，除了extension，其他的请都赋值，最后还需要经过打包工具来打出最终的渠道包", Toast.LENGTH_LONG).show();
	}

}
