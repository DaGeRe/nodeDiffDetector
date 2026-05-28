package de.dagere.nodeDiffDetector.typeFinding;

import java.util.LinkedList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;

import de.dagere.nodeDiffDetector.data.MethodCall;
import de.dagere.nodeDiffDetector.data.Type;

public class TypeFinder {
   public static String getContainingClazz(final Node statement) {
      String clazz = "";
      Node current = statement;
      while (current.getParentNode().isPresent()) {
         if (current instanceof ClassOrInterfaceDeclaration || current instanceof EnumDeclaration || current instanceof AnnotationDeclaration) {
            TypeDeclaration<?> declaration = (TypeDeclaration<?>) current;
            String name = declaration.getNameAsString();
            if (!clazz.isEmpty()) {
               clazz = name + "$" + clazz;
            } else {
               clazz = name;
            }
         }
         current = current.getParentNode().get();

      }
      return clazz;
   }

   public static TypeDeclaration<?> findClazz(final Type entity, final List<Node> nodes) {
      TypeDeclaration<?> declaration = null;
      for (final Node node : nodes) {
         if (node instanceof TypeDeclaration<?>) {
            final TypeDeclaration<?> temp = (TypeDeclaration<?>) node;
            final String nameAsString = temp.getNameAsString();
            if (nameAsString.equals(entity.getSimpleClazzName())) {
               declaration = (ClassOrInterfaceDeclaration) node;
               break;
            } else {
               if (entity.getSimpleClazzName().startsWith(nameAsString + MethodCall.CLAZZ_SEPARATOR)) {
                  Type inner = new Type(entity.getSimpleClazzName().substring(nameAsString.length() + 1), entity.getModule());
                  declaration = findClazz(inner, node.getChildNodes());
               }
            }
         }
      }
      return declaration;
   }
   
   static List<String> getTypes(final Node node, final String parent, final String clazzSeparator) {
      final List<String> clazzes = new LinkedList<>();
      if (node instanceof ClassOrInterfaceDeclaration) {
         addClazzesOrInterfaces(node, parent, clazzSeparator, clazzes);
      } else if (node instanceof EnumDeclaration) {
         addEnums(node, parent, clazzSeparator, clazzes);
      } else {
         for (final Node child : node.getChildNodes()) {
            clazzes.addAll(getTypes(child, parent, MethodCall.CLAZZ_SEPARATOR));
         }
      }
      return clazzes;
   }

   private static void addEnums(final Node node, final String parent, final String clazzSeparator, final List<String> clazzes) {
      final EnumDeclaration enumDecl = (EnumDeclaration) node;
      final String enumName = parent.length() > 0 ? parent + clazzSeparator + enumDecl.getName().getIdentifier() : enumDecl.getName().getIdentifier();
      clazzes.add(enumName);
      for (final Node child : node.getChildNodes()) {
         clazzes.addAll(getTypes(child, enumName, MethodCall.CLAZZ_SEPARATOR));
      }
   }

   private static void addClazzesOrInterfaces(final Node node, final String parent, final String clazzSeparator, final List<String> clazzes) {
      final ClassOrInterfaceDeclaration clazz = (ClassOrInterfaceDeclaration) node;
      final String clazzname = parent.length() > 0 ? parent + clazzSeparator + clazz.getName().getIdentifier() : clazz.getName().getIdentifier();
      clazzes.add(clazzname);
      for (final Node child : node.getChildNodes()) {
         clazzes.addAll(getTypes(child, clazzname, MethodCall.CLAZZ_SEPARATOR));
      }
   }

   public static List<String> getTypes(final CompilationUnit cu) {
      final List<String> clazzes = new LinkedList<>();
      for (final Node node : cu.getChildNodes()) {
         clazzes.addAll(getTypes(node, "", "$"));
      }
      return clazzes;
   }

   public static List<Type> getClazzEntities(final CompilationUnit cu) {
      List<String> clazzes = TypeFinder.getTypes(cu);
      List<Type> entities = new LinkedList<>();
      for (String clazz : clazzes) {
         entities.add(new Type(clazz, ""));
      }
      return entities;
   }
   
   public static List<ClassOrInterfaceDeclaration> getClazzDeclarations(final Node node) {
      final List<ClassOrInterfaceDeclaration> clazzes = new LinkedList<>();
      if (node instanceof ClassOrInterfaceDeclaration) {
         final ClassOrInterfaceDeclaration clazz = (ClassOrInterfaceDeclaration) node;
         clazzes.add(clazz);
         for (final Node child : node.getChildNodes()) {
            clazzes.addAll(getClazzDeclarations(child));
         }
      } else if (node instanceof EnumDeclaration) {
         for (final Node child : node.getChildNodes()) {
            clazzes.addAll(getClazzDeclarations(child));
         }
      } else {
         for (final Node child : node.getChildNodes()) {
            clazzes.addAll(getClazzDeclarations(child));
         }
      }
      return clazzes;
   }
   
   public static List<ClassOrInterfaceDeclaration> getClazzDeclarations(final CompilationUnit cu) {
      final List<ClassOrInterfaceDeclaration> clazzes = new LinkedList<>();
      for (final Node node : cu.getChildNodes()) {
         clazzes.addAll(getClazzDeclarations(node));
      }
      return clazzes;
   }

}
