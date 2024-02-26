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
public class CreateTest {

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
		ensureExists(virtualFolder);
	}

	@Test
	public void testCreateFileInsideOfZip() throws Exception {
		IFolder virtualFolder = ZipFileSystemTestSetup.project
				.getFolder(ZipFileSystemTestSetup.ZIP_FILE_VIRTUAL_FOLDER_NAME);
		IFile textFile = virtualFolder.getFile("NewFile.txt");
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
}
