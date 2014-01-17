//END LOC SECTION//
window.onload=LoadPage;
window.onunload=Window_Unload;
window.onresize=ResizeWindow;
window.onbeforeprint = set_to_print;
window.onafterprint = reset_form;

var languageSelection;
var scrollPos = 0;

function LoadPage()
{	if(IsEvilUrl())
	{
		ReloadEvilUrlAsGoodUrl();
		return;	// in just a moment, page will reload.
	}
	// show correct language
	languageSelection = Load("language");
	if(languageSelection == null)
		languageSelection = "all";
	ChangeLanguage(languageSelection);

	ResizeWindow();
	
	// vs70.js did this to allow up/down arrow scrolling, I think
	try { mainSection.setActive(); } catch(e) { }

	// make body visible, now that we're ready to render
	document.body.style.display = "";

	//set the scroll position
	try{mainSection.scrollTop = scrollPos;}
	catch(e){}
	//START: Feedback Integration

	document.feedback = new FeedBack(L_alias, L_product, L_deliverable, L_productversion, L_docversion, L_FeedBackDivID);
	document.feedback.StartFeedBack("fb");

	//END: Feedback Integration
}

function Window_Unload()
{
	if(IsGoodUrl())
	{
	// save persistable data (except when unloading from a "bad url")
	Save("language", languageSelection);
	}
}

function ResizeWindow()
{
	if (document.body.clientWidth==0) return;
	var header = document.getElementById("header");
	var mainSection = document.getElementById("mainSection");
	if (mainSection == null) return;
	
	
	document.body.scroll = "no"

	if (window.ActiveXObject)
		mainSection.style.overflow= "auto";

	header.style.width= document.body.offsetWidth - 2;
	mainSection.style.paddingRight = "20px"; // Width issue code
	mainSection.style.width= document.body.offsetWidth - 4;
	mainSection.style.top=0;  
	if (document.body.offsetHeight > header.offsetHeight + 10)
		mainSection.style.height= document.body.offsetHeight - (header.offsetHeight + 10) 
	else
		mainSection.style.height=0
	
	try
	{
		mainSection.setActive();
	}
	catch(e)
	{
	}
}

function ChangeLanguage_CheckKey(key)
{
	if(window.event.keyCode == 13)
		ChangeLanguage(key);
}

function ChangeLanguage(key)
{
	languageSelection = key;

	var spanElements = document.getElementsByTagName("span");
	for(i = 0; i < spanElements.length; ++i)
	{
		if(spanElements[i].codeLanguage != null)
		{
			if((spanElements[i].codeLanguage == languageSelection) || (languageSelection == "all") || (spanElements[i].codeLanguage == ""))
			{
				spanElements[i].style.display = "block";
			}
			else
			{
				spanElements[i].style.display = "none";
			}
		}
	}
	
	var spanElements = document.getElementsByTagName("span");
	for(i = 0; i < spanElements.length; ++i)
	{
		if(spanElements[i].name != null)
		{
			if(spanElements[i].name == "label")
			{
				if(languageSelection == "all")
				{
					spanElements[i].style.display = "block";
				}
				else
				{
					spanElements[i].style.display = "none";
				}
			}
		}
	}
}

function ChangeSeeAlso()
{
	var url = selectSeeAlso.options[selectSeeAlso.selectedIndex].value;
	location.href = url;
}

function IsEvilUrl()
{
	var url = "" + document.location + ".";
	var r = url.indexOf("mk:@MSITStore") != -1;
	return r;
}

function IsGoodUrl()
{
	return !IsEvilUrl();
}

function ReloadEvilUrlAsGoodUrl()
{
	var url = "" + document.location + ".";
	var i = url.indexOf("mk:@MSITStore");
	if(i != -1)
	{
		url = "ms-its:" + url.substring(14, url.length - 1);
		document.location.replace(url);
	}
}

function Load(key)
{	
	try
	{
		userDataCache.load("docSettings");
		var value = userDataCache.getAttribute(key);
		return value;
	}
	catch(e){}
}


function Save(key, value)
{
	try
	{
		userDataCache.setAttribute(key, value);
		userDataCache.save("docSettings");
	}
	catch(e){}
}

function loadAll(){
	try 
	{
		scrollPos = allHistory.getAttribute("Scroll");
	}
	catch(e){}
}

function saveAll(){
	try
	{
		allHistory.setAttribute("Scroll", mainSection.scrollTop);
	}
	catch(e){}
}

