package com.github.mc1arke.aws.hsm.testpack;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.security.Key;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(CloudHsmProviderExtension.class)
class JavaApiCompatibilityTest {

    /**
     * The Javadoc for {@link Key#getEncoded()} specifies the method
     * `Returns: the encoded key, or null if the key does not support encoding.`. As the key should not leave the HSM,
     * the call to encode the key should return null. This is used internally inside the JVM's
     * @param provider the provider to use for generating the key pair
     */
    @Test
    void shouldNotThrowExceptionOnAttemptingToExtractEncodedPrivateKey(Provider provider) throws NoSuchAlgorithmException {
        KeyPair keyPair = RsaKeyFactory.generateRsaKeyPair(provider);

        assertThatNoException().isThrownBy(() -> keyPair.getPublic().getEncoded());
        assertThatNoException().isThrownBy(() -> keyPair.getPrivate().getEncoded());
    }


    /**
     * The Javadoc for {@link Key#getFormat()} ()}} specifies the method
     * `Returns the name of the primary encoding format of this key, or null if this key does not support encoding`.
     * As the private key should not leave the HSM, the call to get the format of the key should return null. As the
     * public key can be encoded it should return a format so we can tell how it has been decoded
     * @param provider the provider to use for generating the key pair
     */
    @Test
    void shouldNotThrowExceptionOnAttemptingToGetFormatOfKeys(Provider provider) throws NoSuchAlgorithmException {
        KeyPair keyPair = RsaKeyFactory.generateRsaKeyPair(provider);

        assertThat(keyPair.getPublic().getFormat()).isNotNull();
        assertThatNoException().isThrownBy(() -> keyPair.getPrivate().getFormat());
    }

}
