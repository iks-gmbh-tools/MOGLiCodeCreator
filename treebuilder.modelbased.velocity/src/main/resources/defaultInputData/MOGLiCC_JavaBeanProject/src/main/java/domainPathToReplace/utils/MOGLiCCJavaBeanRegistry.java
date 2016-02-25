/*
 * Copyright 2016 IKS Gesellschaft fuer Informations- und Kommunikationssysteme mbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package <domainPathToReplace>.utils;

import java.util.HashMap;

import com.iksgmbh.moglicc.demo.utils.MOGLiCCJavaBean;

/**
 * Necessary to avoid StackOverflow for reciprocal referencing
 * between JavaBeans.
 * 
 * @author Reik Oberrath
 */
public class MOGLiCCJavaBeanRegistry
{
	private static HashMap<String, MOGLiCCJavaBean> registryMap = new HashMap<String, MOGLiCCJavaBean>();
	
	public static void register(final String registryId, 
			                    final MOGLiCCJavaBean javaBean)
	{
		javaBean.setRegistryId(registryId);
		registryMap.put(registryId, javaBean);
	}
	
	public static boolean isBeanRegistered(final String registryId)
	{
		return registryMap.containsKey(registryId);
	}
	
	public static boolean isBeanRegistered(final MOGLiCCJavaBean javaBean)
	{
		return isBeanRegistered(javaBean.getRegistryId());
	}
	
	public static MOGLiCCJavaBean getJavaBean(final String registryId)
	{
		return registryMap.get(registryId);
	}
	
    /**
     * Removes all of the mappings from this map.
     * The map will be empty after this call returns.
     * Intended use: test purpose.
     */
	public static void clear()
	{
		registryMap.clear();
	}
	
	
}