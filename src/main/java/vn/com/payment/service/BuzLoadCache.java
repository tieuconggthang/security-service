package vn.com.payment.service;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import vn.com.payment.common.keystore.IKeyStore;
import vn.com.payment.common.keystore.PKCS12KeyStoreConnector;
import vn.com.payment.common.keystore.RSAKey;
import vn.com.payment.config.cache.KeyCache;

@Component("BuzLoadCache")
public class BuzLoadCache {
	private static final Logger log = LoggerFactory.getLogger(BuzLoadCache.class);

	IKeyStore keyStore;
//	@Autowired
//	AppConfig appConfig;


	@Value("${app.gateway_keystore_className:vn.com.payment.common.keystore.PKCS12KeyStoreConnector}")
	String ketStoreClassName;

	@Value("${app.gateway_alias:napas}")
	private String napasalias;

	@Value("${app.gateway_passphase:passphase}")
	private String passPhase;

	@Value("${app.gateway_keystore_path:path}")
	private String keyStorePath;

	@Value("${app.gateway_keystore_pass:pass}")
	private String keyStorePass;
	
	
	
  
	public void process() {
		log.info("Start load cache");
		keyStore = IKeyStore.initKeyStore(ketStoreClassName);
		loadHSMKey();
	}


	private void loadHSMKey() {
		try {

			keyStore = IKeyStore.initKeyStore(ketStoreClassName);
			keyStore.load(keyStorePath, keyStorePass);
			log.info("Start load loadHSMKey");
			Map<String, RSAKey> dataMap = new HashMap<>();
			RSAKey napasrsaKey = new RSAKey();
			napasrsaKey.setCert(keyStore.getCertificate(napasalias));
			napasrsaKey.setPrivateKey(keyStore.getPrivateKey(napasalias, passPhase));
			
			dataMap.put(napasalias, napasrsaKey);
			Enumeration<String> aliases = keyStore.getAllAlias();
			List<String> liasList = Collections.list(aliases);
			for (String alias : liasList) {
//				if (merchant.getStatus().intValue() != merchant_status_active)
//					continue;
				if (alias.equalsIgnoreCase(napasalias))
					continue;
				RSAKey rsaKey = new RSAKey();
				rsaKey.setCert(keyStore.getCertificate(alias));
				dataMap.put(alias, rsaKey);

			}
			if (dataMap.size() > 0) {
				KeyCache merchantCache = new KeyCache();
				merchantCache.setCache(dataMap);
				merchantCache.setKey_store_mode(keyStore.getProvider());
			}
		} catch (Exception e) {
			log.error("Exception", e);
		}
	}
	
	
	
	private void loadHSMKeyTest() {
		try {
//            keyStore = new PKCS12KeyStoreConnector();
			IKeyStore hsmkeyStore = IKeyStore.initKeyStore("vn.com.payment.common.keystore.HSMKeyStoreManager");
//			IKeyStore lunaKeyStore = IKeyStore.initKeyStore("vn.com.payment.common.keystore.HSMKeyStoreLunaProvider");
			
			
			hsmkeyStore.load("./keystore/luna.cfg", "TYxC-/WAd-F37A-qdHL");
			log.info("Start load luna hsm (pcks11) to test");
			Enumeration<String> aliases = hsmkeyStore.getAllAlias();
			List<String> aliasList = Collections.list(aliases);
			log.info("alias size: " + aliasList.size());
			for (String alias : aliasList) {
			    System.out.println("Alias: " + alias);
			}


		} catch (Exception e) {
			log.error("Exception", e);
		}
	}
	
	
	private void loadLunaHSMKeyTest() {
		try {
            keyStore = new PKCS12KeyStoreConnector();
//			IKeyStore hsmkeyStore = IKeyStore.initKeyStore("vn.com.payment.common.keystore.HSMKeyStoreManager");
			IKeyStore lunaKeyStore = IKeyStore.initKeyStore("vn.com.payment.common.keystore.HSMKeyStoreLunaProvider");
			
			
			lunaKeyStore.load("./keystore/hsm-config.json", "TYxC-/WAd-F37A-qdHL");
			log.info("Start load luna hsm (luna provider) to test");
			Enumeration<String> aliases = lunaKeyStore.getAllAlias();
			List<String> aliasList = Collections.list(aliases);
			log.info("alias size: " + aliasList.size());
			for (String alias : aliasList) {
			    System.out.println("Alias: " + alias);
			}
//			while (aliases.hasMoreElements()) {
//				String alias = aliases.nextElement();
//				System.out.println("Alias: " + alias);
//			}
//			LunaSlotManager lsm = LunaSlotManager.getInstance();
//			lsm.setDefaultSlot(0);
//			int State = lsm.login("TYxC-/WAd-F37A-qdHL");
////								   TYxC-/WAd-F37A-qdHL
//			System.out.println("login state:" + State);

		} catch (Exception e) {
			log.error("Exception", e);
		}
	}
}
