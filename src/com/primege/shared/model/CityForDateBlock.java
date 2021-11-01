package com.primege.shared.model ;

import java.util.Iterator;
import java.util.Vector;

import com.google.gwt.user.client.rpc.IsSerializable ;

/**
 * Information from all sites for a given city in a given day
 * 
 * Created: 2 Jun 2016
 *
 * Author: PA
 * 
 */
public class CityForDateBlock implements java.lang.Comparable<CityForDateBlock>, IsSerializable 
{
	private int               _iCityId ;
	private String            _sDate ;
	private Vector<FormBlock> _aSitesData ;

	/**
	 * Default constructor (with zero information)
	 */
	public CityForDateBlock() 
	{
		_iCityId    = -1 ;
		_sDate      = "" ;
		_aSitesData = null ;
	}

	/**
	 * Plain vanilla constructor 
	 */
	public CityForDateBlock(int iCityId, String sDate, Vector<FormBlock> aData) 
	{
		_iCityId    = iCityId ;
		_sDate      = sDate ;
		_aSitesData = null ;
		
		setInformation(aData) ;
	}
	
	/**
	 * Copy constructor 
	 */
	public CityForDateBlock(CityForDateBlock model) 
	{
		_aSitesData = null ;
		
		initFromCity4DateBlock(model) ;
	}
	
	public boolean isEmpty() {
		return ((null == _aSitesData) || _aSitesData.isEmpty()) ;
	}
	
	protected void reset()
	{
		_iCityId = -1 ;
		_sDate   = "" ;
		if (null != _aSitesData)
			_aSitesData.clear() ;
	}
	
	public void initFromCity4DateBlock(CityForDateBlock model)
	{
		reset() ;
		
		if (null == model)
			return ;
		
		_iCityId = model._iCityId ;
		_sDate   = model._sDate ;
		setInformation(model._aSitesData) ;
	}
	
	public int getCityId() {
		return _iCityId ;
	}
	public void setCityId(int iCityId) {
		_iCityId = iCityId ;
	}
	
	public String getDate() {
		return _sDate ;
	}
	public void setDate(String sDate) {
		_sDate = sDate ;
	}
	
	public Vector<FormBlock> getInformation() {
		return _aSitesData ;
	}
	public void setInformation(Vector<FormBlock> aData)
	{
		if (null == aData)
			return ;

		if (null == _aSitesData)
			_aSitesData = new Vector<FormBlock>() ;

		if (aData.isEmpty())
			return ;

		for (Iterator<FormBlock> it = aData.iterator() ; it.hasNext() ; )
			_aSitesData.add(new FormBlock(it.next())) ;
	}
	public void addData(FormBlock blockData)
	{
		if (null == _aSitesData)
			_aSitesData = new Vector<FormBlock>() ;
			
		_aSitesData.add(blockData) ;
	}
	
	@Override
	public int compareTo(CityForDateBlock otherBlock)
	{
		if (null == otherBlock)
			return 1 ;
		
		if (-1 != _iCityId)
			return (_iCityId - otherBlock._iCityId) ;
		if (false == "".equals(_sDate))
			return _sDate.compareTo(otherBlock._sDate) ;
		
		return 0 ;
	}
}
