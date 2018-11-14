
(function(ac){
	if (!ac) return;
	
	// If the manifest file has changed the all the files listed in the manifest, 
	// as well as those added to the cache by applicationCache.add().
	// W3C indicates availability of lengthComputable/loaded/total but Firefox does not support it.
	// http://www.bennadel.com/blog/2029-Using-HTML5-Offline-Application-Cache-Events-In-Javascript.htm
	
	for (var key in {"checking":1, "downloading":1, "progress":1,
			"cached":1, "noupdate":1, "updateready":1, "error":1, "obsolete":1 }) ac.addEventListener(key, cacheEvent, false);
	function cacheEvent(ev){
		console.log(ev, String(ev), (ev.lengthComputable ? "loaded "+ ev.loaded +"/"+ ev.total : "") );
		if ("updateready"==ev.type) {
			if (confirm("An application update is ready, use it now?")) location.reload();
		}
	}
	
})(window.applicationCache);

akme.onContent(function(ev){
	console.log(new Date().getTime(), ev, ev.type, " navigator.onLine ", navigator.onLine);

	var baseHref = location.href;
	var origin = baseHref.substring(0, baseHref.indexOf('/', 8)); // "http://localhost";
	window.messageBroker = new akme.core.MessageBroker({id:"window.messageBroker", allowOrigins: [origin]});
	akme.onEvent(window, "message", window.messageBroker);

	var headers = {
		"call": "XMLHttpRequest",
		"method": "GET",
		"url": "index.html"
	};
	window.messageBroker.callAsync({src: location.href, contentWindow: window}, headers, null, callback);
	function callback(headers, content) {
		console.log(new Date().getTime(), headers);
	};
	console.log(new Date().getTime(), " after callAsync");
});

akme.onLoad(function(ev){
	console.log(new Date().getTime(), ev, ev.type, " navigator.onLine ", navigator.onLine);
	var form = document.forms[0];
	akme.onEvent(form.elements["reloadBtn"], "click", function(ev){ location.reload(); });
	akme.onEvent(form.elements["xhrBtn"], "click", function(ev){
		var xhr = akme.xhr.open("HEAD", "index.html");
		xhr.onreadystatechange = function(ev) { console.log(new Date().getTime(), " event ", ev, " this ", this, " readyState ", this.readyState); };
		//IE8 does NOT support xhr.attachEvent, asshats.
		//akme.onEvent(xhr, "readystatechange", function(ev) { console.log(ev, ev.target, " ", ev.target.readyState); });
		xhr.send();
	});
});
