package haplous.rest.services;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

public class GetOtp {

	public String getOTP(String sharedSecret, String eventCounter,int otpLen) throws InvalidKeyException, SignatureException, NoSuchAlgorithmException, UnsupportedEncodingException {
		// TODO Auto-generated method stub

		OTPAlgorithm otp=OTPGenerator.getInstance();
		
		return otp.generateOTP(sharedSecret, eventCounter, otpLen);
		
	}
	
/*	public static void main(String args[]) throws InvalidKeyException, SignatureException, NoSuchAlgorithmException, UnsupportedEncodingException{
		GetOtp g= new GetOtp();
		System.out.println(g.getOTP("oYX4iKQg468O7Ug", "4042203000026",6));
	
		
	}
*/
}
