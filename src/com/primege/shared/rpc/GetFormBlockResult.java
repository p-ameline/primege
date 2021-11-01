package com.primege.shared.rpc;

import com.primege.shared.database.FormDataData;
import com.primege.shared.model.FormBlock;

import net.customware.gwt.dispatch.shared.Result;

public class GetFormBlockResult implements Result 
{
	private FormBlock<FormDataData> _formBlock = new FormBlock<FormDataData>() ;
	private String                  _sMessage ;
	
	public GetFormBlockResult()
	{
		super() ;
		
		_sMessage = "" ;
	}
	
	public GetFormBlockResult(String sMessage) 
	{
		super() ;
		
		_sMessage = sMessage ;
	}

	public FormBlock<FormDataData> getFormBlock() {
  	return _formBlock ;
  }
	public void setFormBlock(FormBlock<FormDataData> formBlock) {
		_formBlock.initFromFormBlock(formBlock) ;
  }

	public String getMessage() {
  	return _sMessage ;
  }
	public void setMessage(String sMessage) {
  	_sMessage = sMessage ;
  }
}
