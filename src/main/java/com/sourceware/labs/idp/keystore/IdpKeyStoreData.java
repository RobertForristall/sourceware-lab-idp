package com.sourceware.labs.idp.keystore;

/**
 * Class for holding all of the data needed to interact with the keystore
 *
 * @author Robert Forristall (robert.s.forristall@gmail.com)
 */
public class IdpKeyStoreData {

	private String storeFileName;
	private String storePassword;
	private String keyAlias;
	private String keyPassword;
	private String keyId;

	public IdpKeyStoreData(String storeFileName, String storePassword, String keyAlias, String keyPassword,
			String keyId) {
		super();
		this.storeFileName = storeFileName;
		this.storePassword = storePassword;
		this.keyAlias = keyAlias;
		this.keyPassword = keyPassword;
		this.keyId = keyId;
	}

	public String getStoreFileName() {
		return storeFileName;
	}

	public String getStorePassword() {
		return storePassword;
	}

	public String getKeyAlias() {
		return keyAlias;
	}

	public String getKeyPassword() {
		return keyPassword;
	}

	public String getKeyId() {
		return keyId;
	}

}
