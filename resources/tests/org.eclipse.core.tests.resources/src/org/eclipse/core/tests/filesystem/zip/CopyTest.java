package org.eclipse.core.tests.filesystem.zip;

import static org.eclipse.core.tests.filesystem.zip.ZipFileSystemTestUtil.ensureDoesNotExist;
import static org.eclipse.core.tests.filesystem.zip.ZipFileSystemTestUtil.ensureExists;
import static org.eclipse.core.tests.filesystem.zip.ZipFileSystemTestUtil.getMonitor;
import static org.mockito.Mockito.mock;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.examples.filesystem.ExpandZipHandler;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 */
public class CopyTest {

	@Before
	public void setup() throws Exception {
		ZipFileSystemTestSetup.setup();
		expandZipFile(ZipFileSystemTestSetup.project.getFile(ZipFileSystemTestSetup.ZIP_FILE_VIRTUAL_FOLDER_NAME));
	}

	@After
	public void teardown() throws Exception {
		ZipFileSystemTestSetup.teardown();
	}

	private void expandZipFile(IFile file) throws Exception {
		ExpandZipHandler expandZipHandler = new ExpandZipHandler();
		Shell shell = mock(Shell.class);
		expandZipHandler.expandZip(file, shell);
		IFolder virtualFolder = ZipFileSystemTestSetup.project.getFolder(file.getName());
		Assert.assertTrue("ZIP file should exist before deletion", virtualFolder.exists());
	}

	@Test
	public void testCopyArchive() throws Exception {
		IFolder virtualFolder = ZipFileSystemTestSetup.project
				.getFolder(ZipFileSystemTestSetup.ZIP_FILE_VIRTUAL_FOLDER_NAME);
		ensureExists(virtualFolder);
		IFolder destinationFolder = ZipFileSystemTestSetup.project.getFolder("Folder");
		destinationFolder.create(true, true, getMonitor());
		ensureExists(destinationFolder);
		IFolder copyDestination = ZipFileSystemTestSetup.project
				.getFolder("Folder" + "/" + ZipFileSystemTestSetup.ZIP_FILE_VIRTUAL_FOLDER_NAME);
		virtualFolder.copy(copyDestination.getFullPath(), true, getMonitor());
		ensureExists(copyDestination);
		ensureExists(virtualFolder);
	}

	@Test
	public void testCopyFileInsideOfArchive() throws Exception {
		IFile textFile = ZipFileSystemTestSetup.project.getFile(
				ZipFileSystemTestSetup.ZIP_FILE_VIRTUAL_FOLDER_NAME + "/" + ZipFileSystemTestSetup.TEXT_FILE_NAME);
		ensureExists(textFile);
		IFolder destinationFolder = ZipFileSystemTestSetup.project.getFolder("Folder");
		destinationFolder.create(true, true, getMonitor());
		ensureExists(destinationFolder);
		IFile copyDestination = ZipFileSystemTestSetup.project
				.getFile("Folder" + "/" + ZipFileSystemTestSetup.TEXT_FILE_NAME);
		textFile.copy(copyDestination.getFullPath(), true, getMonitor());
		ensureExists(copyDestination);
		ensureExists(textFile);
	}

	@Test
	public void testCopyFileIntoArchive() throws Exception {
		IFile textFile = ZipFileSystemTestSetup.project.getFile("NewFile.txt");
		ensureDoesNotExist(textFile);
		String text = "Foo";
		InputStream stream = new ByteArrayInputStream(text.getBytes());
		textFile.create(stream, true, getMonitor());
		stream.close();
		ensureExists(textFile);
		IFile copyDestination = ZipFileSystemTestSetup.project
				.getFile(ZipFileSystemTestSetup.ZIP_FILE_VIRTUAL_FOLDER_NAME + "/" + "NewFile.txt");
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
