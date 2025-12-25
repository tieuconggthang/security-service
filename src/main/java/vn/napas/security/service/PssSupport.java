package vn.napas.security.service;

import java.security.Signature;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;

/**
 * Helper for PS256 (RSA-PSS) parameterization.
 * Many providers require explicit PSS params.
 */
public final class PssSupport {
    private PssSupport() {}

    public static void applyPs256(Signature signature) throws Exception {
        PSSParameterSpec spec = new PSSParameterSpec(
                "SHA-256",
                "MGF1",
                MGF1ParameterSpec.SHA256,
                32,
                1
        );
        signature.setParameter(spec);
    }
}
