package de.adrianwilke.gutenberg.entities;

import java.util.HashMap;
import java.util.Map;

import de.adrianwilke.gutenberg.exceptions.CacheRuntimeException;

/**
 * Caches for RDF resources.
 * 
 * @author Adrian Wilke
 */
public class Cache<T extends RdfResource> {

	private static Map<String, Cache<?>> caches = new HashMap<String, Cache<?>>();

	/**
	 * Returns new instance, if key/URI not found.
	 * 
	 * @throws CacheRuntimeException
	 */
	public static <T> Cache<?> getCache(Class<T> clazz) {
		try {
			if (caches.containsKey(clazz.getName())) {
				return caches.get(clazz.getName());

			} else {
				Cache<?> cache = Cache.class.getDeclaredConstructor(Class.class).newInstance(clazz);
				caches.put(clazz.getName(), cache);
				return cache;
			}
		} catch (Exception e) {
			throw new CacheRuntimeException(e);
		}
	}

	private Map<String, T> cache = new HashMap<String, T>();
	private Class<?> clazz;

	public Cache(Class<?> clazz) {
		this.clazz = clazz;
	}

	/**
	 * Returns new instance, if key/URI not found.
	 * 
	 * @throws CacheRuntimeException
	 */
	public T get(String uri) {
		try {
			if (cache.containsKey(uri)) {
				return cache.get(uri);
			} else {
				@SuppressWarnings("unchecked")
				T newInstance = (T) clazz.getDeclaredConstructor(String.class).newInstance(uri);
				put(newInstance);
				return newInstance;
			}
		} catch (Exception e) {
			throw new CacheRuntimeException(e);
		}
	}

	public void put(T item) {
		cache.put(item.getUri(), item);
	}

	public boolean containsKey(String uri) {
		return cache.containsKey(uri);
	}
}