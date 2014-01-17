//END LOC SECTION//

/* TOC and Index */
var currentSelectedItem = null;

function ToggleNode(node)
{
   var expandThis = document.getElementById(node).style;
   if(expandThis.display == "block")
	expandThis.display = "none";
   else
	expandThis.display="block";
}

function ToggleExpand(node)
{
  var content = document.getElementById(node);
  if(content.src.indexOf("collapse.gif")>-1)
    content.src="local/expand.gif";
  else
    content.src="local/collapse.gif";

}

function SetBgColor(selectedItem)
{
	if(currentSelectedItem!=null)
		currentSelectedItem.style.background = "white";
	selectedItem.style.background = "silver";
	currentSelectedItem = selectedItem;
}

function ToggleFilterBox()
{
	var expandThis = document.getElementById('filterBox').style;
   	if(expandThis.display == "block")
   		{
			expandThis.display = "none";
			document.getElementById("content").style.display= "block";
			document.getElementById("searchResult").style.display= "none";

		}
   	else
   		{
   			expandThis.display="block";
			document.getElementById("content").style.display= "none";
			document.getElementById("searchResult").style.display= "block";
			
		}					
}
			

function Filter() {
		
    	var xmlDoc, newDiv, aTags;
    	var r, re, s;
    	newDiv = ""
    	re = new RegExp(document.forms[0].searchText.value ,"i");

	document.getElementById("ContainsError").style.display = "none";
	document.getElementById("NoFilterResult").style.display = "none";
	document.getElementById("Filter").style.display = "none";
	
	if(document.forms[0].searchText.value != "")
	{
		//Get a tags
		var content = document.getElementById("content");
		aTags=content.getElementsByTagName("a");
	
		//Get options
		for(i =0; i < document.forms[0].elements["searchOptions"].options.length; i++)
		{
			if(document.forms[0].elements["searchOptions"].options[i].selected)
			{
				searchType = document.forms[0].elements["searchOptions"].options[i].value; 
			}
		}
		
		//Loop a tags
		for(var i=0; i < aTags.length; i++) 
		{
			//Ignore id = headerActive"
			if (aTags[i].getAttribute("id") != "headerActive")
			{
				var s = aTags[i].firstChild.nodeValue;
				
				//Contains filter
				if(searchType == 0)
				{
					if(document.forms[0].searchText.value.length > 2)
					{
						//Attempt match on search string.
						r = s.search(re);   
						if (r != -1)
						{							
							newDiv += LinkDiv(aTags[i]);
						}
					}
				}
			
				//Starts With Filter
				else if(searchType == 1)
				{
					if(document.forms[0].searchText.value == s.substr(0, document.forms[0].searchText.value.length ))
					{
						newDiv += LinkDiv(aTags[i]);
					}					
				}
			}
		}
	
	
		if (searchType == 0 && document.forms[0].searchText.value.length < 3)
		{
			//ContainsError
			document.getElementById("Filter").style.display = "block";
			document.getElementById("ContainsError").style.display = "block";
			
		}
		else if (newDiv.length > 0)
		{
			document.getElementById("Filter").style.display = "block";
			
		}
		else
		{
			
			document.getElementById("Filter").style.display = "block";
			document.getElementById("NoFilterResult").style.display = "block";
		}

		
		d = document.getElementById("searchResult");
		
		d.innerHTML = newDiv ;
	
		showResult();
		
	}
	else
	{
		showContent()		
	}   	
	

}

function showResult()
{
	document.getElementById("content").style.display= "none";
	//document.getElementById("searchNavigation").style.display= "block";
	document.getElementById("searchResult").style.display= "block";
}

function showContent()
{
	document.getElementById("content").style.display= "block";
	document.getElementById("searchResult").style.display= "none";
	
}

function LinkDiv(aTag)
{
	var div;
	div = "<div style='padding-left:10px'>";
	div += "<a href='" + aTag.getAttribute("href") + "' target='main'>" + 
	aTag.firstChild.nodeValue + "</a>"
	div += "</div>";
	
	return div;
}
	

