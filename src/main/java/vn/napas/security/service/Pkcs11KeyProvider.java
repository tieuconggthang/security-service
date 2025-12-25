package vn.napas.security.service;

import vn.napas.security.config.CryptoProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.Certificate;

/**
 * HSM provider via SunPKCS11 (JDK built-in).
 *
 * Notes:
 * - You must run with JDK module 'jdk.crypto.cryptoki' available.
 * - Provide correct PKCS#11 library path and slot configuration.
 * - This provider loads a PKCS11 KeyStore and reads keys by alias.
 */
@Component
@Profile("pkcs11")
public class Pkcs11KeyProvider implements CryptoKeyProvider {

    private final CryptoProperties props;
    private final KeyStore keyStore;

    public Pkcs11KeyProvider(CryptoProperties props) throws Exception {
        this.props = props;

        CryptoProperties.Pkcs11 p = props.getPkcs11();
        if (!p.isEnabled()) {
            throw new IllegalStateException("Profile 'pkcs11' is active but crypto.pkcs11.enabled=false");
        }
        if (p.getLibrary() == null || p.getLibrary().isBlank()) {
            throw new IllegalArgumentException("crypto.pkcs11.library is required for pkcs11 profile");
        }

        StringBuilder cfg = new StringBuilder();
        cfg.append("name=").append(p.getName()).append("\n");
        cfg.append("library=").append(p.getLibrary()).append("\n");

        // Choose ONE of these depending on HSM:
        if (p.getSlot() != null) {
            cfg.append("slot=").append(p.getSlot()).append("\n");
        }
        if (p.getSlotListIndex() != null) {
            cfg.append("slotListIndex=").append(p.getSlotListIndex()).append("\n");
        }

        Provider provider = new sun.security.pkcs11.SunPKCS11(
                new ByteArrayInputStream(cfg.toString().getBytes(StandardCharsets.UTF_8))
        );
        Security.addProvider(provider);

        this.keyStore = KeyStore.getInstance("PKCS11", provider);
        char[] pin = (p.getPin() == null) ? null : p.getPin().toCharArray();
        this.keyStore.load(null, pin);
    }

    @Override
    public PrivateKey getPrivateKey(String alias) throws Exception {
        char[] pin = (props.getPkcs11().getPin() == null) ? null : props.getPkcs11().getPin().toCharArray();
        return (PrivateKey) keyStore.getKey(alias, pin);
    }

    @Override
    public PublicKey getPublicKey(String alias) throws Exception {
        Certificate cert = keyStore.getCertificate(alias);
        if (cert == null) {
            throw new IllegalArgumentException("Certificate not found in HSM for alias=" + alias);
        }
        return cert.getPublicKey();
    }

    @Override
    public boolean isHsm() {
        return true;
    }
}
