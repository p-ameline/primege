package com.primege.server.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Vector;

import com.primege.server.DBConnector;
import com.primege.server.Logger;
import com.primege.shared.database.FormData;
import com.primege.shared.model.CityForDateBlock;
import com.primege.shared.model.DashboardBlocks;
import com.primege.shared.model.FormBlock;

public class GetDashboardBlocksInBase  
{	
	protected final DBConnector _dbConnector ;
	protected final int         _iUserId ;
	
	public GetDashboardBlocksInBase(int iUserId, final DBConnector dbConnector)
	{
		_dbConnector = dbConnector ;
		_iUserId     = iUserId ;
	}
	
	/** 
	 * Fill a DashboardBlocks with all forms of a given event for a given city ID and/or a given date 
	 * 
	 * @param  iEventId   event identifier
	 * @param  iCityId    city identifier, or -1
	 * @param  sDate      date, or ""
	 * @param  dashBlocks the DashboardBlocks to be completed by information from database
	 * 
	 * @return <code>true</code> if all went well, <code>false</code> if not   
	 */
	public boolean GetDashboardBlocks(final int iEventId, final int iCityId, final String sDate, DashboardBlocks dashBlocks) 
	{
		if (null == dashBlocks)
			return false ;
		
		// The first step is to get all information from the "form" table and create objects in dashBlocks
		//
		String sQuery = "" ;
			
		if (iCityId > 0)
		{
			sQuery = "SELECT * FROM form WHERE eventID = ? AND cityID = ?" ;

			_dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
			
			_dbConnector.setStatememtInt(1, iEventId) ;
			_dbConnector.setStatememtInt(2, iCityId) ;
		}
		else if (false == "".equals(sDate))
		{
			sQuery = "SELECT * FROM form WHERE eventID = ? AND formDate = ?" ;

			_dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
			
			_dbConnector.setStatememtInt(1, iEventId) ;
			_dbConnector.setStatememtString(2, sDate) ;
		}
		else
		{
			sQuery = "SELECT * FROM form WHERE eventID = ?" ;

			_dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
			
			_dbConnector.setStatememtInt(1, iEventId) ;
		}

		if (false == _dbConnector.executePreparedStatement())
		{
			Logger.trace("GetDashboardBlocksInBase.GetDashboardBlocks: failed query " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
			_dbConnector.closePreparedStatement() ;
			return false ;
		}

		int iNbCode = 0 ;

		FormDataManager formDataManager = new FormDataManager(_iUserId, _dbConnector) ;
		
		ResultSet rs = _dbConnector.getResultSet() ;
		try
		{
			while (rs.next())
			{
				FormData document = new FormData() ;
				formDataManager.fillDataFromResultSet(rs, document) ;
					
				if (document.isValid())
					addFormDataToDashboardBlocks(document, dashBlocks) ;
					
				iNbCode++ ;
			}

			if (0 == iNbCode)
			{
				Logger.trace("GetDashboardBlocksHandler.fillBlocksForCityt: query for city " + iCityId + " and event " + iEventId + " gave no answer", _iUserId, Logger.TraceLevel.WARNING) ;
				_dbConnector.closeResultSet() ;
				_dbConnector.closePreparedStatement() ;
				return false ;
			}
		}
		catch(SQLException ex)
		{
			Logger.trace("GetFormInBase.loadDocument: DBConnector.dbSelectPreparedStatement: executeQuery failed for preparedStatement " + sQuery, _iUserId, Logger.TraceLevel.ERROR) ;
			Logger.trace("GetFormInBase.loadDocument: SQLException: " + ex.getMessage(), _iUserId, Logger.TraceLevel.ERROR) ;
			Logger.trace("GetFormInBase.loadDocument: SQLState: " + ex.getSQLState(), _iUserId, Logger.TraceLevel.ERROR) ;
			Logger.trace("GetFormInBase.loadDocument: VendorError: " +ex.getErrorCode(), _iUserId, Logger.TraceLevel.ERROR) ;        
		}

		// The second step is to complete dashBlocks with all information from the formData table
		//
		addFormsInformation(dashBlocks) ;
			
		_dbConnector.closeResultSet() ;
		_dbConnector.closePreparedStatement() ;
			
		return true ;
	}
	
	/**
	  * Complete all forms inside the DashboardBlocks with their data    
	  * 
	  */
	protected void addFormsInformation(DashboardBlocks dashBlocks)
	{
		if ((null == dashBlocks) || dashBlocks.isEmpty())
			return ;
		
		// Object that get a form, or parts of a form, in database
		//
		GetFormInBase getFormInBase = new GetFormInBase(_iUserId, _dbConnector) ;
		
		for (Iterator<CityForDateBlock> it = dashBlocks.getInformation().iterator() ; it.hasNext() ; )
		{
			CityForDateBlock city4date = it.next() ;
			
			if (false == city4date.isEmpty())
			{
				for (Iterator<FormBlock> itForm = city4date.getInformation().iterator() ; itForm.hasNext() ; )
		  	{
					FormBlock formBlock = itForm.next() ;
					
					// Fill the formBlock with its data
					//
					FormData document = (FormData) formBlock.getDocumentLabel() ;
					if (null != document)
						getFormInBase.loadFormData(document.getFormId(), formBlock) ;
		  	}
			}
		}
	}
	
	/**
	  * Add a FormData into the proper CityForDateBlock into the DashboardBlocks  
	  * 
	  */
	protected void addFormDataToDashboardBlocks(final FormData document, DashboardBlocks dashBlocks)
	{
		if ((null == document) || (null == dashBlocks))
			return ;
		
		FormBlock formBlock = new FormBlock("", document, null) ;
		
		CityForDateBlock city4date = getCity4DateInBlocks(document.getCityId(), document.getEventDate(), dashBlocks) ;
		
		if (null == city4date)
		{
			CityForDateBlock newCity4Date = new CityForDateBlock(document.getCityId(), document.getEventDate(), new Vector<FormBlock>()) ;
			newCity4Date.addData(formBlock) ;
			
			dashBlocks.addData(newCity4Date) ;
		}
		else
			city4date.addData(formBlock) ;
	}
	
	/**
	  * Get the CityForDateBlock attached to a given city and a given date inside the DashboardBlocks  
	  * 
	  * @param iCityId    ID of city to look for
	  * @param sEventDate date to look for
	  * @param dashBlocks DashboardBlocks to look into
	  * 
	  * @return The corresponding CityForDateBlock if found, <code>null</code> if not
	  * 
	  */
	protected CityForDateBlock getCity4DateInBlocks(final int iCityId, final String sEventDate, final DashboardBlocks dashBlocks)
	{
		if ((null == dashBlocks) || (null == dashBlocks.getInformation()) || (null == sEventDate))
			return null ;
		
		if (dashBlocks.getInformation().isEmpty())
			return null ;
		
		for (Iterator<CityForDateBlock> it = dashBlocks.getInformation().iterator() ; it.hasNext() ; )
		{
			CityForDateBlock city4date = it.next() ;
			
			if ((city4date.getCityId() == iCityId) && sEventDate.equals(city4date.getDate()))
				return city4date ;
		}
		
		return null ;
	}
}
