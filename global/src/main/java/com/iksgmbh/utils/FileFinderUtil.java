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
package com.iksgmbh.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * Utility, to find certain files in specific subdirectories within a given main folder.
 * 
 * @author  Reik Oberrath
 */
public class FileFinderUtil 
{
	public static List<File> findJavaFilesInMavenStructure(final File mainFolder) 
	{
		final List<String> subdirsToSearch = new ArrayList<String>();
		subdirsToSearch.add("\\src\\main\\java\\");
		subdirsToSearch.add("/src/test/java/");		
		final List<String> subdirsToIgnore = new ArrayList<String>();
		subdirsToIgnore.add("\\target\\");
		return searchFilesRecursively(mainFolder, new CustomizedFileFilter(subdirsToSearch, subdirsToIgnore, "java", null));
	}
	
	public static List<File> findJavaFilesInMavenStructure(final File mainFolder, final List<String> subdirsToIgnore) 
	{
		final List<String> subdirsToSearch = new ArrayList<String>();
		subdirsToSearch.add("\\src\\main\\java\\");
		subdirsToSearch.add("/src/test/java/");		
		return searchFilesRecursively(mainFolder, new CustomizedFileFilter(subdirsToSearch, subdirsToIgnore, "java", null));
	}
	
	
	public static List<File> findFiles(final File mainFolder, 
			                           final List<String> subdirsToSearch,
			                           final List<String> subdirsToIgnore,
			                           final String fileExtension,
			                           final String partOfFileName) 
	{
		if (mainFolder == null || mainFolder.isFile() || ! mainFolder.exists())
		{
			throw new RuntimeException("Mainfolder is not valid!");
		}
		return searchFilesRecursively(mainFolder, new CustomizedFileFilter(subdirsToSearch, subdirsToIgnore, fileExtension, partOfFileName));
	}	
	

	public static List<File> searchFilesRecursively(final File folder, 
			                             		    final FilenameFilter fileFilter)
	{
		if (! folder.exists())
		{
			throw new IllegalArgumentException("Folder does not exist: " + folder.getAbsolutePath());
		}
		final List<File> toReturn = new ArrayList<File>();
		final File[] children = folder.listFiles(fileFilter);
		for (File child : children) 
		{
			if ( child.isDirectory() )
			{
				toReturn.addAll( searchFilesRecursively(child, fileFilter) );					
			}
			else
			{
				toReturn.add(child);
			}
		}
		return toReturn;
	}
	
	
	public static class CustomizedFileFilter implements FilenameFilter
	{
		private List<String> subdirsToSearch;
		private List<String> subdirsToIgnore;
		private String fileExtension;
		private String partOfFileName;
		
		public CustomizedFileFilter(final List<String> subdirsToSearch,
									final List<String> subdirsToIgnore, 
									final String fileExtension, 
									final String partOfFileName) 
		{
			this.subdirsToSearch = unifyPaths(subdirsToSearch);
			this.subdirsToIgnore = unifyPaths(subdirsToIgnore);
			this.partOfFileName = partOfFileName;
			this.fileExtension = fileExtension;
			if (fileExtension != null && ! fileExtension.startsWith("."))
			{
				this.fileExtension = "." + fileExtension;
			}
		}
		
		@Override
		public boolean accept(File dir, String name) 
		{

			final File file = new File(dir, name);
			
			if (file.isDirectory())  {
				return true;
			}

			if (subdirsToSearch != null) {
				boolean isSubdirToSearch = false;
				for (String subdir : subdirsToSearch) 
				{
					if (getUnifiedPath(dir).contains(subdir)) 
						isSubdirToSearch = true;
				}
				
				if ( ! isSubdirToSearch ) {
					 return false;
				}
			}
			
			if (subdirsToIgnore != null) {
				boolean isSubdirToSearch = true;
				for (String subdir : subdirsToIgnore) 
				{
					if (getUnifiedPath(dir).contains(subdir)) 
						isSubdirToSearch = false;
				}
				
				if ( ! isSubdirToSearch ) {
					 return false;
				}
			}
	
			if (fileExtension != null && ! name.endsWith(fileExtension))  {				
				return false;
			}
			
			if (partOfFileName != null && ! name.contains(partOfFileName))  {				
				return false;
			}

			return true;
		}
		
		private String getUnifiedPath(File dir) {
			String path = dir.getAbsolutePath();
			return getUnifiedPath(path);
		}
		
		/**
		 * Transforms possibly occurring Windows specific path separators into uniquely valid path separators.   
		 */
		private String getUnifiedPath(final String path) {
			return StringUtils.replace(path, "\\", "/");
		}
        
		private List<String> unifyPaths(final List<String> pathList)
		{
			if (pathList == null) return null;
			
			final List<String> toReturn = new ArrayList<String>();
			
			for (String path : pathList)
			{
				toReturn.add( getUnifiedPath(path) );
			}
			
			return toReturn;
		}

		
	}
			
}