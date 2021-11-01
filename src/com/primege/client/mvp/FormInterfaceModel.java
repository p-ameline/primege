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
	void              resetAll() ;
		
	HasChangeHandlers getDateChanged() ;
		
	void              setDefaultValues() ;
				
	Button            getSubmitButton() ;
	Button            getSubmitDraftButton() ;
	void              setSubmitDraftButtonVisible(final boolean bVisible) ;
	HasClickHandlers  getReset() ;
			
	void              popupWarningMessage(final String sMessage) ;
	void              closeWarningDialog() ;
	HasClickHandlers  getWarningOk() ;
		
	void              showWaitCursor() ;
	void              showDefaultCursor() ;
		
	void              popupDeleteConfirmationMessage(boolean bIsBastket) ;
	void              closeDeleteConfirmationDialog() ;
	HasClickHandlers  getDeleteConfirmationOk() ;
	HasClickHandlers  getDeleteConfirmationCancel() ;	
		
	FormBlockPanel    getMasterForm() ;
	void              insertNewBlock(final FormBlockInformation presentation, FormBlockPanel masterBlock) ;
	// void              updateBlockCaption(final String sType, final String sCaption) ;
	void              endOfBlock(final boolean bInPdfWhenEmpty, FormBlockPanel masterBlock) ;
	void              insertNewControl(final String sControlPath, final ArrayList<FormDataData> content, final String sControlCaption, final String sControlType, final String sControlSubtype, final String sControlUnit, final String sControlValue, final ArrayList<FormControlOptionData> aOptions, final String sControlStyle, final String sCaptionStyle, final boolean bInitFromPrev, final String sExclusion, final boolean bInBlockCell, final boolean bInPdfWhenEmpty, FormBlockPanel masterBlock) ;
		
	boolean           getContent(ArrayList<FormDataData> aInformation) ;
	FormDataData      getContentForPath(final String sPath) ;
	
	HasClickHandlers  getInitFromPreviousButton() ;
	
	ArrayList<FormControl> getControls() ;
	Widget            getControlForPath(final String sPath) ;
	FormControl       getPlainControlForPath(final String sControlPath) ;
	void              emptyContentForPath(final String sPath) ;
	
	void              setScreenShotMode(final boolean bScreenShotMode) ;
	boolean           isScreenShotMode() ;
	
	// Actions/Annotations management
	//
	public FormBlockPanel getNewActionBlock(final String sCaption, final int iAnnotationID, ClickHandler actionClickHandler) ;
	public void           initializeActionControls() ;
	public void           initializeActionHistory() ;
	public void           addNewActionButton(final String sCaption, ClickHandler handler, final String sActionId) ;
	public String         getNewAnnotationID(Widget sender) ;
}
