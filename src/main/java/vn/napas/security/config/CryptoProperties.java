package vn.napas.security.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.Map;

@Data
@Validated
@ConfigurationProperties(prefix = "crypto")
public class CryptoProperties {

    @NotNull
    private Pkcs11 pkcs11 = new Pkcs11();

    /**
     * Map by kid (key id). Example:
     * crypto.keys.sign-key-01.alias=SIGN_KEY_01
     * crypto.keys.sign-key-01.alg=PS256
     * crypto.keys.sign-key-01.jcaAlg=RSASSA-PSS
     */
    @NotNull
    private Map<String, KeyRef> keys = new HashMap<>();

    @Data
    public static class Pkcs11 {
        /**
         * Enable PKCS#11/HSM provider.
         * If false, service uses the "software" provider (in-memory keypair) for demo/testing.
         */
        private boolean enabled = false;

        /** PKCS#11 provider name (arbitrary). */
        @NotBlank
        private String name = "hsm";

        /** Path to PKCS#11 library (vendor specific), e.g. libCryptoki2_64.so */
        private String library;

        /** Token slot index (optional; depends on vendor). */
        private Integer slot;

        /** Token slot ID (optional; depends on vendor). */
        private Long slotListIndex;

        /** HSM PIN. Prefer environment variable injection. */
        private String pin;
    }

    @Data
    public static class KeyRef {
        @NotBlank
        private String alias;

        /**
         * Logical alg label exposed to clients: RS256/PS256/ES256...
         */
        @NotBlank
        private String alg = "RS256";

        /**
         * JCA algorithm name for java.security.Signature:
         * - RS256: SHA256withRSA
         * - ES256: SHA256withECDSA
         * - PS256: RSASSA-PSS (requires PSSParameterSpec; see PssSupport)
         */
        @NotBlank
        private String jcaAlg = "SHA256withRSA";
    }
}
