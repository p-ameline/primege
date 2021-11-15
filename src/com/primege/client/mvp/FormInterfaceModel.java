package com.primege.client.mvp;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;
import com.primege.client.util.FormControl;
import com.primege.client.util.FormControlOptionData;
import com.primege.client.widgets.FormBlockInformation;
import com.primege.client.widgets.FormBlockPanel;
import com.primege.shared.database.FormDataData;

public interface FormInterfaceModel extends PrimegeBaseInterface
{
	void                   resetAll() ;
		
	HasChangeHandlers      getDateChanged() ;
		
	void                   setDefaultValues() ;
				
	Button                 getSubmitButton() ;
	Button                 getSubmitDraftButton() ;
	void                   setSubmitDraftButtonVisible(final boolean bVisible) ;
	HasClickHandlers       getReset() ;
			
	void                   popupWarningMessage(final String sMessage) ;
	void                   closeWarningDialog() ;
	HasClickHandlers       getWarningOk() ;
		
	void                   showWaitCursor() ;
	void                   showDefaultCursor() ;
		
	void                   popupDeleteConfirmationMessage(boolean bIsBastket) ;
	void                   closeDeleteConfirmationDialog() ;
	HasClickHandlers       getDeleteConfirmationOk() ;
	HasClickHandlers       getDeleteConfirmationCancel() ;	
	
	/** Get the {@link FormBlockPanel} in charge of the form */
	FormBlockPanel         getMasterForm() ;
	void                   insertNewBlock(final FormBlockInformation presentation, FormBlockPanel masterBlock) ;
	// void                updateBlockCaption(final String sType, final String sCaption) ;
	void                   endOfBlock(final boolean bInPdfWhenEmpty, FormBlockPanel masterBlock) ;
	void                   insertNewControl(final String sControlPath, final ArrayList<FormDataData> content, final String sControlCaption, final String sControlType, final String sControlSubtype, final String sControlUnit, final String sControlValue, final ArrayList<FormControlOptionData> aOptions, final String sControlStyle, final String sCaptionStyle, final boolean bInitFromPrev, final String sExclusion, final boolean bInBlockCell, final boolean bInPdfWhenEmpty, FormBlockPanel masterBlock, boolean bEdited) ;
		
	boolean                getContent(ArrayList<FormDataData> aInformation, FormBlockPanel masterBlock) ;
	FormDataData           getContentForPath(final String sPath, FormBlockPanel masterBlock) ;
	
	HasClickHandlers       getInitFromPreviousButton() ;
	
	ArrayList<FormControl> getControls(FormBlockPanel masterBlock) ;
	Widget                 getControlForPath(final String sPath, FormBlockPanel masterBlock) ;
	FormControl            getPlainControlForPath(final String sControlPath, FormBlockPanel masterBlock) ;
	void                   emptyContentForPath(final String sPath, FormBlockPanel masterBlock) ;
	
	void                   setScreenShotMode(final boolean bScreenShotMode) ;
	boolean                isScreenShotMode() ;
	
	// Actions/Annotations management
	//
	public FormBlockPanel  getNewActionBlock(final String sCaption, final int iAnnotationID, final String sActionId, ClickHandler actionClickHandler) ;
	/**
   * Get the {@link FormBlockPanel} for a given annotation identifier
   * @param iAnnotationID Annotation identifier to look for
   * @param sActionId     Action identifier, only used when annotation identifier is <code>-1</code>
   * @return The {@link FormBlockPanel} if found, <code>null</code> if not
   */
	public FormBlockPanel  getActionFromAnnotationID(final int iAnnotationID, final String sActionId) ;
	public void            initializeActionControls() ;
	public void            initializeActionHistory() ;
	public void            addNewActionButton(final String sCaption, ClickHandler handler, final String sActionId) ;
	public String          getNewAnnotationID(Widget sender) ;
	public String          getActionButtonType(Widget actionButton) ;
	public int             getActionButtonFormID(Widget actionButton) ;
	public String          getActionButtonActionID(Widget actionButton) ;
}
