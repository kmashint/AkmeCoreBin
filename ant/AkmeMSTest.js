// Test some other Microsoft functionality.
// cscript //nologo AkmeMSTest.js
//
var voc = WScript.CreateObject("SAPI.SpVoice");
var msg = "Attention!  This is a test of the emergency broadcasting system.";
WScript.Echo(msg);
voc.Speak(msg);
(function callback(voc) { if (!voc.WaitUntilDone(100)) setTimeout(callback, 0); })(voc);

