package vn.com.payment.config.cache;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import vn.com.payment.common.keystore.RSAKey;

@Component
@Setter
@Getter
@Slf4j
public class KeyCache implements IDataCache<RSAKey> {

	private static Map<String, RSAKey> dataMap = new HashMap<>();
	private static String key_store_mode = "JKS";

	@Override
	public void put(String key, RSAKey value) {
		dataMap.put(key, value);
	}

	@Override
	public RSAKey get(String key) {
		if (dataMap == null) {
			log.info("no cache key");
			return null;
		}
		if (dataMap.containsKey(key) == false) {
			log.info("alias not exit {}", key);
			return null;
		}
		return dataMap.get(key);
	}

	@Override
	public boolean containsKey(String key) {
		return dataMap.containsKey(key);
	}

	@Override
	public Map<String, RSAKey> getAll() {
		return dataMap;
	}

	@Override
	public void setCache(Map<String, RSAKey> cache) {
		// TODO Auto-generated method stub
		dataMap = cache;

	}

//	@Override
	public void setKey_store_mode(String key_store_mode) {
		// TODO Auto-generated method stub
		this.key_store_mode = key_store_mode;
	}

//	@Override
	public String getKey_store_mode() {
		// TODO Auto-generated method stub
		return key_store_mode;
	}

}
