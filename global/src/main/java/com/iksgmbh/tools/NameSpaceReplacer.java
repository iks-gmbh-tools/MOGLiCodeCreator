package com.iksgmbh.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import com.iksgmbh.utils.FileUtil;
import com.iksgmbh.utils.OSUtil;

/**
 * Helper, to rename namespaces of all Eclipse projects within the workspace, e.g.
 * from "de.iks.exampleA.frontend" in "com.iks.exampleB.client".
 *
 * For this purpose, the following three steps are performed:
 * 1. Names of directories in the file system are modified.
 * 2. The 'package' instructions within the java files are changed.
 * 3. All occurrences files of the old namespace (e.g. in configuration files or java comments) are replaced.
 *
 * Note:
 * 1. Always, the whole workspace is modified.
 * 2. Namespaces beginn always with the highest level, e.g. 'de' or 'com'.
 * 3. Namespaces end on the last common part, that all project names in the workspace share.
 * 4. In the file system, each project contains a folder that corresponds the namespace. It must not contain files. Only its subfolders can contain files.
 * 5. Not existing target folders will be created.
 * 6. Existing target files will be deleted (see initTarget).
 *
 * @author OberratR
 */
public class NameSpaceReplacer {

	private static final String defaultWorkspaceDir = "D:\\Reik\\dev\\git\\mogli";
	private static final String sourceNamespace = "de.iks_gmbh.automobile";
	private static final String targetNamespace = "com.iksgmbh.moglicc";
	private static final String[] directoriesToIgnore = { "target", ".git" };
	private static final String[] filesToParse = { ".xml", ".txt", ".java",
		                                           ".properties", ".mf",
		                                           ".exsd", ".product", ".project" };


	public static void main(String[] args) {
		try {
			System.out.println("Starting NamespaceReplacer...");
			System.out.println("");
			final String workspaceDir = getWorkspaceDir(args);
			(new NameSpaceReplacer()).replaceWorkspace(workspaceDir);
			System.out.println("");
			System.out.println("Done!");
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private static String getWorkspaceDir(final String[] args) {
		if (args == null || args.length < 1) {
			return defaultWorkspaceDir;
		}
		return args[0];
	}


	private String sourcePackageStructure;
	private String targetPackageStructure;


	public void replaceWorkspace(final String workspaceDir) throws Exception {
		System.out.println("");
		if (OSUtil.isWindows()) {
			sourcePackageStructure = sourceNamespace.replace('.', '\\');
			targetPackageStructure = targetNamespace.replace('.', '\\');
		} else {			
			sourcePackageStructure = sourceNamespace.replace('.', '/');
			targetPackageStructure = targetNamespace.replace('.', '/');
		}
		

		final File workspace = new File(workspaceDir);
		final File[] files = workspace.listFiles();
		if (files == null) {
			throw new IllegalArgumentException("workspace Directory not found: " + workspace.getAbsolutePath());
		}
		for (int i = 0; i < files.length; i++) {
			System.out.println("");
			File file = files[i];
			if (file.isDirectory()) {
				replaceDirectory(file, workspaceDir);
			} else {
				replaceFile(file);
			}
		}
	}

	private void replaceDirectory(final File dir, final String targetDirPath) throws Exception {
		assert (dir.isDirectory());

		if (isSourceDirectory(dir)) {
			replaceJavaDirectory(dir);
			return;
		}

		if (! isDirectoryToIgnore(dir.getName())) {
			final File[] children = dir.listFiles();
			if (children != null) {
				for (int i = 0; i < children.length; i++) {
					final File childOfDir = children[i];
					if (childOfDir.isDirectory()) {
						replaceDirectory(childOfDir, targetDirPath);
					} else {
						replaceFile(childOfDir);
					}
				}
			}
		}
	}

	private void replaceJavaDirectory(File sourceDir) throws Exception {
		assert(sourceDir.exists());
		final File targetDir;

		if (isSourceDirectory(sourceDir)) {
			System.out.println("Package-Struktur des Verzeichnisses '" + sourceDir + "' wird " +
					           "ge�ndert von <" + sourcePackageStructure + "> auf <" + targetPackageStructure + ">.");
		}

		final String targetPath = sourceDir.getAbsolutePath().replace(sourcePackageStructure, targetPackageStructure);
		targetDir = new File(targetPath);

		FileUtil.deleteDirWithContent(targetDir);

		final File[] children = sourceDir.listFiles();
		for (int i = 0; i < children.length; i++) {
			if (children[i].isDirectory()) {
				replaceJavaDirectory(children[i]);
			} else {
				replaceFile(children[i], targetDir);
			}
		}
	}

	private boolean isSourceDirectory(final File sourceDir) {
		return sourceDir.getAbsolutePath().endsWith(sourcePackageStructure);
	}

	private void replaceFile(final File sourceFile) throws Exception {
		replaceFile(sourceFile, sourceFile.getParentFile());
	}

	private void replaceFile(final File sourceFile, final File targetDir) throws Exception {
		assert (sourceFile.isFile());

		if (isFileToParse(sourceFile)) {
			final ArrayList<String> targetFileContent = replaceContent(sourceFile);

			sourceFile.delete();

			targetDir.mkdirs();
			final File targetFile = new File(targetDir, sourceFile.getName());
			if (!targetFile.createNewFile())
			{
				throw new RuntimeException("folgende Datei kann nicht erzeugt werden: " + targetFile.getAbsoluteFile());
			}
			System.out.println(targetFile.getAbsolutePath());
			PrintWriter writer = new PrintWriter(targetFile);
			for (int i = 0; i < targetFileContent.size(); i++) {
				writer.println(targetFileContent.get(i));
			}
			writer.close();
		}
	}

	protected ArrayList<String> replaceContent(final File file) throws FileNotFoundException, IOException {
		final ArrayList<String> targetFileContent = new ArrayList<String>();
		final String pathPlusFileName = file.getAbsolutePath();
		final BufferedReader reader = new BufferedReader(new FileReader(pathPlusFileName));
		//System.out.println("Reading: " + pathPlusFileName + "...");
		String line = reader.readLine();
		String replacedLine = null;
		int numMatches = 0;

		while (line != null)
		{
			int pos = line.indexOf(sourceNamespace);
			if (pos > -1) {
				replacedLine = line;
				while (pos > -1) {
					replacedLine = replacedLine.substring(0, pos) + targetNamespace
							+ replacedLine.substring(pos + sourceNamespace.length());
					numMatches++;
					pos = replacedLine.indexOf(sourceNamespace);
				}
				targetFileContent.add(replacedLine);
			}
			else
			{
				targetFileContent.add(line);
			}
			line = reader.readLine();
		}

		reader.close();

		if (numMatches > 0) {
			System.out.println("In der Datei " + file.getAbsoluteFile() + " gab es " + numMatches + " Ersetzungen.");
		}

		return targetFileContent;
	}

	private boolean isFileToParse(final File file) {
		String name = file.getName();
		int pos = name.lastIndexOf('.');
		String ext = name.substring(pos);
		for (int i = 0; i < filesToParse.length; i++) {
			if (filesToParse[i].equalsIgnoreCase(ext)) {
				return true;
			}
		}
		return false;
	}

	private boolean isDirectoryToIgnore(final String name) {
		for (int i = 0; i < directoriesToIgnore.length; i++) {
			if (directoriesToIgnore[i].equals(name)) {
				return true;
			}
		}
		return false;
	}

}
