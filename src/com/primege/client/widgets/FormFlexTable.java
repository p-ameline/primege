package com.primege.client.widgets;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.FlexTable;

import com.primege.client.util.FormControlOptionData;
import com.primege.shared.database.FormDataData;

/**
 * FormFlexTable that can be filled from columns description and content from a list of {@link FormDataData}
 */
public class FormFlexTable extends FlexTable
{
	/**
	 * Default Constructor
	 *
	 */
	public FormFlexTable() {
		super() ;
	}

	/**
	 * Create columns' captions row
	 */
	public void initCaptionRow(final ArrayList<FormControlOptionData> aOptions)
	{
		if ((null == aOptions) || aOptions.isEmpty())
			return ;

		// Create the first table line (with columns captions)
		//
		int iCol = 0 ;
		for (FormControlOptionData optionData : aOptions)
			setText(0, iCol++, optionData.getCaption()) ;
	}

	/**
	 * Initialize table cells from a content
	 *
	 * @param sGlobalPath Path to this table (means all sons's paths must be in the form this path + /#N/etc)
	 * @param aContent    Array of {@link FormDataData] used to initialize the control
	 * @param aColsDescs  Columns descriptions
	 */
	public void setMultipleContent(final String sGlobalPath, final ArrayList<FormDataData> aContent, final ArrayList<FormControlOptionData> aColsDescs)
	{
		if ((null == aContent) || aContent.isEmpty() || (null == aColsDescs) || aColsDescs.isEmpty())
			return ;

		ArrayList<String> aRowIndexes = new ArrayList<String>() ;

		for (FormDataData formData : aContent)
			fillCell(sGlobalPath, formData, aColsDescs, aRowIndexes) ;
	}

	/**
	 * Fill the proper cell from a {@link FormDataData} and a list of already allocated row indexes
	 *
	 * @param sGlobalPath Path to this table (means all sons's paths must be in the form this path + /#N/etc)
	 * @param formData    Information the proper cell is to be filled from
	 * @param aColsDescs  Columns descriptions
	 * @param aRowIndexes List of already allocated row indexes
	 */
	protected void fillCell(final String sGlobalPath, final FormDataData formData, final ArrayList<FormControlOptionData> aColsDescs, ArrayList<String> aRowIndexes)
	{
		if ((null == formData) || formData.isEmpty() || (null == sGlobalPath) || sGlobalPath.isEmpty())
			return ;

		int iGlobalPathLen = sGlobalPath.length() ;

		String sPath = formData.getPath() ;

		if (sPath.length() <= iGlobalPathLen + 2)
			return ;

		// The path must be in the form global path/#N/local path
		//
		if (false == sPath.startsWith(sGlobalPath + "/#"))
			return ;

		String sPostGlobalPath = sPath.substring(iGlobalPathLen + 2) ;
		if (sPostGlobalPath.isEmpty())
			return ;

		// Get line index (N in #N)
		//
		int iSeparPos = sPostGlobalPath.indexOf("/") ;
		if ((-1 == iSeparPos) || (iSeparPos >= sPostGlobalPath.length() - 1))
			return ;

		String sIndex     = sPostGlobalPath.substring(0, iSeparPos) ;
		String sLocalPath = sPostGlobalPath.substring(iSeparPos + 1) ;

		// Check if this index is already known and, if not, add it the the list
		//
		int iFoundRow = -1 ;
		int iMaxRow   =  0 ;
		if (false == aRowIndexes.isEmpty())
			for (String sRowIndex : aRowIndexes)
			{
				iMaxRow++ ;
				if (sIndex.equals(sRowIndex))
					iFoundRow = iMaxRow ;
			}

		// Not found; open a new row
		//
		if (-1 == iFoundRow)
		{
			aRowIndexes.add(sIndex) ;
			iFoundRow = iMaxRow + 1 ;

			if (getRowCount() <= iFoundRow)
				insertRow(-1) ;
		}

		// Get col
		//
		int iCol = getColForPath(sLocalPath, aColsDescs) ;
		if (-1 == iCol)
			return ;

		setText(iFoundRow, iCol, formData.getValue()) ;
	}

	/**
	 * Get table's column for a given "local path" (paths are in the form "global path/#N/local path")
	 * 
	 * @param sLocalPath Local path to find the corresponding column
	 * @param aColsDescs Columns descriptions
	 * 
	 * @return Column number if the path belongs to form's control options, <code>-1</code> if not
	 */
	protected int getColForPath(final String sLocalPath, final ArrayList<FormControlOptionData> aColsDescs)
	{
		if ((null == sLocalPath) || sLocalPath.isEmpty())
			return -1 ;

		int iCol = 0 ;
		for (FormControlOptionData option : aColsDescs)
		{
			if (sLocalPath.equals(option.getPath()))
				return iCol ;
			iCol++ ;
		}

		return -1 ;
	}
}
