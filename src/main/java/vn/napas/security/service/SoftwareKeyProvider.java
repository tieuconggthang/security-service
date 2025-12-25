package vn.napas.security.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Demo provider: generates in-memory keypairs. NOT for production.
 * Use only when crypto.pkcs11.enabled=false (default).
 */
@Component
@Profile("!pkcs11")
public class SoftwareKeyProvider implements CryptoKeyProvider {

    private final Map<String, KeyPair> keypairs = new ConcurrentHashMap<>();

    @Override
    public PrivateKey getPrivateKey(String alias) throws Exception {
        return keypairs.computeIfAbsent(alias, this::generateRsa).getPrivate();
    }

    @Override
    public PublicKey getPublicKey(String alias) throws Exception {
        return keypairs.computeIfAbsent(alias, this::generateRsa).getPublic();
    }

    @Override
    public boolean isHsm() {
        return false;
    }

    private KeyPair generateRsa(String alias) {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            return kpg.generateKeyPair();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate RSA keypair for alias=" + alias, e);
        }
    }
}
