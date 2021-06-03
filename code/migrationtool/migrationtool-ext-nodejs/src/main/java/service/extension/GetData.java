package service.extension;

import model.GraphFoundationDAO;
import model.graph.genericAttributes.PassedParameter;
import model.graph.node.AbstractClassNode;
import model.graph.node.ClassNode;
import model.graph.node.InterfaceNode;
import model.graph.node.JavaImplementation;
import model.graph.node.entityAttributes.Constructor;
import model.graph.node.entityAttributes.Field;
import model.graph.node.entityAttributes.Annotation;
import model.graph.node.entityAttributes.AnnotationNameValuePair;
import model.graph.relation.MethodCallRelation;
import model.graph.relation.entityAttributes.Method;
import operations.ModelService;
import operations.dto.ClassDTO;
import parser.utils.AnnotationResolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.google.gson.Gson;

import org.ini4j.Ini;
import java.io.File;

// TODO rename class to transformIntoGraphModel or something like that 
public class GetData extends ModelService<List<ClassDTO>, String> {
	List<ClassDTO> classDTOList = new ArrayList<ClassDTO>();

	// method that inits the ETL and persistence processes
	@Override
	public String save(List<ClassDTO> input) {
		classDTOList = input;
		List<JavaImplementation> javaImplementationsList = new ArrayList<JavaImplementation>();
		
		// hier kommt eine liste an dtos rein
		// für jedes dto wird zunächst ein klassen knoten erstellt
		// (=javaImplementation)
		// danach wird geprüft ob es irgendwelche methoden/interface/extends
		// abhängigkeiten gibt und die entsprechenden Kanten und fehlenden
		// Knoten werden ebenfalls eingefügt
		// danach wird auf entitäten und funktionalitäten geprüft und diese
		// mittels Knoten und Kanten eingefügt
		System.out.println("----------starting reading from dto");
		for (ClassDTO classDTO : classDTOList) {
			JavaImplementation javaImplementation = transformClassDTOtoJavaImplementation(classDTO);

			// true, wenn die Annotation "javax.persistence.Entity" da drin ist,
			// ansonsten false
			// Type means class - jetzt sucht der am Klassenkopf die Annotation
			// und sagt dann ob er da eine gefunden hat
			// AnnotationVisitor annotationVisitor = new
			// AnnotationVisitor("javax.persistence.Entity", TargetTypes.TYPE);
			/*
			 * System.out.print("\nHas Javax.Persistence.Entity: ");
			 * System.out.println(Optional.ofNullable(classDTO.getJavaClass().
			 * accept(annotationVisitor, null)).orElse(false));
			 */
			System.out.println(javaImplementation.toString());
			// for(MethodDeclaration method : classDTO.getMethods()){
			// System.out.println("Body: " + body);
			// System.out.println(method.toString());
			// manageClassPersistence("Test.zwei.drei", fieldsAsJsonObjects);
			// }
			javaImplementationsList.add(javaImplementation);
		}
		for(JavaImplementation javaImplementation: javaImplementationsList){
		System.out.println(javaImplementation);
			processPersistence(javaImplementation);
		}
		System.out.println("----------stopping reading from dto");
		return null;
	}

	// Persistence Management
	// TODO: move to specific class for that
	public void processPersistence(JavaImplementation javaImplementation) {
		try {
			String url = getEnvironment("remote", "ip", "port", "portType");
			String username = getCredential("remote", "username");
			String password = getCredential("remote", "password");
			System.out.println(url + username + password);

			GraphFoundationDAO graphFoundationDAO = GraphFoundationDAO.getInstance();
			graphFoundationDAO.initConnection(url, username, password);
			
			graphFoundationDAO.persistFullClassNode(javaImplementation); 
			// graphFoundationDAO.getClassNode("BatchServiceImpl.java");
			// boolean test = graphFoundationDAO.persistClassNode("TestClass",
			// "TestClass.java");
			// boolean test2 =
			// graphFoundationDAO.setListAttributeInClassNode(className,
			// javaClassName, "field",
			// fieldsAsJsonObjects);
			// System.out.println(test);

			graphFoundationDAO.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println(e.getStackTrace());
		}
	}

