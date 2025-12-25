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

	public boolean isSignatureVerified(BodyMsg bodyMsg) {
		LoggerService loggerService = new LoggerService();
		long start = System.currentTimeMillis();
		try {
			loggerService.setBodyMsg(bodyMsg);
			if (disableVerifySign == true)
				return true;
		
			if (rsaSign == null)
				rsaSign = IRSA.init(keyCache.getKey_store_mode());
			RSAKey rsaKey = keyCache.get(bodyMsg.getPayload().getSendingMember());
			rsaSign.setKey(rsaKey);
//			rsaSign = IRSA.init(keyCache.getKey_store_mode());
			byte[] data = mapper.writeValueAsBytes(bodyMsg.getPayload());
			byte[] signature = Base64.getDecoder().decode(bodyMsg.getHeader().getSignature());
			loggerService.stringInfo("data to verify:", new String(data));
			if (rsaSign.verify(data, signature)) {
				loggerService.stringInfo("validate signature -> OK");
				return true;
			} else {
				if (disableVerifySign == true) {
					loggerService.stringInfo("disableVerifySign -> true");
					return true;
				}
				loggerService.stringInfo("validate signature -> Fail");
				return false;
			}

		} catch (Exception e) {
			loggerService.objectError("validate signature -> Exception: ", e);
			if (disableVerifySign)
				return true;
			return false;
		}
		finally {
			 long end = System.currentTimeMillis();
			 loggerService.stringInfo("isSignatureVerified {} ms", end - start);
		}
	}

	public void createSignature(BodyMsg responseBodyMsg) {
		LoggerService loggerService = new LoggerService();
		long start = System.currentTimeMillis();
		try {
//			if ()
			loggerService.setBodyMsg(responseBodyMsg);
			if (disableVerifySign == true)
				return;
			if (rsaSign == null)
				rsaSign = IRSA.init(keyCache.getKey_store_mode());
			RSAKey rsaKey = keyCache.get(napasAlias);
			
			rsaSign.setKey(rsaKey);

			byte[] data = mapper.writeValueAsBytes(responseBodyMsg.getPayload());
			byte[] sign = rsaSign.sign(data);
			responseBodyMsg.getHeader().setSignature(Base64Util.base64Encode(sign));
			loggerService.stringInfo("create signature -> OK");
		} catch (Exception e) {
			loggerService.objectError("create signature -> Fail: ", e);
		}
		finally {
			 long end = System.currentTimeMillis();
			 loggerService.stringInfo("createSignature {} ms", end - start);
		}
	}
}
