package service.extension;

import data.TargetTypes;
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
import parser.visitors.AnnotationVisitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.TypeParameter;
import com.google.gson.Gson;

import org.ini4j.Ini;
import java.io.File;
import java.nio.charset.StandardCharsets;


// TODO rename class to transformIntoGraphModel or something like that 
public class GetData extends ModelService<List<ClassDTO>, String> {
	
	List<ClassDTO> classDTOList = new ArrayList<ClassDTO>(); 
			
	// Type means class - jetzt sucht der am Klassenkopf die Annotation und sagt dann ob er da eine gefunden hat
	AnnotationVisitor annotationVisitor = new AnnotationVisitor("javax.persistence.Entity", TargetTypes.TYPE);
	
	// ++++++++++++++ INPUT +++++++++++++++++++++
	// class
	List<ImportDeclaration> importDeclarationList;
	List<ClassOrInterfaceType> implementsList; 
	List<ClassOrInterfaceType> extendsList;
	List<ConstructorDeclaration> constructors;
	List<FieldDeclaration> fields;
	
	List<MethodDeclaration> methods;
	List<AnnotationExpr> annotationExprs;
	List<TypeParameter> typeParameterList;
	String classType; 

	// constructors
	String constructorName; 
	String constuctorBody; 
	List<Modifier> constructorModifiers; 
	List<Parameter> constructorParameters; 
	List<AnnotationExpr> constructorParameterAnnotations; 
	
	// fields
	String fieldElementType; 
	List<VariableDeclarator> fieldVariables; 
	List<Modifier> fieldModifiers; 
	List<AnnotationExpr> fieldAnnotations; 
	
