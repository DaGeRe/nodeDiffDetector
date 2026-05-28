package de.dagere.nodeDiffDetector.diffDetection;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.javaparser.ParseException;

import de.dagere.nodeDiffDetector.config.FolderConfig;
import de.dagere.nodeDiffDetector.config.SourceCodeFolders;
import de.dagere.nodeDiffDetector.data.Type;
import de.dagere.nodeDiffDetector.typeFinding.TypeFileFinder;

public class ChangeDetector {
   
   private static final Logger LOG = LogManager.getLogger(ChangeDetector.class);
   
   private final FolderConfig config;
   private final SourceCodeFolders sourceCodeFolders;
   
   public ChangeDetector(FolderConfig config, SourceCodeFolders sourceCodeFolders) {
      this.config = config;
      this.sourceCodeFolders = sourceCodeFolders;
   }

   public void compareClazz(final Map<Type, ClazzChangeData> changedClassesMethods, final Iterator<Type> clazzIterator) {
      final Type clazz = clazzIterator.next();
      final ClazzChangeData changeData = new ClazzChangeData(clazz);
      try {
         TypeFileFinder finder = new TypeFileFinder(config);
         final File newFile = finder.getSourceFile(sourceCodeFolders.getProjectFolder(), clazz);
         final File oldFile = finder.getSourceFile(sourceCodeFolders.getOldSources(), clazz);
         LOG.info("Comparing {} vs {}", newFile, oldFile);
         if (newFile != null && newFile.exists() && oldFile != null) {
            compareFiles(changedClassesMethods, clazzIterator, clazz, changeData, newFile, oldFile);
         } else {
            LOG.info("Class did not exist before: {}", clazz);
            changeData.addClazzChange(clazz);
            changedClassesMethods.put(clazz, changeData);
         }
      } catch (final ParseException | NoSuchElementException pe) {
         LOG.info("Class is unparsable for java parser, so to be sure it is added to the changed classes: {}", clazz);
         changeData.addClazzChange(clazz);
         changedClassesMethods.put(clazz, changeData);
         pe.printStackTrace();
      } catch (final IOException e) {
         LOG.info("Class is unparsable for java parser, so to be sure it is added to the changed classes: {}", clazz);
         changeData.addClazzChange(clazz);
         changedClassesMethods.put(clazz, changeData);
         e.printStackTrace();
      }
   }
   
   private void compareFiles(final Map<Type, ClazzChangeData> changedClassesMethods, final Iterator<Type> clazzIterator, final Type clazz,
         final ClazzChangeData changeData, final File newFile, final File oldFile) throws ParseException, IOException {
      FileComparisonUtil.getChangedMethods(newFile, oldFile, changeData);
      boolean isImportChange = false;
      TypeFileFinder finder = new TypeFileFinder(config);
      for (Type entity : changeData.getImportChanges()) {
         final File entityFile = finder.getSourceFile(sourceCodeFolders.getProjectFolder(), entity);
         if (entityFile != null && entityFile.exists()) {
            isImportChange = true;
            changeData.setChange(true);
            changeData.setOnlyMethodChange(false);
            changeData.addClazzChange(clazz);
         }
      }
      
      if (!changeData.isChange() && !isImportChange) {
         clazzIterator.remove();
         LOG.debug("Files identical: {}", clazz);
      } else {
         changedClassesMethods.put(clazz, changeData);
      }
   }
}
