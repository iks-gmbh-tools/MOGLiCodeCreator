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
package com.iksgmbh.moglicc.provider.model.standard.metainfo.validator;

import java.util.ArrayList;
import java.util.List;

import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfo;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoDummy;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.validation.MetaInfoValidationData;

public class DataHelper {

	public static List<MetaInfo> createMetaInfoTestStandardList() {
		final List<MetaInfo> metaInfoList = new ArrayList<MetaInfo>();
		metaInfoList.add(new MetaInfoDummy("singleMetaInfo", "singleMetaInfoValue"));
		metaInfoList.add(new MetaInfoDummy("OtherMetaInfo", "OtherMetaInfoValue1"));
		metaInfoList.add(new MetaInfoDummy("OtherMetaInfo", "OtherMetaInfoValue2"));
		metaInfoList.add(new MetaInfoDummy("doubleMetaInfo", "doubleMetaInfoValue1"));
		metaInfoList.add(new MetaInfoDummy("doubleMetaInfo", "doubleMetaInfoValue2"));
		metaInfoList.add(new MetaInfoDummy("metaInfoName", "metaInfoValue"));
		metaInfoList.add(new MetaInfoDummy("tripleMetaInfo", "tripleMetaInfoValue1"));
		metaInfoList.add(new MetaInfoDummy("tripleMetaInfo", "tripleMetaInfoValue2"));
		metaInfoList.add(new MetaInfoDummy("tripleMetaInfo", "tripleMetaInfoValue3"));
		return metaInfoList;
	}

	public static ConditionalMetaInfoValidator getValidator(final MetaInfoValidationData metaInfoValidationData) {
		return new ConditionalMetaInfoValidator(metaInfoValidationData);
	}

}