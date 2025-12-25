# security-service (Spring Boot REST)

REST API để các service khác gọi vào **sign/verify** chữ ký.
- Production: bật profile `pkcs11` để ký bằng **HSM (PKCS#11)**.
- Dev/Test: mặc định chạy **software mode** (tự generate RSA keypair in-memory).

## 1) Chạy local (software mode)
```bash
mvn spring-boot:run
# service chạy ở http://localhost:8088
```

Test nhanh:
```bash
# payload = "hello"
PAYLOAD_B64=$(echo -n "hello" | base64)

curl -s -X POST http://localhost:8088/api/v1/sign \
  -H "Content-Type: application/json" \
  -d "{\"kid\":\"sign-key-01\",\"payloadB64\":\"$PAYLOAD_B64\"}" | jq
```

Lấy signature rồi verify:
```bash
SIG_B64=... # lấy từ response sign

curl -s -X POST http://localhost:8088/api/v1/verify \
  -H "Content-Type: application/json" \
  -d "{\"kid\":\"sign-key-01\",\"payloadB64\":\"$PAYLOAD_B64\",\"signatureB64\":\"$SIG_B64\"}" | jq
```

## 2) Chạy với HSM (PKCS#11)
Bật profile `pkcs11` và cấu hình PKCS#11 library + pin:

```bash
export HSM_PIN='your-pin'
mvn spring-boot:run -Dspring-boot.run.profiles=pkcs11 \
  -Dspring-boot.run.arguments="--crypto.pkcs11.library=/path/to/pkcs11.so --crypto.pkcs11.slot=0 --crypto.pkcs11.pin=$HSM_PIN"
```

### Lưu ý vận hành
- Không login/load keystore mỗi request (project này load 1 lần lúc start).
- Nếu HSM giới hạn session/throughput, bạn nên bổ sung bulkhead (Semaphore) + rate-limit theo `kid`.
- Nếu dùng PS256 (RSA-PSS), project đã set `PSSParameterSpec` cho Signature.

## 3) Message format (anti-replay)
Nếu client gửi `ts` và `nonce`, bytes ký sẽ là:
`ts|nonce|payload`

Client verify cũng phải build message giống hệt.

## 4) Endpoint
- POST `/api/v1/sign`
- POST `/api/v1/verify`
- GET  `/api/v1/health`

## 5) Nâng cấp (khuyến nghị)
- Thêm mTLS/JWT auth để bảo vệ service ký.
- Thêm `/\.well-known/jwks.json` để publish public keys (để verify offline).
- Thêm audit log: requester, kid, hash(payload), latency.
