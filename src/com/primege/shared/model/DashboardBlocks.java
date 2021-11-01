package com.primege.shared.model ;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable ;

/**
 * All information for a given city and/or a given date.<br>
 * <br>
 * Note that city or date can be undefined, meaning "all of", reason for there is a ArrayList of "city for date" information<br>
 * <br>
 *
 * Created: 11 Apr 2016<br>
 * Author: PA<br>
 * 
 */
public class DashboardBlocks implements IsSerializable 
{
	private int                      _iConstantCityId ;
	private String                   _sConstantDate ;
	private ArrayList<CityForDateBlock> _aData ;

	/**
	 * Default constructor (with zero information)
	 */
	public DashboardBlocks() 
	{
		_sConstantDate   = "" ;
		_iConstantCityId = -1 ;
		_aData           = null ;
	}

	/**
	 * Plain vanilla constructor 
	 */
	public DashboardBlocks(int iConstantCityId, String sConstantDate, ArrayList<CityForDateBlock> aData) 
	{
		_iConstantCityId = iConstantCityId ;
		_sConstantDate   = sConstantDate ;
		_aData           = null ;
		
		setInformation(aData) ;
	}

	/**
	 * Copy constructor 
	 */
	public DashboardBlocks(DashboardBlocks model) 
	{
		_aData    = null ;
		
		initFromDashboardBlocks(model) ;
	}
	
	public boolean isEmpty() {
		return ((null == _aData) || _aData.isEmpty()) ;
	}
	
	protected void reset()
	{
		_sConstantDate   = "" ;
		_iConstantCityId = -1 ;

		if (null != _aData)
			_aData.clear() ;
	}
	
	public void initFromDashboardBlocks(DashboardBlocks model)
	{
		reset() ;
		
		if (null == model)
			return ;
		
		_sConstantDate   = model._sConstantDate ;
		_iConstantCityId = model._iConstantCityId ;
		
		setInformation(model._aData) ;
	}
	
	public String getConstantDate() {
		return _sConstantDate ;
	}
	public void setConstantDate(String sConstantDate) {
		_sConstantDate = sConstantDate ;
	}
	
	public int getConstantCityId() {
		return _iConstantCityId ;
	}
	public void setConstantCityId(int iConstantCityId) {
		_iConstantCityId = iConstantCityId ;
	}
		
	public ArrayList<CityForDateBlock> getInformation() {
		return _aData ;
	}
	public void setInformation(ArrayList<CityForDateBlock> aData)
	{
		if (null == aData)
			return ;

		if (null == _aData)
			_aData = new ArrayList<CityForDateBlock>() ;

		if (aData.isEmpty())
			return ;

		for (CityForDateBlock city : aData)
			_aData.add(new CityForDateBlock(city)) ;
	}
	public void addData(CityForDateBlock formData)
	{
		if (null == _aData)
			_aData = new ArrayList<CityForDateBlock>() ;
			
		_aData.add(formData) ;
	}
}
