package com.primege.shared.model ;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable ;

import com.primege.shared.database.FormDataData;
import com.primege.shared.database.FormDataModel;
import com.primege.shared.database.FormLink;

/**
 * A document, its data and its annotations
 * 
 * Created: 11 Jul 2011
 *
 * Author: PA
 * 
 */
public class FormBlock<T extends FormDataData> extends FormBlockModel<T> implements IsSerializable 
{
	protected FormDataModel _document ;
	
	protected ArrayList<FormLink> _aLinks = new ArrayList<FormLink>() ;
	// protected ArrayList<FormAnnotationBlock<T>> _aAnnotations = new ArrayList<FormAnnotationBlock<T>>() ;
	
	/**
	 * Default constructor (with zero information)
	 */
	public FormBlock() 
	{
		super() ;
		
		_document = null ;
	}

	/**
	 * Plain vanilla "no annotations" constructor 
	 */
	public FormBlock(final String sLabel, final FormDataModel document, final ArrayList<T> aData) 
	{
		super(sLabel, aData) ;
		
		_document = null ;
		
		setDocumentLabel(document) ;
	}
	
	/**
	 * Plain vanilla constructor 
	 */
	public FormBlock(final String sLabel, final FormDataModel document, final ArrayList<T> aData, final ArrayList<FormLink> aLinks /*, final ArrayList<FormAnnotationBlock<T>> aAnnotations*/) 
	{
		super(sLabel, aData) ;
		
		_document = null ;
		
		setDocumentLabel(document) ;
		setLinks(aLinks) ;
		// setAnnotations(aAnnotations) ;
	}
	
	/**
	 * Copy constructor 
	 */
	public FormBlock(final FormBlock<T> model) 
	{
		super() ;

		_document = null ;
		
		initFromFormBlock(model) ;
	}
	
	public void reset()
	{
		reset4Model() ;
		
		if (null != _document)
			_document.reset() ;
		
		_aLinks.clear() ;
		// _aAnnotations.clear() ;
	}
	
	public void initFromFormBlock(final FormBlock<T> model)
	{
		reset() ;
		
		if (null == model)
			return ;
		
		initFromFormBlock4Model(model) ;
		
		setDocumentLabel(model._document) ;
		setLinks(model._aLinks) ;
		// setAnnotations(model._aAnnotations) ;
	}
		
	public FormDataModel getDocumentLabel() {
		return _document ;
	}
	public void setDocumentLabel(final FormDataModel document) {
		_document = document ;
	}
	
	public ArrayList<FormLink> getLinks() {
		return _aLinks ;
	}
	public void setLinks(final ArrayList<FormLink> aLinks)
	{
		_aLinks.clear() ;
		
		if (null != aLinks)
			_aLinks = aLinks ;
	}
	
	/**
	 * Add a link
	 */
	public void addLink(FormLink link)
	{
		if (null == link)
      return ;
		
		_aLinks.add(link) ;
	}
	
/*
	public ArrayList<FormAnnotationBlock<T>> getAnnotations() {
		return _aAnnotations ;
	}
	public void setAnnotations(final ArrayList<FormAnnotationBlock<T>> aAnnotations)
	{
		_aAnnotations.clear() ;
		
		if (null != aAnnotations)
			_aAnnotations = aAnnotations ;
	}
*/
	
	/**
	 * Add a data, or update it if some information with the same path already exists
	 */
/*
	public void addAnnotation(FormAnnotationBlock<T> aAnnotation)
	{
		if (null == aAnnotation)
      return ;
		
		_aAnnotations.add(aAnnotation) ;
	}
*/
}
