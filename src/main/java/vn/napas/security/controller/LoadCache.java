package vn.napas.security.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import vn.com.payment.service.BuzLoadCache;

@Component("loadCache")
public class LoadCache {
	@Autowired
	BuzLoadCache buzLoadCache;
	public LoadCache() {
	}

	public void loadData() {
		try {
			
			buzLoadCache.process();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	

	

}
