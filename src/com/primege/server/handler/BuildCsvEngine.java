package com.primege.server.handler;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.google.inject.Inject;

import com.primege.server.DBConnector;
import com.primege.server.DbParametersModel;
import com.primege.server.Logger;
import com.primege.server.csv.CsvInfoOption;
import com.primege.server.csv.CsvInformation;
import com.primege.server.csv.CsvRecord;
import com.primege.server.csv.CsvStructure;
import com.primege.server.model.CityDataManager;
import com.primege.server.model.GetDashboardBlocksInBase;
import com.primege.shared.database.CityData;
import com.primege.shared.database.FormData;
import com.primege.shared.database.FormDataData;
import com.primege.shared.model.CityForDateBlock;
import com.primege.shared.model.DashboardBlocks;
import com.primege.shared.model.FormBlock;

public class BuildCsvEngine
{	
	private FileOutputStream _outputStream      = null ;
	private FileOutputStream _outputStreamDaily = null ;
	private String           _sErrorMsg ;
	
	private ArrayList<CityData> _aCities ;
	
	// private DbParameters     _dbParameters ;

	@Inject
	public BuildCsvEngine()
	{
		_sErrorMsg = "" ;
		_aCities   = null ;
	}
	
	public String execute(final int iUserId, final int iEventId, final CsvStructure csvStructure)
	{
		if (null == csvStructure)
			return "Invalid parameters" ;
		
		try
    {
			_outputStream = new FileOutputStream(DbParametersModel.getCSV(), false) ;
    } 
		catch (FileNotFoundException e1)
    {
	    e1.printStackTrace();
	    return "Cannot create file " + DbParametersModel.getCSV() ;
    }
		
		try
    {
			_outputStreamDaily = new FileOutputStream(DbParametersModel.getDailyCSV(), false) ;
    } 
		catch (FileNotFoundException e1)
    {
	    e1.printStackTrace();
	    return "Cannot create file " + DbParametersModel.getDailyCSV() ;
    }

		if ("1".equals(csvStructure.getHeaderLine()))
			addHeader(csvStructure) ;

		DBConnector dbConnector = new DBConnector(false) ;

		// Fill a DashboardBlocks structure with all information in database
		//
		DashboardBlocks dashBlocks = new DashboardBlocks() ;
		
		GetDashboardBlocksInBase dashboardBlocksManager = new GetDashboardBlocksInBase(iUserId, dbConnector) ;
		dashboardBlocksManager.GetDashboardBlocks(iEventId, -1, "", dashBlocks) ;
		
		if (dashBlocks.isEmpty())
			return "No information in database" ;
		
		// Get formated current date
		//
		SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyyMMdd") ;
		String sFormatedNow = "" ;
		
		Date dateNow = new Date() ;
		
		// Before 14 AM, we suppose that the daily CSV is for the day before
		//
		if (dateNow.getHours() < 14)
		{
			long lTime = dateNow.getTime() ;
			long lM24H = 1000 * 60 * 60 * 24 ;
			long lYest = lTime - lM24H ;
			
			Date tYesterday = new Date(lYest) ;
			sFormatedNow = simpleFormat.format(tYesterday) ;
		}
		else
			sFormatedNow = simpleFormat.format(dateNow) ;
		
		// Get the list of cities
		//
		_aCities = new ArrayList<CityData>() ;
		CityDataManager citiesManager = new CityDataManager(iUserId, dbConnector) ;
		citiesManager.fillCitiesForEvent(iUserId, _aCities, iEventId) ;
		
		// Fill the CSV file, one CityForDateBlock at a time
		//
		for (CityForDateBlock city4date : dashBlocks.getInformation())
			addCityForDateBlockToCsv(city4date, csvStructure, sFormatedNow) ;

		closeFile() ;
		
		Logger.trace("CSV file creation ended properly.", iUserId, Logger.TraceLevel.DETAIL) ;
		
		return _sErrorMsg ;
	}
		
	void addStringToFile(String sContent)
	{
		if (null == sContent)
			return ;
		
		String s = sContent + "\n" ;
		byte data[] = s.getBytes() ;
		try
    {
			_outputStream.write(data, 0, data.length) ;
    } 
		catch (IOException x)
    {
			System.err.println(x);
    }
	}
	
