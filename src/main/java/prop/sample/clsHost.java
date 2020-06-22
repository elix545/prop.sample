package prop.sample;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class clsHost {
	/**
	 * 
	 * @return
	 */
	public final static String getHosName() {
		String hostName = "";
		try {
			hostName = InetAddress.getLocalHost().getHostName();
			return hostName;
		} catch (UnknownHostException e) {
			// TODO: handle exception
			hostName = null;
		}

		try {
			Process p = Runtime.getRuntime().exec("hostname");
			BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
			hostName = r.readLine();
			if (hostName == null || hostName.length() == 0) {
				hostName = null;
			}
		} catch (IOException e) {
			// TODO: handle exception
			;
		}

		hostName = System.getenv("COMPUTERNAME");
		if (hostName != null) {
			return hostName;
		}

		hostName = System.getenv("HOSTNAME");
		if (hostName != null) {
			return hostName;
		}

		return null;
	}
	
	public final static byte[] getDigest()
	{
		byte[] inMessage;
		byte[] outMessage = null;
		
		MessageDigest digest;
		
		try {
			inMessage = getHosName().getBytes("UTF-8");
			
			digest = MessageDigest.getInstance("MD5");
			outMessage = digest.digest(inMessage);
			
			inMessage = outMessage;
			digest = MessageDigest.getInstance("SHA-1");
			outMessage = digest.digest(inMessage);

			inMessage = outMessage;
			digest = MessageDigest.getInstance("SHA-256");
			outMessage = digest.digest(inMessage);
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return outMessage;
	}

}
