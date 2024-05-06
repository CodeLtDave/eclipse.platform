
package org.eclipse.core.tests.filesystem.zip;

import static org.eclipse.core.tests.filesystem.zip.ZipFileSystemTestUtil.ensureDoesNotExist;
import static org.eclipse.core.tests.filesystem.zip.ZipFileSystemTestUtil.ensureExists;
import static org.eclipse.core.tests.filesystem.zip.ZipFileSystemTestUtil.getMonitor;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
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

	private static final String NESTED_ZIP_FILE_PARENT_NAME = "NestedZipFileParent.zip";
	private static final String NESTED_ZIP_FILE_CHILD_NAME = "NestedZipFileChild.zip";

	@Parameterized.Parameters
	public static Collection<String[]> zipFileNames() {
		return Arrays.asList(new String[][] { { ZipFileSystemTestSetup.ZIP_FILE_VIRTUAL_FOLDER_NAME },
				{ ZipFileSystemTestSetup.JAR_FILE_VIRTUAL_FOLDER_NAME } });
	}

	private String zipFileName;

	public DeleteTest(String zipFileName) {
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
	public void testDeleteZipFile() throws CoreException, IOException {
		IFolder openedZipFile = ZipFileSystemTestSetup.firstProject
				.getFolder(zipFileName);
		ensureExists(openedZipFile);
		openedZipFile.delete(false, false, getMonitor());
		ensureDoesNotExist(openedZipFile);
		IFile zipFile = ZipFileSystemTestSetup.firstProject.getFile(zipFileName);
		ensureDoesNotExist(zipFile);
	}

	@Test
	public void testDeleteFileInsideOfZipFile() throws CoreException, IOException {
		IFolder openedZipFile = ZipFileSystemTestSetup.firstProject
				.getFolder(zipFileName);
		IFile textFile = openedZipFile.getFile(ZipFileSystemTestSetup.TEXT_FILE_NAME);
		ensureExists(textFile);
		textFile.delete(true, getMonitor());
		ensureDoesNotExist(textFile);
	}

	@Test
	public void testDeleteNestedZipFileParent() throws CoreException, IOException, URISyntaxException {
		ZipFileSystemTestSetup.copyZipFileIntoJavaProject(ZipFileSystemTestSetup.firstProject,
				NESTED_ZIP_FILE_PARENT_NAME);
		IFile nestedZipFileParent = ZipFileSystemTestSetup.firstProject.getFile(NESTED_ZIP_FILE_PARENT_NAME);
		ensureExists(nestedZipFileParent);
		ZipFileSystemTestUtil.openZipFile(nestedZipFileParent);
		IFolder openedNestedZipFileParent = ZipFileSystemTestSetup.firstProject.getFolder(NESTED_ZIP_FILE_PARENT_NAME);
		ensureExists(openedNestedZipFileParent);
		openedNestedZipFileParent.delete(true, getMonitor());
		ensureDoesNotExist(openedNestedZipFileParent);
		ensureDoesNotExist(nestedZipFileParent);
	}

	@Test
	public void testDeleteNestedZipFileChild() throws CoreException, IOException, URISyntaxException {
		ZipFileSystemTestSetup.copyZipFileIntoJavaProject(ZipFileSystemTestSetup.firstProject,
				NESTED_ZIP_FILE_PARENT_NAME);
		IFile nestedZipFileParent = ZipFileSystemTestSetup.firstProject.getFile(NESTED_ZIP_FILE_PARENT_NAME);
		ensureExists(nestedZipFileParent);
		ZipFileSystemTestUtil.openZipFile(nestedZipFileParent);
		IFolder openedNestedZipFileParent = ZipFileSystemTestSetup.firstProject.getFolder(NESTED_ZIP_FILE_PARENT_NAME);
		ensureExists(openedNestedZipFileParent);
		IFile nestedZipFileChild = openedNestedZipFileParent.getFile(NESTED_ZIP_FILE_CHILD_NAME);
		ensureExists(nestedZipFileChild);
		ZipFileSystemTestUtil.openZipFile(nestedZipFileChild);
		IFolder openedNestedZipFileChild = openedNestedZipFileParent.getFolder(NESTED_ZIP_FILE_CHILD_NAME);
		ensureExists(openedNestedZipFileChild);
		openedNestedZipFileChild.delete(true, getMonitor());
		ensureDoesNotExist(openedNestedZipFileChild);
		ensureDoesNotExist(nestedZipFileChild);
		ensureExists(openedNestedZipFileParent);
	}
}
