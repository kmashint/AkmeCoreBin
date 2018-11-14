/**
 * Sample script type="javascript" to run from Ant.
 * Include bsf.jar and Rhino js.jar in ant-1.6.5/lib/, or ant-1.7 works with Java 6 Scripting.
 * 
 * Best to use scriptdef to be able to call it with different attributes.
 * Use project.createTask("echo"); to be thread-safe.
 * Use project.getReference("echoInfo"); with <echo id="echoInfo" level="info"/> if single-threaded.
 */

//
// Initalize common variables.
//
var echoTask = project.getReference("echoInfo");
//var echoTask = project.createTask("echo");
// self is undefined, although Ant docs say it should be defined since 1.6.3.
// echoTask.setLevel(Echo.EchoLevel) does not seem possible from scripting.
//echoTask.setLevel(org.apache.tools.ant.taskdefs.Echo.EchoLevel.ERROR);
//var task = project.getThreadTask(java.lang.Thread.currentThread());
//var targetMap = project.getTargets();
//var taskMap = project.getTaskDefinitions();

//
// Perform the task; may branch to different methods based on given attributes and elements.
//
echo("project "+ project);
echo("self "+ typeof(self));
echo("bsf "+ bsf);
echo("Thread "+ java.lang.Thread.currentThread());
echo("ThreadTask "+ project.getThreadTask(java.lang.Thread.currentThread()));

//project.log("Hello from script attr1 " + attributes.get("attr1"));
echo("Hello from script attr1 " + attributes.get("attr1"));
echo("Hello from script attr2 " + attributes.get("attr2"));
echo("Hello from script elements.size() " + elements.size());

var filepath = project.getBaseDir()+"/ant/build-script.xml";
// filepath = "D:/Download/bioshock-artbookhigh.pdf";

var exists = fileExists(filepath);

var lineCount = 0;
forEachLine(filepath, filepath+".bin", false,
	function(line, writer) { 
		lineCount++;
		//echo(line);
		var ary = line.split("\t");
		writer.write(ary.join("\t"));
		writer.newLine();
	});
echo("hello from "+ filepath +" exists "+ exists +" lines "+ lineCount);


//
// Helper methods.
//

function echo(str) {
	echoTask.setMessage(str);
	echoTask.perform();
}

function loadFile(filepath, propertyName) {
	var inf = java.io.File(filepath);
	var task = project.createTask("loadfile");
	task.setProperty(propertyName);
	task.setSrcFile(inf);
	task.perform(); // or execute(), what's the difference?
	return project.getProperty(propertyName);
}

function closeQuiet(stream) {
	if (stream != null) try { stream.close(); } 
	catch (ex) {}
}

function fileExists(filepath) {
	return java.io.File(filepath).exists();
}

// TODO : allow for appending.
function forEachLine(inpath, outpath, appendTrue, callback /* function(line, writer) */) {
	var inf = java.io.File(inpath);
	var append = (appendTrue) ? true : false; // force boolean
	if (inf.exists()) {
		var ins = java.io.BufferedReader(java.io.FileReader(inf));
		var ouf = (outpath) ? java.io.File(outpath) : null;
		var ous = java.io.BufferedWriter(java.io.FileWriter(ouf, append));
		try {
			for (var line = ins.readLine(); line != null; line = ins.readLine()) {
				callback(line, ous);
			}
		}
		finally {
			closeQuiet(ins);
			closeQuiet(ous);
		}
	}
}

