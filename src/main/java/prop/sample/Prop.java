/**
 * 
 */
package prop.sample;

/**
 * @author Elis Javier Méndez Pérez
 * 
 */
public class Prop {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String env_prefix = System.getenv("PROP_ENV_PREFIX");
		System.out.println(env_prefix);

		String host = clsHost.getHosName();
		System.out.println(host);

		byte[] vkey = clsHost.getDigest();
		System.out.println(vkey);

		String encryptedString = clsAES.encrypt(host, vkey);
		System.out.println(encryptedString);

		String decryptedString = clsAES.decrypt(encryptedString, vkey);
		System.out.println(decryptedString);

		clsProp prop = new clsProp();
		prop.loadProp();
		prop.setProp("testkey", host);
		prop.setProp("host", ">" + host + "<");
		prop.storeProp();

		prop.loadProp();
		System.out.println(prop.getProp("testkey"));
		System.out.println(prop.getProp("host"));
		System.out.println(prop.getProp("digestKey"));

		System.out.println(prop.getProp("testkey2"));
		prop.storeProp();
	}
}
