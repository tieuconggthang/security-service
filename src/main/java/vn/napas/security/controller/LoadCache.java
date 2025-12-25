package vn.napas.security.controller;

import java.util.ArrayList;
import java.util.List;

//import javax.persistence.EntityManager;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

import vn.com.payment.business.BuzLoadCache;
import vn.com.payment.config.cache.IDataCache;

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
