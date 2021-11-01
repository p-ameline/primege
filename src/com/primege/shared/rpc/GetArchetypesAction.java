package com.primege.shared.rpc;

import java.util.Iterator;
import java.util.Vector;

import com.primege.shared.database.ArchetypeData;

import net.customware.gwt.dispatch.shared.Action;

public class GetArchetypesAction implements Action<GetArchetypesResult> 
{	
	private int                   _iUserId ;
	private Vector<ArchetypeData> _aArchetypes = new Vector<ArchetypeData>() ;
	
	public GetArchetypesAction() 
	{
		super() ;
		
		_iUserId = -1 ;
	}
	
	public GetArchetypesAction(final int iUserId, final Vector<ArchetypeData> aArchetypes) 
	{
		_iUserId = iUserId ;
		initFromArchetypes(aArchetypes) ;
	}

	public int getUserId() {
		return _iUserId;
	}
	public void setUserId(int iUserId) {
		_iUserId = iUserId ;
	}

	public Vector<ArchetypeData> getArchetypes() {
		return _aArchetypes ;
	}
	public void initFromArchetypes(Vector<ArchetypeData> aArchetypes)
	{
		_aArchetypes.clear() ;
		
		if ((null == aArchetypes) || aArchetypes.isEmpty())
			return ;
		
		for (Iterator<ArchetypeData> it = aArchetypes.iterator() ; it.hasNext() ; )
			_aArchetypes.add(new ArchetypeData(it.next())) ;
	}	
}
