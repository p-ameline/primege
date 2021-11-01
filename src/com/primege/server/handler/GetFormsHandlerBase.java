package com.primege.server.handler;

public class GetFormsHandlerBase
{	
	protected String            _sQueryWhere ;
		
	public GetFormsHandlerBase() {
		_sQueryWhere  = "" ;
	}
	
	protected void addToQueryWhere(String sField)
	{
		if ((null == sField) || "".equals(sField))
			return ;
		
		if (false == "".equals(_sQueryWhere))
			_sQueryWhere += " AND" ;
		
		_sQueryWhere += " " + sField + " = ?" ;
	}
	
	protected void addToQueryWhereMultipleOr(String sField, int iIdNb)
	{
		if ((null == sField) || "".equals(sField) || (iIdNb < 1))
			return ;
		
		if (false == "".equals(_sQueryWhere))
			_sQueryWhere += " AND" ;
		
		if (1 == iIdNb)
			_sQueryWhere += " " + sField + " = ?" ;
		else
		{
			_sQueryWhere += " (" + sField + " = ?" ;
			for (int i = 1 ; i < iIdNb ; i++)
				_sQueryWhere += " OR " + sField + " = ?" ;
			_sQueryWhere += ")" ;
		}
	}
	
	protected void addToQueryWhereForDateInterval(String sField, String sDateFrom, String sDateTo)
	{
		if ((null == sField) || "".equals(sField))
			return ;
		
		if (((null == sDateFrom) || "".equals(sDateFrom)) && ((null == sDateTo) || "".equals(sDateTo))) 
			return ;
		
		if (false == "".equals(_sQueryWhere))
			_sQueryWhere += " AND" ;
		
		boolean bNeedAnd = false ;
		
		if ((null != sDateFrom) && (false == "".equals(sDateFrom)))
		{
			_sQueryWhere += " " + sField + " >= ?" ;
			bNeedAnd = true ;
		}
					
		if ((null != sDateTo) && (false == "".equals(sDateTo)))
		{
			if (bNeedAnd)
				_sQueryWhere += " AND" ;
			
			_sQueryWhere += " " + sField + " <= ?" ;
		}
	}
}
