
package org.eclipse.core.tests.filesystem.zip;

import static org.eclipse.core.tests.filesystem.zip.ZipFileSystemTestUtil.ensureDoesNotExist;
import static org.eclipse.core.tests.filesystem.zip.ZipFileSystemTestUtil.ensureExists;
import static org.eclipse.core.tests.filesystem.zip.ZipFileSystemTestUtil.getMonitor;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 *
 */
@RunWith(Parameterized.class)
public class DeleteTest {

	@Parameterized.Parameters
	public static Collection<String[]> archiveNames() {
		return Arrays.asList(new String[][] { { ZipFileSystemTestSetup.ZIP_FILE_VIRTUAL_FOLDER_NAME },
				{ ZipFileSystemTestSetup.JAR_FILE_VIRTUAL_FOLDER_NAME } });
	}

	private String archiveName;

	public DeleteTest(String archiveName) {
		this.archiveName = archiveName;
	}

	@Before
	public void setup() throws Exception {
		ZipFileSystemTestSetup.setup();
	}

	@After
	public void teardown() throws Exception {
		ZipFileSystemTestSetup.teardown();
	}

	@Test
	public void testDeleteArchiveFile() throws Exception {
		IFolder archiveFolder = ZipFileSystemTestSetup.firstProject
				.getFolder(archiveName);
		ensureExists(archiveFolder);
		archiveFolder.delete(false, false, getMonitor());
		ensureDoesNotExist(archiveFolder);
		IFile zipFile = ZipFileSystemTestSetup.firstProject.getFile(archiveName);
		ensureDoesNotExist(zipFile);
	}

	@Test
	public void testDeleteFileInsideOfArchive() throws Exception {
		IFolder archiveFolder = ZipFileSystemTestSetup.firstProject
				.getFolder(archiveName);
		IFile textFile = archiveFolder.getFile(ZipFileSystemTestSetup.TEXT_FILE_NAME);
		ensureExists(textFile);
		textFile.delete(true, getMonitor());
		ensureDoesNotExist(textFile);
	}
}
