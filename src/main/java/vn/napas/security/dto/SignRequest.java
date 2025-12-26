package vn.napas.security.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SignRequest {

    @NotBlank
    private String payloadB64;

}
