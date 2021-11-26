package com.primege.client.mvp;

import java.util.ArrayList;

import com.primege.client.util.FormControlOptionData;
import com.primege.client.widgets.FormBlockPanel;
import com.primege.shared.database.FormDataData;
import com.primege.shared.model.FormBlockModel;

public interface FormPresenterInterface
{
	void prepareSreen() ;
	void resetAll(boolean bScreenShotMode) ;
	void initialize() ;
	void createChangeHandlers(FormBlockPanel masterBlock) ;
	void leaveWhenSaved(int iFormId) ;
	void initFromExistingInformation() ;
	void initFromPreviousInformation() ;
	void executePostDisplayProcesses() ;
	void executePostAnnotationsDisplayProcesses() ;
	void saveAnnotation(final int iFormId, final String sActionId, boolean bIsDraft) ;
	void insertNewControl(final String sControlPath, final String sControlCaption, final String sControlType, final String sControlSubtype, final String sControlUnit, final String sControlValue, final ArrayList<FormControlOptionData> aOptions, final String sControlStyle, final String sCaptionStyle, final boolean bInitFromPrev, final String sExclusion, final boolean bInBlockCell, final boolean bInPdfWhenEmpty, FormBlockPanel masterBlock, FormBlockModel<FormDataData> aInformation) ;
}
