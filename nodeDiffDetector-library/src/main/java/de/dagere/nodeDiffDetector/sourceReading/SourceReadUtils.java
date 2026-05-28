package de.dagere.nodeDiffDetector.sourceReading;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.ObjectCreationExpr;

import de.dagere.nodeDiffDetector.data.MethodCall;

public class SourceReadUtils {
   
   private static final Logger LOG = LogManager.getLogger(SourceReadUtils.class);
   
   public static Map<String, TypeDeclaration<?>> getNamedClasses(final Node parent, final String alreadyReadPrefix) {
      final Map<String, TypeDeclaration<?>> foundDeclaredClasses = new HashMap<>();
      for (final Node child : parent.getChildNodes()) {
         LOG.trace(child.getClass());
         if (child instanceof ClassOrInterfaceDeclaration) {
            ClassOrInterfaceDeclaration classOrInterfaceDeclaration = (ClassOrInterfaceDeclaration) child;
            final String ownName = classOrInterfaceDeclaration.getNameAsString();
            if (alreadyReadPrefix.equals("")) {
               foundDeclaredClasses.put(ownName, classOrInterfaceDeclaration);
               foundDeclaredClasses.putAll(getNamedClasses(child, ownName));
            } else {
               foundDeclaredClasses.put(alreadyReadPrefix + "$" + ownName, classOrInterfaceDeclaration);
               foundDeclaredClasses.putAll(getNamedClasses(child, alreadyReadPrefix + "$" + ownName));
            }

         } else if (child instanceof EnumDeclaration) {
            EnumDeclaration enumDeclaration = (EnumDeclaration) child;
            final String ownName = enumDeclaration.getNameAsString();
            if (alreadyReadPrefix.equals("")) {
               foundDeclaredClasses.put(ownName, enumDeclaration);
               foundDeclaredClasses.putAll(getNamedClasses(child, ownName));
            } else {
               foundDeclaredClasses.put(alreadyReadPrefix + "$" + ownName, enumDeclaration);
               foundDeclaredClasses.putAll(getNamedClasses(child, alreadyReadPrefix + "$" + ownName));
            }
         } else {
            foundDeclaredClasses.putAll(getNamedClasses(child, alreadyReadPrefix));
         }

      }
      return foundDeclaredClasses;
   }
   
   public static CallableDeclaration<?> getMethod(final MethodCall currentTraceElement, final CompilationUnit cu) {
      if (currentTraceElement.getClazz().contains("$")) {
         final String indexString = currentTraceElement.getClazz().split("\\$")[1];
         if (indexString.matches("[0-9]+")) {
            return getMethodAnonymousClass(currentTraceElement, cu, indexString);
         } else {
            return getMethodNamedInnerClass(currentTraceElement, cu);
         }
      }
      CallableDeclaration<?> method = null;
      for (final Node node : cu.getChildNodes()) {
         if (node instanceof ClassOrInterfaceDeclaration) {
            MethodReader reader = new MethodReader((ClassOrInterfaceDeclaration) node);
            method = reader.getMethod(node, currentTraceElement);
            if (method != null) {
               break;
            }
         } else if (node instanceof EnumDeclaration) {
            MethodReader reader = new MethodReader(null);
            method = reader.getMethod(node, currentTraceElement);
            if (method != null) {
               break;
            }
         }
      }
      LOG.trace(currentTraceElement.getClazz() + " " + currentTraceElement.getMethod());
      LOG.trace(method);
      return method;
   }

   private static CallableDeclaration<?> getMethodNamedInnerClass(final MethodCall currentTraceElement, final CompilationUnit cu) {
      final Map<String, TypeDeclaration<?>> namedClasses = SourceReadUtils.getNamedClasses(cu, "");
      final String clazz = currentTraceElement.getClazz().substring(currentTraceElement.getClazz().lastIndexOf('.') + 1);
      final TypeDeclaration<?> declaration = namedClasses.get(clazz);
      MethodReader reader = new MethodReader((ClassOrInterfaceDeclaration) null);
      return reader.getMethod(declaration, currentTraceElement);
   }

   private static CallableDeclaration<?> getMethodAnonymousClass(final MethodCall currentTraceElement, final CompilationUnit cu, final String indexString) {
      final int index = Integer.parseInt(indexString) - 1;
      final List<NodeList<BodyDeclaration<?>>> anonymousClazzes = getAnonymusClasses(cu);
      final NodeList<BodyDeclaration<?>> nodes = anonymousClazzes.get(index);
      MethodReader reader = new MethodReader(null);
      for (final Node candidate : nodes) {
         LOG.trace(candidate);
         final CallableDeclaration<?> ret = reader.getMethod(candidate, currentTraceElement);
         if (ret != null) {
            return ret;
         }
      }
      return null;
   }
   
   static List<NodeList<BodyDeclaration<?>>> getAnonymusClasses(final Node parent) {
      final List<NodeList<BodyDeclaration<?>>> foundAnonymousClasses = new LinkedList<>();
      for (final Node child : parent.getChildNodes()) {
         if (child instanceof ObjectCreationExpr) {
            final ObjectCreationExpr expr = (ObjectCreationExpr) child;
            if (expr.getAnonymousClassBody().isPresent()) {
               foundAnonymousClasses.add(expr.getAnonymousClassBody().get());
            } else {
               foundAnonymousClasses.addAll(getAnonymusClasses(child));
            }
         } else {
            foundAnonymousClasses.addAll(getAnonymusClasses(child));
         }
      }
      return foundAnonymousClasses;
   }
}
