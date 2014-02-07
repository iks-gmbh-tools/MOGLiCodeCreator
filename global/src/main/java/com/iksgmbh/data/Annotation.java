package com.iksgmbh.data;

public class Annotation {

	private String name;
	private String additionalInfo;
	
	public Annotation(final String name) {
		this.name = name;
	}

	public void setAdditionalInfo(final String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public String getName() {
		return name;
	}

	public String getAdditionalInfo() {
		return additionalInfo;
	}
	
	public String getFullInfo() {
		if (additionalInfo == null) {
			return name;
		}
		return name + " " + additionalInfo;
	}

	@Override
	public String toString() {
		return "Annotation [name=" + name + ", additionalInfo="
				+ additionalInfo + "]";
	}
	
	
	
}
