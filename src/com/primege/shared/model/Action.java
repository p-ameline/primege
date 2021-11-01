package com.primege.shared.model ;

import com.google.gwt.user.client.rpc.IsSerializable ;
import com.google.gwt.xml.client.Element;
import com.primege.shared.GlobalParameters;

/**
 * A document workflow action
 * 
 * Created: 17 August 2021
 * Author: PA
 * 
 */
public class Action implements IsSerializable 
{
	protected String  _sIdentifier ;
	protected String  _sType ;
	protected String  _sCaption ;
	protected int     _iArchetypeID ;
	
	protected Element _model ;
	
	/**
	 * Default constructor (with zero information)
	 */
	public Action() {
		reset() ;
	}
		
	/**
	 * Plain vanilla constructor 
	 */
	public Action(final String sIdentifier, final String sType, final String sCaption, final String sArchetypeID, final Element model) 
	{
		_sIdentifier = sIdentifier ;
		_sType       = sType ;
		_sCaption    = sCaption ;
		
		_model       = model ;
		
		_iArchetypeID = -1 ;
		if ((null != sArchetypeID) && (false == sArchetypeID.isEmpty()))
		{
			try {
				_iArchetypeID = Integer.parseInt(sArchetypeID) ;
			} catch (NumberFormatException e) {
			}
		}
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param model SicaTraitPathData to initialize from 
	 */
	public Action(final Action model) 
	{
		reset() ;
		
		initFromModel(model) ;
	}
			
	/**
	 * Initialize all information from another SicaTraitPathData
	 * 
	 * @param model SicaTraitPathData to initialize from 
	 */
	public void initFromModel(final Action model)
	{
		reset() ;
		
		if (null == model)
			return ;

		_sIdentifier  = model._sIdentifier ;
		_sType        = model._sType ;
		_sCaption     = model._sCaption ;
		_iArchetypeID = model._iArchetypeID ;
		_model        = model._model ;
	}
		
	/**
	 * Zeros all information
	 */
	public void reset() 
	{
		_sIdentifier  = "" ;
		_sType        = "" ;
		_sCaption     = "" ;
		_iArchetypeID = -1 ;
		_model        = null ;
	}
	
	/**
	 * Check if this object has no initialized data
	 * 
	 * @return true if all data are zeros, false if not
	 */
	public boolean isEmpty()
	{
		if ("".equals(_sIdentifier) &&
				"".equals(_sType)       &&
				"".equals(_sCaption))
			return true ;
		
		return false ;
	}

	public String getIdentifier() {
    return _sIdentifier ;
  }
	public void setIdentifier(final String sIdentifier) {
		_sIdentifier = sIdentifier ;
  }
	
	public String getType() {
    return _sType ;
  }
  public void setType(final String sType) {
  	_sType = sType ;
  }
  
  public String getCaption() {
    return _sCaption ;
  }
	public void setCaption(final String sCaption) {
		_sCaption = sCaption ;
  }
	
	public int getArchetypeID() {
    return _iArchetypeID ;
  }
	public void setArchetypeID(final int iArchetypeID) {
		_iArchetypeID = iArchetypeID ;
  }
	
	public Element getModel() {
    return _model ;
  }
	public void setModel(final Element model) {
		_model = model ;
  }
	
	/**
	 * Determine whether two TraitPath are exactly similar
	 * 
	 * @return true if all data are the same, false if not
	 * @param  otherData TraitPath to compare with
	 */
	public boolean equals(Action otherData)
	{
	  if (this == otherData) {
	    return true ;
		}
		if (null == otherData) {
			return false ;
		}
		
		return GlobalParameters.areStringsEqual(_sIdentifier, otherData._sIdentifier) &&
		       GlobalParameters.areStringsEqual(_sType, otherData._sType) &&
		       GlobalParameters.areStringsEqual(_sCaption, otherData._sCaption);
	}

	/**
	 * Determine whether this TraitPath is exactly similar to another object
	 * 
	 * @return true if all data are the same, false if not
	 * @param o Object to compare with
	 * 
	 */
	public boolean equals(Object o) 
	{
	  if (this == o)
	    return true ;
	  
	  if (null == o || getClass() != o.getClass())
	    return false;

		final Action formData = (Action) o ;

		return equals(formData) ;
	}
}
