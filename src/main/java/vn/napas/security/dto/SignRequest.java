package vn.napas.security.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SignRequest {
    @NotBlank
    private String kid;

    @NotBlank
    private String payloadB64;

    /**
     * Optional anti-replay fields. If you use them, sign the concatenation of (ts|nonce|payload).
     */
    private Long ts;
    private String nonce;
}
