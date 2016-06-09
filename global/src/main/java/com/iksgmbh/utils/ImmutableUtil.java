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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImmutableUtil {
	
//	public static List<String> getImmutableListOf(String...strings) {
//		final List<String> list = new ArrayList<String>();
//		for (int i = 0; i < strings.length; i++) {
//			list.add(strings[i]);
//		}
//		return Collections.unmodifiableList(list);
//	}
	
	public static <T> List<T> getImmutableListFromLists(List<T>... lists) {
		final List<T> list = new ArrayList<T>();
		for (int i = 0; i < lists.length; i++) {
			list.addAll(lists[i]);
		}
		return Collections.unmodifiableList(list);
	}	
	
	public static List<String> getImmutableListOf() {
		return Collections.unmodifiableList(new ArrayList<String>());
	}

	public static <T> List<T> getEmptyImmutableListOf(T clas) {
		return Collections.unmodifiableList(new ArrayList<T>());
	}
	
	public static <T> List<T> getImmutableListOf(T... elements) {
		final List<T> list = new ArrayList<T>();
		for (int i = 0; i < elements.length; i++) {
			list.add(elements[i]);
		}
		return Collections.unmodifiableList(list);
	}	

}