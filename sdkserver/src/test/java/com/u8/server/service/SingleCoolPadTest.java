package com.u8.server.service;

import com.u8.server.sdk.coolpad.api.CpTransSyncSignValid;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author kris
 * @create 2018-01-08 9:35
 */
public class SingleCoolPadTest {


    @Ignore
    @Test
    public void validSign() {
        String transdata = "{\"exorderno\":\"1462306536016576516\",\"transid\":\"T3118010518321187789\",\"waresid\":2,\"appid\":\"5000009189\",\"feetype\":0,\"money\":100,\"count\":1,\"result\":0,\"transtype\":0,\"transtime\":\"2018-01-05 18:32:41\",\"cpprivate\":\"1462306536016576516\",\"paytype\":403}";
        String sign = "c4ff363a06e72aca8cb39c4faac8da1 541dd274440e50dcd4f51f25847c6f5d 13759537ec9650b804cb7942a3db1b91 ";
        String appSecret = "MUJCRTUzMTMyNEUzQzMxNTA5MTZDMjIxMEJENTUxMjc5Q0RCNUREMU1UWTJNRFkxTVRVeU1ETTJNVGszT0RNMk56a3JNakk0T0RRM056VTFNRGswTXpneU5Ua3dPVGt5TlRjeE5EZzFNakl3T1Rjd05URTBNRGM1";

        boolean signResult = CpTransSyncSignValid.validSign(transdata, sign, appSecret);
        System.out.println(signResult);
    }
}
