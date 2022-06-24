package com.github.mc1arke.aws.hsm.testpack;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(CloudHsmProviderExtension.class)
class SignatureCompatibilityTest {

    /**
     * Validates that the same signature instance can be used for both signing and verifying signatures. Providers such
     * as BouncyCastle's TLS implementation create a signature using the private key, and then attempt to verify if
     * using the public key from the certificate obtained from the keystore, with certificates not being stored on the
     * HSM.
     * @param provider the provider to user for creating the signature instance
     */
    @Test
    void shouldAllowVerifyingSignatureWithPublicKeyOutsideHsm(Provider provider) throws GeneralSecurityException {
        KeyPair keyPair = RsaKeyFactory.generateRsaKeyPair(provider);

        Signature signature = Signature.getInstance("SHA256WithRSA", provider);
        signature.initSign(keyPair.getPrivate());

        byte[] input = "Hello".getBytes(StandardCharsets.UTF_8);
        signature.update(input);
        byte[] output = signature.sign();

        // Simulate having the public key outside the HSM by creating a clone of the HSM-generated key
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(keyPair.getPublic().getEncoded()));

        assertThatNoException().isThrownBy(() -> signature.initVerify(publicKey));
        signature.update(input);
        assertThat(signature.verify(output)).isTrue();
    }

}
