package com.primege.server;

import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;

public class Logger 
{
	//PRIVATE //
	
	private static String _sTrace ;

	public static enum TraceLevel { ERROR, WARNING, STEP, SUBSTEP, DETAIL, SUBDETAIL }

	public Logger() {
		_sTrace = DbParametersModel.getTrace() ;
	}
	
	public static void trace(final String sTraceText, final int iUserId, final TraceLevel iTraceLevel) {
		trace(sTraceText, iUserId, iTraceLevel, null) ;
	}
	
	public static void trace(final String sTraceText, final int iUserId, final TraceLevel iTraceLevel, final String sTraceFile)
	{
		if ((null == sTraceText) || "".equals(sTraceText)) 
			return ;
		
		String sTraceFileName = sTraceFile ;
		if ((null == sTraceFile) || "".equals(sTraceFile))
			sTraceFileName = _sTrace ;
		if ((null == sTraceFileName) || "".equals(sTraceFileName))
			sTraceFileName = DbParametersModel.getTrace() ;
		if ((null == sTraceFileName) || "".equals(sTraceFileName))
			return ;
		
		FileOutputStream out = null ;
		
		try
    {
			out = new FileOutputStream(sTraceFileName, true) ;
    } 
		catch (FileNotFoundException e1)
    {
	    e1.printStackTrace();
	    return ;
    }
		
		// Time stamp
		//
		Date dateNow = new Date() ;
		SimpleDateFormat ldvFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") ;
		String sFormatedNow = ldvFormat.format(dateNow) ;
		
		// User Id
		//
		String sUserString = "?" ;
		if (iUserId > 0)
			sUserString = Integer.toString(iUserId) ;
		sUserString = " [" + sUserString + "] " ;
		
		// Trace level indicator
		//
		String sTraceLevel = " " ;
		switch (iTraceLevel)
		{
			case ERROR     : sTraceLevel = " Err " ;
			                 break ;
			case WARNING   : sTraceLevel = " ! " ;
                       break ;
			case STEP      : sTraceLevel = "\t" ;
                       break ;
			case SUBSTEP   : sTraceLevel = "\t\t" ;
                       break ;
			case DETAIL    : sTraceLevel = "\t\t\t" ;
                       break ;
			case SUBDETAIL : sTraceLevel = "\t\t\t\t" ;
                       break ;
		}
		
		String s = sFormatedNow + sUserString + sTraceLevel + sTraceText + "\n" ;
		byte data[] = s.getBytes() ;
		try
    {
	    out.write(data, 0, data.length) ;
    } 
		catch (IOException x)
    {
			System.err.println(x);
    }
		finally
		{		
			try
      {
	      out.flush() ;
      } 
			catch (IOException e)
      {
	      e.printStackTrace();
      }
			try
      {
	      out.close() ;
      } 
			catch (IOException e)
      {
	      e.printStackTrace();
      }
		}
	}
}
