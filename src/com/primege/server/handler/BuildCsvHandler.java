package com.primege.server.handler;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.inject.Inject;
import com.google.inject.Provider;

import com.primege.server.DbParametersModel;
import com.primege.server.EMailer;
import com.primege.server.Logger;
import com.primege.server.csv.CsvInfoOption;
import com.primege.server.csv.CsvInformation;
import com.primege.server.csv.CsvRecord;
import com.primege.server.csv.CsvStructure;
import com.primege.shared.rpc.GetCsvAction;
import com.primege.shared.rpc.GetCsvResult;
import com.primege.shared.util.MailTo;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;

public class BuildCsvHandler extends BuildCsvHandlerBase implements ActionHandler<GetCsvAction, GetCsvResult> 
{	
	protected final Provider<HttpServletRequest> _servletRequest ;
	
	protected       String                       _sModelFileName ;
	
	@Inject
	public BuildCsvHandler(final Provider<ServletContext>     servletContext,
                         final Provider<HttpServletRequest> servletRequest)
	{
		super(servletContext) ;
		
		_servletRequest = servletRequest ;
	}
	
	@Override
	public GetCsvResult execute(GetCsvAction action, ExecutionContext context) throws ActionException 
	{
		int iUserId  = -1 ;
		int iEventId = -1 ;
		if (null != action)
		{
			iUserId  = action.getUserId() ;
			iEventId = action.getEventId() ;
		}
		
 		// Get full file name
 		//
		_sModelFileName = DbParametersModel.getArchetypeDir() + "euro16_csv.xml" ; 
 		
 		// Parse the xml file
 		//
 		CsvStructure csvStructure = new CsvStructure() ;
 		if (false == parseArchetype(csvStructure, iUserId))
 			return new GetCsvResult("", "Server error (cannot parse the Archetype file)") ;
 		
 		// Build the CSV file
 		//
		BuildCsvEngine cvsEngine = new BuildCsvEngine() ;
		String sError = cvsEngine.execute(iUserId, iEventId, csvStructure) ;
		if (false == "".equals(sError))
			return new GetCsvResult("", sError) ;
		
		// Send the mail
		//
		boolean bMailSuccess = sendCsvMail(iUserId, DbParametersModel.getCSV(), DbParametersModel.getDailyCSV()) ;
		if (false == bMailSuccess)
			return new GetCsvResult("", "Server error (cannot send file by mail)") ;
		
		return new GetCsvResult("", sError) ;
	}
		
	/**
	 * Fills an array of links from file
	 * 
	 * @param sDirectory directory where to find file 
	 * @param aLinks array of LdvModelLink to fill
	 * @return true if everything went well
	 * 
	 **/
	protected boolean parseArchetype(CsvStructure csvStructure, final int iUserId)
	{
		// Open the file on disk
		//
		FileInputStream fi ;
		try
		{
			fi = new FileInputStream(_sModelFileName) ;
		} 
		catch (FileNotFoundException e)
		{
			Logger.trace("BuildCsvHandler.parseArchetype: input stream exception for file " + _sModelFileName + " ; stackTrace:" + e.getStackTrace(), iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		// Parse it as a DOM document
		//
		Document archetypeDocument = getDocumentFromInputSource(new InputSource(fi), false, iUserId) ;
		if (null == archetypeDocument)
			return false ;
		
		return parseDocument(archetypeDocument, csvStructure) ;
	}
	
	/**
	* Creates a Document from an InputSource
	* 
	* @param inputSource input source content
	* @param mustValidate true if the xml content must be validated
	* 
	* @return a Document if everything went well, <code>null</code> if not
	* 
	**/
	public Document getDocumentFromInputSource(final InputSource inputSource, boolean mustValidate, final int iUserId)
	{
		if (null == inputSource) 
			return null ;
	
		String sFctName = "LdvXmlGraph.getDocumentFromInputSource" ;
		
		// Get factory instance
		//
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance() ;
	
		if (false == mustValidate)
		{
			factory.setNamespaceAware(false) ;
			factory.setValidating(false) ;
			try
			{
				factory.setFeature("http://xml.org/sax/features/namespaces", false) ;
				factory.setFeature("http://xml.org/sax/features/validation", false) ;
				factory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false) ;
				factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false) ;
			} 
			catch (ParserConfigurationException e)
			{
				Logger.trace(sFctName + ": parser config exception for file \"" + _sModelFileName + "\" ; stackTrace:" + e.getStackTrace(), iUserId, Logger.TraceLevel.ERROR) ;
				e.printStackTrace();
			}
		}
	
		DocumentBuilder builder ;
		try
		{
			builder = factory.newDocumentBuilder() ;
		} 
		catch (ParserConfigurationException e)
		{
			Logger.trace(sFctName + ": parser config exception when getting a document builder for file \"" + _sModelFileName + "\" ; stackTrace:" + e.getStackTrace(), iUserId, Logger.TraceLevel.ERROR) ;
			return null ;
		}
  
		// Create documents
		//
		Document document = null ;
  
		try
		{
			document = builder.parse(inputSource) ;
		} 
		catch (SAXException e)
		{
			Logger.trace(sFctName + ": parser exception for file \"" + _sModelFileName + "\" ; stackTrace:" + e.getStackTrace(), iUserId, Logger.TraceLevel.ERROR) ;
			return null ;
		} 
		catch (IOException e)
		{
			Logger.trace(sFctName + ": parser IO exception for file \"" + _sModelFileName + "\" ; stackTrace:" + e.getStackTrace(), iUserId, Logger.TraceLevel.ERROR) ;
			return null ;
		}
      
		return document ;
	}
	
