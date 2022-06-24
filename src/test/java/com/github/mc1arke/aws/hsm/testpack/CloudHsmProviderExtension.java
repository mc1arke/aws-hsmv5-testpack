package com.github.mc1arke.aws.hsm.testpack;

import java.io.IOException;
import java.security.Provider;

import javax.security.auth.login.LoginException;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import com.amazonaws.cloudhsm.jce.jni.exception.ProviderInitializationException;
import com.amazonaws.cloudhsm.jce.provider.CloudHsmProvider;

public class CloudHsmProviderExtension implements ParameterResolver {

    private static final CloudHsmProvider provider = createDefaultProvider();

    private static CloudHsmProvider createDefaultProvider() {
        try {
            CloudHsmProvider provider = new CloudHsmProvider();
            //add any provider authentication here if needed, e.g.
            //provider.login(null, new UsernamePasswordAuthHandler("username", "password".toCharArray()));
            return provider;
        } catch (IOException | ProviderInitializationException | LoginException e) {
            throw new IllegalStateException("Could not initialise HSM Provider", e);
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return Provider.class.isAssignableFrom(parameterContext.getParameter().getType());
    }

    @Override
    public Provider resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return provider;
    }

}
