package com.iksgmbh.moglicc.provider.model.standard.metainfo;

import java.util.List;

/**
 * Model element to enrich a model, a class or a attribute with data
 * that can be used in template files of generator plugins.
 * 
 * @author Reik Oberrath
 * @since 1.0.0
 */
public interface MetaInfo {
	
	enum HierarchyLevel {Model, Class, Attribute};
	
	String getName();
	
	String getValue();
	
	HierarchyLevel getHierarchyLevel();
	
	List<String> getPluginList();
}