package vn.napas.security.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import vn.napas.security.dto.*;
import vn.napas.security.service.CryptoService;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Base64;

@RestController
@RequestMapping(path = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class CryptoController {

    private final CryptoService cryptoService;

    @PostMapping(path = "/sign", consumes = MediaType.APPLICATION_JSON_VALUE)
    public SignResponse sign(@Valid @RequestBody SignRequest req) throws Exception {
        byte[] payload = Base64.getDecoder().decode(req.getPayloadB64());

        // Optional: bind anti-replay fields into the signed bytes
        byte[] message = buildMessage(req.getTs(), req.getNonce(), payload);

        String signatureB64 = cryptoService.sign(req.getKid(), message);
        return new SignResponse(req.getKid(), cryptoService.algOf(req.getKid()), signatureB64, OffsetDateTime.now().toString());
    }

    @PostMapping(path = "/verify", consumes = MediaType.APPLICATION_JSON_VALUE)
    public VerifyResponse verify(@Valid @RequestBody VerifyRequest req) throws Exception {
        byte[] payload = Base64.getDecoder().decode(req.getPayloadB64());
        byte[] sig = Base64.getDecoder().decode(req.getSignatureB64());

        byte[] message = buildMessage(req.getTs(), req.getNonce(), payload);

        boolean ok = cryptoService.verify(req.getKid(), message, sig);
        return new VerifyResponse(ok, ok ? null : "SIGNATURE_INVALID");
    }

    @GetMapping("/health")
    public String health() {
        return "OK (hsm=" + cryptoService.isHsm() + ")";
    }

    /**
     * Message format:
     *   ts|nonce|payload
     * If ts/nonce are null, it falls back to payload only.
     */
    private byte[] buildMessage(Long ts, String nonce, byte[] payload) {
        if (ts == null && (nonce == null || nonce.isBlank())) {
            return payload;
        }
        String prefix = String.valueOf(ts == null ? "" : ts) + "|" + (nonce == null ? "" : nonce) + "|";
        byte[] p = prefix.getBytes(StandardCharsets.UTF_8);

        byte[] out = new byte[p.length + payload.length];
        System.arraycopy(p, 0, out, 0, p.length);
        System.arraycopy(payload, 0, out, p.length, payload.length);
        return out;
    }
}
