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

	public enum enmType {
		Properties, XML
	}

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

		if (type == enmType.Properties) {
			filePath = "config.properties";
		} else {
			filePath = "config.xml";
		}
	}

	public clsProp(String filePath) {
		super();
		this.prop = new Properties();
		this.filePath = filePath;
		this.setPath(filePath);
	}

	public clsProp(String filePath, enmType type) {
		super();
		this.prop = new Properties();
		this.filePath = filePath;
		this.type = type;
		this.setPath(filePath);
	}

	public void setPath(String path) {
		if (path == null || path.length() == 0) {
			if (type == enmType.Properties) {
				filePath = "config.properties";
			} else {
				filePath = "config.xml";
			}
		} else {
			filePath = path;
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
				input = this.getClass().getClassLoader().getResourceAsStream("config.properties");
				prop.load(input);
			} else {
				input = this.getClass().getClassLoader().getResourceAsStream("config.xml");
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
			}
		}
		return value;
	}

	public boolean storeProp() {
		try {
			OutputStream output;
			if (type == enmType.Properties) {
				output = new FileOutputStream(filePath);
				prop.store(output, null);
			} else {
				output = new FileOutputStream(filePath);
				prop.storeToXML(output, null, "UTF-8");
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
	}

}
