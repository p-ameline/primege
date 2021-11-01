package com.primege.client.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.allen_sauer.gwt.log.client.Log;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import com.primege.client.global.DataDictionaryCallBack;
import com.primege.shared.database.Dictionary;
import com.primege.shared.database.LanguageTag;
import com.primege.shared.rpc_util.GetDictionnaryListFromCodesAction;
import com.primege.shared.rpc_util.GetDictionnaryListFromCodesResult;
import com.primege.shared.rpc_util.ResultElement;
import com.primege.shared.rpc_util.SearchElement;

import net.customware.gwt.dispatch.client.DispatchAsync;

public class DataDictionaryProxy
{
	protected LanguageTag                          _language ;
	
	protected ArrayList<Dictionary>                _aDictionaryBuffer = new ArrayList<Dictionary>() ;
	protected Map<Integer, DataDictionaryCallBack> _aCallBacks        = new HashMap<Integer, DataDictionaryCallBack>() ;
	
	protected int                                  _iCallBackIndex ;

	protected final DispatchAsync                  _dispatcher ;
	
	@Inject
	public DataDictionaryProxy(DispatchAsync dispatcher)
	{
		_language       = null ;
		
		_iCallBackIndex = 0 ;
		_dispatcher     = dispatcher ;
	}
	
	@Inject
	public DataDictionaryProxy(LanguageTag language, DispatchAsync dispatcher)
	{
		_language       = language ;
		
		_iCallBackIndex = 0 ;
		_dispatcher     = dispatcher ;
	}
	
	/**
	 * Get a {@link Dictionary} from its exact code for current language
	 * 
	 * @param sCode            Must be a Dictionnary code
	 * @param callbackFunction Function to be called back if information has to be asynchronously queried from the server
	 * 
	 * @return A {@link Dictionary} if already in the buffer, <code>null</code> if not
	 */
	public Dictionary getDictionnaryFromExactCode(final String sCode, DataDictionaryCallBack callbackFunction, int iUserID) {
		return getDictionnaryFromExactCode(sCode, callbackFunction, _language.getCode(), iUserID) ;
	}
	
	/**
	 * Get a {@link Dictionary} from its exact code
	 * 
	 * @param sCode            Code
	 * @param callbackFunction Function to be called back if information has to be asynchronously queried from the server
	 * @param language         {@link LanguageTag} of the dictionary entry to look for.<br> If <code>null</code>, current language tag is used
	 * 
	 * @return A {@link Dictionary} if already in the buffer, <code>null</code> if not
	 */
	public Dictionary getDictionnaryFromExactCode(final String sCode, DataDictionaryCallBack callbackFunction, final String sLanguageCode, int iUserID)
	{
		if (null == sCode)
			throw new NullPointerException() ;
		
		// First, check if the Dictionary already exists in buffer
		//
		for (Dictionary entry : _aDictionaryBuffer)
			if (entry.getCode().equals(sCode))
				return entry ;
		
		// If not found, asks the server
		//
		getDictionnaryFromExactCodeFromServer(sCode, callbackFunction, sLanguageCode, iUserID) ;
		
		return null ;
	}
	
