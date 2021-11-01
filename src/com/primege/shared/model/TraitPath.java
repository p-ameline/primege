package com.primege.shared.model ;

import com.google.gwt.user.client.rpc.IsSerializable ;
import com.primege.shared.GlobalParameters;

/**
 * A TraitPath object the description of a path that points to a trait
 * 
 * Created: 15 July 2021
 * Author: PA
 * 
 */
public class TraitPath implements IsSerializable 
{
	protected String _sTraitName ;
	protected String _sPath ;
	protected String _sComposite ;
	
	/**
	 * Default constructor (with zero information)
	 */
	public TraitPath() {
		reset() ;
	}
		
	/**
	 * Plain vanilla constructor 
	 */
	public TraitPath(final String sTraitName, final String sPath, final String sComposite) 
	{
		_sTraitName = sTraitName ;
		_sPath      = sPath ;
		_sComposite = sComposite ;
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param model SicaTraitPathData to initialize from 
	 */
	public TraitPath(final TraitPath model) 
	{
		reset() ;
		
		initFromModel(model) ;
	}
			
	/**
	 * Initialize all information from another SicaTraitPathData
	 * 
	 * @param model SicaTraitPathData to initialize from 
	 */
	public void initFromModel(final TraitPath model)
	{
		reset() ;
		
		if (null == model)
			return ;
		
		_sTraitName = model._sTraitName ;
    _sPath      = model._sPath ;
    _sComposite = model._sComposite ;
	}
		
	/**
	 * Zeros all information
	 */
	public void reset() 
	{
		_sTraitName = "" ;
    _sPath      = "" ;
    _sComposite = "" ;
	}
	
	/**
	 * Check if this object has no initialized data
	 * 
	 * @return true if all data are zeros, false if not
	 */
	public boolean isEmpty()
	{
		if ("".equals(_sTraitName) &&
				"".equals(_sPath)      &&
				"".equals(_sComposite))
			return true ;
		
		return false ;
	}

	public String getTraitName() {
    return _sTraitName ;
  }
	public void setTraitName(final String sTraitName) {
		_sTraitName = sTraitName ;
  }
	
	public String getPath() {
    return _sPath ;
  }
  public void setPath(final String sPath) {
    _sPath = sPath ;
  }
  
  public String getComposite() {
    return _sComposite ;
  }
	public void setComposite(final String sComposite) {
		_sComposite = sComposite ;
  }
	
	/**
	 * Determine whether two TraitPath are exactly similar
	 * 
	 * @return true if all data are the same, false if not
	 * @param  otherData TraitPath to compare with
	 */
	public boolean equals(TraitPath otherData)
	{
	  if (this == otherData) {
	    return true ;
		}
		if (null == otherData) {
			return false ;
		}
		
		return GlobalParameters.areStringsEqual(_sTraitName, otherData._sTraitName) &&
		       GlobalParameters.areStringsEqual(_sPath, otherData._sPath) &&
		       GlobalParameters.areStringsEqual(_sComposite, otherData._sComposite);
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

		final TraitPath formData = (TraitPath) o ;

		return equals(formData) ;
	}
}
