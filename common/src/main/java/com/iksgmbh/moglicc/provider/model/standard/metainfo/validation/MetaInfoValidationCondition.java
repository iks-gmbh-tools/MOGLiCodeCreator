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
package com.iksgmbh.moglicc.provider.model.standard.metainfo.validation;

import java.util.List;

import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfo;

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