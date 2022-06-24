package com.github.mc1arke.aws.hsm.testpack;

import static org.assertj.core.api.Assertions.assertThatNoException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.amazonaws.cloudhsm.jce.provider.CloudHsmProvider;

@ExtendWith(CloudHsmProviderExtension.class)
class MultipleProviderInitialisationTest {

    /**
     * Validates that a JCE provider can be constructed multiple times. There's nothing in the documentation that states
     * this shouldn't be allowed, and not allowing this causes challenges where a provider isn't being registered with
     * JCE, but is being used directly in the various cryptographic calls, e.g. Cipher.getInstance("...", provider);
     *
     * Additionally, not allowing different providers means a single application can't connect to different HSM clusters
     * despite multiple clusters being allowed in a single account.
     *
     * Note: this single instantiation is one of the reason for having to use {@link CloudHsmProviderExtension} in tests
     */
    @Test
    void shouldAllowProviderToBeConstructedMultipleTimes() {
        assertThatNoException().isThrownBy(CloudHsmProvider::new);
    }

}
