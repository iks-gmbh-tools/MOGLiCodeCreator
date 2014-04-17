package com.iksgmbh.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipUtil {
	
	public static void unzip(final String sourceFilename, final String targetDir) throws IOException {
		final ZipFile zipFile = new ZipFile(sourceFilename);
		final File dir = new File(targetDir);
		unzip(zipFile, dir);
	}

	public static void unzip(File file, String targetDir) throws IOException {
		final File dir = new File(targetDir);
		unzip(new ZipFile(file), dir);
	}

	public static void unzip(File file, File targetDir) throws IOException {
		unzip(new ZipFile(file), targetDir);
	}

	public static void unzip(ZipFile zipFile, String targetDir) throws IOException {
		final File dir = new File(targetDir);
		unzip(zipFile, dir);
	}

	public static void unzip(final ZipFile zipFile, final File targetDir) throws IOException {
		Enumeration<?> enu = zipFile.entries();
		while (enu.hasMoreElements()) {
			ZipEntry zipEntry = (ZipEntry) enu.nextElement();

			String name = zipEntry.getName().replace("\\", "/");
			//long size = zipEntry.getSize();
			//long compressedSize = zipEntry.getCompressedSize();
			//System.out.printf("name: %-20s | size: %6d | compressed size: %6d\n", name, size, compressedSize);

			File file = new File(targetDir, name);
			if (name.endsWith("/")) {
				file.mkdirs();
				continue;
			}

			File parent = file.getParentFile();
			if (parent != null) {
				parent.mkdirs();
			}

			InputStream is = zipFile.getInputStream(zipEntry);
			FileOutputStream fos = new FileOutputStream(file);
			byte[] bytes = new byte[1024];
			int length;
			while ((length = is.read(bytes)) >= 0) {
				fos.write(bytes, 0, length);
			}
			is.close();
			fos.close();

		}
		zipFile.close();
	}

	public static void zipDir(final String sourceDir, final String targetZipFileName) throws Exception {
		File directoryToZip = new File(sourceDir);

		List<File> fileList = new ArrayList<File>();
		System.out.println("---Getting references to all files in: "
				+ directoryToZip.getCanonicalPath());
		getAllFiles(directoryToZip, fileList);
		System.out.println("---Creating zip file");
		writeZipFile(directoryToZip, fileList, targetZipFileName);
		System.out.println("---Done");
	}

	public static void getAllFiles(File dir, List<File> fileList) {
		try {
			File[] files = dir.listFiles();
			for (File file : files) {
				fileList.add(file);
				if (file.isDirectory()) {
					System.out.println("directory:" + file.getCanonicalPath());
					getAllFiles(file, fileList);
				} else {
					System.out.println("     file:" + file.getCanonicalPath());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeZipFile(File directoryToZip, List<File> fileList, String zipFileName ) {

		try {
			// The output filename, you could use full path here or other
			// the .zip file will be found in the project root directory
			FileOutputStream fos = new FileOutputStream(
					zipFileName);
			ZipOutputStream zos = new ZipOutputStream(fos);

			for (File file : fileList) {
				if (!file.isDirectory()) { // we only zip files, not directories
					addToZip(directoryToZip, file, zos);
				}
			}

			zos.close();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void addToZip(File directoryToZip, File file,
			ZipOutputStream zos) throws FileNotFoundException, IOException {

		FileInputStream fis = new FileInputStream(file);

		// we want the zipEntry's path to be a relative path that is relative
		// to the directory being zipped, so chop off the rest of the path
		String zipFilePath = file.getCanonicalPath().substring(
				directoryToZip.getCanonicalPath().length() + 1,
				file.getCanonicalPath().length());
		System.out.println("Writing '" + zipFilePath + "' to zip file");
		ZipEntry zipEntry = new ZipEntry(zipFilePath);
		zos.putNextEntry(zipEntry);

		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zos.write(bytes, 0, length);
		}

		zos.closeEntry();
		fis.close();
	}

}
