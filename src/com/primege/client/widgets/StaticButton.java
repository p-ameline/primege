package com.primege.client.widgets;

import com.google.gwt.user.client.ui.Button;

import com.primege.shared.database.FormDataData;

/**
 * A button that is a fake ControlModel since it doesn't control any information
 * 
 */
public class StaticButton extends Button implements ControlModel
{
	protected ControlBase _base ;

	/**
	 * Default Constructor
	 *
	 */
	public StaticButton(final String sCaption, final String sPath)
	{
		super(sCaption) ;

		_base = new ControlBase(sPath) ;
	}

	public boolean isSingleData() {
  	return true ;
  }
	
	/**
	 * Get content as a path made of global path + activated button path
	 *
	 */
	public FormDataData getContent() {
		return null ;
	}

	/**
	 * Initialize from a content 
	 */
	public void setContent(FormDataData content, final String sDefaultValue) {
	}

	/**
	 * Initialize from a content 
	 */
	public void setContent(FormDataData content) {
	}

	public void resetContent() {
	}

	public ControlBase getControlBase() {
		return _base ;
	}

	public void setInitFromPrev(final boolean bInitFromPrev) {
		_base.setInitFromPrev(bInitFromPrev) ;
	}

	public boolean getInitFromPrev() {
		return _base.getInitFromPrev() ;
	}
}
