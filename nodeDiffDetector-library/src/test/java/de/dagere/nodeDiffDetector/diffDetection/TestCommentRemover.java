package de.dagere.nodeDiffDetector.diffDetection;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.comments.Comment;

import de.dagere.nodeDiffDetector.data.MethodCall;
import de.dagere.nodeDiffDetector.data.Type;
import de.dagere.nodeDiffDetector.typeFinding.TypeFinder;
import de.dagere.nodeDiffDetector.utils.JavaParserProvider;

/**
 * This class tests whether comments can be removed; therefore, it contains some comments itself
 */
public class TestCommentRemover {

   /**
    * This is a test method (and this comment should be removed)
    * 
    * @throws FileNotFoundException
    */
   @Test
   public void removeComments() throws FileNotFoundException {
      File testFile = new File("src/test/java/de/dagere/nodeDiffDetector/diffDetection/TestCommentRemover.java");
      CompilationUnit unit = JavaParserProvider.parse(testFile);

      // This comment should be removed
      TypeDeclaration<?> clazz = TypeFinder.findClazz(new Type("de.dagere.nodeDiffDetector.diffDetection.TestCommentRemover", ""), unit.getChildNodes());
      new CommentRemover(clazz);

      Assert.assertFalse(clazz.getComment().isPresent());
      MethodDeclaration method = clazz.getMethods().get(0);
      Assert.assertFalse(method.getComment().isPresent());
      for (Node child : method.getChildNodes()) {
         Assert.assertFalse(child instanceof Comment);
      }
   }
}
