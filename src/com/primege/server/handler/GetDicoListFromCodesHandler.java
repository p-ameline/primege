package com.primege.server.handler;

import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;

import com.google.inject.Inject;
import com.google.inject.Provider;

import com.primege.server.DBConnector;
import com.primege.server.Logger;
import com.primege.server.model.DictionaryManager;
import com.primege.shared.database.Dictionary;
import com.primege.shared.rpc_util.GetDictionnaryListFromCodesAction;
import com.primege.shared.rpc_util.GetDictionnaryListFromCodesResult;
import com.primege.shared.rpc_util.ResultElement;
import com.primege.shared.rpc_util.SearchElement;


public class GetDicoListFromCodesHandler implements ActionHandler<GetDictionnaryListFromCodesAction, GetDictionnaryListFromCodesResult>
{
	protected       int          _iUserId ;
	
	@Inject
	public GetDicoListFromCodesHandler(final Provider<ServletContext>     servletContext,
                                     final Provider<HttpServletRequest> servletRequest)
	{
		super() ;
		
		_iUserId = -1 ;
	}
	
	/**
	  * Constructor dedicated to unit tests 
	  */
	public GetDicoListFromCodesHandler()
	{
		super() ;
		
		_iUserId = -1 ;
	}

	@Override
	public GetDictionnaryListFromCodesResult execute(final GetDictionnaryListFromCodesAction action, final ExecutionContext context) throws ActionException 
  {	
		try 
		{
		  _iUserId = action.getUserId() ;
   		
   		ArrayList<SearchElement> _aElementsToFind = action.getCodes() ;
   		if ((null == _aElementsToFind) || _aElementsToFind.isEmpty())
   		  return new GetDictionnaryListFromCodesResult("Void request", null) ;
   		
   		DBConnector dbConnector = new DBConnector(false) ;
   		
   		GetDictionnaryListFromCodesResult flexListResult = new GetDictionnaryListFromCodesResult("", null) ;
   		
   		for (SearchElement searchElement : _aElementsToFind)
   		 getDictionnaryFromCode(searchElement, dbConnector, flexListResult) ;
   		
			return flexListResult ;
		}
		catch (Exception cause) 
		{
			Logger.trace("GetDictionnaryListFromCodesHandler.execute: ; cause: " + cause.getMessage(), _iUserId, Logger.TraceLevel.ERROR) ;
			throw new ActionException(cause);
		}
  }

	/**
	 * Look for an entry with a given code in the "flex" table 
	 * 
	 * @param dbconnector Database connector
	 * @param sCode       Code to be looked for
	 * @param lexicon     Record content
	 * 
	 **/	
	private boolean getDictionnaryFromCode(final SearchElement searchElement, DBConnector dbConnector, GetDictionnaryListFromCodesResult flexListResult)
	{
		String sFctName = "GetDictionnaryListFromCodesHandler.getDictionnaryFromCode" ;
		
		if ((null == dbConnector) || (null == searchElement) || (null == flexListResult))
		{
			Logger.trace(sFctName + ": bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		String sCode = searchElement.getCode() ;
		
	  if ((null == sCode) || "".equals(sCode))
    {
      Logger.trace(sFctName + ": void code", _iUserId, Logger.TraceLevel.ERROR) ;
      return false ;
    }
	  
	  // Get corresponding Flex object
	  //
	  DictionaryManager dicoManager = new DictionaryManager(_iUserId, dbConnector) ;
	  
	  Dictionary dico = new Dictionary() ;
	  if (false == dicoManager.existData(sCode, dico))
	  {
	    Logger.trace(sFctName + ": nothing found for code \"" + sCode + "\" in dictionnary", _iUserId, Logger.TraceLevel.SUBSTEP) ;
	    return false ;
	  }
	  
		// Add it to the result structure
		//
		flexListResult.addResult(new ResultElement(dico, searchElement.getCallbackIndex())) ;
		
		return true ;
	}
	
	@Override
	public void rollback(final GetDictionnaryListFromCodesAction action,
        							 final GetDictionnaryListFromCodesResult result,
                       final ExecutionContext context) throws ActionException
  {
		// Nothing to do here
  }
 
	@Override
	public Class<GetDictionnaryListFromCodesAction> getActionType()
	{
		return GetDictionnaryListFromCodesAction.class ;
	}
}