	// GRAPH_NODES: Class, Abstract Class or Interface
	public JavaImplementation transformClassDTOtoJavaImplementation(ClassDTO classDTO) {
		JavaImplementation javaImplementation;

		// extract and set implementation type (class, interface, abstract
		// class)
		if (classDTO.getJavaClass().isInterface()) {
			javaImplementation = new InterfaceNode();
		} else if (classDTO.getJavaClass().isAbstract()) {
			javaImplementation = new AbstractClassNode();
		} else {
			javaImplementation = new ClassNode();
		}

		// set String variables
		javaImplementation.setPath(classDTO.getFullName());
		javaImplementation.setModules(convertGenericListToString(extractPathStructure(javaImplementation.getPath())));
		javaImplementation.setClassName(extractPathStructure(javaImplementation.getPath()).get(javaImplementation.getModules().size() - 1));
		javaImplementation.setJavaClassName(javaImplementation.getClassName() + ".java");
		javaImplementation.setCompleteClassCode(classDTO.getJavaClass().toString());
		String moduleDeclaration = classDTO.getModuleDeclaration() == null ? "" : classDTO.getModuleDeclaration();
		javaImplementation.setModuleDeclaration(moduleDeclaration);

		// set List<String> variables containing plain Strings
		List<String> implementedInterfaces = new ArrayList<String>();
		List<String> imports = new ArrayList<String>();
		List<String> extensions = new ArrayList<String>();
		for (ImportDeclaration importDeclaration : classDTO.getImports()) {
			imports.add("'" + importDeclaration.getNameAsString() + "'");
		}
		javaImplementation.setImports(imports);
		for (ClassOrInterfaceType implementedInterface : classDTO.getImplementations()) {
			implementedInterfaces.add("'" + implementedInterface.getNameAsString() + "'");
		}
		javaImplementation.setImplementedInterfaces(implementedInterfaces);
		for (ClassOrInterfaceType extendedClass : classDTO.getExtensions()) {
			extensions.add("'" + extendedClass.getNameAsString() + "'");
		}
		javaImplementation.setExtensions(extensions);

		// set List<String> variables containing JSON Objects which were
		// converted to Strings
		javaImplementation.setFieldsAsJsonObjectStrings(getFieldsAsJSONStringList(classDTO.getFields()));
		javaImplementation
				.setConstructorsAsJsonObjectStrings(getConstructorsAsJSONStringList(classDTO.getConstructors()));
		javaImplementation.setAnnotationsAsJsonObjectStrings(
				getAnnotationsAsJSONStringList(classDTO.getAnnotationDeclarationList()));

		return javaImplementation;
	}

	// GRAPH_RELATION: MethodCall between Class, Interface and AbstractClass
	// Nodes
	public MethodCallRelation extractMethodRelation(List<MethodDeclaration> methodDeclarationList) {
		MethodCallRelation methodCallRelation = new MethodCallRelation();
		// methodCallRelation.setProvidingJavaImplementation(methodDeclaration.getClass().getName().toString());
		for (MethodDeclaration methodDeclaration : methodDeclarationList) {
			methodCallRelation.setMethod(transformDeclarationToMethod(methodDeclaration));
		}
		return methodCallRelation;
	}