	void addStringToBothFiles(String sContent)
	{
		if (null == sContent)
			return ;
		
		String s = sContent + "\n" ;
		byte data[] = s.getBytes() ;
		try
    {
			_outputStream.write(data, 0, data.length) ;
			_outputStreamDaily.write(data, 0, data.length) ;
    } 
		catch (IOException x)
    {
			System.err.println(x);
    }
	}
	
	void closeFile()
	{
		try
    {
			_outputStream.flush() ;
			_outputStreamDaily.flush() ;
    } 
		catch (IOException e)
    { e.printStackTrace() ;
    }
		
		try
    {
			_outputStream.close() ;
			_outputStreamDaily.close() ;
    } 
		catch (IOException e)
    { e.printStackTrace() ;
    }
	}
	
	void addHeader(final CsvStructure csvStructure)
	{
		String s = "" ;
		
		if ((null == csvStructure.getRecords()) || csvStructure.getRecords().isEmpty())
		{
			addStringToBothFiles(s) ;
			return ;
		}
			
		for (CsvRecord csvRecord : csvStructure.getRecords())
		{
			if ((null != csvRecord.getInformations()) || (false == csvRecord.getInformations().isEmpty()))
			{
				for (CsvInformation csvInformation : csvRecord.getInformations())
				{
					if (false == "".equals(s))
						s += ";" ;
					s += csvInformation.getCaption() ;
				}
			}
		}
					
		addStringToBothFiles(s) ;
	}

	/**
	 * Add a line to the CSV file. This line will be structured according to a CsvStructure
	 * and will publish information from a CityForDateBlock (all forms for a city at a date)  
	 * 
	 * @param city4date    content to be published 
	 * @param csvStructure structure of the line
	 * 
	 * @return <code>true</code> if everything went well, <code>false</code> if not
	 * 
	 **/
	boolean addCityForDateBlockToCsv(final CityForDateBlock city4date, final CsvStructure csvStructure, final String sFormatedNow)
	{
		if ((null == city4date) || (null == csvStructure))
		{
			_sErrorMsg = "Invalid parameters when processing a CityForDateBlock" ;
			return false ;
		}
		
		if ((null == csvStructure.getRecords()) || csvStructure.getRecords().isEmpty())
		{
			_sErrorMsg = "Invalid CSV description when processing a CityForDateBlock" ;
			return false ;
		}
		
		String s = "" ;
		
		// Processing records. Each record is either global, or dedicated to a site
		//
		for (CsvRecord csvRecord : csvStructure.getRecords())
		{
			int iSiteId = csvRecord.getMasterId() ;
			
			if ((null != csvRecord.getInformations()) || (false == csvRecord.getInformations().isEmpty()))
			{
				for (CsvInformation csvInformation : csvRecord.getInformations())
				{
					String sNewInfo = getInfo(csvInformation, iSiteId, city4date) ;
					
					if (false == "".equals(s))
						s += ";" ;
					s += sNewInfo ;
				}
			}
		}
		
		if (city4date.getDate().equals(sFormatedNow))
			addStringToBothFiles(s) ;
		else
			addStringToFile(s) ;
		
		return true ;
	}
	
	/**
	 * Find the information (site and path) in the CityForDateBlock and, 
	 * if found, return it as a formatted string 
	 * 
	 * @param csvInformation information to look for 
	 * @param iSiteId        site to look information for, or -1 if information is global (date or city for example)
	 * @param city4date      CityForDateBlock to look information into
	 * 
	 * @return A formated string, or ""
	 * 
	 **/
	protected String getInfo(final CsvInformation csvInformation, final int iSiteId, final CityForDateBlock city4date)
	{
		if ((null == csvInformation) || (null == city4date))
			return "" ;
		
		String sPath = csvInformation.getPath() ;
		
		if ("$date$".equals(sPath))
			return getFormatedStringValue(csvInformation.getType(), city4date.getDate(), csvInformation.getFormat()) ;
		
		if ("$city$".equals(sPath))
			return getFormatedIntValue(csvInformation.getType(), city4date.getCityId(), csvInformation.getFormat()) ;
		
		String sValue = getInformationInC4D(iSiteId, csvInformation, city4date) ;
		if ("".equals(sValue))
			return "" ;
		
		return getFormatedStringValue(csvInformation.getType(), sValue, csvInformation.getFormat()) ;
	}
	
