package vn.napas.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.napas.security.config.CryptoProperties;

import java.security.Signature;
import java.util.Base64;

/**
 * Core signing & verifying logic.
 */
@Service
@RequiredArgsConstructor
public class CryptoService {

    private final CryptoProperties props;
    private final CryptoKeyProvider keyProvider;

    public String sign(String kid, byte[] message) throws Exception {
        CryptoProperties.KeyRef keyRef = requireKey(kid);

        Signature sig = Signature.getInstance(keyRef.getJcaAlg());
        if ("RSASSA-PSS".equalsIgnoreCase(keyRef.getJcaAlg()) || "PS256".equalsIgnoreCase(keyRef.getAlg())) {
            PssSupport.applyPs256(sig);
        }

        sig.initSign(keyProvider.getPrivateKey(keyRef.getAlias()));
        sig.update(message);
        byte[] out = sig.sign();
        return Base64.getEncoder().encodeToString(out);
    }

    public boolean verify(String kid, byte[] message, byte[] signatureBytes) throws Exception {
        CryptoProperties.KeyRef keyRef = requireKey(kid);

        Signature sig = Signature.getInstance(keyRef.getJcaAlg());
        if ("RSASSA-PSS".equalsIgnoreCase(keyRef.getJcaAlg()) || "PS256".equalsIgnoreCase(keyRef.getAlg())) {
            PssSupport.applyPs256(sig);
        }

        sig.initVerify(keyProvider.getPublicKey(keyRef.getAlias()));
        sig.update(message);
        return sig.verify(signatureBytes);
    }

    public String algOf(String kid) {
        return requireKey(kid).getAlg();
    }

    public boolean isHsm() {
        return keyProvider.isHsm();
    }

    private CryptoProperties.KeyRef requireKey(String kid) {
        CryptoProperties.KeyRef ref = props.getKeys().get(kid);
        if (ref == null) {
            throw new IllegalArgumentException("Unknown kid: " + kid);
        }
        return ref;
    }
}
