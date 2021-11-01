package com.primege.shared.rpc;

import java.util.ArrayList;

import com.primege.shared.database.FormDataModel;

import net.customware.gwt.dispatch.shared.Result;

/**
 * Object that return information from a query for a set of forms
 * 
 * Created: 16 May 2016
 * Author: PA
 * 
 */
public class GetFormsResult implements Result 
{
	private ArrayList<FormDataModel> _aForms = new ArrayList<FormDataModel>() ;
	private String                   _sMessage ;
	
	public GetFormsResult()
	{
		super() ;
		_sMessage = "" ;
	}
	
	public GetFormsResult(final String sMessage) {
		_sMessage = sMessage ;
	}

	public ArrayList<FormDataModel> getForms() {
  	return _aForms ;
  }
	
	/**
	 * Add a new form to the list
	 * 
	 * @param formData FormData to add to the list of returned objects
	 */
	public void addFormData(FormDataModel formData)
  {
		if (null == formData)
			return ;
		
		_aForms.add(formData) ;
  }

	public String getMessage() {
  	return _sMessage ;
  }
	public void setMessage(final String sMessage) {
  	_sMessage = sMessage ;
  }
}
