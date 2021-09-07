package migration.spring;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.StringBuilderWriter;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BinaryExpr.Operator;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import exceptions.MigrationToolRuntimeException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import migration.ClassFactory;
import migration.Converter;
import migration.model.data.Instance;
import migration.model.data.UseCase;
import migration.model.erm.Entity;
import migration.model.serviceDefintion.Direction;
import migration.model.serviceDefintion.ServiceRelation;
import operations.dto.AstDTO;
import template.ConfigTemplate;
import template.spring.ApplicationProperties;
import template.spring.DockerFile;
import template.spring.PomMaven;

public class SpringConverter extends Converter {

	/** LOGGER */
	private static final Logger LOG = LogManager.getLogger();

	public SpringConverter(ClassFactory factory) {
		super(factory);
	}

	private static final String PREFIX_PACKAGE = "core";

	private static final String JPA_PACKAGE = "javax.persistence";
	private static final String ID_ANNOTATION = "javax.persistence.Id";

	@Override
	public CompilationUnit createStartClass() {
		CompilationUnit unit = this.factory.createClass(PREFIX_PACKAGE, "Application")
				.addImport("org.springframework.boot.SpringApplication")
				.addAnnotation("org.springframework.boot.autoconfigure.SpringBootApplication")
				.addAnnotation("org.springframework.cloud.netflix.eureka.EnableEurekaClient")
				.addMethod(null, "main", Map.of("String[]", "args"))
				.build();
		ClassOrInterfaceDeclaration decl = unit.findFirst(ClassOrInterfaceDeclaration.class).get();
		MethodDeclaration main = decl.getMethodsByName("main").get(0);
		main.addModifier(Keyword.STATIC);
		BlockStmt body = new BlockStmt();
		main.setBody(body);
		MethodCallExpr call = new MethodCallExpr(new NameExpr("SpringApplication"), "run");
		call.addArgument("Application.class");
		call.addArgument("args");
		body.addStatement(call);
		return unit;
	}

	@Override
	public CompilationUnit createEntity(Entity entity, AstDTO dto) {
		List<AnnotationExpr> annotationsType = new ArrayList<>();
		for (AnnotationExpr expr : dto.getAnnotationDeclarationList()) {
			String qualifiedName = expr.resolve().getQualifiedName();
			if (qualifiedName.contains(JPA_PACKAGE) && !qualifiedName.contains(JPA_PACKAGE + ".NamedQuer")) {
				annotationsType.add(expr);
			}
		}
		String packageDef = entity.getName().substring(0, entity.getName().lastIndexOf("."));
		String name = entity.getName().substring(entity.getName().lastIndexOf(".") + 1);
		ClassFactory fac = this.factory.createClass(packageDef, name);
		for (AnnotationExpr annotationExpr : annotationsType) {
			fac = fac.addAnnotation(annotationExpr);
		}
		boolean hasId = false;
		for (String attribute : entity.getAttributes()) {
			for (FieldDeclaration fieldDecl : dto.getFields()) {
				for (VariableDeclarator varDecl : fieldDecl.getVariables()) {
					if (varDecl.getNameAsString().equals(attribute)) {
						List<AnnotationExpr> annotationsField = new ArrayList<>();
						for (AnnotationExpr expr : fieldDecl.getAnnotations()) {
							String qualifiedName = expr.resolve().getQualifiedName();
							fac = fac.addImport(qualifiedName);
							if (qualifiedName.contains(JPA_PACKAGE)) {
								if (qualifiedName.equals(ID_ANNOTATION)) {
									hasId = true;
								}
								annotationsField.add(expr);
							}
						}
						fac = fac.addVariable(fieldDecl.resolve().getType().describe(), attribute, true, true,
								annotationsField.toArray(new AnnotationExpr[0]));
					}
				}
			}
		}
		return hasId ? fac.build() : createDatabaseId(fac);
	}

	private CompilationUnit createDatabaseId(ClassFactory fac) {
		CompilationUnit unit = fac.addVariable("String", "id", true, true)
				.addImport("java.util.UUID")
				.addMethod(null, "initId", null, new MarkerAnnotationExpr(JPA_PACKAGE + ".PrePersist"))
				.build();
		ClassOrInterfaceDeclaration decl = unit.findFirst(ClassOrInterfaceDeclaration.class).get();
		MethodDeclaration initId = decl.getMethodsByName("initId").get(0);
		BlockStmt block = new BlockStmt();
		initId.setBody(block);
		BinaryExpr compare = new BinaryExpr(new FieldAccessExpr(new ThisExpr(), "id"),
				new NullLiteralExpr(), Operator.EQUALS);
		BlockStmt then = new BlockStmt();
		ExpressionStmt exprStmt = new ExpressionStmt(new AssignExpr(new FieldAccessExpr(new ThisExpr(), "id"),
				new MethodCallExpr(new MethodCallExpr(new NameExpr("UUID"), "randomUUID"), "toString"),
				com.github.javaparser.ast.expr.AssignExpr.Operator.ASSIGN));
		then.addStatement(exprStmt);
		IfStmt ifStmt = new IfStmt(compare, then, null);
		block.addStatement(ifStmt);
		return unit;
	}

