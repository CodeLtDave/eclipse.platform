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
public class CreateTest {

	@Parameterized.Parameters
	public static Collection<String[]> zipFileNames() {
		return Arrays.asList(new String[][] { { ZipFileSystemTestSetup.ZIP_FILE_VIRTUAL_FOLDER_NAME },
				{ ZipFileSystemTestSetup.JAR_FILE_VIRTUAL_FOLDER_NAME } });
	}

	private String zipFileName;

	public CreateTest(String zipFileName) {
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
	public void testCreateFileInsideOfZipFile() throws Exception {
		IFolder openedZipFile = ZipFileSystemTestSetup.firstProject.getFolder(zipFileName);
		IFile textFile = openedZipFile.getFile("NewFile.txt");
		ensureDoesNotExist(textFile);
		String text = "Foo";
		InputStream stream = new ByteArrayInputStream(text.getBytes());
		textFile.create(stream, true, getMonitor());
		stream.close();
		ensureExists(textFile);
		try (InputStreamReader isr = new InputStreamReader(textFile.getContents());
				BufferedReader reader = new BufferedReader(isr)) {
			String content = reader.readLine();
			Assert.assertEquals("The content of Text.txt should be 'Foo'", "Foo", content);
		}
	}

	@Test
	public void testCreateFolderInsideOfZipFile() throws Exception {
		IFolder openedZipFile = ZipFileSystemTestSetup.firstProject.getFolder(zipFileName);
		IFolder newFolder = openedZipFile.getFolder("NewFolder");
		ensureDoesNotExist(newFolder);
		newFolder.create(false, true, getMonitor());
		ensureExists(newFolder);
	}
}
