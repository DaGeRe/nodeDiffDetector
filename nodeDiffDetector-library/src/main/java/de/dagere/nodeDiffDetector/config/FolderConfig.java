package de.dagere.nodeDiffDetector.config;

import java.util.List;

/**
 * Defines the interfaces of one project folder tree instance.
 * 
 * For example, for java, it is typically:
 * <ul>
 *  <li>src/main/java for {@link getClazzFolders},</li>
 *  <li>src/test/java for {@link getTestClazzFolders} </li>
 *  <li>and both for {@link getAllClazzFolders}.</li>
 * </ul>
 * 
 * @author DaGeRe
 */
public interface FolderConfig {

   List<String> getClazzFolders();

   List<String> getTestClazzFolders();

   List<String> getAllClazzFolders();
}
