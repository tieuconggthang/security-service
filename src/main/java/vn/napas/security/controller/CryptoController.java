package vn.napas.security.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import vn.com.payment.service.SignatureService;
import vn.napas.security.dto.*;


import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Base64;

@RestController
@RequestMapping(path = "/ach/v2/security", produces = MediaType.APPLICATION_JSON_VALUE)
//@RequestMapping("/ach/v2/oauth2")
@RequiredArgsConstructor
public class CryptoController {

    
    private final SignatureService signatureService;

    @PostMapping(path = "/sign", consumes = MediaType.APPLICATION_JSON_VALUE)
    public SignResponse sign(@Valid @RequestBody SignRequest req) throws Exception {
    	return signatureService.createSignature(req);
    }

    @PostMapping(path = "/verify", consumes = MediaType.APPLICATION_JSON_VALUE)
    public VerifyResponse verify(@Valid @RequestBody VerifyRequest req) throws Exception {
    	return signatureService.isSignatureVerified(req);
    }

    @GetMapping("/health")
    public String health() {
        return "OK....";
    }
}
