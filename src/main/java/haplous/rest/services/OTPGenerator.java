/*
 * SYSTEMi Copyright © 2000-2016, MetricStream, Inc. All rights reserved.
 * @Author: Zishan Shaikh(zishan.shaikh@metricstream.com)
 * @Created: 01/02/2016
 * $Id: HMACOTPGeneratorAlgorithm.java 67172 2015-02-01 06:58:57Z zishan.shaikh $
 */


package haplous.rest.services;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;



public class OTPGenerator implements OTPAlgorithm {

	private static final OTPAlgorithm otpAlgorithm = new OTPGenerator();

	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

	private OTPGenerator() {
	}

	public static OTPAlgorithm getInstance() {
		return otpAlgorithm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.metricstream.caf.ext.mfa.otp.generator.OTPAlgorithm#generateHMACOTP
	 * (java.lang.String, java.lang.String, int)
	 */
	@Override
	public String generateOTP(String sharedSecret, String eventCounter,
			int otpLen) throws java.security.SignatureException,
			NoSuchAlgorithmException, UnsupportedEncodingException,
			InvalidKeyException {

		String result;
		String strFormatString = "%0" + otpLen + "d";
		// Get an hmac_sha1 key from the raw key bytes
		byte[] keyBytes = eventCounter.getBytes("ISO-8859-1");
		SecretKeySpec signingKey = new SecretKeySpec(keyBytes,
				HMAC_SHA1_ALGORITHM);

		// Get an hmac_sha1 Mac instance and initialize with the signing key
		Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
		mac.init(signingKey);

		// Compute the hmac on input data bytes
		byte[] rawHmac = mac.doFinal(sharedSecret.getBytes());

		// Convert raw bytes to Hex
		byte[] hexBytes = new Hex().encode(rawHmac);

		// Covert array of Hex bytes to a String

		int offset = hexBytes[hexBytes.length - 1] & 0xf;
		long bin_code = (hexBytes[offset] & 0x7f) << 24
				| (hexBytes[offset + 1] & 0xff) << 16
				| (hexBytes[offset + 2] & 0xff) << 8
				| (hexBytes[offset + 3] & 0xff);

		result = String.valueOf(bin_code % (long) Math.pow(10, otpLen));
		if (result.length() < otpLen) {
			result = String.format(strFormatString, Integer.parseInt(result));
		}
		return result;
	}

}
