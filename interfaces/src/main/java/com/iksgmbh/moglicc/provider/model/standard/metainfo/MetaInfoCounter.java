package com.iksgmbh.moglicc.provider.model.standard.metainfo;

/**
 * Functionality to count matches between MetaInfoValidator elements and MetaInfo elements
 * To be implemented by concrete MetaInfoValidator classes.
 * 
 * The counted information can be used for statistical purpose, i.e. to have an overview
 * which MetaInfo is used by which plugin (ValidatorVendor).
 * 
 * @author Reik Oberrath
 * @since 1.0.0
 */
public interface MetaInfoCounter {

	int getMetaInfoMatches();
}