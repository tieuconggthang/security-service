package vn.napas.security.service;

import java.security.PrivateKey;
import java.security.PublicKey;

public interface CryptoKeyProvider {
    PrivateKey getPrivateKey(String alias) throws Exception;
    PublicKey getPublicKey(String alias) throws Exception;
    boolean isHsm();
}
