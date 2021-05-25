package com.u8.server.utils;

import java.util.*;

/**
 * Created by ant on 2016/7/30.
 */
public class StringUtils {

    public static boolean isEmpty(String str){

        return str == null || str.trim().length() == 0;
    }

    public static List<String> split2list(String str, String regx){

        List<String> result = new ArrayList<String>();

        if(isEmpty(str)){
            return result;
        }

        String[] splits = str.split(regx);
        if(splits != null){
            for(String s : splits){
                if(!isEmpty(s)){
                    result.add(s);
                }
            }
        }

        return result;
    }

    /**
     * 按照URL参数格式，进行参数字符串拼接
     * @param params
     * @param splitChar
     * @return
     */
    public static String generateUrlSortedParamString(Map<String,String> params, String splitChar, boolean nullExcluded) {

        StringBuffer content = new StringBuffer();
        // 按照key做排序
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key) == null ? "" : params.get(key).toString();
            if(nullExcluded && value.length() == 0){
                continue;
            }
            content.append( key + "=" + value);
            if(!StringUtils.isEmpty(splitChar)){
                content.append(splitChar);
            }
        }
        if(content.length() > 0 && !StringUtils.isEmpty(splitChar)){
            content.deleteCharAt(content.length() - 1);
        }

        return content.toString();
    }

    /**
     * 按照URL参数格式，进行参数字符串拼接，不排序
     * @param params
     * @param splitChar
     * @return
     */
    public static String generateUrlParamString(Map<String,String> params, String splitChar, boolean nullExcluded) {

        StringBuffer content = new StringBuffer();
        // 按照key做排序

        Iterator<String> it = params.keySet().iterator();

        while(it.hasNext()){
            String key = it.next();
            String val = params.get(key) == null ? "" : params.get(key).toString();
            if(nullExcluded && val.length() == 0){
                continue;
            }
            content.append( key + "=" + val);
            if(!StringUtils.isEmpty(splitChar)){
                content.append(splitChar);
            }
        }

        if(content.length() > 0 && !StringUtils.isEmpty(splitChar)){
            content.deleteCharAt(content.length() - 1);
        }

        return content.toString();
    }
}
