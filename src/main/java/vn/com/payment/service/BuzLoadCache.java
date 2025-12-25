package vn.com.payment.service;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import com.safenetinc.luna.LunaSlotManager;

import vn.com.payment.common.keystore.IKeyStore;
import vn.com.payment.common.keystore.PKCS12KeyStoreConnector;
import vn.com.payment.common.keystore.RSAKey;
import vn.com.payment.config.AppConfig;
import vn.com.payment.config.Constants;
import vn.com.payment.config.cache.IDataCache;
import vn.com.payment.config.cache.KeyCache;
import vn.com.payment.config.cache.MerChantCache;
import vn.com.payment.config.cache.ServiceCache;
import vn.com.payment.controllers.APIController;
import vn.com.payment.gateway.database.entities.TblMerchant;
import vn.com.payment.gateway.database.entities.TblService;
import vn.com.payment.gateway.database.home.TblMerchantlHome;
import vn.com.payment.gateway.database.home.TblServiceHome;
import vn.com.payment.rest.RestClient;
import vn.napas.billing.core.commons.GeneralResponse;

import static vn.com.payment.constants.Constants.merchant_status_active;

@Component("BuzLoadCache")
public class BuzLoadCache {
	private static final Logger log = LoggerFactory.getLogger(BuzLoadCache.class);

	IKeyStore keyStore;
	@Autowired
	AppConfig appConfig;


	@Value("${app.gateway_keystore_className:vn.com.payment.common.keystore.PKCS12KeyStoreConnector}")
	String ketStoreClassName;

	@Value("${app.gateway_alias:napas}")
	private String alias;

	@Value("${app.gateway_passphase:passphase}")
	private String passPhase;

	@Value("${app.gateway_keystore_path:path}")
	private String keyStorePath;

	@Value("${app.gateway_keystore_pass:pass}")
	private String keyStorePass;
	
	@Value("${disable.verify.sign:false}")
	Boolean disableVerifySign;
	@Value("${app.api_getmerchant:http://103.109.43.112:8003/config/merchant/internal/list}")
	private String api_get_merchant;

	@Value("${app.api_get_provider:http://103.109.43.112:8003/config/providers/internal/list}")
	private String api_get_provider;

	@Value("${app.api_get_routing:http://103.109.43.112:8003/config/routing/internal/list}")
	private String api_get_routing;

	@Value("${app.api_get_service:http://103.109.43.112:8003/config/services/internal/list}")
	private String api_get_service;

	@Value("${app.api_appid:billing-core}")
	private String api_appid;

	@Value("${app.appSecretKey:8d3c1b2f-4e5f-4a6b-8c9d-0e1f2a3b4c5d}")
	private String api_app_key;
	@Autowired
	RestClient restClient;

	public void process() {
		log.info("Start load cache");

		keyStore = IKeyStore.initKeyStore(ketStoreClassName);

		loadMerchantCache();
		loadServiceCache();
//		loadHSMKeyTest();
//		loadLunaHSMKeyTest();
		
	}

	/**
	 *
	 */
//	private void loadMerchantCache() {
//		try {
//			log.info("Start load loadMerchantCache");
//			List<TblMerchant> listMerchant = tblMerchantlHome.findAll();
//			Map<String, TblMerchant> listmerchantCache = new HashMap<String, TblMerchant>();
//
//			for (TblMerchant merchant : listMerchant)
//				listmerchantCache.put(merchant.getCode(), merchant);
//			if (listmerchantCache.size() > 0) {
//				MerChantCache merchantCache = new MerChantCache();
//				merchantCache.setCache(listmerchantCache);
//			}
//			loadHSMKey(listMerchant);
//		} catch (Exception e) {
//			log.error("Exception", e);
//		}
//	}
//
//	/**
//	 *
//	 */
//	private void loadServiceCache() {
//		try {
//			log.info("Start load loadServiceCache");
//			List<TblService> listService = tblServiceHome.findAll();
//			Map<String, TblService> listServiceCache = new HashMap<String, TblService>();
//
//			for (TblService merchant : listService)
//				listServiceCache.put(merchant.getCode(), merchant);
//			if (listServiceCache.size() > 0) {
//				ServiceCache serviceCache = new ServiceCache();
//				serviceCache.setCache(listServiceCache);
//			}
//		} catch (Exception e) {
//			log.error("Exception", e);
//		}
//	}
//
//	private void loadHSMKey(List<TblMerchant> listMerchant) {
//		try {
////            keyStore = new PKCS12KeyStoreConnector();
//			keyStore.load(keyStorePath, keyStorePass);
//			log.info("Start load loadHSMKey");
//			Map<String, RSAKey> dataMap = new HashMap<>();
//			RSAKey napasrsaKey = new RSAKey();
//			napasrsaKey.setCert(keyStore.getCertificate(alias));
//			napasrsaKey.setPrivateKey(keyStore.getPrivateKey(alias, passPhase));
//			dataMap.put(alias, napasrsaKey);
//			for (TblMerchant merchant : listMerchant) {
//				if (merchant.getStatus().intValue() != merchant_status_active)
//					continue;
//				RSAKey rsaKey = new RSAKey();
//				rsaKey.setCert(keyStore.getCertificate(merchant.getCode()));
//				dataMap.put(merchant.getCode(), rsaKey);
//
//			}
//			if (dataMap.size() > 0) {
//				KeyCache merchantCache = new KeyCache();
//				merchantCache.setCache(dataMap);
//			}
//		} catch (Exception e) {
//			log.error("Exception", e);
//		}
//	}

	
	private void loadMerchantCache() {
		try {
			log.info("Start load loadMerchantCache");
//			List<TblMerchant> listMerchant = tblMerchantlHome.findAll();
			GeneralResponse<List<TblMerchant>> result = restClient.callGetMethod(api_get_merchant, null, buildHttpHeader(),
					new ParameterizedTypeReference<GeneralResponse<List<TblMerchant>>>()  {
					});
//			List<TblMerchant> listMerchant = restClient.callGetMethod(api_get_merchant, null, buildHttpHeader(),
//					new ParameterizedTypeReference<List<TblMerchant>>() {
//					});
			List<TblMerchant> listMerchant = result.getData();
			Map<String, TblMerchant> listmerchantCache = new HashMap<String, TblMerchant>();

			for (TblMerchant merchant : listMerchant)
				listmerchantCache.put(merchant.getCode(), merchant);
			if (listmerchantCache.size() > 0) {
				MerChantCache merchantCache = new MerChantCache();
				merchantCache.setCache(listmerchantCache);
			}
			loadHSMKey(listMerchant);
		} catch (Exception e) {
			log.error("Exception", e);
		}
	}

