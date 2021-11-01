package com.primege.server.handler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import com.google.inject.Inject;
import com.google.inject.Provider;

import com.primege.server.DBConnector;
import com.primege.server.Logger;
import com.primege.server.model.FormDataManager;
import com.primege.server.model.FormInformationManager;
import com.primege.shared.database.FormData;
import com.primege.shared.database.FormDataData;
import com.primege.shared.model.FormBlock;
import com.primege.shared.rpc.RegisterFormAction;
import com.primege.shared.rpc.RegisterFormResult;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;

public class RecordFormHandler implements ActionHandler<RegisterFormAction, RegisterFormResult> 
{	
	//private final static String sqlText = "SELECT nom,password FROM user";

	protected final Provider<ServletContext>     _servletContext ;
	protected final Provider<HttpServletRequest> _servletRequest ;
	
	@Inject
	public RecordFormHandler(final Provider<ServletContext>     servletContext,       
                           final Provider<HttpServletRequest> servletRequest)
	{
		_servletContext = servletContext ;
		_servletRequest = servletRequest ;
	}
	
	@Override
	public RegisterFormResult execute(RegisterFormAction action, ExecutionContext context) throws ActionException 
	{
		RegisterFormResult result = new RegisterFormResult() ;
		
		int iUserId = action.getUserId() ;
		if (0 == iUserId)
		{
			Logger.trace("RecordFormHandler.execute: empty user id", -1, Logger.TraceLevel.ERROR) ;
			return result ;
		}
		
		FormBlock<FormDataData> formBlock = (FormBlock<FormDataData>) action.getFormBlock() ;
		if (null == formBlock)
		{
			Logger.trace("RecordFormHandler.execute: empty parameter", iUserId, Logger.TraceLevel.ERROR) ;
			return result ;
		}
		
		FormData formData = (FormData) formBlock.getDocumentLabel() ;
		
		DBConnector dbconn = new DBConnector(false) ;
		
		// Store the document label (FormData)
		//
		FormDataManager formDataManager = new FormDataManager(iUserId, dbconn) ;
		
		// Existing form, update its document descriptor (FormData)
		//
		if (-1 != formData.getFormId())
		{
			if (formDataManager.updateData(formData))
				result.setRecordedId(formData.getFormId()) ;
		}
		// New form, fill complementary information, like entry date 
		//
		else
		{
			// Get formated current date
			//
			Date dateNow = new Date() ;
			SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyyMMddHHmmss") ;
			String sFormatedNow = simpleFormat.format(dateNow) ;
			
			formData.setEntryDateHour(sFormatedNow) ;
			
			if (false == formDataManager.insertData(formData))
				return result ;
			
			result.setRecordedId(formData.getFormId()) ;
		}
		
		// Store form information
		//
		if (false == storeInformation(dbconn, formData, formBlock.getInformation(), iUserId))
			return result ;
				
		Logger.trace("RecordFormHandler.execute: user " + iUserId + " successfuly recorded form " + formData.getFormId(), iUserId, Logger.TraceLevel.STEP) ;
		
		result.setRecordedId(formData.getFormId()) ;
		
		return result ;
	}
		
	private boolean storeInformation(DBConnector dbConnector, FormData formData, ArrayList<FormDataData> aInformation, int iUserId)
	{
		if ((null == dbConnector) || (null == aInformation))
			return false ;
		
		if (aInformation.isEmpty())
		{
			Logger.trace("RecordFormHandler.storeInformation: Empty form information array", iUserId, Logger.TraceLevel.WARNING) ;
			return true ;
		}
		
		FormInformationManager formInformationManager = new FormInformationManager(iUserId, dbConnector, FormInformationManager.InformationType.form) ;
		
		for (FormDataData formInformation : aInformation)
		{
			formInformation.setFormId(formData.getFormId()) ;
			storeOrUpdateInformation(formInformationManager, formInformation, iUserId) ;
		}
		
		suppressDeletedInformation(dbConnector, formData.getFormId(), aInformation, iUserId) ; 
		
		return true ;
	}
	
