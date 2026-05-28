package de.dagere.nodeDiffDetector.typeFinding;

import java.io.File;
import java.util.List;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsIterableContaining;
import org.junit.jupiter.api.Test;

import de.dagere.nodeDiffDetector.testUtils.TestConstants;
import de.dagere.nodeDiffDetector.typeFinding.TypeFileFinder;

public class TestPackageFinder {

   @Test
   public void testDependencyModule() {
      final List<String> lowestPackage = new TypeFileFinder(TestConstants.DEFAULT_FOLDERS).getTypes(new File("."));
      System.out.println(lowestPackage);
      MatcherAssert.assertThat(lowestPackage, IsIterableContaining.hasItem("de.dagere.nodeDiffDetector.config.FolderConfig"));
      MatcherAssert.assertThat(lowestPackage, Matchers.not(IsIterableContaining.hasItem("de.dagere.nodeDiffDetector.config.FolderConfig.FolderConfig")));
      MatcherAssert.assertThat(lowestPackage, IsIterableContaining.hasItem("de.dagere.nodeDiffDetector.config.SourceCodeFolders"));
   }
}
