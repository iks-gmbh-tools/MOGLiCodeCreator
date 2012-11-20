package com.iksgmbh.moglicc.inserter.modelbased.velocity;

import com.iksgmbh.moglicc.generator.classbased.velocity.VelocityGeneratorResultData;


/**
 * return Object for {@code VelocityEngineProviderStarter.startEngineWithModel()}
 * 
 * @author Reik Oberrath
 */
public interface VelocityInserterResultData extends VelocityGeneratorResultData {
	
	enum KnownInserterPropertyNames { ReplaceStart, ReplaceEnd, 
		                              InsertBelow, InsertAbove };
	
	String getReplaceStartIndicator();
	
	String getReplaceEndIndicator();

	String getInsertBelowIndicator();
	
	String getInsertAboveIndicator();

	boolean mustGeneratedContentBeMergedWithExistingTargetFile();
	
}
