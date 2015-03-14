package wuchou.ssms;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import android.annotation.SuppressLint;
import android.util.Base64;

public class Bao_mat{

	static String publickey,PubKeyModulus,PubKeyExponent,PrivKeyModulus,PrivKeyExponent;	

	// Ma hoa Des
	@SuppressLint("TrulyRandom")
	public static String mahoaDES(String ukey,String nd){		
		String coded = null;
	
			DESKeySpec keySpec = null;
			try {
				keySpec = new DESKeySpec(ukey.getBytes());
			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SecretKeyFactory keyFactory = null;
			try {
				keyFactory = SecretKeyFactory.getInstance("DES");
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SecretKey key = null;
			try {
				key = keyFactory.generateSecret(keySpec);
			} catch (InvalidKeySpecException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
			byte[] bytend = null;
			try {
				bytend = nd.getBytes("UTF8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   

			Cipher cipher = null;
			try {
				cipher = Cipher.getInstance("DES");
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				cipher.init(Cipher.ENCRYPT_MODE, key);
			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	

			//ma hoa va chuan hoa chuoi
			try {
				coded = Base64.encodeToString(cipher.doFinal(bytend), Base64.DEFAULT);
			} catch (IllegalBlockSizeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BadPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		

		return coded.substring(0,coded.lastIndexOf("="));
	}

	////////////////////////////////Giai ma DES

	public static String giaimaDES(String ukey,String coded) throws Exception{
		String decoded = null;	
		byte[] codeBytes = Base64.decode(coded, Base64.DEFAULT);
		try{
			DESKeySpec keySpec = new DESKeySpec(ukey.getBytes());
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey key = keyFactory.generateSecret(keySpec);		

			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] nd = (cipher.doFinal(codeBytes));

			decoded = new String(nd);
		} catch(Exception e){

		}
		return decoded;
	}



	////////////////////////////////////////
	public static String taoKeyRSA() throws Exception, Exception, Exception{

		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");  
		keyPairGenerator.initialize(512+128+128+128); //1024 used for normal securities  
		KeyPair keyPair = keyPairGenerator.generateKeyPair();  
		PublicKey publicKey = keyPair.getPublic();  
		PrivateKey privateKey = keyPair.getPrivate(); 

		KeyFactory keyFactory = KeyFactory.getInstance("RSA");  
		RSAPublicKeySpec rsaPubKeySpec = keyFactory.getKeySpec(publicKey, RSAPublicKeySpec.class);  
		RSAPrivateKeySpec rsaPrivKeySpec = keyFactory.getKeySpec(privateKey, RSAPrivateKeySpec.class);  

		PubKeyModulus = rsaPubKeySpec.getModulus()+"";
		PubKeyExponent = rsaPubKeySpec.getPublicExponent()+"";

		PrivKeyModulus = rsaPrivKeySpec.getModulus()+"";
		PrivKeyExponent = rsaPrivKeySpec.getPrivateExponent()+"";

		MainActivity.luuPriKey(PrivKeyModulus+"S"+PrivKeyExponent);

		System.out.println("PubKey Modulus : " + rsaPubKeySpec.getModulus());  
		System.out.println("PubKey Exponent : " + rsaPubKeySpec.getPublicExponent());  

		System.out.println("PrivKey Modulus : " + rsaPrivKeySpec.getModulus());  
		System.out.println("PrivKey Exponent : " + rsaPrivKeySpec.getPrivateExponent()); 

		return PubKeyModulus+"S"+PubKeyExponent +"\n"+ MainActivity.laytg();

	}	

	//

	public static String MaHoaRsa(String nd,String pubkey) throws Exception, Exception, Exception{
		System.out.println("\n----------------ENCRYPTION STARTED------------");  
		BigInteger modulus = new BigInteger(pubkey.substring(0, pubkey.indexOf("S")));
		BigInteger exponent = new BigInteger(pubkey.substring(pubkey.lastIndexOf("S")+1));

		RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(modulus, exponent);  
		KeyFactory fact = KeyFactory.getInstance("RSA");  
		PublicKey localpublicKey = fact.generatePublic(rsaPublicKeySpec);  

		byte[] dataToEncrypt = nd.getBytes();		
		byte[] encryptedData = null; 
		Cipher cipher = Cipher.getInstance("RSA");  
		cipher.init(Cipher.ENCRYPT_MODE, localpublicKey);  
		encryptedData = cipher.doFinal(dataToEncrypt);  
		System.out.println("Encryted Data: " + encryptedData); 
		System.out.println("----------------ENCRYPTION COMPLETED------------");  

		String coded = Base64.encodeToString(encryptedData, Base64.DEFAULT);
		coded = coded.substring(0, coded.lastIndexOf("=")-1);

		return coded;



	}

	//
	public static String GiaiMaRsa(String nd) throws Exception, Exception, Exception{
		System.out.println("\n----------------DECRYPTION STARTED------------");  

		String prikey = MainActivity.layPriKey();

		BigInteger modulus = new BigInteger(prikey.substring(0, prikey.indexOf("S")));
		BigInteger exponent = new BigInteger(prikey.substring(prikey.lastIndexOf("S")+1));	

		RSAPrivateKeySpec rsaPrivateKeySpec = new RSAPrivateKeySpec(modulus, exponent);  
		KeyFactory fact = KeyFactory.getInstance("RSA");  
		PrivateKey localprivateKey = fact.generatePrivate(rsaPrivateKeySpec);  

		byte[] descryptedData = null;  

		Cipher cipher = Cipher.getInstance("RSA");  
		cipher.init(Cipher.DECRYPT_MODE, localprivateKey);  
		descryptedData = cipher.doFinal(Base64.decode(nd,Base64.DEFAULT));  		
		System.out.println("----------------DECRYPTION COMPLETED------------");    
		return new String(descryptedData);

	}



	///SHA2
	public static String SHA256(String input){
		MessageDigest mDigest = null;
		try {
			mDigest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] result = mDigest.digest(input.getBytes());
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < result.length; i++) {
			sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
		}

		return sb.toString();
	}







}
