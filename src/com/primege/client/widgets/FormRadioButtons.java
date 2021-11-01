package com.primege.client.widgets;

import java.util.ArrayList;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;
import com.primege.client.util.FormControl;
import com.primege.client.util.FormControlOption;
import com.primege.client.util.FormControlOptionData;
import com.primege.shared.database.FormDataData;

/**
 * Set of exclusive radio buttons
 */
public class FormRadioButtons extends FlowPanel implements ControlModel, HasChangeHandlers
{
	protected ControlBaseWithParams            _base ;
	protected ArrayList<FormControlOption>     _aButtons = new ArrayList<FormControlOption>() ;
	protected ArrayList<FormControlOptionData> _aOptions ;
	protected Label                            _popupText ;

	/**
	 * Default Constructor
	 *
	 */
	public FormRadioButtons(final ArrayList<FormControlOptionData> aOptions, final String sPath)
	{
		super() ;

		_base = new ControlBaseWithParams(sPath) ;

		_aOptions = aOptions ;

		init(aOptions) ;

		_popupText = new Label("") ;
		_popupText.addStyleName("controlPopupText") ;
		add(_popupText) ;
	}

	public boolean isSingleData() {
		return true ;
	}

	/**
	 * Create the RadioButton controls
	 *
	 */
	public void init(final ArrayList<FormControlOptionData> aOptions)
	{
		if ((null == aOptions) || aOptions.isEmpty())
			return ;

		for (FormControlOptionData optionData : aOptions)
		{
			// The path is used as group name
			//
			RadioButton newButton = new RadioButton(_base.getPath(), optionData.getCaption()) ;

			newButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					updateForValueChange() ;
				}
			});

			add(newButton) ;
			_aButtons.add(new FormControlOption(optionData.getPath(), newButton, optionData.getCaption())) ;
		}
	}
	
	/**
	 * A button changed value; make necessary adjustments
	 */
	protected void updateForValueChange()
	{
		updatePopup() ;
		
		FormControl formControl = _base.getFormControl() ;
		if (null != formControl)
			formControl.parseExclusionFromOptions(getExclusionsFromOption()) ;
	}

	/**
	 * Get content as a path made of global path + activated button path
	 *
	 */
	public FormDataData getContent()
	{
		if (_aButtons.isEmpty())
			return null ;

		for (FormControlOption option : _aButtons)
		{
			Widget widget = option.getWidget() ;
			if (null != widget)
			{
				RadioButton radioButton = (RadioButton) widget ;
				if (radioButton.getValue())
				{
					FormDataData formData = new FormDataData() ;
					formData.setPath(_base.getPath() + "/" + option.getPath()) ;
					return formData ;
				}
			}
		}

		return null ;
	}

	/**
	 * Initialize RadioButtons from a content and a default value
	 *
	 * @param content       FormDataData used to initialize the control
	 * @param sDefaultValue Configuration parameters, including default value in case there is no content
	 */
	public void setContent(final FormDataData content, final String sDefaultValue) 
	{
		_base.parseParams(sDefaultValue) ;

		setContent(content) ;
	}

	/**
	 * Initialize RadioButtons from a content
	 *
	 * @param content FormDataData used to initialize the control
	 */
	public void setContent(final FormDataData content)
	{
		if (_aButtons.isEmpty())
			return ;

		String sInitPath = "" ;
		if (null == content)
			sInitPath = _base.getDefaultValue() ;
		else
			sInitPath = content.getPath() ;

		for (FormControlOption option : _aButtons)
		{
			RadioButton radioButton = (RadioButton) option.getWidget() ;

			if ("".equals(sInitPath))
				radioButton.setValue(false) ;
			else
			{
				String sLocalPath = _base.getPath() + "/" + option.getPath() ;
				radioButton.setValue(sInitPath.equals(sLocalPath)) ;
			}
		}

		updatePopup() ;
	}

	public void resetContent()
	{
		for (FormControlOption option : _aButtons)
		{
			RadioButton radioButton = (RadioButton) option.getWidget() ;
			radioButton.setValue(false) ;
		}

		updatePopup() ;
	}

	/**
	 * Get informed when state changes
	 */
	protected class stateChangeHandler implements ValueChangeHandler<Boolean> 
	{
		public stateChangeHandler() 
		{
		}

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> valueChangeEvent)
		{
			NativeEvent event = Document.get().createChangeEvent();
			DomEvent.fireNativeEvent(event, (HasHandlers) this) ;
		}
	}

	void installChangeHandler()
	{
		for (FormControlOption option : _aButtons)
		{
			RadioButton radio = (RadioButton) option.getWidget() ;
			radio.addValueChangeHandler(new stateChangeHandler()) ;
		}
	}

	@Override
	public HandlerRegistration addChangeHandler(ChangeHandler handler)
	{
		// _changeHandler = handler ;
		// return null ;

		return addDomHandler(handler, ChangeEvent.getType());
	}

	/**
	 * Update the popup text
	 */
	protected void updatePopup() {
		_popupText.setText(getPopupText()) ;
	}

	/**
	 * Get the popup text of selected button
	 * 
	 * @return A text or <code>""</code> if no selection or selected button has no popup
	 */
	protected String getPopupText()
	{
		FormControlOptionData option = getSelectedOption() ;
		if (null == option)
			return "" ;

		return option.getPopupText() ;
	}
	
	/**
	 * Get the popup text of selected button
	 * 
	 * @return A text or <code>""</code> if no selection or selected button has no popup
	 */
	protected String getExclusionsFromOption()
	{
		FormControlOptionData option = getSelectedOption() ;
		if (null == option)
			return "" ;

		return option.getExclusion() ;
	}

	/**
	 * Get the {@link FormControlOptionData} of (the first) currently selected {@link RadioButton}
	 * 
	 * @return A {@link FormControlOptionData} if a button is selected and has an option, <code>null</code> if not
	 */
	protected FormControlOptionData getSelectedOption()
	{
		String sSelectedPath = getSelectedPath() ;
		if (sSelectedPath.isEmpty())
			return null ;

		return getOptionForPath(sSelectedPath) ;
	}

	/**
	 * Get the path of (the first) currently selected {@link RadioButton}
	 * 
	 * @return <code>""</code> if none is selected or the path of the selected one
	 */
	protected String getSelectedPath()
	{
		for (FormControlOption option : _aButtons)
		{
			RadioButton radioButton = (RadioButton) option.getWidget() ;
			if ((null != radioButton) && radioButton.getValue())
				return option.getPath() ;
		}

		return "" ;
	}

	/**
	 * Get the {@link FormControlOptionData} for a given path
	 * 
	 * @param sPath Path to look for
	 * 
	 * @return A {@link FormControlOptionData} if found, <code>null</code> if not
	 */
	public FormControlOptionData getOptionForPath(final String sPath)
	{
		if ((null == sPath) || sPath.isEmpty())
			return null ;

		for (FormControlOptionData option : _aOptions)
			if (sPath.equals(option.getPath()))
				return option ;

		return null ;
	}

	public ControlBase getControlBase() {
		return _base ;
	}

	public void setInitFromPrev(boolean bInitFromPrev) {
		_base.setInitFromPrev(bInitFromPrev) ;
	}

	public boolean getInitFromPrev() {
		return _base.getInitFromPrev() ;
	}
}
