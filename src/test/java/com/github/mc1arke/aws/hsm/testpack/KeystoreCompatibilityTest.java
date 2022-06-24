package com.github.mc1arke.aws.hsm.testpack;

import static org.assertj.core.api.Assertions.assertThatNoException;

import java.security.KeyStore;
import java.security.Provider;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.amazonaws.cloudhsm.jce.provider.CloudHsmProvider;

@ExtendWith(CloudHsmProviderExtension.class)
class KeystoreCompatibilityTest {

    /**
     * When running on Java 11 or above, an attempt to initialise a CloudHSM keystore (required to use keys already on
     * the HSM) fails unless the JVM has the `--add-opens java.base/java.security=ALL-UNNAMED` startup flag set. This
     * raises a security concern of libraries crossing security boundaries, and raises a question about why the HSM JCE
     * is trying to use reflective access. This requirement should really be included in the documentation (it makes it
     * far easier to get sign-off for making changes to security handling if the documentation mentions this
     * requirement), but really the CloudHsmKeyStore class should just wrap the PKCS12 keystore instance it's trying to
     * access, and delegate to the relevant target methods, e.g.
     * <pre>
     * {@code
     * public class CloudHsmKeystore extends KeyStoreWithAttributesSpi {
     *
     *     // hold a reference to the loaded keystore, rather than trying to pull the SPI out of it
     *     private final KeyStore wrappedKeystore;
     *
     *     public CloudHsmKeyStore() {
     *         wrappedKeystore = Keystore.getInstance("PKCS12");
     *     }
     *
     *     public void engineLoad(InputStream stream, char[] password) throws IOException, NoSuchAlgorithmException, CertificateException {
     *         // call the equivalent method not prefixed by 'engine' on the keystore object to delegate to the keystore's underlying provider
     *         this.wrappedKeystore.load(stream, password);
     *     }
     * }
     * }
     * </pre>
     * @param provider the provider to use when loading the Cloud HSM keystore
     */
    @Test
    void shouldAllowCreatingAKeystoreWithoutReflectiveOperations(Provider provider) {
        assertThatNoException().isThrownBy(() -> KeyStore.getInstance(CloudHsmProvider.CLOUDHSM_KEYSTORE_TYPE, provider));
    }
}
