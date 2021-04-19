package com.u8.sdk;



public abstract class U8UserAdapter implements IUser{

	@Override
	public void login() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void loginCustom(String customData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void switchLogin() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean showAccountCenter() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void logout() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void submitExtraData(UserExtraData extraData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exit() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void postGiftCode(String code){
		
	}
	

	@Override
	public void realNameRegister() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void queryAntiAddiction() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public abstract boolean isSupportMethod(String methodName);





}