function set_to_print()
{
	//breaks out of divs to print
	var i;

	if (window.text)document.getElementsByTagName("*").text.style.height = "auto";

	var allElements = document.getElementsByTagName("*");

	for (i=0; i < allElements.length; i++)
	{
		if (allElements[i].tagName == "body")
		{
			allElements[i].scroll = "yes";
		}
		if (allElements[i].id == "header")
		{
			allElements[i].style.margin = "0px 0px 0px 0px";
			allElements[i].style.width = "100%";
		}
		if (allElements[i].id == "mainSection")
		{
			allElements[i].style.overflow = "visible";
			allElements[i].style.top = "5px";
			allElements[i].style.width = "100%";
			allElements[i].style.padding = "0px 10px 0px 30px";
		}
	}
}

function reset_form()
{
	//returns to the div nonscrolling region after print
	 document.location.reload();
}

/*************************************************************************
 * WSS UX FeedBack Class
 *    Author: Scott Kuykendall (scottku)
 *      Date: 2/8/2004
 * revisions:
 *            2/8/2004 Stubbed out in preperation for mailto re-write.
 *                     It will nolonger be mailto it will be FeedBack. :)
 *************************************************************************/

/*************************************************************************
 * Constructor ***********************************************************
 *************************************************************************/
function FeedBack
 (
  _Alias,
  _Product,
  _Deliverable,
  _ProductVersion,
  _DocumentationVersion,
  _FeedBackDivID
 )
{
	this.Alias                = _Alias;
	this.Product              = _Product;
	this.Deliverable	  = _Deliverable;
	this.ProductVersion       = _ProductVersion;
	this.DocumentationVersion = _DocumentationVersion;
	this.FeedBackDivID	  = _FeedBackDivID;
	this.DefaultBody	  = this.DefaultBody;
}

/*************************************************************************
 *Member Properties ******************************************************
 *************************************************************************/
 
/*************************************************************************
 * START: LOCALIZATION SECTION 1 OF 1 ************************************
 *************************************************************************/

//START: Text Regions
FeedBack.prototype.FeedbackIntroduction = L_fbintroduction; // replaced bug 5104 L_fdintro;
FeedBack.prototype.WhyWrong             = L_fdwhywrong;
FeedBack.prototype.WhatWrong		= L_fdwhatwrong;
FeedBack.prototype.InformationWrong     = L_fdinfowrong;
FeedBack.prototype.NeedsMore            = L_fdneedsmore;
FeedBack.prototype.NotExpected          = L_fdnotexpected;

// added for bug 5104
FeedBack.prototype.Title_1 		= L_fb1Title_Text;
FeedBack.prototype.Text_1		= L_fb1EnterFeedbackHere_Text;
//  END: Text Regions

//START: Button Text
FeedBack.prototype.Yes       = L_fdyes;
FeedBack.prototype.No        = L_fdno;
FeedBack.prototype.Back      = L_fdback;
FeedBack.prototype.Next      = L_fdnext;
FeedBack.prototype.Submit    = L_fdsubmit;
FeedBack.prototype.AltYes    = L_fdaltyes;
FeedBack.prototype.AltNo     = L_fdaltno;
FeedBack.prototype.AltBack   = L_fdaltback;
FeedBack.prototype.AltNext   = L_fdaltnext;
FeedBack.prototype.AltSubmit = L_fdaltsubmit;

// added for bug 5104
FeedBack.prototype.SendFeedback = L_fbsend;
FeedBack.prototype.AltSendFeedback = L_fbaltsend;
//  END: Button Text

//START: Default Mail Body Text
FeedBack.prototype.DefaultBody = L_fddefaultbody;
//  END: Default Mail Body Text

/*************************************************************************
 *  END: LOCALIZATION SECTION 1 OF 1 *************************************
 *************************************************************************/

//CSS Class
FeedBack.prototype.table_CSS		= "fbtable";
FeedBack.prototype.tdtitle_CSS		= "fbtitletd";
FeedBack.prototype.input_CSS		= "fbinputtd";
FeedBack.prototype.textarea_CSS		= "fbtextarea";
FeedBack.prototype.verbatimtable_CSS	= "fbverbatimtable";
FeedBack.prototype.button_CSS		= "fbinputbutton";
//BTN IDs
FeedBack.prototype.YesButton_ID    = "YesButton";
FeedBack.prototype.NoButton_ID     = "NoButton";
FeedBack.prototype.BackButton_ID   = "BackButton";
FeedBack.prototype.NextButton_ID   = "NextButton";
FeedBack.prototype.SubmitButton_ID = "SubmitButton";
FeedBack.prototype.Verbatim_ID	   = "VerbatimTextArea";
FeedBack.prototype.Radio_ID	   = "fbRating";

//FeedBack Location ID's
FeedBack.prototype.SpanTag_ID = "fb";
FeedBack.prototype.DivTag_ID  = "feedbackarea";

