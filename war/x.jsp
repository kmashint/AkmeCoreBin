<%@page import="
	net.sf.ehcache.Cache,
	net.sf.ehcache.CacheManager,
	java.text.DecimalFormat,
	java.text.NumberFormat,
	java.util.Arrays"
%><%

NumberFormat NF = new DecimalFormat("#,###.######"); 

CacheManager cacheManager = CacheManager.getInstance();
String[] cacheNamesArray = cacheManager.getCacheNames();
// sort the array
Arrays.sort(cacheNamesArray);

for (int i=0; i<cacheNamesArray.length; i++) {
	String cacheName = cacheNamesArray[i];
	Cache cache = cacheManager.getCache(cacheName);
	//boolean isStatic =  StaticDataMgr.isStaticSet(cacheName);
	boolean isStatic = false;
	if (cache.getCacheConfiguration().isEternal()) {
		continue;
	}
	int sizeBefore = cache.getSize();
	if (isStatic) {
		//StaticDataMgr.loadAll(cacheName);
	} else {
		cache.flush();
	}
}

%>