package com.primege.server;

import com.google.inject.Inject;
import com.primege.server.handler.BuildCsvEngine;

public class BuildCsvMain
{
	@Inject
	public BuildCsvMain() {
	}
	
	public void main(String[] args)  
	{
		BuildCsvEngine cvsEngine = new BuildCsvEngine() ;
/*		
		String sError = cvsEngine.execute(1) ;
		
		if (sError.equals(""))
			System.out.println("Cvs correctement construit") ;
		else
			System.out.println(sError) ;
*/
	}	
}
