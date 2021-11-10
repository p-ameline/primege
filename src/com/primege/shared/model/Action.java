package com.primege.shared.model ;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable ;
import com.google.gwt.xml.client.Element;
import com.primege.client.mvp.FormPresenterModel;
import com.primege.shared.GlobalParameters;
import com.primege.shared.util.MailTo;

/**
 * Model of document workflow action
 * 
 * Created: 17 August 2021
 * Author: PA
 * 
 */
public class Action implements IsSerializable 
{
	protected String               _sIdentifier ;
	protected String               _sType ;
	protected String               _sCaption ;
	protected int                  _iArchetypeID ;
	
	/** DOM element for the "action" tag inside archetype's "actions" section */
	protected Element              _model ;
	
	protected ArrayList<TraitPath> _aTraits  = new ArrayList<TraitPath>() ;
	
	protected boolean              _bHasMailSection ;
	protected String               _sMailTemplate ;
	protected String               _sMailFrom ;
	protected ArrayList<MailTo>    _aMailAddresses = new ArrayList<MailTo>() ;
	protected String               _sMailCaption ;
	
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
		reset() ;
		
		if (null != sIdentifier)
			_sIdentifier = sIdentifier ;
		if (null != sType)
			_sType       = sType ;
		if (null != sCaption)
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
	 * Plain vanilla constructor with archetype identifier as an int
	 */
	public Action(final String sIdentifier, final String sType, final String sCaption, final int iArchetypeID, final Element model) 
	{
		reset() ;
		
		if (null != sIdentifier)
			_sIdentifier = sIdentifier ;
		if (null != sType)
			_sType       = sType ;
		if (null != sCaption)
			_sCaption    = sCaption ;
		
		_model        = model ;
		
		_iArchetypeID = iArchetypeID ;
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
		
		_aTraits.addAll(model._aTraits) ;
		
		_aMailAddresses.addAll(model._aMailAddresses) ;
		
		_bHasMailSection = model._bHasMailSection ;
		_sMailTemplate   = model._sMailTemplate ;
		_sMailFrom       = model._sMailFrom ;
		_sMailCaption    = model._sMailCaption ;
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
		
		_aTraits.clear() ;
		
		_aMailAddresses.clear() ;
		
		_bHasMailSection = false ;
		_sMailTemplate   = "" ;
		_sMailFrom       = "" ;
		_sMailCaption    = "" ;
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
	
	public ArrayList<TraitPath> getTraits() {
		return _aTraits ;
	}
	
	public void addTrait(TraitPath trait)
	{
		if ((null == trait) || _aTraits.contains(trait))
			return ;
		
		_aTraits.add(trait) ;
	}
	
	public boolean hasMailSection() {
		return _bHasMailSection ;
	}
	public void setHasMailSection(boolean bHasMailSection) {
		_bHasMailSection = bHasMailSection ;
	}
	
	public String getMailTemplate() {
		return _sMailTemplate ;
	}
	public void setMailTemplate(final String sMailTemplate) {
		_sMailTemplate = sMailTemplate ;
	}
	
	public String getMailFrom() {
		return _sMailFrom ;
	}
	public void setMailFrom(final String sMailFrom) {
		_sMailFrom = sMailFrom ;
	}
	
	public String getMailCaption() {
		return _sMailCaption ;
	}
	public void setMailCaption(final String sMailCaption) {
		_sMailCaption = sMailCaption ;
	}
	
	public ArrayList<MailTo> getMailAddresses() {
		return _aMailAddresses ;
	}
	
	public void addMailAddress(MailTo address) {
		if (null == address)
			return ;
		
		_aMailAddresses.add(address) ;
	}
	
	/**
	 * Add an attribute à la <code>to="$trainee$"</code> into the list of MailTo
	 * 
	 * @param sAttributeName    Attribute name, either to, cc or bcc
	 * @param sAttributeContent Content in the form of roles separated by a ';'
	 */
	public void addMailsTo(final String sAttributeName, final String sAttributeContent) {
		FormPresenterModel.addMailsTo(_aMailAddresses, sAttributeName, sAttributeContent) ;
	}
	
	public void clearMailAddresses() {
		_aMailAddresses.clear() ;
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