	/**
	 *
	 */
	private void loadServiceCache() {
		try {
			log.info("Start load loadServiceCache");
//			List<TblService> listService = tblServiceHome.findAll();
			GeneralResponse<List<TblService>> result = restClient.callGetMethod(api_get_service, null, buildHttpHeader(),
					new ParameterizedTypeReference<GeneralResponse<List<TblService>>>()  {
					});
//			List<TblService> listService	= restClient.callGetMethod(api_get_service, null, buildHttpHeader(),
//						new ParameterizedTypeReference<List<TblService>>() {
//						});
			List<TblService> listService = result.getData();
			
			Map<String, TblService> listServiceCache = new HashMap<String, TblService>();

			for (TblService merchant : listService)
				listServiceCache.put(merchant.getCode(), merchant);
			if (listServiceCache.size() > 0) {
				ServiceCache serviceCache = new ServiceCache();
				serviceCache.setCache(listServiceCache);
			}
		} catch (Exception e) {
			log.error("Exception", e);
		}
	}

	private void loadHSMKey(List<TblMerchant> listMerchant) {
		try {
//			IKeyStore keyStore;
			if (disableVerifySign == true)
				return;
			keyStore = IKeyStore.initKeyStore(appConfig.gateway_keystore_className);
//			keyStore = new PKCS12KeyStoreConnector();
			keyStore.load(keyStorePath, keyStorePass);
			log.info("Start load loadHSMKey");
			Map<String, RSAKey> dataMap = new HashMap<>();
			RSAKey napasrsaKey = new RSAKey();
			napasrsaKey.setCert(keyStore.getCertificate(alias));
			napasrsaKey.setPrivateKey(keyStore.getPrivateKey(alias, passPhase));
			
			dataMap.put(alias, napasrsaKey);
			for (TblMerchant merchant : listMerchant) {
				if (merchant.getStatus().intValue() != merchant_status_active)
					continue;
				RSAKey rsaKey = new RSAKey();
				rsaKey.setCert(keyStore.getCertificate(merchant.getCode()));
				dataMap.put(merchant.getCode(), rsaKey);

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
	
//	private void loadHSMKey(List<TblMerchant> listMerchant) {
//		try {
//			IKeyStore keyStore;
//			keyStore = IKeyStore.initKeyStore(appConfig.keystore_classname);
//			keyStore.load(appConfig.keyStorePath, appConfig.keyStorePass);
//			RSAKey napasKey = new RSAKey();
//			log.info("Start load loadHSMKey");
//			Map<String, RSAKey> dataMap = new HashMap<>();
//			// Get Napas key
//			napasKey.setPrivateKey(keyStore.getPrivateKey(appConfig.getNapasKeyAlias(), appConfig.getNapasPassPhase()));
//			napasKey.setCert(keyStore.getCertificate(appConfig.getNapasKeyAlias()));
//			dataMap.put(appConfig.napasKeyAlias, napasKey);
//			for (TblMerchant merchant : listMerchant) {
//				if (merchant.getStatus().intValue() != Constants.merchant_status_active)
//					continue;
//				RSAKey rsaKey = new RSAKey();
//				rsaKey.setCert(keyStore.getCertificate(merchant.getCode()));
//				dataMap.put(merchant.getCode(), rsaKey);
//
//			}
//
//			// Bo sung load key cua Napas theo alias va passphase
//			if (dataMap.size() > 0) {
//				KeyCache merchantCache = new KeyCache();
//				merchantCache.setCache(dataMap);
//			}
//		} catch (Exception e) {
//			log.error("Exception", e);
//		}
//	}

	private HttpHeaders buildHttpHeader() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("accept", "*/*");
		headers.set("appId", api_appid);
		headers.set("appSecretKey", api_app_key);
		return headers;
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