//BTN Event Methods
FeedBack.prototype.startfeedback_EVENT      = "document.feedback.StartFeedBack('[feedback]')";
FeedBack.prototype.getwatsoncurvedata_EVENT = "document.feedback.GetWatsonCurveData('[feedback]')";
FeedBack.prototype.getverbatim_EVENT        = "document.feedback.GetVerbatim('[feedback]')";
FeedBack.prototype.submitfeedback_EVENT     = "document.feedback.SubmitFeedBack(event)";
FeedBack.prototype.setrating_EVENT	    = "document.feedback.SetRating(this.value)";
FeedBack.prototype.textareablur_EVENT	    = "document.feedback.SetVerbatim(this.value)";

//Default FeedBack Values
FeedBack.prototype.Rating	   = 3; // default is 3. 3 is satisfied. 0-3 scale
FeedBack.prototype.Verbatim	   = "";
FeedBack.prototype.Title	   = document.title;
FeedBack.prototype.URL	= location.href.replace(location.hash,"");
FeedBack.prototype.SystemLanguage = navigator.systemLanguage;
FeedBack.prototype.Version	   = 2004;

/*************************************************************************
 * Member Methods ********************************************************
 *************************************************************************/
 FeedBack.prototype.StartFeedBack = _StartFeedBack;
 function _StartFeedBack(FeedBackSpanTag)
 {
  //build feedback div
  var stream = "<div id='" + this.DivTag_ID + "'>"
// added bug 5104
	+ "<H5>" + this.Title_1 + "</H5>"
	+ "<P>" + this.FeedbackIntroduction + "</P>"
// removed bug 5104
// +	"<table class='" + this.table_CSS + "'>"
// + "<tr><td colspan='2' class='" + this.tdtitle_CSS + "'>" + this.FeedbackIntroduction + "</td></tr>"
// +	"<tr><td colspan='2' class='" + this.input_CSS + "' align=right>"
// +	this.MakeButton(this.YesButton_ID, this.SendFeedback, this.submitfeedback_EVENT)
// +	this.MakeButton(this.YesButton_ID, this.Yes, this.submitfeedback_EVENT)
// removed bug 5104 + this.MakeButton(this.NoButton_ID,  this.No,  this.getwatsoncurvedata_EVENT.replace("[feedback]",this.FeedBackDivID))
// +	"</td></tr>"
// +	"</table>"
	+ "<P>" + this.Text_1 + "&nbsp;&nbsp;&nbsp;&nbsp;"
	+ this.MakeButton(this.YesButton_ID, this.SendFeedback, this.submitfeedback_EVENT)  + "</P>"
  + "</div>";

  //load feedback div
  document.getElementById(FeedBackSpanTag).innerHTML = stream;
 }

 FeedBack.prototype.GetWatsonCurveData = _GetWatsonCurveData;
 function _GetWatsonCurveData(FeedBackSpanTag)
 {
  if(this.Rating > 2) this.Rating = 0;
  
  var stream = "<div id='" + this.DivTag_ID + "'>"
		+ "<table class='"+this.verbatimtable_CSS+"'>"
		+ "<tr><td colspan='2' class='" + this.tdtitle_CSS + "'>" + this.WhyWrong + "</td></tr>"
		+ this.MakeRadio(0, this.InformationWrong)
		+ this.MakeRadio(1, this.NeedsMore)
		+ this.MakeRadio(2, this.NotExpected)
		+ "<tr><td colspan='2' class='" + this.input_CSS + "' align=right>"
		+ this.MakeButton(this.BackButton_ID, this.Back, this.startfeedback_EVENT.replace("[feedback]",this.FeedBackDivID))
//Following commented to supress verbatim and the line after enables mail
//		+ this.MakeButton(this.NextButton_ID, this.Next, this.getverbatim_EVENT.replace("[feedback]",this.FeedBackDivID))
		+ this.MakeButton(this.SubmitButton_ID, this.Submit,this.submitfeedback_EVENT)
		+ "</td></tr>"
		+ "</table>"
  + "</div>";

  //load feedback div
  document.getElementById(FeedBackSpanTag).innerHTML = stream;
 }

