package com.u8.server.sdk;

import com.u8.server.log.Log;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by ant on 2015/10/12.
 */
public class UHttpAgent {

    private int connectTimeout = 5000;  //5s
    private int socketTimeout = 5000;   //5s
    private int maxTotalConnections = 200;  //最大连接数，可调整

    private static UHttpAgent instance;

    private CloseableHttpClient httpClient;

    public static String ServerHost = "http://120.25.243.133:8080";
    //public static String ServerHost = "http://www.wgb.cn:9080";
    //public static final String ServerHost = "http://u8s.wgb.cn:9080/pay/wxpay/payCallback";

    private UHttpAgent(){

    }

    public static UHttpAgent getInstance(){
        if(instance == null){
            instance = new UHttpAgent();
        }
        return instance;
    }

    public static UHttpAgent newInstance(){
        return new UHttpAgent();
    }

    public void get(String url, Map<String, String> params, UHttpFutureCallback callback){
        get(url, null, params, "UTF-8", callback);
    }

    public void post(String url, Map<String, String> headers, Map<String, String> params, UHttpFutureCallback callback){
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        if(params != null){

            for(String key : params.keySet()){
                pairs.add(new BasicNameValuePair(key, params.get(key)));
            }
        }

        post(url, headers, new UrlEncodedFormEntity(pairs, Charset.forName("UTF-8")), callback);
    }

    public void post(String url, Map<String, String> params, UHttpFutureCallback callback){

        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        if(params != null){

            for(String key : params.keySet()){
                pairs.add(new BasicNameValuePair(key, params.get(key)));
            }
        }

        post(url, null, new UrlEncodedFormEntity(pairs, Charset.forName("UTF-8")), callback);
    }

    /***
     * http get 请求 默认请求头 参数编码UTF-8
     * @param url       请求地址
     * @param params    请求参数
     * @return
     */
    public String get(String url, Map<String,String> params){

        return get(url, null, params, "UTF-8");
    }

    /***
     * http post 请求 默认请求头 参数编码UTF-8
     * @param url       请求地址
     * @param params    请求参数
     * @return
     */
    public String post(String url, Map<String, String> params){

        return post(url, null, params, "UTF-8");
    }