	// // ++++++++++++++++++++ Transformation Methods
	// ++++++++++++++++++++++++++++++++
	// TODO: move to Transformator.java
	public Method transformDeclarationToMethod(MethodDeclaration methodDeclaration) {
		Method method = new Method();
		method.setType(methodDeclaration.getTypeAsString());
		method.setName(methodDeclaration.getNameAsString());
		method.setModifiers(convertGenericListToString(methodDeclaration.getModifiers()));
		method.setAnnotations(convertAnnotationListToString(methodDeclaration.getAnnotations()));
		method.setParameters(extractParameters(methodDeclaration.getParameters()));
		method.setExceptions(convertGenericListToString(methodDeclaration.getThrownExceptions()));

		/*
		 * parameterList = methodDeclaration.getParameters(); for (Parameter
		 * parameter : parameterList) { System.out.println("Paramter complete: "
		 * + parameter.toString()); methodParameterAnnotations =
		 * parameter.getAnnotations(); for (AnnotationExpr
		 * methodParameterAnnotation : methodParameterAnnotations) {
		 * System.out.println("Parameter Annotation: " +
		 * methodParameterAnnotation); }
		 * 
		 * System.out.println( "Parameter type: " + parameter.getType() +
		 * "   Parameter name: " + parameter.getName().toString()); }
		 */

		/*
		 * exceptionList = methodDeclaration.getThrownExceptions(); for
		 * (ReferenceType exception : exceptionList) {
		 * System.out.println("Thrown Exception: " + exception.toString()); }
		 */

		String body;
		try {
			body = methodDeclaration.getBody().get().toString();
		} catch (Exception ex) {
			body = null;
		}
		method.setBody(body);

		return method;
	}

	public List<String> getAnnotationsAsJSONStringList(List<AnnotationExpr> annotationExpressions) {
		List<String> annotationsAsJsonObjectStrings = new ArrayList<String>();
		for (Annotation annotation : getAnnotationList(annotationExpressions)) {
			String annotationAsJsonObject = new Gson().toJson(annotation);
			annotationsAsJsonObjectStrings.add("'" + annotationAsJsonObject + "'");
		}
		return annotationsAsJsonObjectStrings;
	}

	public List<Annotation> getAnnotationList(List<AnnotationExpr> annotationExpressions) {
		List<Annotation> annotationList = new ArrayList<Annotation>();
		for (AnnotationExpr annotationExpr : annotationExpressions) {
			Annotation annotation = new Annotation();
			annotation.setAnnotation(annotationExpr.getNameAsString());
			List<AnnotationNameValuePair> parameterList = new ArrayList<AnnotationNameValuePair>();
			for (MemberValuePair pair : AnnotationResolver.getParamaterList(annotationExpr)) {
				AnnotationNameValuePair annoPair = new AnnotationNameValuePair();
				annoPair.setName(pair.getNameAsString());
				annoPair.setValue(pair.getValue().toString());
				annoPair.clear();
				parameterList.add(annoPair);
			}
			annotation.setParameters(parameterList);
			annotationList.add(annotation);
		}
		return annotationList;
	}

	public List<Field> getFieldList(List<FieldDeclaration> fields) {
		List<Field> fieldList = new ArrayList<Field>();
		for (FieldDeclaration fieldDeclaration : fields) {
			// create an empty Field
			Field field = new Field();
			List<String> names = field.getNames();
			// names and initializer
			for (VariableDeclarator fieldVariable : fieldDeclaration.getVariables()) {
				// System.out.println("Field variable: " +
				// fieldVariable.getNameAsString());
				names.add(fieldVariable.getNameAsString());
				fieldVariable.getInitializer().ifPresent((init) -> {
					// System.out.println("Field Initializer: " + init);
					String initializer = field.getInitializer();
					initializer = init.toString();
					field.setInitializer(initializer);
				});
			}
			// type, modifiers and annotations
			field.setType(fieldDeclaration.getElementType().asString());
			field.setModifiers(convertGenericListToString(fieldDeclaration.getModifiers()));
			field.setAnnotations(getAnnotationList(fieldDeclaration.getAnnotations()));
			fieldList.add(field);
		}
		return fieldList;
	}

