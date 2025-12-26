package vn.com.payment.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import vn.com.payment.common.keystore.RSAKey;
import vn.com.payment.common.rsa.IRSA;
import vn.com.payment.config.cache.KeyCache;
import vn.com.payment.obj.message.BodyMsg;
import vn.com.payment.ultities.Base64Util;
import vn.napas.security.dto.SignRequest;
import vn.napas.security.dto.SignResponse;
import vn.napas.security.dto.VerifyRequest;
import vn.napas.security.dto.VerifyResponse;

@Service
@Slf4j
public class SignatureService {

	final ObjectMapper mapper;

	@Value("${app.gateway_alias:napas}")
	String napasAlias;
	@Value("${disable.verify.sign:false}")
	Boolean disableVerifySign;

//	@Autowired
	private KeyCache keyCache;
	private IRSA rsaSign;

	public SignatureService() {
		super();
		// TODO Auto-generated constructor stub
		keyCache = new KeyCache();
//		rsaSign = IRSA.init(keyCache.getKey_store_mode());

		mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.configure(SerializationFeature.INDENT_OUTPUT, false);
	}

	//
////    @Autowired
//	private KeyCache keyCache;

//	private final LoggerService loggerService = new LoggerService();

	public VerifyResponse isSignatureVerified(VerifyRequest verifyReq) {

		long start = System.currentTimeMillis();
		VerifyResponse verifyResponse = new VerifyResponse(true, "init");
		try {
			if (disableVerifySign == true)
				return verifyResponse;
			if (rsaSign == null)
				rsaSign = IRSA.init(keyCache.getKey_store_mode());
			RSAKey rsaKey = keyCache.get(verifyReq.getAliasName());
			rsaSign.setKey(rsaKey);
//			rsaSign = IRSA.init(keyCache.getKey_store_mode());
			byte[] data = mapper.writeValueAsBytes(verifyReq.getPayloadB64());
			byte[] signature = Base64.getDecoder().decode(verifyReq.getSignatureB64());
			if (rsaSign.verify(data, signature)) {
				verifyResponse.setValid(true);
				verifyResponse.setReason("Veriry ok");
				return verifyResponse;
//				return true;
			} else {
				if (disableVerifySign == true) {
					verifyResponse.setValid(true);
					verifyResponse.setReason("disableVerifySign = true");
					return verifyResponse;
				}
				verifyResponse.setReason("verify false");
				verifyResponse.setValid(false);
				return verifyResponse;
			}

		} catch (Exception e) {

			if (disableVerifySign) {
				verifyResponse.setValid(true);
				verifyResponse.setReason("disableVerifySign = true");
				return verifyResponse;
			}
			verifyResponse.setValid(false);
			verifyResponse.setReason("disableVerifySign = true");
			return verifyResponse;
		} finally {
			long end = System.currentTimeMillis();

		}
	}

	public SignResponse createSignature(SignRequest signRequest) {

		long start = System.currentTimeMillis();
		SignResponse response = new SignResponse(signRequest.getPayloadB64(), "");
		try {
			if (rsaSign == null)
				rsaSign = IRSA.init(keyCache.getKey_store_mode());
			RSAKey rsaKey = keyCache.get(napasAlias);
			rsaSign.setKey(rsaKey);
			byte[] data = mapper.writeValueAsBytes(signRequest.getPayloadB64());
			byte[] sign = rsaSign.sign(data);
			response.setSignatureB64(Base64Util.base64Encode(sign));
//			return Base64Util.base64Encode(sign);
			return response;
		} catch (Exception e) {
			return null;
		} finally {
			long end = System.currentTimeMillis();

		}
	}
}
