package org.eclipse.core.tests.filesystem.zip;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ZipFileSystemTest {

	private static final String PROJECT_NAME = "TestProject";
	private static final String ZIP_FILE_NAME = "BasicText.zip"; // Adjust as necessary
	private static IProject project;

	@BeforeClass
	public static void setup() throws CoreException {
		// Initialize the Eclipse workspace and project
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		project = workspace.getRoot().getProject(PROJECT_NAME);
		if (!project.exists()) {
			project.create(null);
		}
		project.open(null);
	}

	@Test
	public void testZipFileInProject() throws Exception {
		// URL to the ZIP file in the plugin bundle
		URL testCasesArchive = Platform.getBundle("org.eclipse.core.tests.resources")
				.getEntry("resources/ZipFileSystem/" + ZIP_FILE_NAME);
		URL resolvedURL = FileLocator.resolve(testCasesArchive); // Resolves any redirection or bundling
		File zipFile = new File(resolvedURL.toURI());

		// Copy the ZIP file to the project as a "virtual folder"
		IFile virtualFolder = project.getFile(ZIP_FILE_NAME);
		try (InputStream input = new FileInputStream(zipFile)) {
			if (virtualFolder.exists()) {
				virtualFolder.setContents(input, true, false, null);
			} else {
				virtualFolder.create(input, true, null);
			}
		}

		// Check if the "virtual folder" (ZIP file) exists in the project
		assertTrue("ZIP file should be present in the project", virtualFolder.exists());
	}

	@AfterClass
	public static void cleanup() throws CoreException {
		// Optionally clean up by deleting the test project
		if (project != null && project.exists()) {
			project.delete(true, null);
		}
	}
}
