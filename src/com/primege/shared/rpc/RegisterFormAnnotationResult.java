package com.primege.shared.rpc;

import java.util.ArrayList;

import com.primege.shared.database.FormLink;

import net.customware.gwt.dispatch.shared.Result;

public class RegisterFormAnnotationResult implements Result 
{
	private int     _iRecordedId ;
	private String  _sActionId ;
	private boolean _bNewAnnotation ;
	private String  _sMessage ;
	
	private ArrayList<FormLink> _aNewLinks ;
	
	public RegisterFormAnnotationResult()
	{
		super() ;
		
		_iRecordedId    = -1 ;
		_sActionId      = "" ;
		_bNewAnnotation = false ;
		_sMessage       = "" ;
		_aNewLinks      = null ;
	}
	
	public RegisterFormAnnotationResult(int iRecordedId, final String sActionId, boolean bNewAnnotation, String sMessage, ArrayList<FormLink> aNewLinks)
	{
		super() ;
		
		_iRecordedId    = iRecordedId ;
		_sActionId      = sActionId ;
		_bNewAnnotation = bNewAnnotation ;
		_sMessage       = sMessage ;
		_aNewLinks      = aNewLinks ;
	}

	public int getRecordedId() {
  	return _iRecordedId ;
  }
	public void setRecordedId(int iRecordedId) {
  	_iRecordedId = iRecordedId ;
  }
	
	public String getActionId() {
  	return _sActionId ;
  }
	public void setActionId(final String sActionId) {
		_sActionId = sActionId ;
  }
	
	public boolean isNewAnnotation() {
  	return _bNewAnnotation ;
  }
	public void setNewAnnotation(boolean bNewAnnotation) {
		_bNewAnnotation = bNewAnnotation ;
  }
	
	public String getMessage() {
  	return _sMessage ;
  }
	public void setMessage(final String sMessage) {
  	_sMessage = sMessage ;
  }
	
	public ArrayList<FormLink> getNewLinks() {
  	return _aNewLinks ;
  }
	public void setNewLinks(ArrayList<FormLink> aNewLinks) {
		_aNewLinks = aNewLinks ;
  }
}
