package com.primege.server;

import java.util.*;
import java.io.*;

import javax.mail.*;
import javax.mail.internet.*;

import com.google.inject.Inject;
import com.primege.shared.util.MailTo;

public class EMailer 
{
	//PRIVATE //

	private static String     _sPropertiesPath ;
  private static Properties _fMailServerConfig = new Properties() ;
  private static boolean    _bPropertiesInitialized ;
  
  @Inject
  public EMailer(String sPropertiesPath)
  {
  	Logger.trace("Entering EMailer constructor.", -1, Logger.TraceLevel.DETAIL) ;
  	
  	_sPropertiesPath        = sPropertiesPath ;
  	_bPropertiesInitialized = fetchConfig() ;
  	
  	Logger.trace("EMailer initialized with properties path = " + _sPropertiesPath, -1, Logger.TraceLevel.DETAIL) ;
  }
  
  /**
   * Send a single email, from a text body
   */
  public boolean sendEmail(final String sFromEmailAddr, final ArrayList<MailTo> aToEmailAddr, final String sSubject, final String sBody, final ArrayList<String> aAttachedFileNames)
  {
  	String sHtmlBody = "" ;
  	if ((null != sBody) && (false == "".equals(sBody)))
  		sHtmlBody = "<p>" + sBody + "</p>" ;
  	
  	return sendEmail(sFromEmailAddr, aToEmailAddr, sSubject, sBody, sHtmlBody, aAttachedFileNames) ;
  }
  
  /**
   * Send a single email.
   */
  public boolean sendEmail(final String sFromEmailAddr, final ArrayList<MailTo> aToEmailAddr, final String sSubject, final String sTxtBody, final String sHtmlBody, final ArrayList<String> aAttachedFileNames)
  {
  	if (false == _bPropertiesInitialized)
  	{
  		Logger.trace("Cannot send email: properties not initialized (properties path: " + _sPropertiesPath + ")", -1, Logger.TraceLevel.ERROR) ;
  		return false ;
  	}

  	String sSendTo = "" ;
  	
    // Here, no Authenticator argument is used (it is null).
    // Authenticators are used to prompt the user for user
    // name and password.
  	//
    Session session = Session.getDefaultInstance(_fMailServerConfig, null) ;
    
    // Create the message and the multipart container
    //
    Message message = new MimeMessage(session) ;
    Multipart multipart = new MimeMultipart() ;
    
    try {
    	
    	// creates a body part for the message
    	//
    	if ((null != sTxtBody) && (false == "".equals(sTxtBody)))
    	{
    		MimeBodyPart textPart = new MimeBodyPart() ;
    		textPart.setText(sTxtBody, "utf-8") ;
    		multipart.addBodyPart(textPart) ;
    	}

    	if ((null != sHtmlBody) && (false == "".equals(sHtmlBody)))
    	{
    		MimeBodyPart htmlPart = new MimeBodyPart() ;
    		htmlPart.setContent(sHtmlBody, "text/html; charset=utf-8" ) ;
    		multipart.addBodyPart(htmlPart) ;
    	}

    	// creates a body part for each attachment
    	//
    	if ((null != aAttachedFileNames) && (false == aAttachedFileNames.isEmpty()))
    	{
    		for (String sFileName : aAttachedFileNames)
    		{
    			if (false == "".equals(sFileName))
    			{
    				MimeBodyPart attachPart = new MimeBodyPart() ;
    				attachPart.attachFile(sFileName) ;
    				multipart.addBodyPart(attachPart) ;
    			}
    		}
    	}

    	// sets the multipart as message's content
    	//
    	message.setContent(multipart) ;
    
      // adds recipients
      //
    	if ((null != aToEmailAddr) && (false == aToEmailAddr.isEmpty()))
    	{
    		for (MailTo mailTo : aToEmailAddr)
    		{
    			if (false == "".equals(sSendTo))
    				sSendTo += ", " ;
    			
    			// Get mail address
    			//
    			String sRecipient = mailTo.getAddress() ;
    			
    			// Get recipient type (to, carbon copy, blind carbon copy). Default is "to".
    			//
    			Message.RecipientType recipientType ;
    			
    			switch(mailTo.getRecipientType())
    			{
    				case To  :	recipientType = Message.RecipientType.TO ;
    				            sSendTo += "to " ;
    				           	break ;
    				case Cc  :	recipientType = Message.RecipientType.CC ;
    										sSendTo += "cc " ;
	          						break ;
    				case Bcc :	recipientType = Message.RecipientType.BCC ;
    										sSendTo += "bcc " ;
  											break ;
  					default  :	recipientType = Message.RecipientType.TO ;
    			}
    			
    			message.addRecipient(recipientType, new InternetAddress(sRecipient)) ;
    			
    			sSendTo += sRecipient ;
    		}
    	}
    	else
    		Logger.trace("No recipient for mail.", -1, Logger.TraceLevel.WARNING) ;
    	
    	// Set the subject
    	//
      message.setSubject(sSubject) ;
      
      // Set sender's address
      //
      if (false == "".equals(sFromEmailAddr))
      	message.setFrom(new InternetAddress(sFromEmailAddr)) ;
      
      // Send
      //
      Transport.send(message) ;
    }
    catch (MessagingException ex){
    	Logger.trace("Cannot send email: " + ex, -1, Logger.TraceLevel.ERROR) ;
      // System.err.println("Cannot send email. " + ex);
      return false ;
    } catch (IOException ex) {
    	Logger.trace("Cannot send email because of attached file: " + ex, -1, Logger.TraceLevel.ERROR) ;
    	return false ;
		}
    
    Logger.trace("Mail sent to " + sSendTo, -1, Logger.TraceLevel.DETAIL) ;
    
    return true ;
  }

  /**
  * Allows the config to be refreshed at runtime, instead of
  * requiring a restart.
  */
  public void refreshConfig() 
  {
    _fMailServerConfig.clear() ;
    fetchConfig() ;
  }

  /**
  * Open a specific text file containing mail server
  * parameters, and populate a corresponding Properties object.
  */
  private boolean fetchConfig() 
  {
  	boolean     bSuccess = false ;
  	InputStream input    = null ;
    try {
      //If possible, one should try to avoid hard-coding a path in this
      //manner; in a web application, one should place such a file in
      //WEB-INF, and access it using ServletContext.getResourceAsStream.
      //Another alternative is Class.getResourceAsStream.
      //This file contains the javax.mail config properties mentioned above.
      // input = new FileInputStream( "C:\\Temp\\MyMailServer.txt" );
    	// input = context.getResourceAsStream("/yourfilename.cnf");
    	
    	input = new FileInputStream(_sPropertiesPath + "/WEB-INF/MailServer.properties") ;
    	if (null != input)
    	{
    		_fMailServerConfig.load(input) ;
    		bSuccess = true ;
    	}
    }
    catch ( IOException ex ){
    	Logger.trace("Cannot open and load mail server properties file.", -1, Logger.TraceLevel.ERROR) ;
      // System.err.println("Cannot open and load mail server properties file.") ;
      bSuccess = false ;
    }
    finally {
      try {
        if (input != null) 
        	input.close() ;
      }
      catch ( IOException ex ){
      	Logger.trace("Cannot close mail server properties file.", -1, Logger.TraceLevel.ERROR) ;
        // System.err.println( "Cannot close mail server properties file." );
      }
    }
    return bSuccess ;
  }
  
  public boolean arePropertiesInilialized() {
  	return _bPropertiesInitialized ; 
  }
}