// FeedBack.prototype.GetVerbatim = _GetVerbatim;
 function _GetVerbatim(FeedBackSpanTag)
 {
  var stream = "<div id='" + this.DivTag_ID + "'>"
		+ "<table class='"+this.verbatimtable_CSS+"'>"
		+ "<tr><td colspan='2' class='" + this.tdtitle_CSS + "'>" + this.WhatWrong + "</td></tr>"
		+ "<tr><td colspan='2' class='" + this.input_CSS + "'><textarea class="+ this.textarea_CSS +" name=" + this.Verbatim_ID + " maxlength=750 onblur=\"" + this.textareablur_EVENT.replace("[textarea]",this.Verbatim_ID) + "\">" + this.Verbatim + "</textarea></td></tr>"
		+ "<tr><td colspan='2' class='" + this.input_CSS + "' align=right>"
		+ this.MakeButton(this.BackButton_ID, this.Back, this.getwatsoncurvedata_EVENT.replace("[feedback]",this.FeedBackDivID))
		+ this.MakeButton(this.SubmitButton_ID, this.Submit, this.submitfeedback_EVENT)
		+ "</td></tr>"
		+ "</table>"
  + "</div>";
  
  //load feedback div
  document.getElementById(FeedBackSpanTag).innerHTML = stream;
  
  //scroll down to feedback  
  this.ScrollToFeedBack(FeedBackSpanTag);  
 }

 FeedBack.prototype.MakeRadio = _MakeRadio;
 function _MakeRadio(val, txt)
 { 
  var stream = "<tr><td colspan='2' class='" + this.input_CSS + "'>"
		+ "<input name=" + this.Radio_ID + " type=radio value=" + val + " onclick='" + this.setrating_EVENT + "' "
		+ " " + ((this.Rating == val) ? "CHECKED" : "") + ">" + txt + "</input>"
		+ "</tr>"

  return stream;
 }
 
 FeedBack.prototype.MakeButton = _MakeButton;
 function _MakeButton(id, val, evt)
 {
  var stream = "<input class=" + this.button_CSS + " type=button id=" + id + " value=\"" + val + "\" onclick=\"" + evt + "\";>"
  return stream;
 }
 
 FeedBack.prototype.SubmitFeedBack = _SubmitFeedBack;
 function _SubmitFeedBack(event)
 {
  var src = (event.srcElement) ? event.srcElement : event.target;

  if(src.id == this.YesButton_ID)
  {
   this.Rating = 3;
   this.Verbatim = this.DefaultBody + '\n\n\n';
  }
  
  var subject = this.Title
  + " ("
  + "/1:"
  + this.Product
  + "/2:"
  + this.ProductVersion
  + "/3:"
  + this.DocumentationVersion
  + "/4:"
  + this.DeliverableValue()
  + "/5:"
  + this.URLValue()
  + "/6:"
  + this.Rating
  + "/7:"
  + this.DeliveryType()
  + "/8:"
  + this.SystemLanguage
  + "/9:"
  + this.Version
		+ ")"; 

  var sEntireMailMessage = "MAILTO:"
  + this.Alias
  + "?subject=" + subject 
	 + "&body=" + ((this.Verbatim != "") ? this.Verbatim : this.DefaultBody);

  location.href=sEntireMailMessage;  

  feedbackarea.style.display="none"; 
}
 
 FeedBack.prototype.CheckDeliverable = _CheckDeliverable;
 function _CheckDeliverable()
 {
  var stream = "CheckDeliverable";
  
  alert(stream);
 }

 FeedBack.prototype.SetRating = _SetRating;
 function _SetRating(val)
 {
  this.Rating = val;
 }

 FeedBack.prototype.ReloadFeedBack = _ReloadFeedBack;
 function _ReloadFeedBack()
 {
  location.reload(true);
 }
 
 FeedBack.prototype.ScrollToFeedBack = _ScrollToFeedBack;
 function _ScrollToFeedBack(FeedBackSpanTag)
 {
  window.scrollTo(0,10000);
  FeedBackSpanTag.scrollIntoView(true);
 }
 
 FeedBack.prototype.SetVerbatim = _SetVerbatim;
 function _SetVerbatim(TextAreaValue)
 {
  this.Verbatim = TextAreaValue;
 }
 
FeedBack.prototype.DeliveryType = _DeliveryType;
function _DeliveryType()
{
 if (this.URL.indexOf("ms-help://")!=-1) {return("h");}
	else if (this.URL.indexOf(".chm::/")!=-1) {return("c");}
	else if (this.URL.indexOf("http://")!=-1) {return("w");}
	else if (this.URL.indexOf("file:")!=-1) {return("f");}
	else return("0");
}
FeedBack.prototype.DeliverableValue = _DeliverableValue;
function _DeliverableValue()
{
 if (this.URL.indexOf("ms-help://")!=-1) 
	{
	delvalue  = location.href.slice(0,location.href.lastIndexOf("/html/"));
	delvalue  = delvalue.slice(delvalue.lastIndexOf("/")+1);
	return delvalue;
	}
	else return(this.Deliverable);
}
FeedBack.prototype.URLValue = _URLValue;
function _URLValue()
{
 if (this.URL.indexOf(".chm::")!=-1) 
	{
	a = this.URL;
	while (a.indexOf("\\") < a.indexOf(".chm::") || a.indexOf("//") > a.indexOf(".chm::")) {
		if (a.indexOf("\\")==-1)
		{
		break;
		}
		a = a.substring(a.indexOf("\\")+1,a.length);
	}
	return("ms-its:"+a)
	}
 else if (this.URL.indexOf("file:///")!=-1) 
	{
	a = this.URL;

	b = a.substring(a.lastIndexOf("html")+5,a.length);
	return("file:///"+b);
	}
	else return(this.URL);
}
