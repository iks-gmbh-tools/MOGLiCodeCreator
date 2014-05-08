package com.iksgmbh.moglicc.treebuilder.modelbased.velocity.data;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.generator.GeneratorResultData;
import com.iksgmbh.moglicc.generator.classbased.velocity.BuildUpVelocityGeneratorResultData;
import com.iksgmbh.utils.StringUtil;

/**
 * Object to build a data structure with information needed to build a folder tree.
 *
 * @author Reik Oberrath
 * @since 1.3.0
 */
public class VelocityTreeBuilderResultData extends BuildUpVelocityGeneratorResultData
{

	public enum KnownTreeBuilderPropertyNames { RootName, Exclude, ReplaceIn, RenameFile, RenameDir, CleanTarget };

	public VelocityTreeBuilderResultData(final GeneratorResultData generatorResultData) {
		super(generatorResultData);
	}

	@Override
	protected void checkTargetFile(final File targetFile) throws MOGLiPluginException {
		if (isTargetToBeCreatedNewly()) {
			return;
		}
		if (targetFile.exists() && ! targetFile.isFile()) {
			throw new MOGLiPluginException(TEXT_TARGET_FILE_IS_A_DIRECTORY + "\n" + targetFile.getAbsolutePath());
		}
	}


	@Override
	public void validatePropertyKeys(final String artefact) throws MOGLiPluginException {
		super.validatePropertyKeys(artefact);
	}

	public String getRootName() {
		return getProperty(KnownTreeBuilderPropertyNames.RootName.name());
	}

	public String getCleanTarget() {
		return getProperty(KnownTreeBuilderPropertyNames.CleanTarget.name());
	}

	public List<String> getExludes() {
		final String line = getProperty(KnownTreeBuilderPropertyNames.Exclude.name());
		final String[] elements = StringUtil.getListFromLineWithCommaSeparatedElements(line);
		return Arrays.asList(elements);
	}

	public String getReplaceIn() {
		return getProperty(KnownTreeBuilderPropertyNames.ReplaceIn.name());
	}

	public String getRenameFile() {
		return getProperty(KnownTreeBuilderPropertyNames.RenameFile.name());
	}

	public String getRenameDir() {
		return getProperty(KnownTreeBuilderPropertyNames.RenameDir.name());
	}

	@Override
	public String getGeneratedContent() {
		return ""; // for tree builder no content exists!
	}

}