package org.eclipse.core.tests.filesystem.zip;

import static org.eclipse.core.tests.filesystem.zip.ZipFileSystemTestUtil.ensureDoesNotExist;
import static org.eclipse.core.tests.filesystem.zip.ZipFileSystemTestUtil.ensureExists;
import static org.eclipse.core.tests.filesystem.zip.ZipFileSystemTestUtil.getMonitor;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 *
 */
@RunWith(Parameterized.class)
public class CopyTest {

	@Parameterized.Parameters
	public static Collection<String[]> zipFileNames() {
		return Arrays.asList(new String[][] { { ZipFileSystemTestSetup.ZIP_FILE_VIRTUAL_FOLDER_NAME },
				{ ZipFileSystemTestSetup.JAR_FILE_VIRTUAL_FOLDER_NAME } });
	}

	private String zipFileName;

	public CopyTest(String zipFileName) {
		this.zipFileName = zipFileName;
	}

	@Before
	public void setup() throws Exception {
		ZipFileSystemTestSetup.defaultSetup();
	}

	@After
	public void teardown() throws Exception {
		ZipFileSystemTestSetup.teardown();
	}

	@Test
	public void testCopyZipFile() throws Exception {
		IFolder openedZipFile = ZipFileSystemTestSetup.firstProject
				.getFolder(zipFileName);
		ensureExists(openedZipFile);
		IFolder destinationFolder = ZipFileSystemTestSetup.firstProject.getFolder("Folder");
		destinationFolder.create(true, true, getMonitor());
		ensureExists(destinationFolder);
		IFolder copyDestination = ZipFileSystemTestSetup.firstProject
				.getFolder("Folder" + "/" + zipFileName);
		openedZipFile.copy(copyDestination.getFullPath(), true, getMonitor());
		ensureExists(copyDestination);
		ensureExists(openedZipFile);
	}

	@Test
	public void testCopyFileInsideOfZipFile() throws Exception {
		IFile textFile = ZipFileSystemTestSetup.firstProject.getFile(
				zipFileName + "/" + ZipFileSystemTestSetup.TEXT_FILE_NAME);
		ensureExists(textFile);
		IFolder destinationFolder = ZipFileSystemTestSetup.firstProject.getFolder("Folder");
		destinationFolder.create(true, true, getMonitor());
		ensureExists(destinationFolder);
		IFile copyDestination = ZipFileSystemTestSetup.firstProject
				.getFile("Folder" + "/" + ZipFileSystemTestSetup.TEXT_FILE_NAME);
		textFile.copy(copyDestination.getFullPath(), true, getMonitor());
		ensureExists(copyDestination);
		ensureExists(textFile);
	}

	@Test
	public void testCopyFileIntoZipFile() throws Exception {
		IFile textFile = ZipFileSystemTestSetup.firstProject.getFile("NewFile.txt");
		ensureDoesNotExist(textFile);
		String text = "Foo";
		InputStream stream = new ByteArrayInputStream(text.getBytes());
		textFile.create(stream, true, getMonitor());
		stream.close();
		ensureExists(textFile);
		IFile copyDestination = ZipFileSystemTestSetup.firstProject
				.getFile(zipFileName + "/" + "NewFile.txt");
		textFile.copy(copyDestination.getFullPath(), true, getMonitor());
		ensureExists(copyDestination);
		ensureExists(textFile);
		try (InputStreamReader isr = new InputStreamReader(copyDestination.getContents());
				BufferedReader reader = new BufferedReader(isr)) {
			String content = reader.readLine();
			Assert.assertEquals("The content of NewFile.txt should be 'Foo'", "Foo", content);
		}
	}
}
