package com.iksgmbh.moglicc.provider.model.standard.buildup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.iksgmbh.moglicc.provider.model.standard.ClassDescriptor;
import com.iksgmbh.moglicc.provider.model.standard.Model;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfo;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaModelObject;

/**
 * Used to build a Model Object. 
 * @author Reik Oberrath
 * @since 1.0.0
 */
public class BuildUpModel extends MetaModelObject implements Model {
	
	private HashMap<String, HashSet<String>> metaInfoNamesForPluginIds;
	private HashMap<String, String> variables;
	private List<ClassDescriptor> classDescriptorList;
	private String name;
	
	public BuildUpModel(final String name) {
		if (StringUtils.isEmpty(name)) {
			throw new IllegalArgumentException("Name of Model must not be empty!");
		}
		this.name = name;
		this.classDescriptorList = new ArrayList<ClassDescriptor>();
		metaInfoList = new ArrayList<MetaInfo>();
	}

	@Override
	public String toString() {
		return "BuildUpModel [name=" + name + ", classNumber=" + getSize() + ", metaInfoList=" + getCommaSeparatedListOfMetaInfoNames() + "]";
	}

	@Override
	public List<ClassDescriptor> getClassDescriptorList() {
		return classDescriptorList;
	}

	@Override
	public int getSize() {
		return classDescriptorList.size();
	}

	@Override
	public String getName() {
		return name;
	}

	public void addClassDescriptor(final ClassDescriptor classDescriptor) {
		classDescriptorList.add(classDescriptor);
	}
	
	public boolean hasClassDescriptorAreadyInList(final String fullyQualifiedClassName) {
		for (final ClassDescriptor classDescriptor : classDescriptorList) {
			if (fullyQualifiedClassName.equals(classDescriptor.getFullyQualifiedName())) {
				return true;
			}
		}
		return false;
	}

	public HashMap<String, HashSet<String>> getMetaInfoNamesForPluginIds() {
		final List<MetaInfo> totalMetaInfoList = getAllMetaInfos();
		metaInfoNamesForPluginIds = new HashMap<String, HashSet<String>>();
		for (final MetaInfo metaInfo : totalMetaInfoList) {
			analyseForPluginIds(metaInfo);
		}
		return metaInfoNamesForPluginIds;
	}

	private void analyseForPluginIds(MetaInfo metaInfo) {			
		final Set<String> keySet = metaInfoNamesForPluginIds.keySet();
		final List<String> pluginListOfMetaInfoElement = metaInfo.getPluginList();
		for (final String pluginId : pluginListOfMetaInfoElement) {
			if (keySet.contains(pluginId)) {
				// pluginId already known -> add metaInfoName to its list
				metaInfoNamesForPluginIds.get(pluginId).add(getMetaInfoUsageMessage(metaInfo));
			} else {
				// pluginId unknown -> create new list for metaInfoNames
				final HashSet<String> metaInfoNameList = new HashSet<String>();
				metaInfoNameList.add(getMetaInfoUsageMessage(metaInfo));
				metaInfoNamesForPluginIds.put(pluginId, metaInfoNameList);
			}
		}
	}

	private String getMetaInfoUsageMessage(MetaInfo metaInfo) {
		return "'" + metaInfo.getName() + "' in " + metaInfo.getHierarchyLevel() + " level";
	}

	/**
	 * @return MetaInfos of model and containing classDescriptors and attributeDescriptors 
	 */
	@Override
	public List<MetaInfo> getAllMetaInfos() {
		final List<MetaInfo> totalMetaInfoList = new ArrayList<MetaInfo>();
		totalMetaInfoList.addAll(getMetaInfoList());
		collectMetaInfosFromContainingHierarchyLevels(totalMetaInfoList);
		return totalMetaInfoList;
	}

	private void collectMetaInfosFromContainingHierarchyLevels(final List<MetaInfo> totalMetaInfoList) {
		for (final ClassDescriptor classDescriptor: classDescriptorList) {
			BuildUpClassDescriptor buildUpClassDescriptor = (BuildUpClassDescriptor) classDescriptor;
			totalMetaInfoList.addAll(buildUpClassDescriptor.getAllMetaInfos());
		}
	}

	@Override
	public ClassDescriptor getClassDescriptor(final String classname)
	{
		for (final ClassDescriptor classDescriptor : classDescriptorList)
		{
			if (classDescriptor.getFullyQualifiedName().equals(classname)) {
				return classDescriptor;
			}
		}
		
		for (final ClassDescriptor classDescriptor : classDescriptorList)
		{
			if (classDescriptor.getSimpleName().equals(classname)) {
				return classDescriptor;
			}
		}
		
		return null;
	}

	public void addVariable(final String variableKey, 
			                final String variableValue)
	{
		variables.put(variableKey, variableValue);
	}

	public HashMap<String, String> getVariables()
	{
		return variables;
	}
	
	public void setVariables(final HashMap<String, String>  variables)
	{
		this.variables = variables;
	}

}