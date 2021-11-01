package com.primege.server.handler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import com.google.inject.Inject;
import com.google.inject.Provider;

import com.primege.server.DBConnector;
import com.primege.server.Logger;
import com.primege.server.model.FormDataManager;
import com.primege.shared.database.FormData;
import com.primege.shared.rpc.GetFormsAction;
import com.primege.shared.rpc.GetFormsResult;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;

public class GetFormsHandler extends GetFormsHandlerBase implements ActionHandler<GetFormsAction, GetFormsResult> 
{	
	protected final Provider<ServletContext>     _servletContext ;
	protected final Provider<HttpServletRequest> _servletRequest ;
	
	@Inject
	public GetFormsHandler(final Provider<ServletContext>     servletContext,       
                         final Provider<HttpServletRequest> servletRequest)
	{
		super() ;
		
		_servletContext = servletContext ;
		_servletRequest = servletRequest ;
	}

	@Override
	public GetFormsResult execute(GetFormsAction action, ExecutionContext context) throws ActionException 
	{
		String sFctName = "GetFormsHandler.execute" ;
		
		GetFormsResult result = new GetFormsResult() ;
		
		int iUserId = action.getUserId() ;
		
		// Build query
		//
		String sQuery = "SELECT * FROM form WHERE" ;
		_sQueryWhere  = "" ;
		
		int iArchetypeId = action.getArchetypeId() ;
		if (-1 != iArchetypeId)
			addToQueryWhere("archetypeID") ;
		
		int iEventId = action.getEventId() ;
		if (-1 != iEventId)
			addToQueryWhere("eventID") ;
		
		Vector<Integer> aCitiesId = action.getCities() ;
		if (false == aCitiesId.isEmpty())
			addToQueryWhereMultipleOr("cityID", aCitiesId.size()) ;
		
		Vector<Integer> aSitesId = action.getSites() ;
		if (false == aSitesId.isEmpty())
			addToQueryWhereMultipleOr("siteID", aSitesId.size()) ;
		
		int iAuthorId = action.getAuthorId() ;
		if (-1 != iAuthorId)
			addToQueryWhere("userID") ;
		
		String sEventDateFrom = action.getEventDateFrom() ;
		String sEventDateTo   = action.getEventDateTo() ;
		addToQueryWhereForDateInterval("formDate", sEventDateFrom, sEventDateTo) ;
		
		String sEntryDateFrom = action.getEntryDateFrom() ;
		String sEntryDateTo   = action.getEntryDateTo() ;
		addToQueryWhereForDateInterval("formEntryDate", sEntryDateFrom, sEntryDateTo) ;
		
		if ("".equals(_sQueryWhere))
		{
			Logger.trace(sFctName + ": empty query " + sQuery, iUserId, Logger.TraceLevel.ERROR) ;
			result.setMessage("Empty query") ;
			return result ;
		}
		
		DBConnector dbConnector = new DBConnector(false) ;

		dbConnector.prepareStatememt(sQuery + _sQueryWhere, Statement.NO_GENERATED_KEYS) ;
		
		int iPos = 1 ;
		
		if (-1 != iArchetypeId)
			dbConnector.setStatememtInt(iPos++, iArchetypeId) ;
		if (-1 != iEventId)
			dbConnector.setStatememtInt(iPos++, iEventId) ;
		if (false == aCitiesId.isEmpty())
			for (Iterator<Integer> it = aCitiesId.iterator() ; it.hasNext() ; )
				dbConnector.setStatememtInt(iPos++, it.next()) ;
		if (false == aSitesId.isEmpty())
			for (Iterator<Integer> it = aSitesId.iterator() ; it.hasNext() ; )
				dbConnector.setStatememtInt(iPos++, it.next()) ;
		if (-1 != iAuthorId)
			dbConnector.setStatememtInt(iPos++, iAuthorId) ;
		if (false == "".equals(sEventDateFrom))
			dbConnector.setStatememtString(iPos++, sEventDateFrom) ;
		if (false == "".equals(sEventDateTo))
			dbConnector.setStatememtString(iPos++, sEventDateTo) ;
		if (false == "".equals(sEntryDateFrom))
			dbConnector.setStatememtString(iPos++, sEntryDateFrom) ;
		if (false == "".equals(sEntryDateTo))
			dbConnector.setStatememtString(iPos++, sEntryDateTo) ;
		
		if (false == dbConnector.executePreparedStatement())
		{
			Logger.trace(sFctName + ": failed query " + sQuery, iUserId, Logger.TraceLevel.ERROR) ;
			result.setMessage("Database error") ;
			return result ;
		}

		int iNbCode = 0 ;
		
		FormDataManager dataManager = new FormDataManager(iUserId, dbConnector) ;

		ResultSet rs = dbConnector.getResultSet() ;
		try
		{        
			while (rs.next())
			{
				FormData formData = new FormData() ;
				dataManager.fillDataFromResultSet(rs, formData) ;
				
				if (formData.isValid())
					result.addFormData(formData) ;
				
				iNbCode++ ;
			}
			
			if (0 == iNbCode)
			{
				Logger.trace(sFctName + ": query gave no answer", iUserId, Logger.TraceLevel.WARNING) ;
				dbConnector.closePreparedStatement() ;
				result.setMessage("Query gave no answer.") ;
				return result ;
			}
		}
		catch(SQLException ex)
		{
			Logger.trace(sFctName + ": DBConnector.dbSelectPreparedStatement: executeQuery failed for preparedStatement " + sQuery, action.getUserId(), Logger.TraceLevel.ERROR) ;
			Logger.trace(sFctName + ": SQLException: " + ex.getMessage(), action.getUserId(), Logger.TraceLevel.ERROR) ;
			Logger.trace(sFctName + ": SQLState: " + ex.getSQLState(), action.getUserId(), Logger.TraceLevel.ERROR) ;
			Logger.trace(sFctName + ": VendorError: " +ex.getErrorCode(), action.getUserId(), Logger.TraceLevel.ERROR) ;
		}

		dbConnector.closeResultSet() ;
		dbConnector.closePreparedStatement() ;
		
		Logger.trace(sFctName + ": query gave " + iNbCode + " answers", iUserId, Logger.TraceLevel.DETAIL) ;
		
		return result ;
	}
			
	@Override
	public Class<GetFormsAction> getActionType() {
		return GetFormsAction.class;
	}

	@Override
	public void rollback(GetFormsAction action, GetFormsResult result,
			ExecutionContext context) throws ActionException {
		// TODO Auto-generated method stub
	}
}