	/**
	 * Return a formated string 
	 * 
	 * @param sType   information type, for example "date"
	 * @param sValue  value to format
	 * @param sFormat format pattern, for example "DD/MM" for dates 
	 * 
	 * @return The formated value
	 * 
	 **/
	protected String getFormatedStringValue(final String sType, final String sValue, final String sFormat)
	{
		if ("".equals(sFormat))
			return sValue ;
		
		// Date
		//
		if ("date".equalsIgnoreCase(sType))
		{
			if (sValue.length() != 8)
				return sValue ;
			
			String sToReturn = sFormat ;
			
			sToReturn = sToReturn.replace("YYYY", sValue.substring(0, 4)) ;
			sToReturn = sToReturn.replace("MM", sValue.substring(4, 6)) ;
			sToReturn = sToReturn.replace("DD", sValue.substring(6, 8)) ;
			
			return sToReturn ;
		}
		
		return sValue ;
	}
	
	/**
	 * Return a formated string 
	 * 
	 * @param sType   information type, for example "date"
	 * @param sValue  value to format
	 * @param sFormat format pattern, for example "DD/MM" for dates 
	 * 
	 * @return The formated value
	 * 
	 **/
	protected String getFormatedIntValue(final String sType, final int iValue, final String sFormat)
	{
		String sStringValue = Integer.toString(iValue) ;
 		
		// City
		//
		if ("city".equalsIgnoreCase(sType))
		{
			if ((null == _aCities) || _aCities.isEmpty())
				return sStringValue ;
			
			for (CityData data : _aCities)
			{
				if (data.getId() == iValue)
				{
					if ("abbreviated".equalsIgnoreCase(sFormat))
						return data.getAbbreviation() ;
					return data.getLabel() ;
				}
			}
		}
		
		return sStringValue ;
	}
	
	/**
	 * Find the information (site and path) in the CityForDateBlock 
	 * 
	 * @param iSiteId        site to look information for, or -1 if information is global (date or city for example)
	 * @param csvInformation CsvInformation to look information for
	 * @param city4date      CityForDateBlock to look information into
	 * 
	 * @return The value if site and path exist in CityForDateBlock, "" if not
	 * 
	 **/
	protected String getInformationInC4D(final int iSiteId, final CsvInformation csvInformation, final CityForDateBlock city4date)
	{
		if ((null == csvInformation) || (null == city4date) || city4date.isEmpty())
			return "" ;
		
		String sPath = csvInformation.getPath() ;
		if ("".equals(sPath))
			return "" ;

		// Get the form filled for this site
		//
		FormBlock formBlock = getBlockForSite(iSiteId, city4date) ;
		if (null == formBlock)
			return "" ;
		
		// Get the set of data for this form
		//
		ArrayList<FormDataData> aData = formBlock.getInformation() ;
		if ((null == aData) || aData.isEmpty())
			return "" ;
		
		ArrayList<CsvInfoOption> aOptions = csvInformation.getOptions() ;
		
		for (FormDataData data : aData)
		{
			if (sPath.equals(data.getPath()))
				return data.getValue() ;
			
			// In case this element contains options, check global path: path + "/" + option path
			//
			if ((null != aOptions) && (false == aOptions.isEmpty()))
			{
				for (CsvInfoOption currentOption : aOptions)
				{
					String sOptionPath = sPath + "/" + currentOption.getPath() ; 
					if (sOptionPath.equals(data.getPath()))
						return currentOption.getCaption() ;
				}
			}
		}
		
		return "" ;
	}
		
	/**
	 * Find the information (site and path) in the CityForDateBlock 
	 * 
	 * @param iSiteId        site to look information for, or -1 if information is global (date or city for example)
	 * @param sPath        path to look information for
	 * @param city4date      CityForDateBlock to look information into
	 * 
	 * @return The value if site and path exist in CityForDateBlock, "" if not
	 * 
	 **/
	protected FormBlock getBlockForSite(final int iSiteId, final CityForDateBlock city4date)
	{
		if ((null == city4date) || city4date.isEmpty())
			return null ;
		
		// Processing records. Each record is either global, or dedicated to a site
		//
		for (FormBlock formBlock : city4date.getInformation())
		{
			FormData formData = (FormData) formBlock.getDocumentLabel() ;
			if ((null != formData) && (formData.getSiteId() == iSiteId))
				return formBlock ;
		}
		
		return null ;
	}
}
