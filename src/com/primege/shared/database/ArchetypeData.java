package com.primege.shared.database ;

import com.google.gwt.user.client.rpc.IsSerializable ;
import com.primege.shared.GlobalParameters;

/**
 * An ArchetypeData object represents a form model
 * 
 * Created: 17 May 2016
 * Author: PA
 * 
 */
public class ArchetypeData implements IsSerializable 
{
	private int    _id ;
	private String _sLabel ;
	private String _sFile ;
	private String _sType ;
	private String _sRoot ;

	/**
	 * Default constructor (with zero information)
	 */
	public ArchetypeData() {
		reset() ;
	}

	/**
	 * Plain vanilla constructor 
	 */
	public ArchetypeData(int iId, final String sLabel, final String sFile, final String sType, final String sRoot) 
	{
		_id     = iId ;
		_sLabel = sLabel ;
		_sFile  = sFile ;
		_sType  = sType ;
		_sRoot  = sRoot ;
	}

	/**
	 * Copy constructor
	 * 
	 * @param model ArchetypeData to initialize from 
	 */
	public ArchetypeData(ArchetypeData model)
	{
		reset() ;
		initFromArchetype(model) ;
	}

	/**
	 * Initialize all information from another ArchetypeData
	 * 
	 * @param model ArchetypeData to initialize from 
	 */
	public void initFromArchetype(ArchetypeData model)
	{
		if (null == model)
			return ;
		
		_id     = model._id ;
		_sLabel = model._sLabel ;
		_sFile  = model._sFile ;
		_sType  = model._sType ;
		_sRoot  = model._sRoot ;
	}

	/**
	 * Zeros all information
	 */
	public void reset() 
	{
		_id     = -1 ;
		_sLabel = "" ;
		_sFile  = "" ;
		_sType  = "" ;
		_sRoot  = "" ;
	}
	
	/**
	 * Check if this object has no initialized data
	 * 
	 * @return true if all data are zeros, false if not
	 */
	public boolean isEmpty()
	{
		if ((-1 == _id)          &&
				("".equals(_sLabel)) &&
				("".equals(_sFile))  &&
				("".equals(_sType))  &&
				("".equals(_sRoot)))
			return true ;
		
		return false ;
	}

	public int getId() {
		return _id ;
	}
	public void setId(int iId) {
		_id = iId ;
	}

	public String getLabel() {
		return _sLabel ;
	}
	public void setLabel(String sLabel) {
		_sLabel = (null == sLabel) ? "" : sLabel ;
	}

	public String getFile() {
		return _sFile ;
	}
	public void setFile(String sFile) {
		_sFile = (null == sFile) ? "" : sFile ;
	}
	
	public String getType() {
		return _sType ;
	}
	public void setType(String sType) {
		_sType = (null == sType) ? "" : sType ;
	}
	public boolean isForm() {
		return "F".equals(_sType) ;
	}
	public boolean isDashboard() {
		return "D".equals(_sType) ;
	}
	public boolean isCsv() {
		return "C".equals(_sType) ;
	}
	public boolean isStatic() {
		return "S".equals(_sType) ;
	}
	public boolean isInformation() {
		return "I".equals(_sType) ;
	}
	
	public void setAsForm() {
		_sType = "F" ;
	}
	public void setAsDashboard() {
		_sType = "D" ;
	}
	public void setAsCsv() {
		_sType = "C" ;
	}
	public void setAsStatic() {
		_sType = "S" ;
	}
	public void setAsInformation() {
		_sType = "I" ;
	}
	
	public String getRoot() {
		return _sRoot ;
	}
	public void setRoot(String sRoot) {
		_sRoot = (null == sRoot) ? "" : sRoot ;
	}
	
	/**
	  * Determine whether two ArchetypeData are exactly similar
	  * 
	  * @return true if all data are the same, false if not
	  * @param  otherData ArchetypeData to compare with
	  * 
	  */
	public boolean equals(ArchetypeData otherData)
	{
		if (this == otherData) {
			return true ;
		}
		if (null == otherData) {
			return false ;
		}
		
		return (_id  == otherData._id)  &&
		       GlobalParameters.areStringsEqual(_sLabel, otherData._sLabel) && 
		       GlobalParameters.areStringsEqual(_sFile,  otherData._sFile)  && 
		       GlobalParameters.areStringsEqual(_sType,  otherData._sType)  &&
		       GlobalParameters.areStringsEqual(_sRoot,  otherData._sRoot) ;
	}

	/**
	  * Determine whether this ArchetypeData is exactly similar to another object
	  * 
	  * @return true if all data are the same, false if not
	  * @param o Object to compare with
	  * 
	  */
	public boolean equals(Object o) 
	{
		if (this == o) {
			return true ;
		}
		if ((null == o) || (getClass() != o.getClass())) {
			return false ;
		}

		final ArchetypeData otherData = (ArchetypeData) o ;

		return equals(otherData) ;
	}
}
