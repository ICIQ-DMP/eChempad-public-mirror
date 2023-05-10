/////////////////// Geometry loading ////////////////////////////////		
jmol_isReady = function(applet) {		
	Jmol.resizeApplet(jmolApplet, ["100%","100%"]);
}		

var JmolConfigInfo = {
	width: '100%',
	height: '100%',
	debug: false,
	color: "0xFFFFFF",
	addSelectionOptions: false,		
	use: "HTML5",
	j2sPath: "/create/html/xslt/jsmol/j2s",
	readyFunction: jmol_isReady,
	disableJ2SLoadMonitor: true,
  	disableInitialConsole: true,
  	allowJavaScript: true,
  	console: "none"
};

function hasCell(document){
	return document.find("crystal").length > 0;                                    
}
 
function getCellParameters(document){
	var sc1 = document.find("scalar[id='sc1']").text();                                       
	var sc2 = document.find("scalar[id='sc2']").text();
	var sc3 = document.find("scalar[id='sc3']").text();
	var sc4 = document.find("scalar[id='sc4']").text();
	var sc5 = document.find("scalar[id='sc5']").text();
	var sc6 = document.find("scalar[id='sc6']").text();
	var unitCell = "{ " + sc1 + " " + sc2 + " " + sc3 + " " + sc4 + " " + sc5 + " " + sc6 + "}";
	return unitCell;
}	

function clearMolecule() {
	if (typeof jmolApplet !== "undefined" && jmolApplet !== null)
		Jmol.script(jmolApplet,"delete *");
	if (typeof jmolMoldenApplet !== "undefined" && jmolMoldenApplet !== null)
		Jmol.script(jmolMoldenApplet,"delete *");	
}

function clearMoldenOrbital() {
	if (typeof jmolOrbitalApplet !== "undefined" && jmolOrbitalApplet !== null)
		Jmol.script(jmolOrbitalApplet,"delete *");
}

function loadMolecule(id, file){
	loadMolecule(id, file, true);
}

function loadMolecule(id, file, isXml){
 	jmolApplet = Jmol.getApplet("jmolApplet", JmolConfigInfo);
 		$('.jsmoldiv').html(Jmol.getAppletHtml(jmolApplet));

	if(isXml){
		$.ajax({ url: "/create/innerServices/getfile?id=" + id + "&file=" + file, 
	         async: false,
	         dataType: 'xml',
	         success: function(data , textStatus, jqXHR) {
				 var url = jqXHR.url;
	        	 var geometry = $(data);
	        	 if(hasCell(geometry)) {
	 				var cellParameters = getCellParameters(geometry);
	 				Jmol.script(jmolApplet,"set zoomLarge TRUE;"); // Set it to FALSE in order to make molecules stretch to current viewpoint
	 				Jmol.script(jmolApplet,"delete *; load " + url + " { 1 1 1 } unitcell " + cellParameters + "; set platformSpeed 1; centerAt average;");
	 			 } else {  				
	 				Jmol.script(jmolApplet,"set zoomLarge TRUE;");
	 				Jmol.script(jmolApplet,"delete *; load " + url + " ;set platformSpeed 1; centerAt average;"); 				
	 			 }			        	 			        	 
	         },
	         error: function(jqXHR, textStatus, errorThrown){
	        	 Jmol.script(jmolApplet,"delete *;");
	         }
		});				
	} else {		
		 var url = "/create/innerServices/getfile?id=" + id + "&file=" + file;		 
		 Jmol.script(jmolApplet,"set zoomLarge TRUE;");
		 Jmol.script(jmolApplet,"delete *; load " + url + " ;set platformSpeed 1; centerAt average;");
	}
}
	
////////////////////////// Molden orbital file display ///////////////////////////
jmolOrbital_isReady = function(applet) {
	Jmol.resizeApplet(jmolOrbitalApplet, ["100%","100%"]);
}

var jmolOrbitalConfigInfo = {
	width: "100%",
	height: "100%",
	debug: false,
	color: "0xFFFFFF",
	addSelectionOptions: false,		
	use: "HTML5",
	j2sPath: "/create/html/xslt/jsmol/j2s",
	readyFunction: jmolOrbital_isReady,
	disableJ2SLoadMonitor: true,
  	disableInitialConsole: true,
  	allowJavaScript: true,
  	console: "none"
};

function loadMoldenOrbital(id, file, orbitalNumber){
	jmolOrbitalApplet = Jmol.getApplet("jmolOrbitalApplet", jmolOrbitalConfigInfo);	    			
	if($('.jsmolorbitaldiv').empty())	    		
		$('.jsmolorbitaldiv').html(Jmol.getAppletHtml(jmolOrbitalApplet));
	
	var platformSpeed = 5;
	var moldenFileUrl = "/create/innerServices/molden?id=" + id + "&file=" + file + "&index=" + orbitalNumber;
	var orientation = Jmol.getPropertyAsArray(jmolOrbitalApplet, 'orientationInfo.moveTo');						    			
	Jmol.script(jmolOrbitalApplet,"delete *; load " + moldenFileUrl + "; set platformSpeed " + platformSpeed + ";isosurface mo 1 cutoff 0.05; wireframe on;color cpk;cpk off;color isosurface translucent; centerAt average;");
	Jmol.script(jmolOrbitalApplet,"animation OFF; " + orientation);
		jmolOrbital_isReady();
	}
 