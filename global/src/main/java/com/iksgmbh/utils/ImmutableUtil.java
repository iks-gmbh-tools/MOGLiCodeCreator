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