	@Override
	public CompilationUnit createRepository(Entity entity) {
		String name = entity.getName().substring(entity.getName().lastIndexOf(".") + 1);
		CompilationUnit rep = this.factory.createInterface("repositories", name + "Repository")
				.addExtensions("org.springframework.data.jpa.repository.JpaRepository<" + entity.getName()
						+ ", java.lang.String>")
				.build();
		return rep;
	}

	@Override
	public List<CompilationUnit> createService(String qualifiedClassName, List<UseCase> methods) {
		List<CompilationUnit> list = new ArrayList<>();
		String name = qualifiedClassName.substring(qualifiedClassName.lastIndexOf(".") + 1);
		name = name.endsWith("Impl") ? name.substring(0, name.length() - 4) : name;

		ClassFactory facInterface = this.factory.createInterface("services", name);
		for (UseCase useCase : methods) {
			String method = useCase.getName().substring(useCase.getName().lastIndexOf(".") + 1);
			facInterface = facInterface.addMethod(null, method, null);
		}
		CompilationUnit interfaceUnit = facInterface.build();
		interfaceUnit.findAll(MethodDeclaration.class).forEach(x -> x.setBody(null));
		list.add(interfaceUnit);

		ClassFactory facImpl = this.factory.createClass("services.impl", name + "Impl")
				.addImplementations("services." + name)
				.addAnnotation("org.springframework.stereotype.Component");
		for (UseCase useCase : methods) {
			String method = useCase.getName().substring(useCase.getName().lastIndexOf(".") + 1);
			facImpl = facImpl.addMethod(null, method, null, new MarkerAnnotationExpr("Override"));
		}
		list.add(facImpl.build());

		ClassFactory facREST = this.factory.createClass("network", name + "RESTService")
				.addImplementations("services." + name)
				.addImport("org.springframework.http.MediaType")
				.addImport("org.springframework.http.HttpStatus")
				.addImport("org.springframework.http.ResponseEntit")
				.addImport("org.springframework.web.bind.annotation.RequestMethod")
				.addImport("org.springframework.beans.factory.annotation.Autowired")
				.addImport("org.springframework.web.bind.annotation.RequestMapping")
				.addAnnotation("org.springframework.web.bind.annotation.RestController")
				.addVariable("services." + name, "service")
				.addMethod("ResponseEntity<String>", "verify", null);
		for (UseCase useCase : methods) {
			String method = useCase.getName().substring(useCase.getName().lastIndexOf(".") + 1);
			facREST = facREST.addMethod(null, method, null, new MarkerAnnotationExpr("Override"));
		}
		CompilationUnit unit = facREST.build();
		ClassOrInterfaceDeclaration clazz = unit.findFirst(ClassOrInterfaceDeclaration.class).get();
		clazz.addSingleMemberAnnotation("RequestMapping", new StringLiteralExpr(("/" + name)));
		FieldDeclaration field = clazz.getFieldByName("service").get();
		field.addMarkerAnnotation("Autowired");
		MethodDeclaration method = clazz.getMethodsByName("verify").get(0);
		createRESTMethod(method);
		list.add(unit);
		return list;
	}

	@Override
	public CompilationUnit publishLanguage(String currentService, ServiceRelation relation, Set<Entity> entities) {
		if (relation.getDirection().equals(Direction.INCOMING) || !relation.getServiceIdA().equals(currentService)) {
			return null;
		}
		String name = "PublishedLanguage" + relation.getServiceIdA() + relation.getServiceIdB();
		if (relation.getServiceIdA().equals(currentService) && !relation.getDirection().equals(Direction.INCOMING)) {
			name = "PublishedLanguage" + relation.getServiceIdB();
		}
		ClassFactory facREST = this.factory.createClass("network", name)
				.addImport("org.springframework.http.MediaType")
				.addImport("org.springframework.http.HttpStatus")
				.addImport("org.springframework.http.ResponseEntit")
				.addImport("org.springframework.web.bind.annotation.RequestMethod")
				.addImport("org.springframework.beans.factory.annotation.Autowired")
				.addImport("org.springframework.web.bind.annotation.RequestMapping")
				.addAnnotation("org.springframework.web.bind.annotation.RestController")
				.addMethod("ResponseEntity<String>", "verify", null);

		if (relation.getServiceIdA().equals(currentService) && !relation.getDirection().equals(Direction.INCOMING)) {
			for (Instance instance : relation.getSharedEntities()) {
				facREST = facREST.addMethod(null, "get" + instance.getName(), null);
			}
		}
		if (relation.getDirection().equals(Direction.BIDIRECTIONAL)) {
			for (Instance instance : relation.getSharedEntities()) {
				for (Entity entity : entities) {
					for (String attr : entity.getAttributes()) {
						if (instance.getQualifiedName().equals(entity.getName() + "." + attr)) {
							facREST = facREST.addMethod(null, "get" + instance.getName(), null);
						}
					}
				}
			}
		}
		CompilationUnit unit = facREST.build();
		ClassOrInterfaceDeclaration clazz = unit.findFirst(ClassOrInterfaceDeclaration.class).get();
		clazz.addSingleMemberAnnotation("RequestMapping", new StringLiteralExpr(("/" + name)));
		MethodDeclaration method = clazz.getMethodsByName("verify").get(0);
		createRESTMethod(method);
		return unit;
	}

