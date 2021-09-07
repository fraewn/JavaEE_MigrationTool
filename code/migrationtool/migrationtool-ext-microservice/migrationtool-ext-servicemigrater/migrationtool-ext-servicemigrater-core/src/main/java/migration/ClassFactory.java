package migration;

import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.expr.AnnotationExpr;

public interface ClassFactory {

	ClassFactory createInterface(String packageDef, String className);

	ClassFactory createClass(String packageDef, String className);

	ClassFactory createInterface(String packageDef, String className, Keyword... modifier);

	ClassFactory createClass(String packageDef, String className, Keyword... modifier);

	ClassFactory addImport(String... imports);

	ClassFactory addAnnotation(AnnotationExpr... annotations);

	ClassFactory addAnnotation(String... annotations);

	ClassFactory addVariable(String type, String name);

	ClassFactory addVariable(String type, String name, boolean getter, boolean setter, AnnotationExpr... annotations);

	ClassFactory addConsts(String type, String name, String value);

	ClassFactory addImplementations(String... implementations);

	ClassFactory addExtensions(String... extensions);

	ClassFactory addMethod(String returnType, String name, Map<String, String> args, AnnotationExpr... annotations);

	CompilationUnit build();
}
