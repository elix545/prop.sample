package prop.sample;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;
import java.util.Properties;

public class clsProp {

	private String filePath;
	private Properties prop;
	private enmType type;
	private byte[] digestKey;
	private boolean isUpdated = false;

	public enum enmType {
		Properties, XML
	}

	private static String ENV_PREFIX = System.getenv("PROP_ENV_PREFIX");

	private static String DEFAULT_FILE_NAME = "config";
	private static String EXT_SEPARATOR = ".";
	private static String EXT_POPERTIES = "properties";
	private static String EXT_XML = "xml";

	private static String CONFIG_POPERTIES = (ENV_PREFIX == null) ? DEFAULT_FILE_NAME + EXT_SEPARATOR + EXT_POPERTIES
			: DEFAULT_FILE_NAME + EXT_SEPARATOR + ENV_PREFIX + EXT_SEPARATOR + EXT_POPERTIES;
	private static String CONFIG_XML = (ENV_PREFIX == null) ? DEFAULT_FILE_NAME + EXT_SEPARATOR + EXT_XML
			: DEFAULT_FILE_NAME + EXT_SEPARATOR + ENV_PREFIX + EXT_SEPARATOR + EXT_XML;

	private void fillDigestKey() {
		if (this.digestKey == null) {

			String strDigestKey = this.getProp("digestKey");
			if (strDigestKey == null) {
				this.digestKey = clsHost.getDigest();
			} else {
				this.digestKey = strDigestKey.getBytes();
			}
		}
	}

	public clsProp() {
		this.prop = new Properties();
		this.type = enmType.Properties;

		this.isUpdated = false;

		if (type == enmType.Properties) {
			filePath = CONFIG_POPERTIES;
		} else {
			filePath = CONFIG_XML;
		}
	}

	public clsProp(String filePath) {
		super();
		this.prop = new Properties();
		this.filePath = filePath;
		this.setPath(filePath);

		this.isUpdated = false;
	}

	public clsProp(String filePath, enmType type) {
		super();
		this.prop = new Properties();
		this.filePath = filePath;
		this.type = type;
		this.setPath(filePath);

		this.isUpdated = false;
	}

	public void setPath(String path) {
		if (path == null || path.length() == 0) {
			if (type == enmType.Properties) {
				filePath = CONFIG_POPERTIES;
			} else {
				filePath = CONFIG_XML;
			}
		} else {
			filePath = path;
		}

		if (filePath.endsWith(EXT_SEPARATOR.concat(EXT_POPERTIES))) {
			this.type = enmType.Properties;
		} else if (filePath.endsWith(EXT_SEPARATOR.concat(EXT_XML))) {
			this.type = enmType.XML;
		} else {
			filePath = filePath.concat("/").concat(CONFIG_POPERTIES);
			this.type = enmType.Properties;
		}
	}

	public boolean loadProp() {
		try {
			InputStream input;
			if (type == enmType.Properties) {
				input = new FileInputStream(filePath);
				prop.load(input);
			} else {
				input = new FileInputStream(filePath);
				prop.load(input);
			}

			fillDigestKey();
			return true;

		} catch (FileNotFoundException e) {
			this.storeProp();
			return this.loadProp();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean loadPropCP() {
		try {
			InputStream input;
			if (type == enmType.Properties) {
				input = this.getClass().getClassLoader().getResourceAsStream(CONFIG_POPERTIES);
				prop.load(input);
			} else {
				input = this.getClass().getClassLoader().getResourceAsStream(CONFIG_XML);
				prop.loadFromXML(input);
			}

			fillDigestKey();
			return true;

		} catch (FileNotFoundException e) {
			this.storeProp();
			return this.loadPropCP();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public String getProp(String key) {
		String value = prop.getProperty(key);
		if (value != null && value.length() != 0) {
			if (value.startsWith("<") && value.endsWith(">")) {
				String crypVal = value.substring(1, value.length() - 1);
				crypVal = new String(Base64.getDecoder().decode(crypVal));
				value = clsAES.decrypt(crypVal, this.digestKey);
				return value;
			}
			if (value.startsWith(">") && value.endsWith("<")) {
				String crypVal = value.substring(1, value.length() - 1);
				value = clsAES.encrypt(crypVal, this.digestKey);
				value = Base64.getEncoder().encodeToString(value.getBytes());
				value = "<" + value + ">";
				prop.setProperty(key, value);
				value = crypVal;

				this.isUpdated = true;
			}
		}
		return value;
	}

	public boolean storeProp() {
		try {
			if (this.isUpdated) {
				OutputStream output;
				if (type == enmType.Properties) {
					output = new FileOutputStream(filePath);
					prop.store(output, null);
				} else {
					output = new FileOutputStream(filePath);
					prop.storeToXML(output, null, "UTF-8");
				}
				this.isUpdated = false;
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public void setProp(String key, String value) {
		String oldValue = prop.getProperty(key);
		if (oldValue != null && oldValue.length() != 0) {
			if (oldValue.startsWith("<") && oldValue.endsWith(">")) {
				String crypVal = value.substring(1, value.length() - 1);
				value = clsAES.encrypt(crypVal, this.digestKey);
				value = Base64.getEncoder().encodeToString(value.getBytes());
				value = "<" + value + ">";
				prop.setProperty(key, value);
			} else {
				if (value.startsWith(">") && value.endsWith("<")) {
					String crypVal = value.substring(1, value.length() - 1);
					value = clsAES.encrypt(crypVal, this.digestKey);
					value = Base64.getEncoder().encodeToString(value.getBytes());
					value = "<" + value + ">";
					prop.setProperty(key, value);
					value = crypVal;
				} else {
					prop.setProperty(key, value);
				}
			}
		} else {
			prop.setProperty(key, value);
		}
		this.isUpdated = true;
	}

}
