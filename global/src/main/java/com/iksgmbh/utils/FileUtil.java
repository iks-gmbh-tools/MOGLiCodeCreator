package com.iksgmbh.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
	
	public static BufferedReader getBufferedReader(final File file) throws IOException {
		return new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
	}
	
	public static BufferedWriter getBufferedWriter(final File file) throws IOException {
		return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
	}
	

	public static String getFileContent(final File file) throws IOException {
		return getFileContent(getBufferedReader(file), file.getName());
	}
	
	public static String getFileContent(final String filename) throws IOException {
		final File file = new File(filename);
		return getFileContent(getBufferedReader(file), filename);
	}


	public static List<String> getFileContentAsList(final File file) throws IOException {
		return getFileContentAsList(getBufferedReader(file), file.getName());
	}
	
	public static String getFileContent(final InputStream inputStream,
			                            final String filename) throws IOException {
		final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8")); 
		return getFileContent(br, filename);
	}

	private static String getFileContent(BufferedReader br, String filename) throws IOException {
		List<String> fileContentAsList = getFileContentAsList(br, filename);
		return transformStringListToString(fileContentAsList);
	}

	private static String transformStringListToString(List<String> fileContentAsList) {
		final StringBuffer sb = new StringBuffer();
		for (String line : fileContentAsList) {
			sb.append(line);
			sb.append(getSystemLineSeparator());
		}
		return sb.toString().trim();
	}

	private static List<String> getFileContentAsList(
			                    final BufferedReader br, 
			                    final String filename) throws IOException {
		final List<String> content = new ArrayList<String>();
		try {
			String line = br.readLine();
			while (line != null) {
				content.add(line);
				line = br.readLine();
			}
		} finally {
			if (br != null) {
				br.close();
			}
		}
		return content;
	}

	public static void appendToFile(final File file, final String text) throws IOException {
		final String oldFileContent;
		final String newFileContent;
		
		if (file.exists()) {
			oldFileContent = getFileContent(file);
		} else {
			oldFileContent = "";
		}
		
		if (oldFileContent.length() > 0) {
			newFileContent = oldFileContent + getSystemLineSeparator() + text;
		} else {
			newFileContent = text;
		}
		
		Writer writer = null;
		try {
			writer = getBufferedWriter(file);
			writer.write(newFileContent);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	public static void createNewFileWithContent(final File file, final List<String> content) throws Exception {
		createNewFileWithContent(file, transformStringListToString(content));
	}
	
	public static void createNewFileWithContent(final File file, final String content) throws Exception {
		createNewFile(file);
		
		Writer writer = null;
		try {
			writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
			writer.write(content);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	protected static void createNewFile(final File file) throws IOException {
		boolean exists = file.exists();
		if (exists) {
			final boolean deleted = file.delete();
			if (!deleted)
				throw new RuntimeException("File not deleted: " + file.getAbsolutePath());
		}
		file.createNewFile();
	}

	public static void createNewFileWithContent(final File dir, final String filename, final String content) throws Exception {
		final File file = new File(dir, filename);
		createNewFileWithContent(file, content);
	}

	public static String getSystemLineSeparator() {
		return System.getProperty("line.separator");
	}

	public static String removeFileExtension(final String filename) {
		final int pos = filename.lastIndexOf('.');
		if (pos == -1) {
			return filename;
		}
		return filename.substring(0, pos);
	}

	public static boolean areFilePathsIdentical(final File file1,
			final File file2) {
		return file1.getAbsolutePath().equals(file2.getAbsolutePath());
	}

	public static boolean deleteDirWithContent(final String path) {
		final File dir = new File(path);
		return deleteDirWithContent(dir);
	}

	public static boolean deleteDirWithContent(final File dir) {
		boolean ok = true;
		if (dir.exists() && dir.isDirectory()) {
			final File[] listFiles = dir.listFiles();
			for (int i = 0; i < listFiles.length; i++) {
				if (listFiles[i].isDirectory()) {
					deleteDirWithContent(listFiles[i]);
				} else {
					boolean b = listFiles[i].delete();
					if (ok) ok = b;
				}
			}
			boolean b = dir.delete();
			if (ok) ok = b;
		}
		return ok;
	}
	
	public static void deleteFiles(final File[] cleanUpFiles) {
		for (int i = 0; i < cleanUpFiles.length; i++) {
			final File f = cleanUpFiles[i];
			if (f.isDirectory()) {
				FileUtil.deleteDirWithContent(f);
			} else {
				f.delete();
			}
		}
	}
	
	public static void copyTextFile(final File sourcefile, final String targetdir) {
		final File targetFile = new File(targetdir + "/" + sourcefile.getName());
		copyTextFile(sourcefile, targetFile);
	}
	
	/**
	 * Uses reader and writer to perform copy
	 * @param sourcefile
	 * @param targetFile
	 */
	public static void copyTextFile(final File sourcefile, final File targetFile) {
		if (!sourcefile.exists()) {
			throw new RuntimeException("Sourcefile does not exist: "
					+ sourcefile.getAbsolutePath());
		}
		if (!sourcefile.isFile()) {
			throw new RuntimeException("Sourcefile is no file: "
					+ sourcefile.getAbsolutePath());
		}
		final String targetdir = targetFile.getParent();
		if (! targetFile.getParentFile().exists()) {
			throw new RuntimeException("TargetDirectory does not exist: "
					+ targetdir);
		}

		BufferedReader in = null;
		BufferedWriter out = null;
		try {
			out = getBufferedWriter(targetFile);
			in = getBufferedReader(sourcefile);
			int c;

			while ((c = in.read()) != -1)
				out.write(c);
		} catch (Exception e) {
			throw new RuntimeException("Error copying "
					+ sourcefile.getAbsolutePath() + " to " + targetdir, e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					throw new RuntimeException("Error closing reader " + in, e);
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					throw new RuntimeException("Error writer reader " + out, e);
				}
			}
		}
	}
	
	/**
	 * Uses streams to perform copy
	 * @param fromFileName
	 * @param toFileName
	 * @throws IOException
	 */
	public static void copyBinaryFile(final String fromFileName, final String toFileName) {
		final File fromFile = new File(fromFileName);
		final File toFile = new File(toFileName);
		copyBinaryFile(fromFile, toFile);
	}

	/**
	 * Uses streams to perform copy
	 * @param fromFile
	 * @param toFile
	 * @throws IOException
	 */
	public static void copyBinaryFile(final File fromFile, File toFile) {
		if (!fromFile.exists())
			throw new RuntimeException("FileCopy: " + "no such source file: "
					+ fromFile.getAbsolutePath());
		if (!fromFile.isFile())
			throw new RuntimeException("FileCopy: " + "can't copy directory: "
					+ fromFile.getAbsolutePath());
		if (!fromFile.canRead())
			throw new RuntimeException("FileCopy: " + "source file is unreadable: "
					+ fromFile.getAbsolutePath());

		if (toFile.isDirectory())
			toFile = new File(toFile, fromFile.getName());

		if (toFile.exists()) {
			if (!toFile.canWrite())
				throw new RuntimeException("FileCopy: "
						+ "destination file is unwriteable: " + toFile.getAbsolutePath());
		} else {
			String parent = toFile.getParent();
			if (parent == null)
				parent = System.getProperty("user.dir");
			File dir = new File(parent);
			if (!dir.exists())
				throw new RuntimeException("FileCopy: "
						+ "destination directory doesn't exist: " + parent);
			if (dir.isFile())
				throw new RuntimeException("FileCopy: "
						+ "destination is not a directory: " + parent);
			if (!dir.canWrite())
				throw new RuntimeException("FileCopy: "
						+ "destination directory is unwriteable: " + parent);
		}

		FileInputStream from = null;
		FileOutputStream to = null;
		try {
			from = new FileInputStream(fromFile);
			to = new FileOutputStream(toFile);
			byte[] buffer = new byte[4096];
			int bytesRead;

			while ((bytesRead = from.read(buffer)) != -1)
				to.write(buffer, 0, bytesRead); // write
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (from != null)
				try {
					from.close();
				} catch (IOException e) {
					;
				}
			if (to != null)
				try {
					to.close();
				} catch (IOException e) {
					;
				}
		}
	}
	
	public static File getSubDir(final File parent, final String subDir) {
		final File[] listFiles = parent.listFiles();
		for (int i = 0; i < listFiles.length; i++) {
			final File f = listFiles[i];
			if (f.isDirectory() && f.getName().equals(subDir)) {
				return f;
			}
			
		}
		return null;
	}

	public static long getFileSizeKB(File file) {
		 long filesize = file.length();
         long fileSizeInKB = filesize / 1024;
		return fileSizeInKB;
	}
	
	public static String readTextResourceContentFromClassPath(final Class<?> clazz,
			                                                  final String pathToResource) throws IOException {
		final InputStream resource = clazz.getClassLoader().getResourceAsStream(pathToResource);
		final int pos = pathToResource.lastIndexOf('.');
		final String filename = pathToResource.substring(pos);
		return FileUtil.getFileContent(resource, filename);
	}
	
	public static List<String> getNamesOfSubdirs(final File dir) {
		if (dir.isFile()) {
			return null;
		}
		final List<String> toReturn = new ArrayList<String>();
		final File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				toReturn.add(file.getName());
			}
		}
		return toReturn;
	}
	
	public static List<File> getOnlyFileChildren(final File dir) {
		if (dir.isFile() || ! dir.exists()) {
			return null;
		}
		final List<File> toReturn = new ArrayList<File>();
		final File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isFile()) {
				toReturn.add(file);
			}
		}
		return toReturn;
	}

}
