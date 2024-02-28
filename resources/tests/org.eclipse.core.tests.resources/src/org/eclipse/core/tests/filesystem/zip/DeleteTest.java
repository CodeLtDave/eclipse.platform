
package org.eclipse.core.tests.filesystem.zip;

import static org.eclipse.core.tests.filesystem.zip.ZipFileSystemTestUtil.ensureDoesNotExist;
import static org.eclipse.core.tests.filesystem.zip.ZipFileSystemTestUtil.ensureExists;
import static org.eclipse.core.tests.filesystem.zip.ZipFileSystemTestUtil.getMonitor;
import static org.mockito.Mockito.mock;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.internal.ide.commands.ExpandZipHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 */
public class DeleteTest {

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
	public void testDeleteZipFile() throws Exception {
		IFolder virtualFolder = ZipFileSystemTestSetup.project
				.getFolder(ZipFileSystemTestSetup.ZIP_FILE_VIRTUAL_FOLDER_NAME);
		ensureExists(virtualFolder);
		virtualFolder.delete(false, false, getMonitor());
		ensureDoesNotExist(virtualFolder);
		IFile zipFile = ZipFileSystemTestSetup.project.getFile(ZipFileSystemTestSetup.ZIP_FILE_VIRTUAL_FOLDER_NAME);
		ensureDoesNotExist(zipFile);
	}

	@Test
	public void testDeleteFileInsideOfZip() throws Exception {
		IFolder virtualFolder = ZipFileSystemTestSetup.project
				.getFolder(ZipFileSystemTestSetup.ZIP_FILE_VIRTUAL_FOLDER_NAME);
		IFile textFile = virtualFolder.getFile(ZipFileSystemTestSetup.TEXT_FILE_NAME);
		ensureExists(textFile);
		textFile.delete(true, getMonitor());
		ensureDoesNotExist(textFile);
	}
}
