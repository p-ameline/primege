package com.primege.client.widgets;

import java.util.ArrayList;

import com.primege.shared.database.FormDataData;

/**
 * Methods all multiple form data controls must implement
 */
public interface ControlModelMulti extends ControlModelCore
{
	public void                    setMultipleContent(final ArrayList<FormDataData> aContent, final String sDefaultValue) ;
	public void                    setMultipleContent(final ArrayList<FormDataData> aContent) ;
	public ArrayList<FormDataData> getMultipleContent() ;
	
	public void         resetContent() ;
}
