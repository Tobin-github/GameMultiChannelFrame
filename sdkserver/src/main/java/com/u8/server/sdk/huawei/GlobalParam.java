/*
Copyright (C) Huawei Technologies Co., Ltd. 2015. All rights reserved.
See LICENSE.txt for this sample's licensing information.
*/
package com.u8.server.sdk.huawei;

public class GlobalParam
{
    /**
     * 联盟为应用分配的应用ID
     */
    /**
     * APP ID
     */
    public static final String APP_ID = "10172150";
    
    /**
     * 浮标密钥，CP必须存储在服务端，然后通过安全网络（如https）获取下来，存储到内存中，否则存在私钥泄露风险
     */
    /**
     * private key for buoy, the CP need to save the key value on the server for security
     */
    /**************TODO:DELETE*******************/
    public static String BUOY_SECRET = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAK2yrlGlXXF6E+e2z8QHXB5cF+Eb434jGTf7H9AOOaSz4GceTSJakqKcrID6tntId4JbZXXhTNvMRubEXVwjlkhuH5kOLCN2dgaMzqyVtsKCf6YwR0KkRp84Cv84o9kyZDcmvPJvSNLOS+zGCzN7Iaxu7HPtauKlhGhGr3KrwWqdAgMBAAECgYAHgYEm5g5zqOLTUIMJ5YeFiFU/1QSvnrSoRqHJS9QR2fQIgLa0lVVg0YRiznK0QR1o9Kodve6kUN9/eVzPbno/9KUg3x2OYKLv7Sc3sDaLdM3u0PsTTOTmyKnLg7D8eTv12PP6N5vV5HvLLI8UCzyOvDleHBHp+Tc822scSe6aAQJBAO6Xo4LK1GRLF8oG9Ekd+AgvhAFo5V/uFNu3BxT2J99CxW4mZT+UCctbBWi18NQWo1XGXKS4AxAiA8E5kYQgW/UCQQC6Xvabbo1tKuG8WVyCRUufEZ04GUbaBcRvE/QHEXlwvbsKJAFKo6e3lF6nnYWyjzI/d5j2QYN1dKVdsHt0vxMJAkEAsSdspA+gNju/lSUmuyeCY+mL9VQChAEOAbnbi0fegRpd55Sgtt1fjFuwH3iAMaoBaw3W+gMbWx42dYEeN+GjBQJBAI7+C4IIGXSYASiM+6Br4HCEiDchla3z3NpI2eOOcbmhqN9H7sHAvQ7qRJGgF5N/sNLnRTIz49P7kmFG5gIWFFkCQDv4lcPjCffuW63qKn6exq6968v/J5IRYU0hBxV0OC6oFdZfI8QO/j8e46fwSLnsjGJDIzKRBp/8shY8N8BvNQM=";
    
    
    /**
     * 支付ID
     */
	 /**
     * Pay ID
     */
    public static final String PAY_ID = "900086000000103770";
    
    /**
     * 支付私钥，CP必须存储在服务端，然后通过安全网络（如https）获取下来，存储到内存中，否则存在私钥泄露风险
     */
    /**
     * private key for pay, the CP need to save the key value on the server for security
     */
    /**************TODO:DELETE*******************/
    public static String PAY_RSA_PRIVATE = "MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEAjdeJFzQ5fFZDpIKyf5n4UpiV+aommZ3B06SAXX0U+PwsQ68+WnZpFl7YPO1YzJoFE5SuIYOM6Ux1Id0vVZ23zwIDAQABAkA850sp93aneLLatHPIbmg9rt7WsMSaOS68nWmwusCCl7/whM1S1za1gil8kWCmW0KosXG/5m8R/IoKU25C95ohAiEA1nVhMDDC2JGiDYv+MqNJr9ncLjuT8YtHJgeAM4bM/f0CIQCpUTo+IKKzvKkBSXPsV0ZHubOZ54kSGjG9hNyh8L/wuwIfLTyVQ5UFhKkzhagB9qx63p0V1Kq8ijbWyy7J3BSTKQIhAJUob4ynp217d88gbDT6NXmeSG/+nqwJ02PHla47rntdAiEAryI2K6zcq77OCSEIRT4QyuvJKHXfq/be6GrLUH20QUM=";
    
    /**
     * 支付公钥
     */
    /**
     * public key for pay
     */
    public static final String PAY_RSA_PUBLIC = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAI3XiRc0OXxWQ6SCsn+Z+FKYlfmqJpmdwdOkgF19FPj8LEOvPlp2aRZe2DztWMyaBROUriGDjOlMdSHdL1Wdt88CAwEAAQ==";
    
    /**
     * 登录签名公钥
     */
	
	public static final String LOGIN_RSA_PUBLIC = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmKLBMs2vXosqSR2rojMzioTRVt8oc1ox2uKjyZt6bHUK0u+OpantyFYwF3w1d0U3mCF6rGUnEADzXiX/2/RgLQDEXRD22er31ep3yevtL/r0qcO8GMDzy3RJexdLB6z20voNM551yhKhB18qyFesiPhcPKBQM5dnAOdZLSaLYHzQkQKANy9fYFJlLDo11I3AxefCBuoG+g7ilti5qgpbkm6rK2lLGWOeJMrF+Hu+cxd9H2y3cXWXxkwWM1OZZTgTq3Frlsv1fgkrByJotDpRe8SwkiVuRycR0AHsFfIsuZCFwZML16EGnHqm2jLJXMKIBgkZTzL8Z+201RmOheV4AQIDAQAB";

    /*
     * 支付页面横竖屏参数：1表示竖屏，2表示横屏，默认竖屏
     */
    // portrait view for pay UI
	public static final int PAY_ORI = 1;
	// landscape view for pay UI
	public static final int PAY_ORI_LAND = 2;
    
    /**
     * 支付签名类型：RSA256
     */
    public static final String SIGN_TYPE = "RSA256";
    

	/**
	 * 生成签名时需要使用RSA的私钥，安全考虑，必须放到服务端，通过此接口使用安全通道获取
	 */
	/**
	 * the server url for getting the pay private key.The CP need to modify the
	 * value for the real server.
	 */
	public static final String GET_PAY_PRIVATE_KEY = "https://ip:port/HuaweiServerDemo/getPayPrivate";

	/**
	 * 调用浮标时需要使用浮标的私钥，安全考虑，必须放到服务端，通过此接口使用安全通道获取
	 */
	/**
	 * the server url for getting the buoy private key.The CP need to modify the
	 * value for the real server.
	 */
	public static final String GET_BUOY_PRIVATE_KEY = "https://ip:port/HuaweiServerDemo/getBuoyPrivate";
    
    public interface PayParams
    {
        public static final String USER_ID = "userID";
        
        public static final String APPLICATION_ID = "applicationID";
        
        public static final String AMOUNT = "amount";
        
        public static final String PRODUCT_NAME = "productName";
        
        public static final String PRODUCT_DESC = "productDesc";
        
        public static final String REQUEST_ID = "requestId";
        
        public static final String USER_NAME = "userName";
        
        public static final String SIGN = "sign";
        
        public static final String NOTIFY_URL = "notifyUrl";
        
        public static final String SERVICE_CATALOG = "serviceCatalog";
        
        public static final String SHOW_LOG = "showLog";
        
        public static final String SCREENT_ORIENT = "screentOrient";
        
        public static final String SDK_CHANNEL = "sdkChannel";
        
        public static final String URL_VER = "urlver";
        
        public static final String EXT_RESERVED = "extReserved";
        
        public static final String SIGN_TYPE = "signType";
    }
    
}