	/**
	* Create a CsvStructure from the XML Document  
	* 
	* @param archetypeDocument the XML DOM Document to be parsed
	* @param csvStructure      the CsvStructure to initialize
	* 
	* @return <code>true</code> if everything went well, <code>false</code> if not
	* 
	**/
	protected boolean parseDocument(Document document, CsvStructure csvStructure)
	{
		if ((null == document) || (null == csvStructure))
			return false ;
		
		// Get first level Element: the archetype
		//
		NodeList listOfArchetypes = document.getElementsByTagName("archetype") ;
		if (null == listOfArchetypes)
			return false ;
		
		int iTotalElements = listOfArchetypes.getLength() ;
    if (iTotalElements <= 0)
    	return false ;
    
    Node firstArchetype = listOfArchetypes.item(0) ;
    Element archetypeElement = (Element) firstArchetype ;
		
    // Get second level Element: the csv description
 		//
    NodeList listOfCsv = archetypeElement.getElementsByTagName("csv") ;
		if (null == listOfCsv)
			return false ;
		
    int iTotalCsvElements = listOfCsv.getLength() ;
    if (iTotalCsvElements <= 0)
    	return false ;
    
    Node firstCsv = listOfCsv.item(0) ;
    Element csvElement = (Element) firstCsv ;
    
    String sHeaderLine = csvElement.getAttribute("headerLine") ;
    if (null != sHeaderLine)
    	csvStructure.setHeaderLine(sHeaderLine) ;
    
    // Get third level Element: the record description
  	//
    NodeList listOfRecord = csvElement.getElementsByTagName("record") ;
 		if (null == listOfRecord)
 			return false ;
 		
    int iTotalRecordElements = listOfRecord.getLength() ;
    if (iTotalRecordElements <= 0)
     	return false ;
     
    for (int i = 0 ; i < iTotalRecordElements ; i++)
    {
    	Node recordNode = listOfRecord.item(i) ;
    	if (null != recordNode)
    	{
    		if (false == parseRecord((Element) recordNode, csvStructure))
    			return false ;
    	}
    }
    
		return true ;
	}
	
	/**
	* Instantiate a csv record from a XML Element   
	* 
	* @param recordElement the XML DOM Element to be parsed
	* @param csvStructure  the CsvStructure to initialize
	* 
	* @return <code>true</code> if everything went well, <code>false</code> if not
	* 
	**/
	protected boolean parseRecord(Element recordElement, CsvStructure csvStructure)
	{
		if ((null == recordElement) || (null == csvStructure))
			return false ;
		
		// Check that this record contains information. If not, no need to process it
		//
	  NodeList listOfInformation = recordElement.getElementsByTagName("information") ;
	 	if (null == listOfInformation)
	 		return true ;
	 		
	  int iTotalInformationElements = listOfInformation.getLength() ;
	  if (iTotalInformationElements <= 0)
	  	return true ;
		
		// Get site ID from the attribute in the form staticData="$site$=2"
		//
		int iSiteId = -1 ;
		
		String sStaticData = recordElement.getAttribute("staticData") ;
    if ((null != sStaticData) && (false == "".equals(sStaticData)))
    {
    	int iPos = sStaticData.indexOf("=") ;
    	if ((iPos > 0) && (iPos < sStaticData.length() - 1))
    	{
    		String sDescriptor = sStaticData.substring(0, iPos) ;
    		String sValue      = sStaticData.substring(iPos + 1, sStaticData.length()) ;
    		
    		if ("$site$".equals(sDescriptor))
    		{
    			try {
    				iSiteId = Integer.parseInt(sValue) ;
    			} catch (NumberFormatException e) {
    			}
    		}
    	}
    }
    
    CsvRecord newRecord = new CsvRecord(iSiteId) ;
    
    for (int i = 0 ; i < iTotalInformationElements ; i++)
    {
    	Node informationNode = listOfInformation.item(i) ;
    	if (null != informationNode)
    	{
    		if (false == parseInformation((Element) informationNode, newRecord))
    			return false ;
    	}
    }
    
    csvStructure.addToRecords(newRecord) ;
    
    return true ;
	}
	
