package vn.napas.security.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyRequest {
    @NotBlank
    private String kid;

    @NotBlank
    private String payloadB64;

    @NotBlank
    private String signatureB64;

    private Long ts;
    private String nonce;
}
