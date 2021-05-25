package com.u8.server.service;

import com.u8.server.sdk.huawei.Base64;
import com.u8.server.sdk.huawei.RSAUtil;
import org.junit.Ignore;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author kris
 * @create 2018-01-16 17:24
 */
public class SingleHuaweiValidSignTest {

    @Ignore
    @Test
    public void validSign() {
        String appId = "";
        String cpId = "";
        String priKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCHJJ1kxhOXDh4M9QgXj2sXahxG7clGnWDp5YW9f/+Xyf2RzZma1JB76KqXh2ZNyJfpG/4tsUqm1KBQ3w1glvsvUsCcuiAhVmT3CkN7+M/S+ttcXIlNfT+x2UH4h50d1IPLr7qSjgiBkPcW2WFFXxRfaUqSg7xhLp+9ydXJJFQbvTBKCpr2HFRl4DEuF5SxganG8SQau/swCf2l3lHnrIm4ER5B+O4RNqYr/AMo6tge1LXaYp9ss8TM3VmTTGNHGcIaPvSIeBsT4XolhkZ0RyyT++m1ABSKn1kA8Rv5vjzj5WbHDi8LefreQ3gcBax26h/6tbvgTgMwIDzokRrUOM39AgMBAAECggEAcpm7GtTZkfP3ybcUKJ6HCvEBj6hfUZFtuIrZccwUW4x/id/WzTRKXbj8yMiaGYXsRFJnpim9C2ItnMa5mloOIaBEE+PGEV8o+VDrzzo8SkZONLGIAX0fwVpiFjYyJzSqmtSnG1Z0oiLjVa37TY+GQC6SfVJXMfYOoiuBLjOvW2FB8AJQX6G8dU6S4WZc1RdJ9ZyyQgcfKtt/kvja/JroMkOx2SQ13Yd7BU19xFVJCPiEPF9K+CtTIYO/hkHdUwh8+p74QaEHemJ1HfE2bFtZMEplBoSa06zwzUVx0WV1Y3j/qg7rxrDNl6ITmWTyQEn3bdJn0XPrvcRmv4sgce1K4QKBgQDWNyEUlg3yMKJyNmnK8oLUggXNkBC7ayRkN67MsUdS9gsFBhlOZeocrzb+T9GZUpVfDGmLO5708P8eZbRDDNRSVMyr0eV89sPXx579kyV9/nMzA65LasldlNC15gyr61/ldp6Uleb3bUEeYe7AkRnzSrJfLOTLUoTi7huVY59vKQKBgQChgP/FoWD9U/Ahz72IGS3CFFeHZMjFQfryrVBu5aL8iXeTj9ansKeUbIecYW3UqS+HbO6vbx+VvHeG5wlSEbernMNWrny5FQzeichKjbOkXmp50dQVvuebyvPYaIAkB7BFLEXqk0rBn5TPPQFS4bLcXe/xeCkF0Gd+uBrI4IpGtQKBgG1uFjkU+qTZUXL09xBU2J7EmUBMsy966UlE5MfuXBg2VqTHW9Af4furSnWZwuIHPQUkKxqUZ3yLTFhz7iU+fYxdg3zWqdwvlxY5BLBXJhT6ElFiNPyT3bAvoHr7vUdp40AuW45eEXIeXuCteLDorxAI/Zv/LBXt3rKqnm6vSLgZAoGASa7dAoGaCnndOM/anNk/8yfstyzYHIb5wvYnmDDUp3rgP0aEnIUQL7tEM6iPv1JhCNw+GXQNaPdPYRDPQ84pifY/eLCq3pYoBO+/naQAraEV2vZMWI98g6uYjMdAjy+i0CxeyaLhnGz+K36dt/6Y58lDy1sS/EAUt8+vCK7I53ECgYEAu/Sup6U7CPpNarNbfgNJGpNtijMWSwuM5cwB/7T1uhiA9pR9pxkjOA2RxopokqmjfhKSNi6MLSfkxayK/x/33VqJotqMylQns2G9ZlAJ+zUkB/Ihe1eSkP14e3jiFDaYuXwdW8JUUHVXv+dagCdu/aTZdrJg9UmrnYY6qx9F7gc=";
        String paramsStr = "appId=10000&cpId=1000&method=external.hms.gs.checkPlayerSign&playerId=100&playerLevel=10&playerSSign=VUOoWexHeQC98OFHyWapgKSACDwBgEHWb6IvPutKO0Z%2FwSVU3SDoK7%2FvnaLsYte6cYJu%2FRVWxoGh8lJfHuMoMucKutoNEXnAnPgTG5cfXf79DCtTnhMJ3lHBjaYFD03RWb2XBRKlnF7m455DeU2bvPZOsi7BhTDNPD0bTxY7PWlASLCSX7C7WqHN4%2FAWxDiU%2Bki2pPBstuSDecoUQQATBU35bQE2V7DtOsoGAhseuKXZe7yExMqszyZHLKaaqsbqq1rCua6FvJtwlwO82eY7N5kyW29r3MQ%2FuW1XGh4aPDods9UfD90BSLoPPmLjV9tREX%2FHFIdxkZ3FVWbkcWR4YQ%3D%3D&ts=1350539672156";

        String s = generateSign(priKey, paramsStr);
        System.out.println(s);
    }

    private String generateSign(String priKey, String paramsStr) {
        try {

            String rsaSign = RSAUtil.sign(paramsStr, priKey);
            rsaSign= Base64.encode(rsaSign.getBytes("UTF-8"));
            rsaSign = URLEncoder.encode(rsaSign, "UTF-8");
            return rsaSign;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return "";
    }
}