	/**
	* Instantiate a csv information from a XML Element   
	* 
	* @param informationElement the XML DOM Element to be parsed
	* @param record             the CsvRecord to add the CsvInformation to
	* 
	* @return <code>true</code> if everything went well, <code>false</code> if not
	* 
	**/
	protected boolean parseInformation(Element informationElement, CsvRecord record)
	{
		if ((null == informationElement) || (null == record))
			return false ;
		
		String sPath    = informationElement.getAttribute("path") ;
		String sCaption = informationElement.getAttribute("caption") ;
		String sType    = informationElement.getAttribute("type") ;
		String sFormat  = informationElement.getAttribute("format") ;

		CsvInformation newInformation = new CsvInformation(sPath, sCaption, sType, sFormat) ;
		
		// Check that this record contains options.
		//
	  NodeList listOfOption = informationElement.getElementsByTagName("option") ;
	 	if (null != listOfOption)
	 	{
	 		int iTotalOptionElements = listOfOption.getLength() ;
	 		for (int i = 0 ; i < iTotalOptionElements ; i++)
	 		{
	 			Node optionNode = listOfOption.item(i) ;
	 			if (null != optionNode)
	 			{
	 				if (false == parseOption((Element) optionNode, newInformation))
	 					return false ;
	 			}
	 		}
	 	}
	  
	 	record.addToInformations(newInformation) ;
	 	
	  return true ;
	}
	
	/**
	* Instantiate a csv option from a XML Element   
	* 
	* @param informationElement the XML DOM Element to be parsed
	* @param information        the CsvInformation to add the CsvInfoOption to
	* 
	* @return <code>true</code> if everything went well, <code>false</code> if not
	* 
	**/
	protected boolean parseOption(Element informationElement, CsvInformation information)
	{
		if ((null == informationElement) || (null == information))
			return false ;
		
		String sPath    = informationElement.getAttribute("path") ;
		String sCaption = informationElement.getAttribute("caption") ;

		CsvInfoOption newOption = new CsvInfoOption(sPath, sCaption) ;
			  
		information.addToOptions(newOption) ;
	 	
	  return true ;
	}
	
	/**
	  * Send the CSV file by mail
	  */
	public boolean sendCsvMail(final int iUserId, final String sFileName, final String sDailyFileName)
	{
		if ((null == sFileName) || "".equals(sFileName))
			return false ;
		
		Logger.trace("Sending CSV file", iUserId, Logger.TraceLevel.DETAIL) ;
		
		String sRealPath  = _servletContext.get().getRealPath("") ;
		
		String sMailTitle = "[Euro2016] Fichier CSV" ;
		String sMailBody  = "Vous trouverez ci-joint le fichier CSV." ;
		
		ArrayList<MailTo> aToEmailAddr  = new ArrayList<MailTo>() ;
		aToEmailAddr.add(new MailTo("anna.markovic@kronenbourg.com", MailTo.RecipientType.To)) ;
		aToEmailAddr.add(new MailTo("PCO@kronenbourg.com", MailTo.RecipientType.To)) ;
		aToEmailAddr.add(new MailTo("philippe.ameline@free.fr", MailTo.RecipientType.To)) ;
		
		ArrayList<String> aAttachedFiles = new ArrayList<String>() ;
		aAttachedFiles.add(sFileName) ;
		aAttachedFiles.add(sDailyFileName) ;
		
		EMailer mailer = new EMailer(sRealPath) ;
		boolean bMailSent = mailer.sendEmail("", aToEmailAddr, sMailTitle, sMailBody, aAttachedFiles) ;
		
		return bMailSent ;
	}
	
	@Override
	public Class<GetCsvAction> getActionType() {
		return GetCsvAction.class;
	}

	@Override
	public void rollback(GetCsvAction action, GetCsvResult result,
			ExecutionContext context) throws ActionException {
		// TODO Auto-generated method stub
	}
}