	// methods
	String returnType; 
	String methodName; 
	String body; 
	List<Modifier> methodModifiers; 
	List<AnnotationExpr> methodAnnotations; 
	List<Parameter> parameterList; 
	List<AnnotationExpr> methodParameterAnnotations; 
	List<ReferenceType> exceptionList; 
	
	
	// ++++++++++++++ OUTPUT +++++++++++++++++++++
	// die variablen sind alle bullshit hier, weil die ja in jeder schleifen iteration (Pro classDTO) neu gesetzt werden 
	// also kann man machne, aber fehler prone wahrsch 
	
	
	
	
	// method that inits the ETL and persistence processes 
	@Override
	public String save(List<ClassDTO> input) {
		classDTOList = input;
		List<JavaImplementation> javaImplementationsList; 
		
		// hier kommt eine liste an dtos rein 
		// für jedes dto wird zunächst ein klassen knoten erstellt (=javaImplementation)
		// danach wird geprüft ob es irgendwelche methoden/interface/extends abhängigkeiten gibt und die entsprechenden Kanten und fehlenden Knoten werden ebenfalls eingefügt 
		// danach wird auf entitäten und funktionalitäten geprüft und diese mittels Knoten und Kanten eingefügt 
		System.out.println("----------starting reading from dto");
		for(ClassDTO classDTO: classDTOList){
			JavaImplementation javaImplementation = transformClassDTOtoJavaImplementation(classDTO);
			// true, wenn die Annotation "javax.persistence.Entity" da drin ist, ansonsten false
			/*System.out.print("\nHas Javax.Persistence.Entity: ");
			System.out.println(Optional.ofNullable(classDTO.getJavaClass().accept(annotationVisitor, null)).orElse(false));*/
			System.out.println(javaImplementation.toString());
			 //for(MethodDeclaration method : classDTO.getMethods()){
				 //System.out.println("Body: " + body);
				 //System.out.println(method.toString());
				 //manageClassPersistence("Test.zwei.drei", fieldsAsJsonObjects); 
			 //} 
		}
		System.out.println("----------stopping reading from dto");
		return null;
	}
	
	
	// GRAPH_NODES: Class, Abstract Class or Interface 
	public JavaImplementation transformClassDTOtoJavaImplementation(ClassDTO classDTO) {
		JavaImplementation javaImplementation; 
		
		// extract and set implementation type (class, interface, abstract class)
		if(classDTO.getJavaClass().isInterface()){
			javaImplementation = new InterfaceNode(); 
		}
		else if(classDTO.getJavaClass().isAbstract()){
			javaImplementation = new AbstractClassNode(); 
		}
		else {
			javaImplementation = new ClassNode(); 
		}
		
		// set String variables 
		javaImplementation.setPath(classDTO.getFullName());
		javaImplementation.setModules(extractPathStructure(javaImplementation.getPath()));
		javaImplementation.setClassName(javaImplementation.getModules().get(javaImplementation.getModules().size() - 1));
		javaImplementation.setJavaClassName(javaImplementation.getClassName() + ".java");
		javaImplementation.setCompleteClassCode(classDTO.getJavaClass().toString());
		
		String moduleDeclaration = classDTO.getModuleDeclaration() == null ? "" : classDTO.getModuleDeclaration();
		javaImplementation.setModuleDeclaration(moduleDeclaration);
		
		// set List<String> variables 
		List<String> constructorsAsJsonObjectStrings = new ArrayList<String>();
		List<String> fieldsAsJsonObjectStrings = new ArrayList<String>(); 
		List<String> implementedInterfaces = new ArrayList<String>(); 
		List<String> imports = new ArrayList<String>(); 
		List<String> extensions = new ArrayList<String>(); 
		List<String> annotations = new ArrayList<String>(); 
		
		// set List<String> variables containing plain Strings
		for(ImportDeclaration importDeclaration : classDTO.getImports()){
			imports.add(importDeclaration.getNameAsString());
		}
		javaImplementation.setImports(imports);
		for (ClassOrInterfaceType implementedInterface : classDTO.getImplementations()) {
			implementedInterfaces.add(implementedInterface.getNameAsString());
		}
		javaImplementation.setImplementedInterfaces(implementedInterfaces);
		for (ClassOrInterfaceType extendedClass : classDTO.getExtensions()) {
			extensions.add(extendedClass.getNameAsString());
		}
		javaImplementation.setExtensions(extensions);

		// set List<String> variables containing JSON Objects which were converted to Strings
		fieldsAsJsonObjectStrings = getFieldsAsJSONStringList(classDTO.getFields()); 
		javaImplementation.setFieldsAsJsonObjectStrings(fieldsAsJsonObjectStrings);
		
		constructorsAsJsonObjectStrings = getConstructorsAsJSONStringList(classDTO.getConstructors());
		javaImplementation.setConstructorsAsJsonObjectStrings(constructorsAsJsonObjectStrings);
		
		javaImplementation.setAnnotations(getAnnotations(classDTO.getAnnotationDeclarationList()));
		
		return javaImplementation; 
	}
	
	
	// GRAPH_RELATION: MethodCall between Class, Interface and AbstractClass Nodes 
	public MethodCallRelation extractMethodRelation(List<MethodDeclaration> methodDeclarationList){
		MethodCallRelation methodCallRelation = new MethodCallRelation(); 
		//methodCallRelation.setProvidingJavaImplementation(methodDeclaration.getClass().getName().toString());
		for(MethodDeclaration methodDeclaration : methodDeclarationList){
			methodCallRelation.setMethod(transformDeclarationToMethod(methodDeclaration));
		}
		return methodCallRelation; 
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
			
			//graphFoundationDAO.getClassNode("BatchServiceImpl.java");
			//boolean test = graphFoundationDAO.persistClassNode("TestClass", "TestClass.java");
			//boolean test2 = graphFoundationDAO.setListAttributeInClassNode(className, javaClassName, "field",
					//fieldsAsJsonObjects);
			//System.out.println(test);
			
			graphFoundationDAO.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println(e.getStackTrace());
		}
	}
	
	// // ++++++++++++++++++++ Transformation Methods ++++++++++++++++++++++++++++++++
	// TODO: move to Transformator.java 
	public Method transformDeclarationToMethod(MethodDeclaration methodDeclaration) {
		Method method = new Method();
		method.setType(methodDeclaration.getTypeAsString());
		method.setName(methodDeclaration.getNameAsString());
		method.setModifiers(convertGenericListToString(methodDeclaration.getModifiers()));
		method.setAnnotations(convertAnnotationListToString(methodDeclaration.getAnnotations()));
		method.setParameters(extractParameters(methodDeclaration.getParameters()));
		method.setExceptions(convertGenericListToString(methodDeclaration.getThrownExceptions()));
		
		/*parameterList = methodDeclaration.getParameters();
		for (Parameter parameter : parameterList) {
			System.out.println("Paramter complete: " + parameter.toString());
			methodParameterAnnotations = parameter.getAnnotations();
			for (AnnotationExpr methodParameterAnnotation : methodParameterAnnotations) {
				System.out.println("Parameter Annotation: " + methodParameterAnnotation);
			}
			
			System.out.println(
					"Parameter type: " + parameter.getType() + "   Parameter name: " + parameter.getName().toString());
		}*/

		/*exceptionList = methodDeclaration.getThrownExceptions();
		for (ReferenceType exception : exceptionList) {
			System.out.println("Thrown Exception: " + exception.toString());
		}*/
		
		String body; 
		try {
			body = methodDeclaration.getBody().get().toString();
		} catch (Exception ex) {
			body = null;
		}
		method.setBody(body);

		return method;
	}
	
	public List<String> getAnnotations(List<AnnotationExpr> annotationExpressions){
		List<String> annotationsAsJsonObjectStrings = new ArrayList<String>(); 
		for(AnnotationExpr annotationExpr : annotationExpressions){
			Annotation annotation = new Annotation(); 
			annotation.setAnnotation(annotationExpr.getNameAsString()); 
			List<AnnotationNameValuePair> parameterList = new ArrayList<AnnotationNameValuePair>(); 
			for(MemberValuePair pair : AnnotationResolver.getParamaterList(annotationExpr)){
				AnnotationNameValuePair annoPair = new AnnotationNameValuePair(); 
				annoPair.setName(pair.getNameAsString());
				annoPair.setValue(pair.getValue().toString());
				parameterList.add(annoPair);
			}
			annotation.setParameters(parameterList);
			String annotationAsJsonObject = new Gson().toJson(annotation);
			annotationsAsJsonObjectStrings.add("'" + annotationAsJsonObject + "'");
		}
		return annotationsAsJsonObjectStrings ; 
	}
	
	
	public List<String> getConstructorsAsJSONStringList(List<ConstructorDeclaration> constructors) {
		List<String> constructorsAsJsonObjectStrings = new ArrayList<String>();
		for (ConstructorDeclaration constructor : constructors) {
			// empty json object
			String constructorAsJsonObject = ""; 
			
			// create constructor entity
			Constructor constructorEntity = new Constructor();
			
			// name, modifiers and annotations
			constructorEntity.setName(constructor.getNameAsString());
			constructorEntity.setModifiers(convertGenericListToString(constructor.getModifiers()));
			constructorEntity.setAnnotations(getAnnotations(constructor.getAnnotations()));
			
			// body 
			String body = constructorEntity.getBody();
			try {
				body = constructor.getBody().toString();
			} catch (Exception ex) {
				body = ""; 
			}
			constructorEntity.setBody(body);
			
			constructorEntity.setParameters(extractParameters(constructor.getParameters()));
			// creating a nested object for each parameter and adding it to constructor object
			/*List<PassedParameter> parameters = constructorEntity.getParameters(); 
			for (Parameter parameter : constructor.getParameters()) {
				// create Parameter object 
				PassedParameter parameterAsJsonObject = new PassedParameter(); 
				// set name, type, modifiers and annotations as Strings 
				parameterAsJsonObject.setName(parameter.getNameAsString());
				parameterAsJsonObject.setType(parameter.getType().asString());
				parameterAsJsonObject.setModifiers(convertGenericListToString(parameter.getModifiers()));
				parameterAsJsonObject.setAnnotations(convertAnnotationListToString(parameter.getAnnotations()));
				
				// add parameter object to parameter list 
				parameters.add(parameterAsJsonObject); 
			}
			constructorEntity.setParameters(parameters);*/
			
			// create constructor json objects and add to string list 
			constructorAsJsonObject = new Gson().toJson(constructorEntity);
			//System.out.println("Constructor JSON++++++++++++++++ " + constructorAsJsonObject);
			constructorsAsJsonObjectStrings.add("'" + constructorAsJsonObject + "'");
		}
		return constructorsAsJsonObjectStrings;
	}
	
	public List<String> getFieldsAsJSONStringList(List<FieldDeclaration> fields){
		List<String> fieldsAsJsonObjectStrings = new ArrayList<String>(); 
		for (FieldDeclaration field : fields) {
			// create empty json string for one field 
			String fieldAsJsonObject = "";
			// create an empty Field
			Field fieldEntity = new Field();
			List<String> names = fieldEntity.getNames();
			

			// names and initializer
			for (VariableDeclarator fieldVariable : field.getVariables()) {
				//System.out.println("Field variable: " + fieldVariable.getNameAsString());
				names.add(fieldVariable.getNameAsString());
				fieldVariable.getInitializer().ifPresent((init) -> {
					// System.out.println("Field Initializer: " + init);
					String initializer = fieldEntity.getInitializer();
					initializer = init.toString();
					fieldEntity.setInitializer(initializer);
				});
			}

			// type, modifiers and annotations
			fieldEntity.setType(field.getElementType().asString());
			fieldEntity.setModifiers(convertGenericListToString(field.getModifiers()));
			fieldEntity.setAnnotations(getAnnotations(field.getAnnotations()));
			
			// create JsonObject from Field
			fieldAsJsonObject = new Gson().toJson(fieldEntity);
			//fieldAsJsonObject.replace("\\u0027", "'");
			fieldsAsJsonObjectStrings.add("'" + fieldAsJsonObject + "'");
			//System.out.println("JSON++++++++++++++++ " + fieldAsJsonObject);
		}
		return fieldsAsJsonObjectStrings;
	}
	
	public List<PassedParameter> extractParameters(List<Parameter> parameterList){
		List<PassedParameter> passedParameterList = new ArrayList<PassedParameter>(); 
		for (Parameter parameter : parameterList) {
			PassedParameter passedParameter = new PassedParameter(); 
			passedParameter.setName(parameter.getName().toString());
			passedParameter.setType(parameter.getType().toString());
			passedParameter.setAnnotations(convertAnnotationListToString(parameter.getAnnotations()));
			passedParameter.setModifiers(convertGenericListToString(parameter.getModifiers()));
			passedParameterList.add(passedParameter);
			/*
			System.out.println("Paramter complete: " + parameter.toString());
			methodParameterAnnotations = parameter.getAnnotations();
			for (AnnotationExpr methodParameterAnnotation : methodParameterAnnotations) {
				System.out.println("Parameter Annotation: " + methodParameterAnnotation);
			}
			
			System.out.println(
					"Parameter type: " + parameter.getType() + "   Parameter name: " + parameter.getName().toString());*/
		}
		return passedParameterList; 
	}
	
	// ++++++++++++++++++++ Helper Methods ++++++++++++++++++++++++++++++++
	// TODO: move to EnvironmentUtils.java Class 
	public String getCredential(String infrastructure, String key) throws Exception{
		Ini ini = new Ini(new File("src/main/resources/neo4j_conf.ini"));
		return ini.get(infrastructure, key);
	}
	// TODO: move to EnvironmentUtils.java Class 
	public String getEnvironment(String environment, String key_ip, String key_port, String key_portType) throws Exception{
		Ini ini = new Ini(new File("src/main/resources/neo4j_conf.ini"));
		String portType = ini.get(environment, key_portType);
		String ip = ini.get("remote", key_ip);
		String port = ini.get("remote", key_port);
		return portType + "://" + ip + ":" + port;
	}
	
	// TODO: move to ConverterUtils.java class (converts a path to a list of strings) (path is not Env but is necessary for process)
	public List<String> extractPathStructure(String fullPath){
		List<String> modules = new ArrayList<String>(); 
		if(fullPath.contains(".")){
			modules = Arrays.asList(fullPath.split(Pattern.quote(".")));
		}
		else {
			modules.add(fullPath);
		}
		return modules; 
	}
	
	// TODO: move to ConverterUtils.java class
	public List<String> convertGenericListToString(List list){
		List<String> convertedObjects = new ArrayList<String>();
		for(Object obj : list){
			convertedObjects.add(obj.toString());
		}
		return convertedObjects; 
	}
	
	// TODO: move to Converter.java class
	public List<String> convertAnnotationListToString(List<AnnotationExpr> list){
		List<String> convertedObjects = new ArrayList<String>();
		for(AnnotationExpr annotationExpr : list){
			convertedObjects.add(annotationExpr.getNameAsString()); 
		}
		return convertedObjects; 
	}
}
