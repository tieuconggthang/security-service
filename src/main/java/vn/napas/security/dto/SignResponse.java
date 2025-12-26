package vn.napas.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignResponse {
	private String payloadB64;
    private String signatureB64;
}