	private boolean storeOrUpdateInformation(FormInformationManager formInformationManager, FormDataData formInformation, int iUserId)
	{
		if ((null == formInformationManager) || (null == formInformation))
			return false ;
		
		boolean bDataSaved ;
		
		if (formInformation.getId() == -1)
			bDataSaved = formInformationManager.insertData(formInformation) ;
		else
			bDataSaved = formInformationManager.updateData(formInformation) ;
		
		return bDataSaved ;
	}

	/** 
	 * Remove all information that appear in database and are no longer in FormDataData array 
	 * 
	 * @param  dbConnector Database connector
	 * @param  iContactId  Contact identifier 
	 * @param  soapBasket  Basket array
	 * @param  iUserId     User identifier
	 * @return true if everything ok, false if any problem
	 */
	private boolean suppressDeletedInformation(DBConnector dbConnector, int iFormId, ArrayList<FormDataData> aInformation, int iUserId)
	{
		if ((null == dbConnector) || (null == aInformation))
			return false ;
		
		String sQuery = "SELECT id FROM formData WHERE formID = ?" ;
		
		dbConnector.prepareStatememt(sQuery, Statement.NO_GENERATED_KEYS) ;
		dbConnector.setStatememtInt(1, iFormId) ;
	   		
		if (false == dbConnector.executePreparedStatement())
		{
			Logger.trace("RecordFormHandler.suppressDeletedInformation: failed query " + sQuery, iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
	   		
		ResultSet rs = dbConnector.getResultSet() ;
		if (null == rs)
		{
			Logger.trace("RecordFormHandler.suppressDeletedInformation: no FormDataData found for form = " + iFormId, iUserId, Logger.TraceLevel.WARNING) ;
			return false ;
		}
		
		try
		{
	    while (rs.next())
	    {
	    	int iInformationId = rs.getInt("id") ;
	    	if (false == isInformationInArray(iInformationId, aInformation))
	    		deleteInformation(dbConnector, iInformationId, iUserId) ;
	    }
		} catch (SQLException e)
		{
			Logger.trace("RecordFormHandler.suppressDeletedInformation: exception when iterating results " + e.getMessage(), iUserId, Logger.TraceLevel.ERROR) ;
		}
		
		dbConnector.closeResultSet() ;
		dbConnector.closePreparedStatement() ;
				
		return true ;
	}
	
	private boolean isInformationInArray(int iInformationId, ArrayList<FormDataData> aInformation)
	{
		if (aInformation.isEmpty())
			return false ;
		
		for (FormDataData formInformation : aInformation)
			if (formInformation.getId() == iInformationId)
				return true ;
		
		return false ;
	}
	
	/** 
	 * Remove from database all ContactCodes and ContactCodeEcogens linked to a given ContactElement, then remove the ContactElement itself  
	 * 
	 * @param  dbConnector Database connector
	 * @param  iContactElementId Identifier of ContactElement to be deleted 
	 * @param  iUserId     User identifier
	 * @return true if everything ok, false if any problem
	 */
	private boolean deleteInformation(DBConnector dbConnector, int iInformationId, int iUserId)
	{
		if ((null == dbConnector) || (-1 == iInformationId))
			return false ;
		
		String sQuery = "DELETE FROM formData WHERE id = ?" ;
	   
		dbConnector.prepareStatememt(sQuery, Statement.RETURN_GENERATED_KEYS) ;
		dbConnector.setStatememtInt(1, iInformationId) ;
		
		// Execute query 
		//
		int iNbAffectedRows = dbConnector.executeUpdatePreparedStatement(true) ;
		if (-1 == iNbAffectedRows)
		{
			Logger.trace("RecordFormHandler.deleteInformation: failed deleting record " + iInformationId, iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		dbConnector.closePreparedStatement() ;
				
		return true ;
	}
		
	@Override
	public Class<RegisterFormAction> getActionType() {
		return RegisterFormAction.class;
	}

	@Override
	public void rollback(RegisterFormAction action, RegisterFormResult result,
			ExecutionContext context) throws ActionException {
		// TODO Auto-generated method stub
	}
}
