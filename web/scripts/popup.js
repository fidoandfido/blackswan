function createRequest() {
	try {
		request = new XMLHttpRequest();
	} catch (tryMs) {
		try {
			request = new ActiveXObject("Msxml12.XMLHTTP");
		} catch (otherMs) {
			try {
				request = new ActiveXObject("Microsoft.XMLHTTP");
			} catch (failed) {
				request = null;
			}
		}
	}
	return request;
}

//(C) Stephen Daly
// www.stephendaly.org
// Date: 11/3/2008

// Checks if the browsers is IE or another.
// document.all will return true or false depending if its IE
// If its not IE then it adds the mouse event
if (!document.all)
document.captureEvents(Event.MOUSEMOVE)

// On the move of the mouse, it will call the function getPosition
document.onmousemove = getPosition;

// These varibles will be used to store the position of the mouse
var X = 0
var Y = 0

// This is the function that will set the position in the above varibles 
function getPosition(args) 
{
  // Gets IE browser position
  if (document.all) 
  {
    X = event.clientX + document.body.scrollLeft
    Y = event.clientY + document.body.scrollTop
  }
  
  // Gets position for other browsers
  else 
  {  
    X = args.pageX
    Y = args.pageY
  }  
}


function popUpData(companyCode)
{
   	remove('dataDiv');
    
    div = document.createElement('div');
    div.id = 'dataDiv';
    div.setAttribute('onclick', 'remove("dataDiv")'); 
    document.body.appendChild(div);
    div.innerHTML = "<p>Data</p><p>" + companyCode + "</p><p>And... this is where additional company data should be.";
    // if the style.display value is blank we try to check it out here 
    
    if(div.style.display==''&&div.offsetWidth!=undefined&&div.offsetHeight!=undefined)
    {
	div.style.display = (div.offsetWidth!=0&&div.offsetHeight!=0)?'block':'none'; 
    }
    
    // If the PopUp is hidden ('none') then it will display it ('block').
    // If the PopUp is displayed ('block') then it will hide it ('none').
    div.style.display = (div.style.display==''||div.style.display=='block')?'none':'block';
    
    // Off-sets the X position by 15px
    X = X + 15;
    
    // Sets the position of the DIV
    div.style.left = X+'px';
    div.style.top = Y+'px';
}


function popUpGraph(companyCode)
{
	remove('graphDiv');
	
    var div = document.createElement('div');
    div.id = 'graphDiv';
    div.innerHTML = "<div id='innerDiv' onclick=remove('graphDiv')></div>"
    document.body.appendChild(div);
    
    var request = createRequest();
    request.onreadystatechange = showgraph;
    request.open("GET", "/myapp/CompanyGraph.jsp?company_code=" + companyCode, true);
    request.send(null);
}

function showgraph() {
    if (request.readyState == 4) {
		if (request.status == 200) {
		    var div = document.getElementById('graphDiv');
		    var innerDiv = document.getElementById('innerDiv');
		    innerDiv.innerHTML = request.responseText;
	    	    // if the style.display value is blank we try to check it out here 
		    if(div.style.display==''&&div.offsetWidth!=undefined&&div.offsetHeight!=undefined)
		    {
			div.style.display = (div.offsetWidth!=0&&elem.offsetHeight!=0)?'block':'none'; 
		    }
		    // If the PopUp is hidden ('none') then it will display it ('block').
		    // If the PopUp is displayed ('block') then it will hide it ('none').
		    div.style.display = (div.style.display==''||div.style.display=='block')?'none':'block';
		    
		    // Off-sets the X position by 15px
		    X = X - 250;
		    Y = Y + 10;
		    // Sets the position of the DIV
		    div.style.left = X+'px';
		    div.style.top = Y+'px';
		}
    }
}

function remove(divId)
{
	var div = document.getElementById(divId);
	if (div != null) {		
		div.parentNode.removeChild(div);
	}
}


