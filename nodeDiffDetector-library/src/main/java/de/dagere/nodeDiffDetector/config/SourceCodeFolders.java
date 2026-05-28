package de.dagere.nodeDiffDetector.config;

import java.io.File;

/**
 * Defines the folders that are used for project processing. Typically, the {@link getProjectFolder} is the root folder of the raw project and {@link getOldSources} is a temporary folder, where all project source are copied for comparison.
 * 
 * @author DaGeRe
 */
public interface SourceCodeFolders {

   File getProjectFolder();

   File getOldSources();

}
