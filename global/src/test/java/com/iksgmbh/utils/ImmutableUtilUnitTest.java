package com.iksgmbh.utils;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class ImmutableUtilUnitTest {

	@Test
	public void testGetImmutableListOf() {
		List<String> immutableList = ImmutableUtil.getImmutableListOf("1");
		try {
			immutableList.add("2");
		} catch (UnsupportedOperationException e) {
			return;
		}
		fail();
	}
}
