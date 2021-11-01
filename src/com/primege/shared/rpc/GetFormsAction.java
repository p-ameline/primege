package com.primege.shared.rpc;

import java.util.Iterator;
import java.util.Vector;

import net.customware.gwt.dispatch.shared.Action;

/**
 * Object to query information for a set of forms
 * 
 * Created: 16 May 2016
 * Author: PA
 * 
 */
public class GetFormsAction implements Action<GetFormsResult> 
{	
	private int             _iUserId ;
	
	private int             _iEventId ;
	private Vector<Integer> _aCitiesId = new Vector<Integer>() ;
	private Vector<Integer> _aSitesId  = new Vector<Integer>() ;
	private String          _sEventDateFrom ;
	private String          _sEventDateTo ;
	
	private int             _iAuthorId ;
	private String          _sEntryDateFrom ;
	private String          _sEntryDateTo ;
	
	private int             _iArchetypeId ;
	
	/**
	 * Default constructor (with zero information)
	 */
	public GetFormsAction() 
	{
		super() ;
		reset() ;
	}
	
	/**
	 * Constructor for all forms of current user 
	 */
	public GetFormsAction(int iUserId) 
	{
		super() ;
		reset() ;
		
		_iUserId   = iUserId ;
		_iAuthorId = iUserId ; 
	}
	
	/**
	 * Plain vanilla constructor 
	 */
	public GetFormsAction(final int iUserId, final int iEventID, 
			                  final Vector<Integer> aCitiesId, final Vector<Integer> aSitesId, 
			                  final String sEventDateFrom, final String sEventDateTo, 
			                  final int iAuthorID, 
			                  final String sEntryDateFrom, final String sEntryDateTo, 
			                  final int iArchetypeID) 
	{
		super() ;
		
		_iUserId        = iUserId ;
		
		_iEventId       = iEventID ;
		
		if (false == aCitiesId.isEmpty())
			for (Iterator<Integer> it = aCitiesId.iterator() ; it.hasNext() ; )
				_aCitiesId.add(new Integer(it.next())) ;
		
		if (false == aSitesId.isEmpty())
			for (Iterator<Integer> it = aSitesId.iterator() ; it.hasNext() ; )
				_aSitesId.add(new Integer(it.next())) ;
		
		_sEventDateFrom = sEventDateFrom ;
		_sEventDateTo   = sEventDateTo ;
		
		_iAuthorId      = iAuthorID ;
		_sEntryDateFrom = sEntryDateFrom ;
		_sEntryDateTo   = sEntryDateTo ;
		
		_iArchetypeId   = iArchetypeID ;
	}

	/**
	 * Zeros all information
	 */
	public void reset()
	{
		_iUserId        = -1 ;
		_iEventId       = -1 ;
		_aCitiesId.clear() ;
		_aSitesId.clear() ;
		_sEventDateFrom = "" ;
		_sEventDateTo   = "" ;
		_iAuthorId      = -1 ;
		_sEntryDateFrom = "" ;
		_sEntryDateTo   = "" ;
		_iArchetypeId   = -1 ; 
	}
	
	public int getUserId() {
		return _iUserId ;
	}
	public void setUserId(int iUserId) {
		_iUserId = iUserId ;
	}

	public int getEventId() {
		return _iEventId ;
	}
	public void setEventId(int iEventId) {
		_iEventId = iEventId ;
	}

	public Vector<Integer> getCities() {
		return _aCitiesId ;
	}
	public void addCityId(int iCityId) {
		_aCitiesId.addElement(new Integer(iCityId)) ;
	}

	public Vector<Integer> getSites() {
		return _aSitesId ;
	}
	public void addSiteId(int iSiteId) {
		_aSitesId.addElement(new Integer(iSiteId)) ;
	}

	public String getEventDateFrom() {
		return _sEventDateFrom ;
	}
	public void setEventDateFrom(String sEventDateFrom) {
		_sEventDateFrom = sEventDateFrom ;
	}

	public String getEventDateTo() {
		return _sEventDateTo ;
	}
	public void setEventDateTo(String sEventDateTo) {
		_sEventDateTo = sEventDateTo ;
	}

	public int getAuthorId() {
		return _iAuthorId ;
	}
	public void setAuthorId(int iAuthorId) {
		_iAuthorId = iAuthorId ;
	}

	public String getEntryDateFrom() {
		return _sEntryDateFrom ;
	}
	public void setEntryDateFrom(String sEntryDateFrom) {
		_sEntryDateFrom = sEntryDateFrom ;
	}

	public String getEntryDateTo() {
		return _sEntryDateTo ;
	}
	public void setEntryDateTo(String sEntryDateTo) {
		_sEntryDateTo = sEntryDateTo ;
	}

	public int getArchetypeId() {
		return _iArchetypeId ;
	}
	public void setArchetypeId(int iArchetypeId) {
		_iArchetypeId = iArchetypeId ;
	}
}
