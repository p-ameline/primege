package com.primege.shared.rpc;

import java.util.ArrayList;

import com.primege.shared.database.FormDataData;
import com.primege.shared.model.FormBlock;
import com.primege.shared.model.TraitPath;

import net.customware.gwt.dispatch.shared.Action;

public class RegisterFormAnnotationAction implements Action<RegisterFormAnnotationResult> 
{	
	private int                     _iUserId ;
	private int                     _iMasterFormId ;
	private ArrayList<TraitPath>    _aTraits ;
	private String                  _sActionId ;
	private FormBlock<FormDataData> _annotationFormBlock ;
	
	/**
	 * Void constructor, needed for serialization purposes
	 */
	public RegisterFormAnnotationAction() 
	{
		super() ;
		
		_iUserId             = 0 ;
		_annotationFormBlock = null ;
		_iMasterFormId       = -1 ;
		_aTraits             = null ;
		_sActionId           = "" ;
	}
	
	/**
	 * Plain vanilla constructor
	 */
	public RegisterFormAnnotationAction(int iUserId, int iMasterFormId, final String sActionId, final FormBlock<FormDataData> annotationFormBlock, final ArrayList<TraitPath> aTraits) 
	{
		_iUserId             = iUserId ;
		_annotationFormBlock = annotationFormBlock ;
		_iMasterFormId       = iMasterFormId ;
		_aTraits             = aTraits ;
		_sActionId           = sActionId ;
	}

	public int getUserId() {
		return _iUserId;
	}
	public void setUserId(int iUserId) {
		_iUserId = iUserId ;
	}

	public int getMasterFormId() {
		return _iMasterFormId ;
	}
	public void setMasterFormId(int iMasterFormId) {
		_iMasterFormId = iMasterFormId ;
	}
	
	public String getActionId() {
		return _sActionId ;
	}
	public void setActionId(final String sActionId) {
		_sActionId = sActionId ;
	}
	
	public FormBlock<FormDataData> getAnnotationFormBlock() {
		return _annotationFormBlock ;
	}
	public void setAnnotationFormBlock(FormBlock<FormDataData> annotationFormBlock) {
		_annotationFormBlock = annotationFormBlock ;
	}
	
	public ArrayList<TraitPath> getTraits() {
		return _aTraits ;
	}
	public void setTraits(ArrayList<TraitPath> aTraits) {
		_aTraits = aTraits ;
	}
}