	/**
	 * Get a {@link Dictionary} from its exact code
	 * 
	 * @param sCode            Code
	 * @param callbackFunction Function to be called back if information has to be asynchronously queried from the server
	 * @param language         {@link LanguageTag} of the dictionary entry to look for.<br> If <code>null</code>, current language tag is used
	 * 
	 * @return A {@link Dictionary} if already in the buffer, <code>null</code> if not
	 */
	public ArrayList<Dictionary> getDictionnariesFromExactCodes(final ArrayList<String> aCodes, DataDictionaryCallBack callbackFunction, final String sLanguageCode, int iUserID)
	{
		if (null == aCodes)
			throw new NullPointerException() ;
		
		ArrayList<Dictionary> aResult = new ArrayList<Dictionary>() ;
		
		if (aCodes.isEmpty())
			return aResult ;
		
		// First, check if the Dictionary already exists in buffer
		//
		ArrayList<String> aMissingCodes = new ArrayList<String>() ;
		
		for (String sCode : aCodes)
		{
			boolean bFound = false ;
			for (Dictionary entry : _aDictionaryBuffer)
			{
				if (entry.getCode().equals(sCode))
				{
					aResult.add(entry) ;
					bFound = true ;
				}
			}
			if (false == bFound)
				aMissingCodes.add(sCode) ;
		}
		
		// If some were not found, asks the server
		//
		if (false == aMissingCodes.isEmpty())
			getDictionnariesFromExactCodesFromServer(aMissingCodes, callbackFunction, sLanguageCode, iUserID) ;
		
		return aResult ;
	}
	
	/**
   * Get a {@link Dictionary} from its exact code
   * 
   * @param sCode            Code
   * @param callbackFunction Function to be called back if information has to be asynchronously queried from the server
   * @param language         {@link LanguageTag} of the dictionary entry to look for.<br> If <code>null</code>, current language tag is used
   * 
   * @return A {@link Dictionary} if already in the buffer, <code>null</code> if not
   */
  public Dictionary getDictionnaryFromExactCodeFromBuffer(final String sCode, final String sLanguageCode)
  {
    if (null == sCode)
      throw new NullPointerException() ;
    
    // Check if the Dictionary already exists in buffer
    //
    for (Dictionary entry : _aDictionaryBuffer)
      if (entry.getCode().equals(sCode))
        return entry ;
    
    return null ;
  }
	
	/**
	 * Asks server for a {@link org.quadrifolium.shared.ontology.Flex Flex} from a code<br>
	 * If a Flex code, this is straightforward. For a Lemma or concept code, we are asking for the "to be displayed" Flex. 
	 * 
	 * @param sCode            Code (either at Flex, Lemma or Code level)
	 * @param callbackFunction Function to be executed once the result is back
	 * @param language         Language to look the Flex for
	 * @param iUserID          User identifier
	 */
	protected void getDictionnaryFromExactCodeFromServer(final String sCode, DataDictionaryCallBack callbackFunction, final String sLanguageCode, int iUserID)
	{
		if ((null == sCode) || (null == _dispatcher))
			throw new NullPointerException() ;
		
		// Add the callback function to the Map
		//
		int iIndex = getNexCallbackIndex() ;
		_aCallBacks.put(Integer.valueOf(iIndex), callbackFunction) ;
		
		ArrayList<SearchElement> aElements = new ArrayList<SearchElement>() ;
		aElements.add(new SearchElement(sCode, sLanguageCode, iIndex)) ;
		
		GetDictionnaryListFromCodesAction action = new GetDictionnaryListFromCodesAction(iUserID, aElements) ;
		
		_dispatcher.execute(action, new LoadDictionnaryFromCodeCallback()) ;
	}
	
	/**
	 * Asks server for a {@link org.quadrifolium.shared.ontology.Flex Flex} from a code<br>
	 * If a Flex code, this is straightforward. For a Lemma or concept code, we are asking for the "to be displayed" Flex. 
	 * 
	 * @param aCodes           List of codes (either at Flex, Lemma or Code level)
	 * @param callbackFunction Function to be executed once the result is back
	 * @param language         Language to look the Flex for
	 * @param iUserID          User identifier
	 */
	protected void getDictionnariesFromExactCodesFromServer(final ArrayList<String> aCodes, DataDictionaryCallBack callbackFunction, final String sLanguageCode, int iUserID)
	{
		if ((null == aCodes) || aCodes.isEmpty() || (null == _dispatcher))
			throw new NullPointerException() ;
		
		// Add the callback function to the Map
		//
		int iIndex = getNexCallbackIndex() ;
		_aCallBacks.put(Integer.valueOf(iIndex), callbackFunction) ;
		
		ArrayList<SearchElement> aElements = new ArrayList<SearchElement>() ;
		for (String sCode : aCodes)
			aElements.add(new SearchElement(sCode, sLanguageCode, iIndex)) ;
		
		GetDictionnaryListFromCodesAction action = new GetDictionnaryListFromCodesAction(iUserID, aElements) ;
		
		_dispatcher.execute(action, new LoadDictionnaryFromCodeCallback()) ;
	}
	