	public List<String> getConstructorsAsJSONStringList(List<ConstructorDeclaration> constructors) {
		List<String> constructorsAsJsonObjectStrings = new ArrayList<String>();
		for (ConstructorDeclaration constructorDeclaration : constructors) {
			// create constructor entity
			Constructor constructor = new Constructor();

			// name, modifiers, annotations and parameters
			constructor.setName(constructorDeclaration.getNameAsString());
			constructor.setModifiers(convertGenericListToString(constructorDeclaration.getModifiers()));
			constructor.setAnnotations(getAnnotationList(constructorDeclaration.getAnnotations()));
			constructor.setParameters(extractParameters(constructorDeclaration.getParameters()));
			// constructorDeclaration has no fields

			// body
			String body = constructor.getBody();
			try {
				body = constructorDeclaration.getBody().toString();
			} catch (Exception ex) {
				body = "";
			}
			constructor.setBody(body);

			// create constructor Json object string and add to list in the
			// right format
			String constructorAsJsonObject = new Gson().toJson(constructor);
			constructorsAsJsonObjectStrings.add("'" + constructorAsJsonObject + "'");
		}
		return constructorsAsJsonObjectStrings;
	}

	public List<String> getFieldsAsJSONStringList(List<FieldDeclaration> fields) {
		List<String> fieldsAsJsonObjectStrings = new ArrayList<String>();
		for (Field field : getFieldList(fields)) {
			fieldsAsJsonObjectStrings.add("'" + new Gson().toJson(field) + "'");
		}
		return fieldsAsJsonObjectStrings;
	}

	public List<PassedParameter> extractParameters(List<Parameter> parameterList) {
		List<PassedParameter> passedParameterList = new ArrayList<PassedParameter>();
		for (Parameter parameter : parameterList) {
			PassedParameter passedParameter = new PassedParameter();
			passedParameter.setName(parameter.getName().toString());
			passedParameter.setType(parameter.getType().toString());
			passedParameter.setAnnotations(getAnnotationList(parameter.getAnnotations()));
			passedParameter.setModifiers(convertGenericListToString(parameter.getModifiers()));
			passedParameterList.add(passedParameter);
		}
		return passedParameterList;
	}

	// ++++++++++++++++++++ Helper Methods ++++++++++++++++++++++++++++++++
	// TODO: move to EnvironmentUtils.java Class
	public String getCredential(String infrastructure, String key) throws Exception {
		Ini ini = new Ini(new File("src/main/resources/neo4j_conf.ini"));
		return ini.get(infrastructure, key);
	}

	// TODO: move to EnvironmentUtils.java Class
	public String getEnvironment(String environment, String key_ip, String key_port, String key_portType)
			throws Exception {
		Ini ini = new Ini(new File("src/main/resources/neo4j_conf.ini"));
		String portType = ini.get(environment, key_portType);
		String ip = ini.get("remote", key_ip);
		String port = ini.get("remote", key_port);
		return portType + "://" + ip + ":" + port;
	}

	// TODO: move to ConverterUtils.java class (converts a path to a list of
	// strings) (path is not Env but is necessary for process)
	public List<String> extractPathStructure(String fullPath) {
		List<String> modules = new ArrayList<String>();
		if (fullPath.contains(".")) {
			modules = Arrays.asList(fullPath.split(Pattern.quote(".")));
		} else {
			modules.add(fullPath);
		}
		return modules;
	}

	// TODO: move to ConverterUtils.java class
	public List<String> convertGenericListToString(List list) {
		List<String> convertedObjects = new ArrayList<String>();
		for (Object obj : list) {
			String tmp = "'" + obj.toString() + "'";
			convertedObjects.add(tmp);
		}
		return convertedObjects;
	}

	// TODO: move to Converter.java class
	public List<String> convertAnnotationListToString(List<AnnotationExpr> list) {
		List<String> convertedObjects = new ArrayList<String>();
		for (AnnotationExpr annotationExpr : list) {
			convertedObjects.add(annotationExpr.getNameAsString());
		}
		return convertedObjects;
	}
}
