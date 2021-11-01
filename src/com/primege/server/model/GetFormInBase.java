package com.primege.server.model;

import java.util.ArrayList;

import com.primege.server.DBConnector;
import com.primege.server.Logger;
import com.primege.shared.database.FormData;
import com.primege.shared.database.FormDataData;
import com.primege.shared.database.FormLink;
import com.primege.shared.model.FormBlock;

/**
 * Object in charge of loading a {@link FormBlock} from database
 * 
 * @author Philippe
 *
 */
public class GetFormInBase  
{	
	protected final DBConnector _dbConnector ;
	protected final int         _iUserId ;
	
	public GetFormInBase(int iUserId, final DBConnector dbConnector) 
	{
		_dbConnector = dbConnector ;
		_iUserId     = iUserId ;
	}
	
	/** 
	 * Fill a FormBlock with a form and its information from a given form ID 
	 * 
	 * @param  iFormId form's unique identifier
	 * @param  form    FormBlock to be completed by information from database
	 * 
	 * @return <code>true</code> if all went well, <code>false</code> if not   
	 */
	public boolean GetForm(int iFormId, FormBlock<FormDataData> form) 
	{
		String sFctName = "GetFormInBase.GetForm" ;
		
		if (null == form)
		{
			Logger.trace(sFctName + ": bad parameter", _iUserId, Logger.TraceLevel.ERROR) ;
			return false ;
		}
		
		Logger.trace(sFctName + ": get information from database for form " + iFormId, _iUserId, Logger.TraceLevel.STEP) ;
		
		// If not already loaded, get the form description
		//
		if ((null == form.getDocumentLabel()) || (form.getDocumentLabel().isEmpty()))
			loadDocument(iFormId, form) ;

		// If not already loaded, get form's data
		//
		if ((null == form.getInformation()) || (form.getInformation().isEmpty()))
			loadFormData(iFormId, form) ;

		// If not already loaded, get annotations
		//
		// if (form.getAnnotations().isEmpty())
		//	loadAnnotations(iFormId, form) ;
		
		// If not already loaded, get annotations
		//
		if (form.getLinks().isEmpty())
			loadLinks(iFormId, form) ;
		
		buildDocumentLabel(form) ;
		
		return true ;
	}		
	
	/** 
	 * Get information from table form for a given form 
	 * 
	 * @param  iFormId form's unique identifier
	 * @param  form    FormBlock to be completed by information from database
	 * 
	 * @return <code>true</code> if all went well, <code>false</code> if not   
	 */
	public boolean loadDocument(int iFormId, FormBlock<FormDataData> form) 
	{
		FormDataManager formDataManager = new FormDataManager(_iUserId, _dbConnector) ;
		
		FormData document = new FormData() ;
		if (false == formDataManager.existData(iFormId, document))
			return false ;
		
		form.setDocumentLabel(document) ;
		
		return true ;
	}
	
	/** 
	 * Get information from table formData for a given form
	 * 
	 * @param  iFormId form's unique identifier
	 * @param  form    FormBlock to be completed by information from database
	 * 
	 * @return <code>true</code> if all went well, <code>false</code> if not
	 */
	public boolean loadFormData(int iFormId, FormBlock<FormDataData> form) 
	{
		if (null == form)
			return false ;
		
		FormInformationManager formInformationManager = new FormInformationManager(_iUserId, _dbConnector, FormInformationManager.InformationType.form) ;
		
		return formInformationManager.loadFormData(iFormId, form) ; 
	}
	
	/** 
	 * Load annotations for a given form
	 * 
	 * @param  iFormId form's unique identifier
	 * @param  form    FormBlock to be completed by information from database
	 * 
	 * @return <code>true</code> if all went well, <code>false</code> if not
	 */
/*
	public boolean loadAnnotations(int iFormId, FormBlock<FormDataData> form) 
	{
		if (null == form)
			return false ;
		
		// Get annotation's descriptors
		//
		ArrayList<FormAnnotation> aAnnotations = new ArrayList<FormAnnotation>() ;
		
		FormAnnotationManager formAnnotationManager = new FormAnnotationManager(_iUserId, _dbConnector) ;
		if (false == formAnnotationManager.getAnnotationsForForm(iFormId, aAnnotations))
			return false ;
		
		// Load data for each annotation
		//
		for (FormAnnotation annotation : aAnnotations)
		{
			FormAnnotationBlock<FormDataData> annotationBlock = new FormAnnotationBlock<FormDataData>() ;
			annotationBlock.setAnnotation(annotation) ;
			
			FormInformationManager formInformationManager = new FormInformationManager(_iUserId, _dbConnector, FormInformationManager.InformationType.annotation) ;
			if (formInformationManager.loadFormData(annotation.getAnnotationId(), annotationBlock))
				form.addAnnotation(annotationBlock) ;
		}
		
		return true ;
	}
*/
	/** 
	 * Load links for a given form
	 * 
	 * @param  iFormId form's unique identifier
	 * @param  form    FormBlock to be completed by information from database
	 * 
	 * @return <code>true</code> if all went well, <code>false</code> if not
	 */
	public boolean loadLinks(int iFormId, FormBlock<FormDataData> form) 
	{
		if (null == form)
			return false ;
		
		// Get annotation's descriptors
		//
		ArrayList<FormLink> aLinks = new ArrayList<FormLink>() ;
		
		FormLinkManager formLinksManager = new FormLinkManager(_iUserId, _dbConnector) ;
		if (false == formLinksManager.getLinksForFormAsSubject(iFormId, aLinks))
			return false ;
		
		form.setLinks(aLinks) ;
		
		return true ;
	}
	
	/** 
	 * Get information from table contact for a given message 
	 * 
	 * @param    iMessageId message unique identifier
	 * @param    encounter EncounterBlock to be completed by contact information
	 * 
	 * @return   void  
	 */
	public boolean buildDocumentLabel(FormBlock<FormDataData> form) 
	{		
		return true ;
	}
}
