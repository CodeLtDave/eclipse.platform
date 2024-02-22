package org.eclipse.core.tests.filesystem.zip;

import static org.mockito.Mockito.mock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IAccessRule;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.examples.filesystem.CollapseZipHandler;
import org.eclipse.ui.examples.filesystem.ExpandZipHandler;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ZipFileSystemTest {

	private static final String PROJECT_NAME = "TestProject";
	private static final String ZIP_FILE_VIRTUAL_FOLDER_NAME = "BasicText.zip"; // Assuming the ZIP is represented as
																				// this
	// folder
	private static final String TEXT_FILE_NAME = "Text.txt";
	private static IProject project;
	private static IJavaProject javaProject;
	private static IProgressMonitor progressMonitor = new NullProgressMonitor();

	@Before
	public void setup() throws Exception {
		createProject();
		createJavaProject();
		refreshProject();
		copyZipIntoJavaProject(ZIP_FILE_VIRTUAL_FOLDER_NAME);
		refreshProject();
		expandZipFile(project.getFile(ZIP_FILE_VIRTUAL_FOLDER_NAME));
	}

	@After
	public void teardown() throws Exception {
		deleteProject();
	}

	private static void deleteProject() throws CoreException {
		if (project != null && project.exists()) {
			project.delete(true, true, progressMonitor);
			project = null;
		}
	}

	private static void createProject() throws CoreException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		project = workspace.getRoot().getProject(PROJECT_NAME);

		if (!project.exists()) {
			project.create(progressMonitor);
		}
		project.open(progressMonitor);
	}

	private static void createJavaProject() throws CoreException, JavaModelException {
		IProjectDescription description = project.getDescription();
		description.setNatureIds(new String[] { JavaCore.NATURE_ID });
		project.setDescription(description, progressMonitor);

		javaProject = JavaCore.create(project);

		IFolder srcFolder = project.getFolder("src");
		if (!srcFolder.exists()) {
			srcFolder.create(false, true, progressMonitor);
		}

		IFolder binFolder = project.getFolder("bin");
		if (!binFolder.exists()) {
			binFolder.create(false, true, progressMonitor);
		}
		javaProject.setOutputLocation(binFolder.getFullPath(), progressMonitor);

		// Set Java compliance level and JRE container
		javaProject.setOption(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_8);
		javaProject.setOption(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_8);
		javaProject.setOption(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_8);

		// Add the JRE container to the classpath
		IClasspathEntry jreContainerEntry = JavaCore.newContainerEntry(new Path(JavaRuntime.JRE_CONTAINER),
				new IAccessRule[0],
				new IClasspathAttribute[] { JavaCore.newClasspathAttribute("owner.project.facets", "java") }, false);
		IClasspathEntry srcEntry = JavaCore.newSourceEntry(srcFolder.getFullPath());

		javaProject.setRawClasspath(new IClasspathEntry[] { jreContainerEntry, srcEntry }, null);
	}

	private static void refreshProject() {
		try {
			if (project.exists() && project.isOpen()) {
				// Refreshing the specific project
				project.refreshLocal(IResource.DEPTH_INFINITE, null);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	public static void copyZipIntoJavaProject(String zipFileName) throws Exception {
		// Resolve the source file URL from the plugin bundle
		URL zipFileUrl = Platform.getBundle("org.eclipse.core.tests.resources")
				.getEntry("resources/ZipFileSystem/" + zipFileName);
		// Ensure proper conversion from URL to URI to Path
		URL resolvedURL = FileLocator.resolve(zipFileUrl); // Resolves any redirection or bundling
		java.nio.file.Path sourcePath;
		try {
			// Convert URL to URI to Path correctly handling spaces and special characters
			URI resolvedURI = resolvedURL.toURI();
			sourcePath = Paths.get(resolvedURI);
		} catch (URISyntaxException e) {
			throw new IOException("Failed to resolve URI for the ZIP file", e);
		}

		// Determine the target location within the project
		java.nio.file.Path targetPath = Paths.get(project.getLocation().toOSString(), zipFileName);

		// Copy the file using java.nio.file.Files
		Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);

		// Refresh the project to make Eclipse aware of the new file
		project.refreshLocal(IResource.DEPTH_INFINITE, null);
	}

	@Test
	public void testZipFileInProject() throws Exception {
		// Check if the "virtual folder" (ZIP file) exists in the project
		IFolder virtualFolder = project.getFolder(ZIP_FILE_VIRTUAL_FOLDER_NAME);
		Assert.assertTrue("Virtual folder should exist in the project", virtualFolder.exists());
	}

	@Test
	public void testCollapseZipFile() throws Exception {
		IFolder virtualFolder = project.getFolder(ZIP_FILE_VIRTUAL_FOLDER_NAME);
		Assert.assertTrue("Virtual folder should exist in the project", virtualFolder.exists());
		collapseZipFile(virtualFolder);
		IFile zipFile = project.getFile(ZIP_FILE_VIRTUAL_FOLDER_NAME);
		Assert.assertTrue("Virtual folder should not exist in the project", !virtualFolder.exists());
		Assert.assertTrue("ZipFile should exist in the project", zipFile.exists());
	}

	@Test
	public void testTextFileInVirtualFolder() throws Exception {
		printContents(project, PROJECT_NAME);

		IFolder virtualFolder = project.getFolder(ZIP_FILE_VIRTUAL_FOLDER_NAME);

		IFile textFile = virtualFolder.getFile(TEXT_FILE_NAME);
		Assert.assertTrue("Text.txt should exist in the virtual folder", textFile.exists());

		// Read and verify the content of Text.txt
		try (InputStreamReader isr = new InputStreamReader(textFile.getContents());
				BufferedReader reader = new BufferedReader(isr)) {
			String content = reader.readLine(); // Assuming the file has a single line with "Hello World!"
			Assert.assertEquals("The content of Text.txt should be 'Hello World!'", "Hello World!", content);
		}
	}

	@Test
	public void testDeleteZipFile() throws Exception {
		IFolder virtualFolder = project.getFolder(ZIP_FILE_VIRTUAL_FOLDER_NAME);
		Assert.assertTrue("Virtual Folder should exist before deletion", virtualFolder.exists());
		virtualFolder.delete(false, false, null);
		Assert.assertFalse("Virtual Folder should not exist after deletion", virtualFolder.exists());
		IFile zipFile = project.getFile(ZIP_FILE_VIRTUAL_FOLDER_NAME);
		Assert.assertFalse("ZIP file should not exist after deletion", zipFile.exists());
	}

	private void expandZipFile(IFile file) throws Exception {
		ExpandZipHandler expandZipHandler = new ExpandZipHandler();
		Shell shell = mock(Shell.class);
		expandZipHandler.expandZip(file, shell);
		IFolder virtualFolder = project.getFolder(file.getName());
		Assert.assertTrue("ZIP file should exist before deletion", virtualFolder.exists());
	}

	private void collapseZipFile(IFolder folder) throws Exception {
		CollapseZipHandler collapseZipHandler = new CollapseZipHandler();
		Shell shell = mock(Shell.class);
		collapseZipHandler.collapseZip(folder, shell);
		IFile zipFile = project.getFile(folder.getName());
		Assert.assertTrue("ZIP file should exist before deletion", zipFile.exists());
	}

	private static void printContents(IContainer container, String indent) throws CoreException {
		IResource[] members = container.members();
		for (IResource member : members) {
			if (member instanceof IFile) {
				System.out.println(indent + "File: " + member.getName());
			} else if (member instanceof IContainer) { // This can be IFolder or IProject
				System.out.println(indent + "Folder: " + member.getName());
				printContents((IContainer) member, indent + "  "); // Recursively print contents
			}
		}
	}

	@AfterClass
	public static void cleanup() throws CoreException, InterruptedException {
		// Optionally clean up by deleting the test project
		if (project != null && project.exists()) {
			project.delete(true, null);
		}
	}
}
