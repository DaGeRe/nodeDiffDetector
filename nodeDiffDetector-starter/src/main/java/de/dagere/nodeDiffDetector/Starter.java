package de.dagere.nodeDiffDetector;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import de.dagere.nodeDiffDetector.config.FolderConfig;
import de.dagere.nodeDiffDetector.config.SourceCodeFolders;
import de.dagere.nodeDiffDetector.data.Type;
import de.dagere.nodeDiffDetector.diffDetection.ChangeDetector;
import de.dagere.nodeDiffDetector.diffDetection.ClazzChangeData;
import de.dagere.nodeDiffDetector.typeFinding.TypeFinder;
import de.dagere.nodeDiffDetector.utils.JavaParserProvider;
import com.github.javaparser.ast.CompilationUnit;

public class Starter {

    public static void main(String[] args) {
        args = new String[2];
        args[0] = "/tmp/JacksonDatabind_1_buggy/New";
        args[1] = "/tmp/JacksonDatabind_1_buggy/Old";
        if (args.length < 2) {
            System.err.println("Usage: Starter <old_file> <new_file>");
            System.exit(1);
        }

        File oldFile = new File(args[0]);
        File newFile = new File(args[1]);

        try {
            // Use JavaParser to find the types in the new file
            CompilationUnit newCu = JavaParserProvider.parse(newFile.listFiles()[0]);
            List<Type> types = TypeFinder.getClazzEntities(newCu);

            if (types.isEmpty()) {
                System.err.println("No classes found in the new file.");
                System.exit(1);
            }

            // We will compare each type found in the new file.
            // To make this work with ChangeDetector, we need to mock FolderConfig and SourceCodeFolders.
            FolderConfig config = new FolderConfig() {
                @Override
                public List<String> getClazzFolders() { return Collections.singletonList(""); }
                @Override
                public List<String> getTestClazzFolders() { return Collections.emptyList(); }
                @Override
                public List<String> getAllClazzFolders() { return Collections.singletonList(""); }
            };

            SourceCodeFolders sourceCodeFolders = new SourceCodeFolders() {
                @Override
                public File getProjectFolder() {
                    // This is used by TypeFileFinder to find the new file.
                    // Since we have the file directly, we return its parent directory.
                    return newFile;
                }

                @Override
                public File getOldSources() {
                    // This is used by TypeFileFinder to find the old file.
                    return oldFile;
                }
            };

            ChangeDetector detector = new ChangeDetector(config, sourceCodeFolders);
            Map<Type, ClazzChangeData> changedClassesMethods = new HashMap<>();
            
            // We need an iterator of types to pass to compareClazz
            Iterator<Type> typeIterator = types.iterator();
            
            // Since compareClazz processes one type at a time from the iterator:
            while (typeIterator.hasNext()) {
                detector.compareClazz(changedClassesMethods, typeIterator);
            }

            // Output as JSON to stdout
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            System.out.println(mapper.writeValueAsString(changedClassesMethods));

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
