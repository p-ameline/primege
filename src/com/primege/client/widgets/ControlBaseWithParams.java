package com.primege.client.widgets;

/**
 * Base class of all controls operating in "Primege" forms and owning specific parameters 
 */
public class ControlBaseWithParams extends ControlBase
{
  /**
   * Default Constructor
   *
   */
  public ControlBaseWithParams(final String sPath) {
  	super(sPath) ;
  }

  /**
	 * Parse parameters, 
	 * 
	 * @param sParams parameters in the form "weight=3|semiFitDelay=001800|fitDelay=002400"
	 */
	public void parseParams(final String sParams)
	{
		if ((null == sParams) || "".equals(sParams))
			return ;
		
		String[] params = sParams.split("\\|") ;
		
		int iParamsCount = params.length ;
		for (int i = 0 ; i < iParamsCount ; i++)
		{
			String[] blocks = params[i].split("\\=") ;
			if (blocks.length == 2)
				fillParam(blocks[0], blocks[1]) ;
		}
	}
	
	/**
	 * Initialize a parameter from its name
	 * Nothing here for the generic class, must be defined by derived components
	 * 
	 * @param sParam Name of parameter to initialize
	 * @param sValue Value to initialize this parameter with (can be null to "default value")
	 */
	public void fillParam(final String sParam, final String sValue)
	{
		if ((null == sParam) || "".equals(sParam))
			return ;
		
		// Mandatory control?
		if      ("mandatory".equalsIgnoreCase(sParam))
		{
			if ((null == sValue) || "".equals(sValue))
				_bMandatory = true ;
			else
				_bMandatory = isTrue(sValue) ;
		}
		else if ("default".equalsIgnoreCase(sParam))
		{
			if ((null == sValue) || "".equals(sValue))
				_sDefaultValue = "" ;
			else
				_sDefaultValue = sValue ;
		}
	}
}