	private void createRESTMethod(MethodDeclaration method) {
		MemberValuePair value = new MemberValuePair("value", new StringLiteralExpr("/verify"));
		MemberValuePair produces = new MemberValuePair("produces",
				new ArrayInitializerExpr(
						new NodeList<>(new FieldAccessExpr(new NameExpr("MediaType"), "TEXT_PLAIN_VALUE"))));
		MemberValuePair methodPair = new MemberValuePair("method",
				new FieldAccessExpr(new NameExpr("RequestMethod"), "GET"));
		NodeList<MemberValuePair> pairs = new NodeList<>(value, produces, methodPair);
		AnnotationExpr expr = new NormalAnnotationExpr(new Name("RequestMapping"), pairs);
		method.addAnnotation(expr);

		BlockStmt block = new BlockStmt();
		method.setBody(block);
		VariableDeclarationExpr varDecl = new VariableDeclarationExpr(new ClassOrInterfaceType(null, "String"),
				"result");
		varDecl.getVariables().get(0).setInitializer(new StringLiteralExpr("Service started successfully"));
		ExpressionStmt exprStmt = new ExpressionStmt(varDecl);
		block.addStatement(exprStmt);
		ObjectCreationExpr returnExpr = new ObjectCreationExpr(null,
				new ClassOrInterfaceType(null, "ResponseEntity").setDiamondOperator(),
				new NodeList<>(new NameExpr("result"), new FieldAccessExpr(new NameExpr("HttpStatus"), "OK")));
		ReturnStmt returnStmt = new ReturnStmt(returnExpr);
		block.addStatement(returnStmt);
	}

	@Override
	public void processAllClasses(List<CompilationUnit> allClasses) {
		Set<String> ownPackages = new HashSet<>();
		for (CompilationUnit compilationUnit : allClasses) {
			String packageDef = compilationUnit.getPackageDeclaration().get().getNameAsString();
			if (!packageDef.startsWith(PREFIX_PACKAGE)) {
				ownPackages.add(packageDef);
			}
		}
		for (CompilationUnit compilationUnit : allClasses) {
			String packageDef = compilationUnit.getPackageDeclaration().get().getNameAsString();
			if (!packageDef.startsWith(PREFIX_PACKAGE)) {
				compilationUnit.setPackageDeclaration(PREFIX_PACKAGE + "." + packageDef);
			}
			List<ImportDeclaration> toRemove = new ArrayList<>();
			for (ImportDeclaration imports : compilationUnit.getImports()) {
				String def = imports.getNameAsString().substring(0, imports.getNameAsString().lastIndexOf("."));
				if (ownPackages.contains(def)) {
					toRemove.add(imports);
				}
			}
			for (ImportDeclaration decl : toRemove) {
				compilationUnit.remove(decl);
				compilationUnit.addImport(PREFIX_PACKAGE + "." + decl.getNameAsString());
			}
		}
	}

	@Override
	public Map<String, String> generateSpecificFiles() {
		Map<String, String> res = new HashMap<>();
		try {
			res.put("Dockerfile", generateDockerFile());
			res.put("pom.xml", generatePom());
			res.put("src/main/resources/application.properties", generateApplicationProperties());
		} catch (IOException | TemplateException e) {
			LOG.error(e.getMessage());
			throw new MigrationToolRuntimeException(e.getMessage());
		}
		return res;
	}

	private String generateDockerFile() throws IOException, TemplateException {
		Template temp = ConfigTemplate.getInstance().getTemplate("spring/dockerFile.ftl");
		Writer writer = new StringBuilderWriter();
		temp.process(new DockerFile().getConfig(), writer);
		return writer.toString();
	}

	private String generatePom() throws IOException, TemplateException {
		Template temp = ConfigTemplate.getInstance().getTemplate("spring/pomMaven.ftl");
		Writer writer = new StringBuilderWriter();
		temp.process(new PomMaven().getConfig(), writer);
		return writer.toString();
	}

	private String generateApplicationProperties() throws IOException, TemplateException {
		Template temp = ConfigTemplate.getInstance().getTemplate("spring/applicationProperties.ftl");
		Writer writer = new StringBuilderWriter();
		temp.process(new ApplicationProperties().getConfig(), writer);
		return writer.toString();
	}
}
