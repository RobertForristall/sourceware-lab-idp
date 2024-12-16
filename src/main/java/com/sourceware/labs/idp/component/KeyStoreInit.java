package com.sourceware.labs.idp.component;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.JWSAlgorithm;
import com.sourceware.labs.idp.keystore.IdpKeyStoreAccessor;
import com.sourceware.labs.idp.keystore.IdpKeyStoreData;

import jakarta.annotation.PostConstruct;

@Component
public class KeyStoreInit {
	
	@Value("${keystore.dir.name}")
	private String keyStoreDir;
	
	@Value("${keystore.store.name}")
	private String keyStoreName;
	
	@Value("${keystore.store.password}")
	private String keyStorePassword;
	
	@Value("${keystore.key.token.alias}")
	private String tokenAlias;
	
	@Value("${keystore.key.token.password}")
	private String tokenPassword;
	
	
	@PostConstruct
	private void createKeystoreAndKey() {
		IdpKeyStoreData keyStoreData = new IdpKeyStoreData(
				keyStoreDir,
				keyStoreName,
				keyStorePassword,
				tokenAlias,
				tokenPassword,
				"initial");
		File keyStoreDir = new File("ks");
		if (!keyStoreDir.exists()) {
			keyStoreDir.mkdir();
		}
		IdpKeyStoreAccessor.prepareKeyStoreAndSet(JWSAlgorithm.ES256, keyStoreData);
	}
	
}
