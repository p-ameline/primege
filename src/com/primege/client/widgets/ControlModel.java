package com.primege.client.widgets;

import com.primege.shared.database.FormDataData;

/**
 * Methods all single form data control must implement
 */
public interface ControlModel extends ControlModelCore
{
	public void         setContent(final FormDataData content, final String sDefaultValue) ;
	public void         setContent(final FormDataData content) ;
	public FormDataData getContent() ;
	
	public void         resetContent() ;
}
