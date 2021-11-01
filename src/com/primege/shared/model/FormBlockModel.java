package com.primege.shared.model ;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable ;

import com.primege.shared.database.FormDataData;

/**
 * All information in a form
 * 
 * Created: 11 Jul 2011
 *
 * Author: PA
 * 
 */
public class FormBlockModel<T extends FormDataData> implements IsSerializable 
{
	protected String       _sLabel ;
	protected ArrayList<T> _aData ;

	/**
	 * Default constructor (with zero information)
	 */
	public FormBlockModel() 
	{
		_sLabel   = "" ;
		_aData    = null ;
	}

	/**
	 * Plain vanilla constructor 
	 */
	public FormBlockModel(final String sLabel, final ArrayList<T> aData)
	{
		_sLabel   = sLabel ;
		_aData    = null ;
		
		setInformation(aData) ;
	}
	
	/**
	 * Copy constructor 
	 */
	public FormBlockModel(final FormBlockModel<T> model) 
	{
		_aData = null ;
		
		initFromFormBlock4Model(model) ;
	}
	
	public void reset4Model()
	{
		_sLabel = "" ;
		if (null != _aData)
			_aData.clear() ;
	}
	
	public void initFromFormBlock4Model(final FormBlockModel<T> model)
	{
		reset4Model() ;
		
		if (null == model)
			return ;
		
		_sLabel = model._sLabel ;
		setInformation(model._aData) ;
	}
	
	public String getLabel() {
		return _sLabel ;
	}
	public void setLabel(String sLabel) {
		_sLabel = sLabel ;
	}
	
	public boolean isEmpty() {
		return _aData.isEmpty() ;
	}
	
	/**
	 * Get a data from it's path
	 *
	 * @return <code>null</code> if not found or something went wrong.
	 */
	public FormDataData getDataForPath(final String sPath)
	{
		if ((null == sPath) || "".equals(sPath) || (null == _aData) || _aData.isEmpty())
			return null ;
		
		for (FormDataData formData : _aData)
			if (sPath.equals(formData.getPath()))
					return formData ;
			
		return null ;
	}
	
	public ArrayList<T> getInformation() {
		return _aData ;
	}
	public void setInformation(ArrayList<T> aData)
	{
		if (null == aData)
			return ;

		if (null == _aData)
			_aData = new ArrayList<T>() ;
		else
			_aData.clear() ;

		if (aData.isEmpty())
			return ;

		for (T formDataData : aData)
			// _aData.add(new FormDataData(formDataData)) ; // changed 16/08/2021
			_aData.add(formDataData) ;
	}
	
	/**
	 * Add a data, or update it if some information with the same path already exists
	 */
	public void addData(T formData)
	{
		if (null == formData)
      return ;
		
		if (null == _aData)
		{
			_aData = new ArrayList<T>() ;
			_aData.add(formData) ;
			return ;
		}
		
		FormDataData samePathData = getDataForPath(formData.getPath()) ;
		if (null == samePathData)
			_aData.add(formData) ;
		else
			samePathData.initFromFormData(samePathData) ;
	}
}
