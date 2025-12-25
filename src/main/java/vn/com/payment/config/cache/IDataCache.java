package vn.com.payment.config.cache;

import java.util.Map;

public interface IDataCache<T> {

	public void put(String key, T value);

	public T get(String key);

	public boolean containsKey(String key);

	public Map<String, T> getAll();
	
	public void setCache(Map<String, T> cache);

//	public void setKey_store_mode(String key_store_mode);
//	
//	public String  getKey_store_mode();
}