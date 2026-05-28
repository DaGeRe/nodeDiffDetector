package de.dagere.nodeDiffDetector.testUtils;

import java.io.File;
import java.util.Arrays;

import org.mockito.Mockito;

import de.dagere.nodeDiffDetector.config.FolderConfig;

public class TestConstants {

   public static final File TEST_RESOURCES = new File("src/test/resources");

   public static final FolderConfig DEFAULT_FOLDERS = Mockito.mock(FolderConfig.class);
   static {
      Mockito.when(DEFAULT_FOLDERS.getClazzFolders()).thenReturn(Arrays.asList(new String[] { "src/main/java", "src/java" }));
      Mockito.when(DEFAULT_FOLDERS.getAllClazzFolders()).thenReturn(Arrays.asList(new String[] { "src/main/java", "src/java" }));
   }
}
