
akme.onContent(function(ev){
	console.log(new Date().getTime(), ev, ev.type);

	var baseHref = location.href;
	
	var cx = akme.getContext();
	var shiftAccess = new akme.core.CouchAccess("shiftdb", "../proxy/couchdb.jsp?/shiftdb");
	cx.set("shiftAccess", shiftAccess);
	console.log(akme.formatJSON(shiftAccess.info("2012-01-01_AM_0250")));
	console.log(akme.formatJSON(shiftAccess.info("1")));
	console.log(akme.formatJSON(shiftAccess.read("1")));
	//shiftAccess.read("0");
	//var xhr = akme.xhr.open("HEAD", "../common/akme-core.src.js", false);
	//xhr.send();
	//xhr = akme.xhr.open("GET", "../common/akme-core.src.js", false);
	//xhr.send();
	
});

akme.onLoad(function(ev){
	console.log(new Date().getTime(), ev, ev.type);
	var form = document.forms[0];
	akme.onEvent(form.elements["reloadBtn"], "click", function(ev){ location.reload(); });
	
	akme.onEvent(form.elements["writeBtn"], "click", writeEvent);
	function writeEvent(ev){
		var cx = akme.getContext();
		var shiftAccess = cx.get("shiftAccess");
		var info = shiftAccess.info("1");
		console.log(akme.formatJSON(info));
		var data = shiftAccess.read("1");
		console.log(akme.formatJSON(data));
		//shiftAccess.clear();
		delete data["_rev"];
		console.log(akme.formatJSON(shiftAccess.write("1", data)));
		console.log(akme.formatJSON(shiftAccess.read("1")));
		
		info = shiftAccess.info("0");
		console.log(akme.formatJSON( shiftAccess.copy("1", "0"+ (info["rev"] ? "?rev="+info["rev"] : "")) ));
		/*
		var xhr = akme.xhr.open("COPY", shiftAccess.url+"/1", false);
		xhr.setRequestHeader("Destination", "0");
		xhr.send();
		console.log("COPY ", xhr.status, xhr.statusText, xhr.responseText);
		*/
	};
});