	/**
	 *  Asynchronous callback function for calls to GetFlexListFromCodesHandler
	 */
	public class LoadDictionnaryFromCodeCallback implements AsyncCallback<GetDictionnaryListFromCodesResult> 
	{
		public LoadDictionnaryFromCodeCallback() {
			super();
		}

		@Override
		public void onFailure(final Throwable cause)
		{
			Log.error("Handle Failure:", cause) ;    				
		}

		@Override
		public void onSuccess(final GetDictionnaryListFromCodesResult result)
		{
			if (null == result)
				return ;
			
			ArrayList<ResultElement> aResults = result.getResults() ;
			
			if (aResults.isEmpty())
				return ;
			
			int iCallBackIndex = -1 ;
			
			for (ResultElement entry : aResults)
			{
				if (false == _aDictionaryBuffer.contains(entry.getFlex()))
					_aDictionaryBuffer.add(entry.getFlex()) ;
				
				if (-1 == iCallBackIndex)
					iCallBackIndex = entry.getCallbackIndex() ;
			}
			
			executeCallbackFunction(iCallBackIndex) ;
		}
	}

	/**
	 * Asks for a set of {@link org.quadrifolium.shared.ontology.Flex Flex} from a set of {@link org.quadrifolium.shared.rpc4ontology.SearchElement SearchElement}<br>
	 * 
	 * @param aElements        Set of elements to look for
	 * @param callbackFunction Function to be executed once the result is back
	 * @param iUserID          User identifier (as an <code>int</code>, or <code>-1</code>)
	 */
	public ArrayList<Dictionary> getFlexFromCodes(final ArrayList<SearchElement> aCodes, DataDictionaryCallBack callbackFunction, int iUserID)
	{
		if ((null == aCodes) || (null == _dispatcher))
			throw new NullPointerException() ;
		
		if (aCodes.isEmpty())
			return null ;
		
		// If the buffer is empty, all elements must be asked to the server for
		//
		if (_aDictionaryBuffer.isEmpty())
		{
			getFlexFromCodesFromServer(aCodes, callbackFunction, iUserID) ;
			return null ;
		}
		
		// Check for already buffered elements
		//
		ArrayList<Dictionary> aExistingElements = new ArrayList<Dictionary>() ;
		ArrayList<SearchElement>  aUnknownElements  = new ArrayList<SearchElement>() ;
		
		for (SearchElement searchElement : aCodes)
		{
			Dictionary existingFlex = getSearchElementInBuffer(searchElement) ;
			if (null == existingFlex)
				aUnknownElements.add(searchElement) ;
			else
				aExistingElements.add(existingFlex) ;
		}
		
		// All elements were found, no need to ask the server
		//
		if (aUnknownElements.isEmpty())
			return aExistingElements ;
		
		// Ask the server for missing elements
		//
		getFlexFromCodesFromServer(aUnknownElements, callbackFunction, iUserID) ;
		
		if (aExistingElements.isEmpty())
			return null ;
		
		return aExistingElements ;
	}

	
	/**
	 * Asks server for a set of {@link org.quadrifolium.shared.ontology.Flex Flex} from a set of {@link org.quadrifolium.shared.rpc4ontology.SearchElement SearchElement}<br>
	 * 
	 * @param aElements        Set of elements to look for
	 * @param callbackFunction Function to be executed once the result is back
	 * @param iUserID          User identifier (as an <code>int</code>, or <code>-1</code>)
	 */
	protected void getFlexFromCodesFromServer(ArrayList<SearchElement> aElements, DataDictionaryCallBack callbackFunction, int iUserID)
	{
		if ((null == aElements) || (null == _dispatcher))
			throw new NullPointerException() ;
		
		if (aElements.isEmpty())
			return ;
		
		// Add the callback function to the Map
		//
		int iIndex = getNexCallbackIndex() ;
		_aCallBacks.put(Integer.valueOf(iIndex), callbackFunction) ;
		
		for (SearchElement element : aElements)
			element.setCallbackIndex(iIndex) ;
		
		GetDictionnaryListFromCodesAction action = new GetDictionnaryListFromCodesAction(iUserID, aElements) ;
		
		_dispatcher.execute(action, new LoadDictionnaryFromCodeCallback()) ;
	}
	
