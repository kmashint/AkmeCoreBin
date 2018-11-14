// Global utility methods: console.log("hello") for BIRT, akme JS enhancements, and akme.birt specific enhancements.
// Note JS with Java may not properly support the following although Java7 has better JS support than Java6:
// - arguments within function(){} scope
// - this within function(){} scope

/**
 * Basic Javascript enhancements.
 */
akme = {
	constructor : function(parent) {
		// Cover gaps in Rhino JS.
		/**
		 * Perform a binary search of an array for an object assuming the array is already sorted.
		 */
		if (!Array.binarySearch) Array.binarySearch = function (a,o) {
		    var l = 0, u = a.length, m = 0;
		    while ( l <= u ) { 
		        if ( o > a[( m = Math.floor((l+u)/2) )] ) l = m+1;
		        else u = (o == a[m]) ? -2 : m - 1;
		    }
		    return (u == -2) ? m : -1;
		};
		
		/**
		 * Add Object.create(prototype) if not available that creates an Object with the given prototype 
		 * without calling the typical constructor function for that prototype.
		 * https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/create
		 */
		if (!Object.create) Object.create = (function(){
		    function F(){};
		    return function(o){
		        if (arguments.length != 1) {
		            throw new Error('Object.create implementation only accepts one parameter.');
		        }
		        F.prototype = o;
		        return new F();
		    };
		})();
		
		/**
		 * Add Object.getPrototypeOf() if not available that returns the prototype of an object.
		 * https://developer.mozilla.org/en/JavaScript/Reference/Global_Objects/Object/getPrototypeOf
		 */
		if (!Object.getPrototypeOf) Object.getPrototypeOf = function(obj){ return obj.__proto__; };
		
		
		// Add a JFrame console window as needed.
		
		var System = Packages.java.lang.System,
			BoxLayout = Packages.javax.swing.BoxLayout,
			JFrame = Packages.javax.swing.JFrame,
			JTextArea = Packages.javax.swing.JTextArea,
			JPanel = Packages.javax.swing.JPanel,
			JScrollPane = Packages.javax.swing.JScrollPane;

		/**
		 * Implement a console for BIRT, e.g. console.log("hello") using either Swing or System.out.
		 * Since this may be re-created elsewhere using toSource() and eval(), avoid private closure variables.
		 */
		parent.console = {
			enabled : !(reportContext.getHttpServletRequest().getUserPrincipal()),
			device : !(reportContext.getHttpServletRequest().getUserPrincipal()) ? "window" : "console",
			assert : function(text) { if (!text) console.log("?! "+ text); },
			error : function(t1,t2,t3,t4,t5) { console.log("! "+ console.join([t1,t2,t3,t4,t5])); },
			warn : function(t1,t2,t3,t4,t5) { console.log("? "+ console.join([t1,t2,t3,t4,t5])); },
			info : function(t1,t2,t3,t4,t5) { console.log("* "+ console.join([t1,t2,t3,t4,t5])); },
			debug : function(t1,t2,t3,t4,t5) { console.log(console.join([t1,t2,t3,t4,t5])); },
			join : function(a) { return a.join(" "); },
			log : function (text) {
			    if (!this.enabled) return;
			    if ("console" == this.device) {
				    System.out.println(String(text));
			    	return;
			    }
			    var frameName = "consoleJFrame";
			    
			    var currJText = reportContext.getGlobalVariable(frameName);
			    if (currJText == null) {
			        var jFrame = new JFrame("BIRT Console - "+ reportContext.getReportRunnable().getReportName());
			        var jText = new JTextArea(String(text));
			        jText.append("\n");

			        var jPanel = new JPanel();
			        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
			        jPanel.add(jText);

			        var jScrollPane = new JScrollPane(jPanel, 
			        		JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
			        		JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			        jScrollPane.getHorizontalScrollBar().setUnitIncrement(5);
			        jScrollPane.getVerticalScrollBar().setUnitIncrement(5);
			        jScrollPane.setAlignmentX(JScrollPane.LEFT_ALIGNMENT);

			        jFrame.add(jScrollPane);
			        jFrame.setSize(800, 600);
			        jFrame.setVisible(true);
			 
			        reportContext.setGlobalVariable(frameName, jText);
			    } else {
			    	currJText.append(String(text));
			    	currJText.append("\n");
			    }
			}
		};
		
	},
	
	WHITESPACE_TRIM_REGEXP : /^\s+|\s+$/gm,
	PRINTABLE_EXCLUDE_REGEXP : /[^\x20-\x7e\xc0-\xff]/g,
	MILLIS_IN_DAY : 24*60*60000,
	
	isDefinedNotNull : function(obj) { 
		return typeof obj !== "undefined" && obj !== null; 
	},
	clone : function (obj) {
		if (obj === null || obj === undefined) return obj;
		if (typeof obj.clone === "function") return obj.clone();
		var clone = Object.create(Object.getPrototypeOf(obj));
		for (var key in obj) if (obj.hasOwnProperty(key)) clone[key] = obj[key];
		return clone;
	},
	copy : function (obj, map, /*boolean*/ all) {
		if (map === null || typeof map === "undefined") return obj;
		all = !!all;
		for (var key in map) if (all || map.hasOwnProperty(key)) obj[key] = map[key];
		return obj;
	},
	copyAll : function (obj, map) { 
		return this.copy(obj, map, true); 
	},
	copyArrayToObject : function (obj, ary, keyName, valName) {
		if (typeof valName != 'undefined') for (var i=0; i<ary.length; i++) obj[ary[i][keyName]] = ary[i][valName];
		else for (var i=0; i<ary.length; i++) obj[ary[i][keyName]] = ary[i];
		return obj;
	},
	concat : function (ary /*, coll, ... */) {
		for (var j=1,m=arguments.length; j<m; j++) { var coll = arguments[j];
			for (var i=0,n=coll.length; i<n; i++) ary[ary.length]=(coll[i]);
		}
		return ary;
	},
	concatMapKeys : function(ary, map) {
		for (var key in map) ary[ary.length] = key;
		return ary;
	},
	getProperty : function ( /*object*/ obj, /*Array or String*/ path, def ) {
		if ( typeof path === 'string' || path instanceof String ) { path = path.split('.'); }
		var prop = obj;
		var n = path.length;
		for (var i=0; i<n; i++) {
			if (prop != null && path[i] in prop) prop = prop[path[i]];
			else return def;
		}
		return prop;
	},
	setProperty : function ( /*object*/ obj, /*Array or String*/ path, val ) {
		if ( typeof path === 'string' || path instanceof String ) { path = path.split('.'); }
		var prop = obj;
		var n = path.length-1;
		for (var i=0; i<n; i++) {
			if (path[i] in prop) prop = prop[path[i]];
			else prop = prop[path[i]] = {};
		}
		prop[path[n]] = val;
		return prop;
	},
	newApplyArgs : function (fn, args) {
		if (!args || args.length === 0) return new fn();
		switch (args.length) {
		case 1: return new fn(args[0]);
		case 2: return new fn(args[0],args[1]);
		case 3: return new fn(args[0],args[1],args[2]);
		case 4: return new fn(args[0],args[1],args[2],args[3]);
		case 5: return new fn(args[0],args[1],args[2],args[3],args[4]);
		default: 
			var buf = new Array(args.length);
			for (var i=0; i<args.length; i++) buf[i] = "a["+ i +"]";
			return (new Function("f","a","return new f("+ buf.join(",") +");"))(fn,args);
		}
	},
	limitLengthWithDots : function (str,len) {
		str = String(str);
		return (str.length > len) ? str.substring(0,len-3)+"..." : str;
	},
	padLeft : function (val, size, ch) {
		var s = String(val);
		if (s.length >= size) return s;
		if (s.length+1 == size) return ch+s;
		var a = new Array(1 + (size > s.length ? size-s.length : 0));
		a[a.length-1] = s;
		return a.join(ch ? ch : " ");
	},
	setValidDateTimeInt: function(defaultDt, dateTimeLong) {
		var d = Math.floor(dateTimeLong / 1000000);
		var t = (dateTimeLong % 1000000);
		return this.setValidDateTime(defaultDt, 
				Math.floor(d / 10000), Math.floor(d / 100) % 100, d % 100,
				Math.floor(t / 10000), Math.floor(t / 100) % 100, t % 100);
	},
	setValidDateTime: function(defaultDt, ye, mo, da, ho, mi, se, ms) {
		var dt = new Date(ye, parseInt(mo||1,10)-1, parseInt(da||1,10), parseInt(ho||0,10), parseInt(mi||0,10), parseInt(se||0,10), parseInt(ms||0,10));
		if (dt.getFullYear()!=ye || dt.getMonth()+1!=mo || dt.getDate()!=da) dt = defaultDt;
		else if (arguments.length >= 5 && (dt.getHours()!=ho || dt.getMinutes()!=mi || dt.getSeconds()!=se)) dt = defaultDt;
		else if (arguments.length >= 8 && dt.getMilliseconds()!=ms) dt = defaultDt;
		return dt;
	},
	parseDate : function (dateStr) { // Based on example from Paul Sowden.
	    var d = dateStr.match(/([0-9]{4})(-([0-9]{2})(-([0-9]{2})([T ]([0-9]{2}):([0-9]{2})(:([0-9]{2})(\.([0-9]+))?)?(Z|(([-+])([0-9]{2}):([0-9]{2})))?)?)?)?/);
	    //var offset = 0;
	    var date = new Date(d[1], parseInt(d[3]||1,10)-1, parseInt(d[5]||1,10), 
	    		parseInt(d[7]||0,10), parseInt(d[8]||0,10), parseInt(d[10]||0,10), d[12] ? Number("0."+d[12])*1000 : 0);
	    return date;
	},
	formatIsoDate : function (date) {
		return date.getFullYear()+'-'+this.padLeft(date.getMonth()+1, 2, '0')+'-'+this.padLeft(date.getDate(), 2, '0');
	},
	formatIsoDateTime : function (dt, delimiter) {
		if (!delimiter) delimiter = "T";
        var dn = (dt.getFullYear()*10000+(dt.getMonth()+1)*100+dt.getDate()) * 1000000 +
        	dt.getHours()*10000+dt.getMinutes()*100+dt.getSeconds();
        return String(dn).replace(/^(....)(..)(..)(..)(..)(..)/, "$1-$2-$3"+delimiter+"$4:$5:$6."+String(dt.getMilliseconds()+1000).substring(1));
	},
	addDays : function(date,days) {
		date.setDate(thuDate.getDate() + days);
		return date;
	},
    /**
     * Return a 7-digit year*1000+dayOfYear where dayOfYear=1 on the Monday of the week with 4-Jan in it,
     * equivalently the dayOfYear=1 on the Monday of the week with the first Thursday in it (ISO-8601).
     * Use (int)result/1000 to get the year, (int)result%1000 to get the dayOfYear,
     * and (int)(result%1000-1)/7+1 to get the weekOfYear.
     */
	getYearDayByIsoMonday : function(date) {
		var thuOffset = 3 - (date.getDay()+6)%7; // 0=Sun
		var thuDate = new Date(date.getTime());
		thuDate.setDate(thuDate.getDate() + thuOffset); // bridge year by first Thursday
		var isoYear = thuDate.getFullYear();
        var isoWeek0 = Math.round(( thuDate.getTime()-new Date(thuDate.getFullYear(),0,1).getTime() )/this.MILLIS_IN_DAY-1)/7;
		return isoYear*1000 + isoWeek0*7 + 3-thuOffset;
	},
	
	/**
	 * Return a Date given a year and day of year based on ISO weeks (ISO-8601) that start the Monday of the week with 4-Jan in it.
	 * JavaScript getDay() gives 0 for Sunday to 6 for Saturday. Java gives 1 for Sunday to 7 for Saturday. Ouch.
	 */
	getDateByIsoMonday : function(year, doy) {
		year = Math.floor(year);
		week = Math.floor(doy);
        var result = new Date(year, 1-1, 4);
        result.setDate(result.getDate() -(result.getDay()+6)%7 + (doy-1));
        return result;
	},
	
	/**
	 * BIRT-specific Javascript enhancements.
	 */
	birt : {
		recentSunday : function(bdate) {
			return BirtDateTime.addDay(bdate, -BirtDateTime.weekDay(bdate,1) +
				( "123".indexOf(String(BirtDateTime.weekDay(bdate,1))) != -1 ? -6 : 1 )
			 );
		}
	}
	
};
akme.constructor(this); // Use deferred constructor to allow for later re-construction via fw.toSource() and eval().
