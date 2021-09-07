package migration.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;

import migration.ClassFactory;

public abstract class SimpleClassFactory implements ClassFactory {

	private CompilationUnit unit;

	@Override
	public ClassFactory createInterface(String packageDef, String className) {
		this.unit = new CompilationUnit(packageDef);
		this.unit.addInterface(className);
		return this;
	}

	@Override
	public ClassFactory createClass(String packageDef, String className) {
		this.unit = new CompilationUnit(packageDef);
		this.unit.addClass(className);
		return this;
	}

	@Override
	public ClassFactory createInterface(String packageDef, String className, Keyword... modifier) {
		this.unit = new CompilationUnit(packageDef);
		this.unit.addInterface(className, modifier);
		return this;
	}

	@Override
	public ClassFactory createClass(String packageDef, String className, Keyword... modifier) {
		this.unit = new CompilationUnit(packageDef);
		this.unit.addClass(className, modifier);
		return this;
	}

	@Override
	public ClassFactory addImport(String... imports) {
		if (this.unit != null) {
			for (String importString : imports) {
				if (importString.contains(".")) {
					this.unit.addImport(importString);
				}
			}
		}
		return this;
	}

	@Override
	public ClassFactory addAnnotation(AnnotationExpr... annotations) {
		if (this.unit != null) {
			ClassOrInterfaceDeclaration decl = this.unit.findFirst(ClassOrInterfaceDeclaration.class).orElseGet(null);
			if (decl != null) {
				for (AnnotationExpr annotationExpr : annotations) {
					addImport(annotationExpr.resolve().getQualifiedName());
					decl.addAnnotation(annotationExpr);
				}
			}
		}
		return this;
	}

	@Override
	public ClassFactory addAnnotation(String... annotations) {
		if (this.unit != null) {
			ClassOrInterfaceDeclaration decl = this.unit.findFirst(ClassOrInterfaceDeclaration.class).orElseGet(null);
			if (decl != null) {
				for (String annotationExpr : annotations) {
					addImport(annotationExpr);
					decl.addMarkerAnnotation(shorten(annotationExpr));
				}
			}
		}
		return this;
	}

	@Override
	public ClassFactory addVariable(String type, String name) {
		return addVariable(type, name, false, false);
	}

	@Override
	public ClassFactory addVariable(String type, String name, boolean getter, boolean setter,
			AnnotationExpr... annotations) {
		if (this.unit != null) {
			ClassOrInterfaceDeclaration decl = this.unit.findFirst(ClassOrInterfaceDeclaration.class).orElseGet(null);
			if ((decl != null) && !decl.isInterface()) {
				addImport(getImports(type).toArray(new String[0]));
				FieldDeclaration field = decl.addPrivateField(shorten(type), name);
				for (AnnotationExpr annotationExpr : annotations) {
					field.addAnnotation(annotationExpr);
				}
				if (getter) {
					MethodDeclaration get = field.createGetter();
					get.setJavadocComment("@return the " + name);
				}
				if (setter) {
					MethodDeclaration set = field.createSetter();
					set.setJavadocComment("@param " + name + " the " + name + " to set");
				}
			}
		}
		return this;
	}

	@Override
	public ClassFactory addConsts(String type, String name, String value) {
		if (this.unit != null) {
			ClassOrInterfaceDeclaration decl = this.unit.findFirst(ClassOrInterfaceDeclaration.class).orElseGet(null);
			if ((decl != null)) {
				addImport(type);
				FieldDeclaration consts = decl.addField(shorten(type), name, Modifier.Keyword.FINAL,
						Modifier.Keyword.STATIC);
				consts.getVariable(0).setInitializer(value);
			}
		}
		return this;
	}

	@Override
	public ClassFactory addImplementations(String... implementations) {
		if (this.unit != null) {
			ClassOrInterfaceDeclaration decl = this.unit.findFirst(ClassOrInterfaceDeclaration.class).orElseGet(null);
			if ((decl != null)) {
				for (String impl : implementations) {
					addImport(getImports(impl).toArray(new String[0]));
					decl.addImplementedType(shorten(impl));
				}
			}
		}
		return this;
	}

	@Override
	public ClassFactory addExtensions(String... extensions) {
		if (this.unit != null) {
			ClassOrInterfaceDeclaration decl = this.unit.findFirst(ClassOrInterfaceDeclaration.class).orElseGet(null);
			if ((decl != null)) {
				for (String impl : extensions) {
					addImport(getImports(impl).toArray(new String[0]));
					decl.addExtendedType(shorten(impl));
				}
			}
		}
		return this;
	}

	@Override
	public ClassFactory addMethod(String returnType, String name, Map<String, String> args,
			AnnotationExpr... annotations) {
		if (this.unit != null) {
			ClassOrInterfaceDeclaration decl = this.unit.findFirst(ClassOrInterfaceDeclaration.class).orElseGet(null);
			if ((decl != null)) {
				MethodDeclaration method = decl.addMethod(name, Keyword.PUBLIC);
				if (returnType != null) {
					method.setType(returnType);
				}
				if (args != null) {
					for (Entry<String, String> e : args.entrySet()) {
						method.addParameter(e.getKey(), e.getValue());
					}
				}
				for (AnnotationExpr annotationExpr : annotations) {
					method.addAnnotation(annotationExpr);
				}
			}
		}
		return this;
	}

	@Override
	public CompilationUnit build() {
		return this.unit.clone();
	}

	private String shorten(String qualifiedName) {
		if (qualifiedName.contains("<")) {
			String mainType = qualifiedName.substring(0, qualifiedName.indexOf("<"));
			String[] tmp = qualifiedName.substring(qualifiedName.indexOf("<") + 1, qualifiedName.lastIndexOf(">"))
					.split(",");
			StringJoiner sb = new StringJoiner(",");
			for (String subType : tmp) {
				sb.add(shorten(subType));
			}
			return mainType.substring(mainType.lastIndexOf(".") + 1) + "<" + sb.toString() + ">";
		}
		return qualifiedName.substring(qualifiedName.lastIndexOf(".") + 1);
	}

	private List<String> getImports(String qualifiedName) {
		List<String> res = new ArrayList<>();
		resolveTypes(qualifiedName, res);
		return res;
	}

	private static void resolveTypes(String type, List<String> res) {
		if (type.contains("<")) {
			String mainType = type.substring(0, type.indexOf("<"));
			res.add(mainType);
			String[] tmp = type.substring(type.indexOf("<") + 1, type.lastIndexOf(">")).split(",");
			for (String subType : tmp) {
				resolveTypes(subType, res);
			}
		} else {
			res.add(type);
		}
	}
}
