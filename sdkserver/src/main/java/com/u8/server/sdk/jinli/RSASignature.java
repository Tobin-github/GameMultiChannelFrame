package com.u8.server.sdk.jinli;

import com.u8.server.sdk.huawei.Base64Util;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

/**
 * RSA签名验签类
 * 
 * @author tianxb
 * @date 2012-12-1 下午2:23:27
 * @since 2.0.0
 */
public class RSASignature {

	private static final String SIGN_ALGORITHMS = "SHA1WithRSA";

	/**
	 * RSA签名
	 * 
	 * @param content
	 *            待签名数据
	 * @param privateKey
	 *            商户私钥
	 * @param encode
	 *            字符集编码
	 * @return 签名值
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws SignatureException
	 * @throws InvalidKeyException
	 */
	/*public static String sign(String content, String privateKey, String encode) throws IOException,
			NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, InvalidKeyException {
		String charset = CharEncoding.UTF_8;
		if (!StringUtils.isBlank(encode)) {
			charset = encode;
		}
		PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64Util.decode(privateKey));
		KeyFactory keyf = KeyFactory.getInstance("RSA");
		PrivateKey priKey = keyf.generatePrivate(priPKCS8);

		java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);
		signature.initSign(priKey);
		signature.update(content.getBytes(charset));
		byte[] signed = signature.sign();
		return Base64Util.encodeBytes(signed);

	}*/

	/**
	 * <pre>
	 * <p>函数功能说明:RSA验签名检查</p>
	 * <p>修改者名字:guocl</p>
	 * <p>修改日期:2012-11-30</p>
	 * <p>修改内容:抛异常</p>
	 * </pre>
	 * 
	 * @param content
	 *            待签名数据
	 * @param sign
	 *            签名值
	 * @param publicKey
	 *            支付宝公钥
	 * @param encode
	 *            字符集编码
	 * @return 布尔值
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws InvalidKeySpecException
	 * @throws InvalidKeyException
	 * @throws SignatureException
	 */
	public static boolean doCheck(String content, String sign, String publicKey, String encode)
			throws NoSuchAlgorithmException, IOException, InvalidKeySpecException, InvalidKeyException,
			SignatureException {
		String charset = CharEncoding.UTF_8;
		if (!StringUtils.isBlank(encode)) {
			charset = encode;
		}
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		byte[] encodedKey = Base64Util.decode(publicKey);
		PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));

		java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);

		signature.initVerify(pubKey);
		signature.update(content.getBytes(charset));

		boolean bverify = signature.verify(Base64Util.decode(sign));
		return bverify;

	}
}
