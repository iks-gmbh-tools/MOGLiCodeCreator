package com.iksgmbh.moglicc.provider.model.standard.metainfo;

import java.util.List;

public class MetaInfoValidationCondition {

	private boolean mustNotHaveotherMetaInfo;
	private String otherMetaInfoName;
	private String otherMetaInfoValue;

	public MetaInfoValidationCondition(){};

	public MetaInfoValidationCondition(final boolean mustNotHaveotherMetaInfo,
			                           final String otherMetaInfoName,
			                           final String otherMetaInfoValue) {
		this.mustNotHaveotherMetaInfo = mustNotHaveotherMetaInfo;
		this.otherMetaInfoName = otherMetaInfoName;
		this.otherMetaInfoValue = otherMetaInfoValue;
	}

	public MetaInfoValidationCondition(final String otherMetaInfoName, final String otherMetaInfoValue) {
		this(false, otherMetaInfoName, otherMetaInfoValue);
	}

	public MetaInfoValidationCondition(final boolean mustNotHaveotherMetaInfo,
			                           final String otherMetaInfoName) {
		this(mustNotHaveotherMetaInfo, otherMetaInfoName, null);
	}

	public MetaInfoValidationCondition(final String otherMetaInfoName) {
		this(false, otherMetaInfoName, null);
	}

	public boolean isTrueFor(final List<MetaInfo> metaInfoList) {
		if (mustNotHaveotherMetaInfo) {
			return checkForAbsence(metaInfoList);
		}
		return checkForPresence(metaInfoList);
	}

	private boolean checkForPresence(final List<MetaInfo> metaInfoList) {
		for (final MetaInfo metaInfo : metaInfoList) {
			if (otherMetaInfoName.equals(metaInfo.getName())) {
				boolean ok = checkForValuePresence(metaInfo);
				if (ok) {
					return true; // otherMetaInfo is present
				}

			}
		}
		return false; // otherMetaInfo is NOT present with otherMetaInfoValue
	}

	private boolean checkForValuePresence(final MetaInfo metaInfo) {
		if (otherMetaInfoValue != null) {
			if (! otherMetaInfoValue.equals(metaInfo.getValue())) {
				return false; // not present
			}
		}
		return true; // present
	}

	private boolean checkForAbsence(final List<MetaInfo> metaInfoList) {
		for (final MetaInfo metaInfo : metaInfoList) {
			if (otherMetaInfoName.equals(metaInfo.getName())) {
				boolean ok = checkValueForAbsence(metaInfo);
				if (! ok) {
					return false; // otherMetaInfo is NOT absent
				}

			}
		}
		return true;  // otherMetaInfo is absent
	}

	private boolean checkValueForAbsence(final MetaInfo metaInfo) {
		if (otherMetaInfoValue != null) {
			if (! otherMetaInfoValue.equals(metaInfo.getValue())) {
				return true;  // values mismatch -> metainfo with value is not present -> validation result true
			}
		}
		return false;  // value is not part of condition -> metainfo is present -> validation result false
	}

	public boolean getMustNotHaveotherMetaInfo() {
		return mustNotHaveotherMetaInfo;
	}

	public String getOtherMetaInfoName() {
		return otherMetaInfoName;
	}

	public String getOtherMetaInfoValue() {
		return otherMetaInfoValue;
	}

}
