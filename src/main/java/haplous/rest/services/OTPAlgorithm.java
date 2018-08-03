/*
 * SYSTEMi Copyright © 2000-2016, MetricStream, Inc. All rights reserved.
 * @Author: Zishan Shaikh(zishan.shaikh@metricstream.com)
 * @Created: 01/02/2016
 * $Id: OTPAlgorithm.java 67172 2015-02-01 06:58:57Z zishan.shaikh $
 */


package haplous.rest.services;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface OTPAlgorithm {

	/**
	 * @param sharedSecret
	 * @param eventCounter
	 * @param otpLen
	 * @return
	 * @throws java.security.SignatureException
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 * @throws InvalidKeyException
	 */
	public abstract String generateOTP(String sharedSecret,
			String eventCounter, int otpLen)
			throws java.security.SignatureException, NoSuchAlgorithmException,
			UnsupportedEncodingException, InvalidKeyException;

}
