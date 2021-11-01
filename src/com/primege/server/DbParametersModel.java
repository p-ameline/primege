package com.primege.server;

public class DbParametersModel 
{	
	protected static String _sTrace ;
	protected static String _sBase ;
	protected static String _sUser ;
	protected static String _sPass ;
	protected static String _sCSV ;
	protected static String _sCSV_daily ;
	protected static String _sArchetypesDir ;
	protected static String _sVersion ;
	
	public DbParametersModel(final String sTrace, final String sBase, final String sUser, final String sPass, final String sCSV, final String sCSV_daily, final String sArchetypesDir, final String sVersion)
	{
		_sTrace         = sTrace ;
		_sBase          = sBase ;
		_sUser          = sUser ;
		_sPass          = sPass ;
		_sCSV           = sCSV ;
		_sCSV_daily     = sCSV_daily ;
		_sArchetypesDir = sArchetypesDir ;
		_sVersion       = sVersion ;
	}	
	
	public static String getTrace() {
		return _sTrace ;
	}
	
	public static String getBase() {
		return _sBase ;
	}
	
	public static String getUser() {
		return _sUser ;
	}
	
	public static String getPass() {
		return _sPass ;
	}
	
	public static String getCSV() {
		return _sCSV ;
	}
	
	public static String getDailyCSV() {
		return _sCSV_daily ;
	}
	
	public static String getArchetypeDir() {
		return _sArchetypesDir ;
	} 
	
	public static String getVersion() {
		return _sVersion ;
	}
}
