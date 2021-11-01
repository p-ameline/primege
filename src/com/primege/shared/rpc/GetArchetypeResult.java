package com.primege.shared.rpc;

import com.primege.shared.database.ArchetypeData;

import net.customware.gwt.dispatch.shared.Result;

public class GetArchetypeResult implements Result 
{
	private boolean  _bSuccess ;
	private String   _sArchetype ;
	private String   _sMessage ;
	
	private ArchetypeData _archetype ;

	/**
	 * Plain vanilla constructor 
	 */
	public GetArchetypeResult(final boolean bSuccess, final String sArchetype, final ArchetypeData archetype, final String sMessage) 
	{
		_bSuccess   = bSuccess ;
		_sMessage   = sMessage ;
		_sArchetype = sArchetype ;
		_archetype  = archetype ;
	}

	/**
	 * Default constructor (with zero information)
	 */
	public GetArchetypeResult() 
	{
		_bSuccess   = false ;
		_sArchetype = "" ;
		_sMessage   = "" ;
		_archetype  = null ;
	}

	public boolean wasSuccessful() {
		return _bSuccess ;
	}
	
	public String getArchetype() {
		return _sArchetype ;
	}
	
	public ArchetypeData getArchetypeData() {
		return _archetype ;
	}

	public String getMessage() {
		return _sMessage ;
	}
}
