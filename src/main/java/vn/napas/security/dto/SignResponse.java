package vn.napas.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignResponse {
    private String kid;
    private String alg;
    private String signatureB64;
    private String signedAt;
}
