package com.sourceware.labs.idp.jwt;

import java.io.File;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.text.ParseException;
import java.util.List;

import org.bouncycastle.operator.OperatorCreationException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

import com.google.gson.Gson;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sourceware.labs.idp.keystore.IdpKeyStoreAccessor;
import com.sourceware.labs.idp.keystore.IdpKeyStoreData;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class JwtManagerTests {

  private final String storeDir = "testKeyStoreDir";
  private final String storeName = "testKeyStoreName";
  private final String storePass = "testKeyStorePass";
  private final String ecKeyAlias = "ecTestKeyAlias";
  private final String ecKeyPass = "ecTestKeyPass";
  private final String ecKeyId = "ecTestKeyId";
  private final String clientId = "Sourceware Labs Frontend";
  private final String audience = "Sourceware Labs Backend";
  private long tokenExpiration = 60; // One minute

  private final IdpKeyStoreData ecIdpKeyStoreData = new IdpKeyStoreData(
          storeDir,
          storeName,
          storePass,
          ecKeyAlias,
          ecKeyPass,
          ecKeyId);

  private final File keyStoreFile = new File(storeDir + "/" + storeName);

  @BeforeAll
  public void setupKeystores() {
    Assertions.assertTrue(
            IdpKeyStoreAccessor.prepareKeyStoreAndSet(JWSAlgorithm.ES256, ecIdpKeyStoreData));
  }

  @AfterAll
  public void cleanupKeystores() {
    keyStoreFile.delete();
    new File("ks").delete();
  }

  @Test
  @Order(1)
  public void testJwt() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, UnrecoverableEntryException, OperatorCreationException, IOException, JOSEException, ParseException {
    SignedJWT jwt = JwtManager.getSignedJwtToken(
            JWSAlgorithm.ES256,
            clientId,
            audience,
            tokenExpiration,
            ecIdpKeyStoreData,
            Long.valueOf(1),
            "testApplication",
            "testRole",
            List.of("perm1", "perm2"));
    Assertions.assertNotNull(jwt.serialize());
    Assertions.assertTrue(
            JwtManager
                    .verifySignedJwtToken(jwt.serialize(), JWSAlgorithm.ES256, ecIdpKeyStoreData));
  }
  
  @Test
  @Order(2)
  public void testClaimSetReturned() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, UnrecoverableEntryException, OperatorCreationException, IOException, JOSEException, ParseException {
    SignedJWT jwt = JwtManager.getSignedJwtToken(
            JWSAlgorithm.ES256,
            clientId,
            audience,
            tokenExpiration,
            ecIdpKeyStoreData,
            Long.valueOf(1),
            "testApplication",
            "testRole",
            List.of("perm1", "perm2"));
    Assertions.assertNotNull(jwt.serialize());
    JWTClaimsSet claimSet = JwtManager.getClaimsSetFromJwt(jwt.serialize(), JWSAlgorithm.ES256, ecIdpKeyStoreData);
    Assertions.assertEquals(Long.valueOf(1), claimSet.getLongClaim("userId"));
    Assertions.assertEquals("testApplication", claimSet.getStringClaim("application"));
    Assertions.assertEquals("testRole", claimSet.getStringClaim("roleName"));
    Assertions.assertIterableEquals(List.of("perm1", "perm2"), List.of(new Gson().fromJson(claimSet.getStringClaim("additionalPermissions"), String[].class)));
  }
}
