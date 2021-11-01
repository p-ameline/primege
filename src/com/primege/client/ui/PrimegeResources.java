package com.primege.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.resources.client.ImageResource;

/**
 * An object that implements this interface may have validation components.
 *
 * @author lowec
 *
 */
public interface PrimegeResources extends ClientBundle 
{
	public static final PrimegeResources INSTANCE =  GWT.create(PrimegeResources.class) ;

	@NotStrict
  @Source("Primege.css")
  public CssResource css() ;

	@Source("logo_Carlsberg.png")
	public ImageResource welcomeImg() ;
	
/*
  @Source("config.xml")
  public TextResource initialConfiguration();

  @Source("manual.pdf")
  public DataResource ownersManual();
*/
}
