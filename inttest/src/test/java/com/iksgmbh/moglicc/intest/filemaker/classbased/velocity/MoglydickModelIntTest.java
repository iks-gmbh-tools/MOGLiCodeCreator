package com.iksgmbh.moglicc.intest.filemaker.classbased.velocity;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.iksgmbh.moglicc.core.InfrastructureService;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.intest.IntTestParent;
import com.iksgmbh.utils.FileUtil;

public class MoglydickModelIntTest extends IntTestParent {

	@Test
	public void buildsConsoleComicStripShellScript() throws MOGLiPluginException, IOException {
		// prepare test
		createModelPropertiesFileWithContent("modelfile=Moglydick.txt");
		standardModelProviderStarter.doYourJob();

		// call functionality under test
		velocityClassBasedFileMakerStarter.doYourJob();
		velocityModelBasedLineInserterStarter.doYourJob();

		// verify test result
		final InfrastructureService infrastructure = velocityClassBasedFileMakerStarter.getInfrastructure();
		final File outputDir = new File(infrastructure.getPluginOutputDir(), "ConsoleComicStrip");
		assertChildrenNumberInDirectory(outputDir, 42);
		final File file = new File(infrastructure.getApplicationRootDir(), "Moglydick.sh");
		assertFileExists(file);
		final List<String> content = FileUtil.getFileContentAsList(file);
		assertEquals("lines in file", 21844, content.size());
	}


}
