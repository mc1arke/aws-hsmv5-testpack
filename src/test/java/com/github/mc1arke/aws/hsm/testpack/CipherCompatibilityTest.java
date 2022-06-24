package com.github.mc1arke.aws.hsm.testpack;

import static org.assertj.core.api.Assertions.assertThatNoException;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyPair;
import java.security.Provider;
import java.security.Security;

import javax.crypto.Cipher;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(CloudHsmProviderExtension.class)
class CipherCompatibilityTest {

    /**
     * The Java Cipher mechanism delegates to each registered provider during the `init` operation to see which one
     * claims to support operations with a given key. This works by calling {@link Key#getFormat()} which fails with an
     * HSM key, so requires the use of `Cipher.getInstance(algorithm, cloudHsmProvider)` rather than using provider
     * fall-though handling as is generally recommended.
     *
     * @param provider the provider to use for generating the keys for encryption
     */
    @Test
    void shouldAllowFallthroughCipherSelection(Provider provider) throws GeneralSecurityException {
        BouncyCastleProvider bouncyCastleProvider = new BouncyCastleProvider();
        try {
            // Set a non-Sun, non-HSM provider as being top of the provider list, equivalent to someone changing their
            // JVM security file to re-order providers
            Security.insertProviderAt(bouncyCastleProvider, 1);
            Security.addProvider(provider);

            KeyPair keyPair = RsaKeyFactory.generateRsaKeyPair(provider);

            Cipher cipher = Cipher.getInstance("RSA");
            assertThatNoException().isThrownBy(() -> cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic()));
            cipher.doFinal("Hello".getBytes(StandardCharsets.UTF_8));
        } finally {
            Security.removeProvider(provider.getName());
            Security.removeProvider(bouncyCastleProvider.getName());

        }
    }
}
