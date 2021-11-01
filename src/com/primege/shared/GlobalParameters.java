package com.primege.shared;

import java.util.Date;
import java.util.ArrayList;

public class GlobalParameters 
{		
	public static boolean areStringsEqual(String s1, String s2)
	{
		if (null == s1)
			return (null == s2) ; 
		
		return s1.equals(s2) ;
	}
	
	/**
	 * Return a Date in the form YYYYMMDD
	 * 
	 * */
	public static String getDateAsString(Date date)
	{
		if (null == date)
			return "" ;
		
		int iYear  = date.getYear() + 1900 ;
		int iMonth = date.getMonth() + 1 ;
		int iDay   = date.getDate() ;
		
		String sDay = "00" ;
		if (iDay > 0)
		{
			sDay = Integer.toString(iDay) ;
			if (sDay.length() == 1)
				sDay = "0" + sDay ;
		}
		
		String sMonth = "00" ;
		if (iMonth > 0)
		{
			sMonth = Integer.toString(iMonth) ;
			if (sMonth.length() == 1)
				sMonth = "0" + sMonth ;
		}
		
		String sYear = "0000" ;
		if (iYear > 0)
			sYear = Integer.toString(iYear) ;
		
		return sYear + sMonth + sDay ;
	}
	
	/**
	 * Return a Date in the form YYYYMMDD
	 * 
	 * */
	public static ArrayList<String> ParseString(final String sToParse, final String sSeparator)
	{
		if ((null == sToParse) || "".equals(sToParse))
			return null ;
		
		ArrayList<String> result = new ArrayList<String>() ;
		
		// If there is no separator or the separator is not found in the string to parse, 
		// then simply return the string to parse as only result
		//
		if ((null == sSeparator) || "".equals(sSeparator))
		{
			result.add(sToParse) ;
			return result ;
		}
		
		int iVarSepar = sToParse.indexOf(sSeparator) ;
		if (-1 == iVarSepar)
		{
			result.add(sToParse) ;
			return result ;
		}
		
		int iSeparatorLen = sSeparator.length() ;
		
		// Cut the string to parse while the separator is found
		//
		String sRemains = sToParse ;
		
		while (-1 != iVarSepar)
		{
			String sLeft = sRemains.substring(0, iVarSepar) ;
			result.add(sLeft) ;
			
			// When taking right part, check if the separator doesn't end up the string
			//
			int iRemainsLen = sRemains.length() ;
			if (iVarSepar < iRemainsLen - iSeparatorLen)
			{
				sRemains = sRemains.substring(iVarSepar + iSeparatorLen, iRemainsLen) ;
				iVarSepar = sRemains.indexOf(sSeparator) ;
			}
			else
			{
				sRemains = "" ;
				iVarSepar = -1 ;
			}
		}
		
		if (false == "".equals(sRemains))
			result.add(sRemains) ;
		
		return result ;
	}
}