    /***
     * http get 异步请求方式 TODO: 目前实现还是同步，异步待实现
     * @param url
     * @param headers
     * @param params
     * @param encoding
     * @param callback
     */
    public void get(String url, Map<String, String> headers, Map<String,String> params, String encoding, UHttpFutureCallback callback){

        String result = get(url, headers, params, encoding);

        if(result != null){
            try {
                callback.completed(result);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            try {
                callback.failed(url + " failed");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /***
     * http post 异步请求方式 TODO: 目前实现还是同步，异步待实现
     * @param url
     * @param headers
     * @param entity
     * @param callback
     */
    public void post(String url, Map<String, String> headers, HttpEntity entity, UHttpFutureCallback callback){
        String result = post(url, headers, entity);
        if(result != null){
            try {
                callback.completed(result);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            try {
                callback.failed(url + "failed");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /***
     * http get 请求
     * @param url       请求地址
     * @param headers   请求头
     * @param params    参数
     * @param encoding  编码 UTF-8等
     * @return
     */
    public String get(String url , Map<String, String> headers, Map<String,String> params, String encoding){

        if(this.httpClient == null){
            init();
        }

        String fullUrl = url;
        String urlParams = parseGetParams(params, encoding);

        if (urlParams != null)
        {
            if (url.contains("?"))
            {
                fullUrl = url + "&" + urlParams;
            }
            else
            {
                fullUrl = url + "?" + urlParams;
            }
        }

        Log.d("the full url is "+ fullUrl);
        HttpGet getReq = new HttpGet(fullUrl.trim());
        getReq.setHeaders(parseHeaders(headers));
        ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
            @Override
            public String handleResponse(HttpResponse httpResponse) throws IOException {
                HttpEntity entity = httpResponse.getEntity();
                return entity != null ? EntityUtils.toString(entity) : null;
            }
        };

        try {

            String res = httpClient.execute(getReq, responseHandler);

            return res;

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            getReq.releaseConnection();
        }

        return null;
    }

    /***
     * http post 请求
     * @param url       请求地址
     * @param headers   请求头
     * @param params    参数
     * @param encoding  编码 UTF-8等
     * @return
     */
    public String post(String url, Map<String,String> headers, Map<String,String> params, String encoding){

        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        if(params != null){

            for(String key : params.keySet()){
                pairs.add(new BasicNameValuePair(key, params.get(key)));
            }
        }

        return post(url, headers, new UrlEncodedFormEntity(pairs, Charset.forName(encoding)));
    }

    /***
     * http post 请求
     * @param url       请求地址
     * @param headers   请求头
     * @param entity    参数内容
     * @return
     */
    public String post(String url, Map<String,String> headers, HttpEntity entity){

        if(this.httpClient == null) {
            init();
        }

        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeaders(parseHeaders(headers));
        httpPost.setEntity(entity);

        ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
            @Override
            public String handleResponse(HttpResponse httpResponse) throws IOException {
                HttpEntity entity = httpResponse.getEntity();

                return entity != null ? EntityUtils.toString(entity, Charset.forName("UTF-8")) : null;
            }
        };

        try {

            String body = httpClient.execute(httpPost, responseHandler);

            return body;

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            httpPost.releaseConnection();
        }

        return null;

    }

    //解析请求头
    private Header[] parseHeaders(Map<String, String> headers){
        if(null == headers || headers.isEmpty()){
            return getDefaultHeaders();
        }

        Header[] hs = new BasicHeader[headers.size()];
        int i = 0;
        for(String key : headers.keySet()){
            hs[i] = new BasicHeader(key, headers.get(key));
            i++;
        }

        return hs;
    }

    //获取默认的请求头
    private Header[] getDefaultHeaders(){
        Header[] hs = new BasicHeader[2];
        hs[0] = new BasicHeader("Content-Type", "application/x-www-form-urlencoded");
        hs[1] = new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36");
        return hs;
    }

    //将参数解析成get请求的参数格式
    private String parseGetParams(Map<String, String> data, String encoding){
        if(data == null || data.size() <= 0){
            return null;
        }

        StringBuilder result = new StringBuilder();

        Iterator<String> keyItor = data.keySet().iterator();
        while(keyItor.hasNext()){
            String key = keyItor.next();
            String val = data.get(key);

            try {
                result.append(key).append("=").append(URLEncoder.encode(val, encoding).replace("+", "%2B")).append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return result.deleteCharAt(result.length() - 1).toString();

    }

    //初始化， 创建httpclient实例
    private void init(){

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(connectTimeout)
                .setSocketTimeout(socketTimeout)
                .setExpectContinueEnabled(true)
                .setAuthenticationEnabled(true)
                .build();

        HttpRequestRetryHandler retryHandler = new HttpRequestRetryHandler() {
            @Override
            public boolean retryRequest(IOException e, int retryNum, HttpContext httpContext) {

                if(retryNum >= 3){
                    return false;
                }


                if(e instanceof NoHttpResponseException
                        || e instanceof ClientProtocolException
                        || e instanceof java.net.SocketException){

                    return true;
                }

                return false;
            }
        };

        ConnectionKeepAliveStrategy myStrategy = new ConnectionKeepAliveStrategy() {
            @Override
            public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
                try{

                    HeaderElementIterator it = new BasicHeaderElementIterator
                            (response.headerIterator(HTTP.CONN_KEEP_ALIVE));
                    while (it.hasNext()) {
                        HeaderElement he = it.nextElement();
                        String param = he.getName();
                        String value = he.getValue();
                        if (value != null && param.equalsIgnoreCase
                                ("timeout")) {
                            return Long.parseLong(value) * 1000;
                        }
                    }
                    return 3 * 1000;

                }catch(Exception e){
                    e.printStackTrace();
                }

                return 0;
            }
        };


        try{

            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                //信任所有
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();

            HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;
            SSLConnectionSocketFactory sslFactory = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);

            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", sslFactory)
                    .build();

            PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager( socketFactoryRegistry);
            connMgr.setMaxTotal(maxTotalConnections);
            connMgr.setDefaultMaxPerRoute((connMgr.getMaxTotal()));

            HttpClientBuilder builder = HttpClients.custom()
                    .setKeepAliveStrategy(myStrategy)
                    .setDefaultRequestConfig(requestConfig)
                    .setSslcontext(sslContext)
                    .setConnectionManager(connMgr)
                    .setRetryHandler(retryHandler);

            this.httpClient = builder.build();


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public HttpClient getHttpClient(){

        return this.httpClient;
    }

    //销毁
    public void destroy(){

        if(this.httpClient != null){
            try{
                this.httpClient.close();
                this.httpClient = null;
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }


}
