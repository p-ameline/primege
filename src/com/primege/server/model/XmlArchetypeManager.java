package com.primege.server.model;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.primege.server.Logger;

/**
 * A LdvXmlArchetypeManager manages Archetypes as a Dom Document
 * 
 **/
public class XmlArchetypeManager
{	
	protected String            _sFileName ;	
		
	public XmlArchetypeManager(final String sFileName) {
		_sFileName    = sFileName ;
	}
		
	/**
	 * Parse a file into an xml document
	 * 
	 * @param logger  logs manager  
	 * @param iUserId user identifier
	 * 
	 * @return an xml document if everything went well, <code>null</code> if not
	 * 
	 **/
	public String openDocument(int iUserId)
	{
		String sFileContent = "" ;
		
		// Reading file as a String StandardCharsets.UTF_8 platform default
		//
		// The character set can be the platform default: Charset.defaultCharset()
		// It can also be a predefined encodings like StandardCharsets.UTF_8
 		//
		try 
		{
			sFileContent = readFile(_sFileName, StandardCharsets.UTF_8) ;
		} 
		catch (IOException e) 
		{
			Logger.trace("XmlArchetypeManager.openDocument: parser config exception when reading file \"" + _sFileName + "\" ; stackTrace:" + e.getStackTrace(), iUserId, Logger.TraceLevel.ERROR) ;
			return null ;
		}
		
		return sFileContent ;
	}
	
	/**
	* Return the content of a file as a String
	* 
	* @param path     full file name
	* @param encoding character encoding ex. StandardCharsets.UTF_8 or Charset.defaultCharset()
	* 
	* @return a String that preserves line breaks
	* 
	**/
	protected String readFile(String path, Charset encoding) throws IOException 
	{
		byte[] encoded = Files.readAllBytes(Paths.get(path)) ;
		return new String(encoded, encoding) ;
	}
}
