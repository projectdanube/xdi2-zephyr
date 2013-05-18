package xdi2.core.impl.zephyr.util;

import java.util.Map.Entry;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ZephyrCache {

	private static final Logger log = LoggerFactory.getLogger(ZephyrCache.class);

	private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
	private static final JsonParser jsonParser = new JsonParser();

	private Cache cache;
	private ZephyrCacheLog zephyrCacheLog;

	public ZephyrCache(Cache cache) {

		this.cache = cache;
		this.zephyrCacheLog = new ZephyrCacheLog();
	}

	private JsonObject get(String graphContextNodePath) {

		if (this.getZephyrCacheLog() != null) this.getZephyrCacheLog().add("get()", graphContextNodePath);
		if (log.isDebugEnabled()) log.debug("get(" + graphContextNodePath + ")");

		Element element = this.getCache().get(graphContextNodePath);
		if (element == null) return null;

		return (JsonObject) element.getObjectValue();
	}

	private void put(String graphContextNodePath, JsonObject cachedJson) {

		if (this.getZephyrCacheLog() != null) this.getZephyrCacheLog().add("put()", graphContextNodePath);
		if (log.isDebugEnabled()) log.debug("put(" + graphContextNodePath + "," + cachedJson + ")");

		this.getCache().put(new Element(graphContextNodePath, cachedJson));
	}

	private void remove(String graphContextNodePath) {

		if (this.getZephyrCacheLog() != null) this.getZephyrCacheLog().add("remove()", graphContextNodePath);
		if (log.isDebugEnabled()) log.debug("remove(" + graphContextNodePath + ")");

		this.getCache().remove(graphContextNodePath);
	}

	public void removeAll() {

		if (this.getZephyrCacheLog() != null) this.getZephyrCacheLog().add("remove()", "/*");
		if (log.isDebugEnabled()) log.debug("removeAll()");

		this.getCache().removeAll();
	}

	public JsonObject fetchFromCache(String graphContextNodePath) {

		JsonObject cachedJson = this.get(graphContextNodePath);

		if (cachedJson == null) {

			if (log.isDebugEnabled()) log.debug("MISS(" + graphContextNodePath + ")");
			if (this.getZephyrCacheLog() != null) this.getZephyrCacheLog().miss();

			return null;
		} else {

			if (log.isDebugEnabled()) log.debug("HIT(" + graphContextNodePath + "): " + cachedJson);
			if (this.getZephyrCacheLog() != null) this.getZephyrCacheLog().hit();

			return cloneJsonObject(cachedJson);
		}
	}

	public void storeIntoCache(String graphContextNodePath, JsonObject json) {

		if (graphContextNodePath == null) throw new NullPointerException();

		// main url

		if (json != null) {

			JsonObject cachedJson = cloneJsonObject(json);

			this.put(graphContextNodePath, cachedJson);
		} else {

			this.remove(graphContextNodePath);
		}

		// parent urls

		if (! graphContextNodePath.endsWith("/*")) {

			String parentGraphContextNodePath = graphContextNodePath;

			do {

				this.storeIntoCacheInner(parentGraphContextNodePath + "/*", graphContextNodePath, json);
			} while ((parentGraphContextNodePath = parentGraphContextNodePath(parentGraphContextNodePath)) != null);
		}

		// child urls

		if (graphContextNodePath.endsWith("/*") && json != null) {

			for (Entry<String, JsonElement> entry : json.entrySet()) {

				this.storeIntoCache(entry.getKey(), (JsonObject) entry.getValue());
			}
		}

		// child urls (star)

		if (graphContextNodePath.endsWith("/*") && json != null) {

			for (Entry<String, JsonElement> entry : json.entrySet()) {

				JsonObject tempJson = cloneJsonObject(json);

				for (Entry<String, JsonElement> entry2 : tempJson.entrySet()) {

					if (! entry2.getKey().equals(entry.getKey()) && ! entry2.getKey().startsWith(entry.getKey() + "/")) continue;
				}

				this.put(entry.getKey() + "/*", tempJson);
			}
		}
	}

	private void storeIntoCacheInner(String graphContextNodePath, String innerGraphContextNodePath, JsonObject json) {

		if (graphContextNodePath == null) throw new NullPointerException();
		if (innerGraphContextNodePath == null) throw new NullPointerException();

		if (! graphContextNodePath.endsWith("/*")) throw new IllegalArgumentException("Illegal graph context node path when putting inner graph context node path: " + innerGraphContextNodePath);

		JsonObject cachedJson = this.get(graphContextNodePath);

		if (cachedJson != null) {

			if (json != null) {

				JsonObject cachedInnerJson = cloneJsonObject(json);

				cachedJson.add(innerGraphContextNodePath, cachedInnerJson);
			} else {

				cachedJson.remove(innerGraphContextNodePath);
			}

			this.put(graphContextNodePath, cachedJson);
		}
	}

	public void mergeIntoCache(String graphContextNodePath, JsonObject json) {

		if (graphContextNodePath == null) throw new NullPointerException();
		if (json == null) throw new NullPointerException();

		if (graphContextNodePath.endsWith("/*")) throw new IllegalArgumentException("Illegal graph context node path when merging: " + graphContextNodePath);

		// main url

		JsonObject cachedJson = this.get(graphContextNodePath);

		if (cachedJson != null) {

			for (Entry<String, JsonElement> entry : json.entrySet()) {

				cachedJson.add(entry.getKey(), entry.getValue());
			}

			this.put(graphContextNodePath, cachedJson);
		}

		// parent urls

		String parentGraphContextNodePath = graphContextNodePath;

		do {

			this.mergeIntoCacheInner(parentGraphContextNodePath + "/*", graphContextNodePath, json);
		} while ((parentGraphContextNodePath = parentGraphContextNodePath(parentGraphContextNodePath)) != null);
	}

	private void mergeIntoCacheInner(String graphContextNodePath, String innerGraphContextNodePath, JsonObject json) {

		if (graphContextNodePath == null) throw new NullPointerException();
		if (innerGraphContextNodePath == null) throw new NullPointerException();
		if (json == null) throw new NullPointerException();

		if (! graphContextNodePath.endsWith("/*")) throw new IllegalArgumentException("Illegal graph context node path when merging inner graph context node path: " + graphContextNodePath);

		JsonObject cachedJson = this.get(graphContextNodePath);

		if (cachedJson != null) {

			JsonObject cachedInnerJson = cachedJson.getAsJsonObject(innerGraphContextNodePath);

			if (cachedInnerJson != null) {

				for (Entry<String, JsonElement> entry : json.entrySet()) {

					cachedInnerJson.add(entry.getKey(), entry.getValue());
				}
			} else {

				cachedInnerJson = cloneJsonObject(json);

				cachedJson.add(innerGraphContextNodePath, cachedInnerJson);
			}

			this.put(graphContextNodePath, cachedJson);
		}
	}

	private static String parentGraphContextNodePath(String graphContextNodePath) {

		if (graphContextNodePath.isEmpty()) return null;
		if (graphContextNodePath.lastIndexOf('/') == -1) return "";

		return graphContextNodePath.substring(0, graphContextNodePath.lastIndexOf('/'));
	}

	private static JsonObject cloneJsonObject(JsonObject json) {

		if (json == null) throw new NullPointerException();

		return (JsonObject) jsonParser.parse(gson.toJson(json));
	}

	public Cache getCache() {

		return this.cache;
	}

	public void setCache(Cache cache) {

		this.cache = cache;
	}

	public ZephyrCacheLog getZephyrCacheLog() {

		return this.zephyrCacheLog;
	}

	public void setZephyrCacheLog(ZephyrCacheLog zephyrCacheLog) {

		this.zephyrCacheLog = zephyrCacheLog;
	}
}
