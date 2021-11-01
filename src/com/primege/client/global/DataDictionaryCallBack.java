package com.primege.client.global;

/**
 * Callback function called when information requested from the data dictionary are ready
 * 
 * @author Philippe
 *
 */
public interface DataDictionaryCallBack
{
  /**
   * Called when querying the data dictionnary throws an exception
   * 
   * @param caught failure encountered
   */
  void onFailure(Throwable caught) ;

  /**
   * Called when the query was successful.
   * 
   *
   * @return <code>true</code> if expected data were delivered, <code>false</code> if not
   */
  boolean onSuccess() ;
}
