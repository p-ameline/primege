package com.primege.client.util;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.Widget;
import com.primege.client.widgets.ControlBase;
import com.primege.shared.database.FormDataData;

/**
 * Referencing a control inside a form
 * 
 */
public class FormControl
{   
	protected String                  _sPath ;

	protected FormDataData            _content ;
	protected ArrayList<FormDataData> _aMultipleContent ;

	protected Widget                  _widget ;
	protected ControlBase             _base ;

	protected boolean                 _bProvidesInformation ;
	protected boolean                 _bMandatory ;

	protected ArrayList<String>       _aExclusions = new ArrayList<String>() ;
	protected ArrayList<String>       _aExclusionsFromOptions = new ArrayList<String>() ;

	/**
	 * Default Constructor
	 */
	public FormControl(final ControlBase base, final Widget widget, final FormDataData content, final String sExclusion) {
		initialize(base, widget, content, true, base.isMandatory(), sExclusion) ;
	}

	/**
	 * Default Constructor for multiple data controls
	 */
	public FormControl(final ControlBase base, final Widget widget, final ArrayList<FormDataData> aMultipleContent, final String sExclusion) {
		initializeForMultiple(base, widget, aMultipleContent, true, base.isMandatory(), sExclusion) ;
	}

	/**
	 * Constructor to be used when a control is not an information provider (for example a button)
	 */
	public FormControl(final ControlBase base, final Widget widget, final FormDataData content, boolean bProvidesInformation, boolean bMandatory) {
		initialize(base, widget, content, bProvidesInformation, bMandatory, "") ;
	}

	/**
	 * Constructor for exotic controls
	 */
	public FormControl(final Widget widget, final FormDataData content, final String sPath, final String sExclusion)
	{
		initializeCore(null, widget, true, true, sExclusion) ;

		_content          = content ;
		_aMultipleContent = null ;
		
		_sPath = sPath ;
	}

	/**
	 * Initialize non-exotic controls
	 */
	protected void initialize(final ControlBase base, final Widget widget, final FormDataData content, boolean bProvidesInformation, boolean bMandatory, final String sExclusion)
	{
		initializeCore(base, widget, bProvidesInformation, bMandatory, sExclusion) ;

		_content          = content ;
		_aMultipleContent = null ;
	}

	/**
	 * Initialize non-exotic controls with multiple content
	 */
	protected void initializeForMultiple(final ControlBase base, final Widget widget, final ArrayList<FormDataData> aMultipleContent, boolean bProvidesInformation, boolean bMandatory, final String sExclusion)
	{
		initializeCore(base, widget, bProvidesInformation, bMandatory, sExclusion) ;

		_content          = null ;
		_aMultipleContent = aMultipleContent ;
	}

	/**
	 * Initialize core information (everything but content)
	 */
	protected void initializeCore(final ControlBase base, final Widget widget, boolean bProvidesInformation, boolean bMandatory, final String sExclusion)
	{
		_widget  = widget ;
		_base    = base ;

		if (null != _base)
			_base.setFormControl(this) ;

		_sPath = "" ;
		if (null != _base)
			_sPath = _base.getPath() ;

		_bProvidesInformation = bProvidesInformation ;
		_bMandatory           = bMandatory ;

		parseExclusion(sExclusion) ;
	}

	/**
	 * Parse the exclusion list
	 * 
	 * @param sExclusion exclusion paths separated by a '|'
	 */
	public void parseExclusion(final String sExclusion) {
		parseExclusionForList(sExclusion, _aExclusions) ;
	}
	
	/**
	 * Parse the exclusion list to fill the list of exclusions from the selected option
	 * 
	 * @param sExclusion exclusion paths separated by a '|'
	 */
	public void parseExclusionFromOptions(final String sExclusion) {
		parseExclusionForList(sExclusion, _aExclusionsFromOptions) ;
	}
	
	/**
	 * Parse the exclusion list to fill an exclusion strings list
	 * 
	 * @param sExclusion  exclusion paths separated by a '|'
	 * @param aExclusions array to be filled
	 */
	public void parseExclusionForList(final String sExclusion, ArrayList<String> aExclusions)
	{
		aExclusions.clear() ;
		
		if ((null == sExclusion) || "".equals(sExclusion))
			return ;

		String[] params = sExclusion.split("\\|") ;

		int iParamsCount = params.length ;
		for (int i = 0 ; i < iParamsCount ; i++)
			aExclusions.add(params[i]) ;
	}

	public ControlBase getControlBase() {
		return _base ;
	}

	public String getPath() {
		return _sPath ;
	}

	public FormDataData getContent() {
		return _content ;
	}

	public ArrayList<FormDataData> getMultipleContent() {
		return _aMultipleContent ;
	}

	public Widget getWidget() {
		return _widget ;
	}

	public boolean isInformationProvider() {
		return _bProvidesInformation ;
	}

	public boolean isMandatory() {
		return _bMandatory ;
	}

	public boolean isMultiple() {
		return (null != _aMultipleContent) ;
	}

	public ArrayList<String> getExclusions() {
		return _aExclusions ;
	}
	
	public ArrayList<String> getExclusionsFromOptions() {
		return _aExclusionsFromOptions ;
	}

	public boolean isPathInExclusions(final String sPath)
	{
		if ((null == sPath) || sPath.isEmpty() || (_aExclusions.isEmpty() && _aExclusionsFromOptions.isEmpty()))
			return false ;

		for (String sExclusionPath : _aExclusions)
			if (sPath.equals(sExclusionPath))
				return true ;
		
		for (String sExclusionPath : _aExclusionsFromOptions)
			if (sPath.equals(sExclusionPath))
				return true ;

		return false ;
	}
}