	/**
	*
	* Add a flex to the buffer 
	*  
	* @param flex {@link org.quadrifolium.shared.ontology.Flex} object to add to the buffer
	*
	**/
	public void addFlex(final Dictionary flex)
	{
		if (null == flex)
			return ;
		
		if (_aDictionaryBuffer.isEmpty() || (false == _aDictionaryBuffer.contains(flex)))
			_aDictionaryBuffer.add(flex) ;
	}
	
	/**
	 * Find the best Flex in the buffer that fits a given {@link org.quadrifolium.shared.rpc4ontology.SearchElement SearchElement}
	 * 
	 * @param searchElement What to look for in buffer (must not be <code>null</code>)
	 * 
	 * @return The best matching {@link Dictionary} is exists, <code>null</code> if not
	 * 
	 * @throws NullPointerException
	 */
	protected Dictionary getSearchElementInBuffer(final SearchElement searchElement) throws NullPointerException
	{
		if (null == searchElement)
			throw new NullPointerException() ;
		
		if (_aDictionaryBuffer.isEmpty())
			return null ;
		
		String sCode = searchElement.getCode() ;
		
		if ((null == sCode) || "".equals(sCode))
			return null ;
		
		// We end up returning the Flex with the longest language tag
		//
		Dictionary resultCandidate = null ;
		int iCandidateTagLen = 0 ;
		
		for (Dictionary dictionnary : _aDictionaryBuffer)
		{
		  String sLanguage = searchElement.getLanguage() ;
      
      int iLanguageTagLen = dictionnary.getLanguage().length() ;
      
      if ((iLanguageTagLen > iCandidateTagLen) && dictionnary.getCode().startsWith(sCode) && ("".equals(sLanguage) || sLanguage.startsWith(dictionnary.getLanguage())))
      {
        resultCandidate  = dictionnary ;
        iCandidateTagLen = iLanguageTagLen ;
      }
		}
		
		return resultCandidate ;
	}
	
	public LanguageTag getLanguage() {
		return _language ;
	}
	public void setLanguage(final LanguageTag language) {
		_language = language ;
	}
	
	/**
	 * Execute the <code>onSuccess()</code> method from callback function at a given index
	 * 
	 * @return The result of the <code>onSuccess()</code> method
	 */
	protected boolean executeCallbackFunction(int iCallbackIndex)
	{
		DataDictionaryCallBack callbackFunction = _aCallBacks.get(iCallbackIndex) ;
		
		if (null == callbackFunction)
			return false ;
		
		// Remove the callback function from the map once it is executed
		//
		_aCallBacks.remove(Integer.valueOf(iCallbackIndex)) ;
		
		// Execute the onSuccess() method
		//
		return callbackFunction.onSuccess() ;
	}
	
	/**
	 * Set the next value to the callback index and return it
	 */
	protected int getNexCallbackIndex()
	{
		if (Integer.MAX_VALUE == _iCallBackIndex)
			_iCallBackIndex = 0 ;
		
		_iCallBackIndex++ ;
		
		return _iCallBackIndex ;
	}
}
