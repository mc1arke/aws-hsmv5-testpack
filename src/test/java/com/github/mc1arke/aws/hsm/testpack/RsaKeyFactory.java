package com.github.mc1arke.aws.hsm.testpack;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;

class RsaKeyFactory {

    static KeyPair generateRsaKeyPair(Provider provider) throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", provider);
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }
}
