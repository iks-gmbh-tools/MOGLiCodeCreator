package com.iksgmbh.moglicc.provider.model.standard.metainfo.validator;

import java.util.ArrayList;
import java.util.List;

import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfo;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoDummy;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoValidationData;

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
