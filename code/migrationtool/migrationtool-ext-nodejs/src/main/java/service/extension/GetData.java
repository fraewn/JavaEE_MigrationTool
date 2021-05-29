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
import operations.ModelService;
import operations.dto.ClassDTO;
import parser.visitors.AnnotationVisitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithName;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.TypeParameter;
import com.google.gson.Gson;

import org.ini4j.Ini;
import java.io.File;


// TODO rename class to transformIntoGraphModel or something like that 
public class GetData extends ModelService<List<ClassDTO>, String> {
	
	List<ClassDTO> classDTOList = new ArrayList(); 
			
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
	String className = "";
	String javaClassName = "";
	String path = "";
	String moduleDeclaration;
	String completeClassCode = "";
	
	List<String> modules;
	List<String> constructorsAsJsonObjectStrings = new ArrayList();
	List<String> fieldsAsJsonObjectStrings = new ArrayList(); 
	List<String> implementedInterfaces; 
	List<String> imports;
	List<String> extensions; 
	List<String> exceptions; 
	
	JavaImplementation javaImplementation; 
	
	@Override
	public String save(List<ClassDTO> input) {
		classDTOList = input;
		
		System.out.println("----------starting reading from dto");
		for(ClassDTO classDTO: classDTOList){
			System.out.println("\nClassname: " + classDTO.getFullName());
			
			// sind auch relations quasi schon 
			
			
			// true, wenn die Annotation "javax.persistence.Entity" da drin ist, ansonsten false
			System.out.print("\nHas Javax.Persistence.Entity: ");
			System.out.println(Optional.ofNullable(classDTO.getJavaClass().accept(annotationVisitor, null)).orElse(false));
			
			 methods = classDTO.getMethods();  
			 System.out.println("\nMethods:");
			 for(MethodDeclaration method : methods){
				 methodAnnotations = method.getAnnotations(); 
				 for(AnnotationExpr methodAnnotation : methodAnnotations){
				 		System.out.println("Method Annotation: " + methodAnnotation.getNameAsString());
				 }
				 methodModifiers = method.getModifiers(); 
				 for(Modifier methodModifier : methodModifiers){
				 		System.out.println("Method Modifier: " + methodModifier);
				 }
				 
				 returnType = method.getTypeAsString();
				 System.out.println("Return Type: " + returnType.toString());
				 
				 methodName = method.getNameAsString();
				 System.out.println("Method name: " + methodName);
				 
				 parameterList = method.getParameters(); 
				 for(Parameter parameter : parameterList){
					 System.out.println("Paramter complete: " + parameter.toString());
					 methodParameterAnnotations = parameter.getAnnotations(); 
					 for(AnnotationExpr methodParameterAnnotation : methodParameterAnnotations){
						 System.out.println("Parameter Annotation: " + methodParameterAnnotation);
					 }
					 System.out.println("Parameter type: " + parameter.getType() + "   Parameter name: " + parameter.getName().toString());
				 }
				 
				 exceptionList = method.getThrownExceptions(); 
				 for(ReferenceType exception : exceptionList){
					 System.out.println("Thrown Exception: " + exception.toString());
				 }
				 
				 try{
					 body = method.getBody().get().toString();
				 }
				 catch(Exception ex){
					 body = null; 
				 }
				 //System.out.println("Body: " + body);
				 //System.out.println(method.toString());
				 //manageClassPersistence("Test.zwei.drei", fieldsAsJsonObjects); 
			 } 
		}
		System.out.println("----------stopping reading from dto");
		return null;
	}
	
	public JavaImplementation transformClassDTOtoJavaImplementation(ClassDTO classDTO) {
		// extract and set implementation type (class, interface, abstract class)
		if(classDTO.getJavaClass().isInterface()){
			javaImplementation = new InterfaceNode(); 
			System.out.println("Is Interface: " + classDTO.getJavaClass().isInterface());
		}
		if(classDTO.getJavaClass().isAbstract()){
			javaImplementation = new AbstractClassNode(); 
			System.out.println("Is Abstract: " + classDTO.getJavaClass().isAbstract());
		}
		else {
			javaImplementation = new ClassNode(); 
		}
		
		// set String variables 
		javaImplementation.setPath(classDTO.getFullName());
		javaImplementation.setModules(extractPathStructure(path));
		javaImplementation.setClassName(modules.get(modules.size() - 1));
		javaImplementation.setJavaClassName(className + ".java");
		javaImplementation.setCompleteClassCode(classDTO.getClass().toString());
		
		String moduleDeclaration = classDTO.getModuleDeclaration() == null ? "" : classDTO.getModuleDeclaration();
		javaImplementation.setModuleDeclaration(moduleDeclaration);
		
		/*path = classDTO.getFullName(); 
		modules = extractPathStructure(path);
		className = modules.get(modules.size() - 1);
		javaClassName = className + ".java";
		completeClassCode = classDTO.getClass().toString(); 
		String moduleDeclaration = classDTO.getModuleDeclaration() == null ? "" : classDTO.getModuleDeclaration(); */
		
		// set List<String> variables 
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
		fieldsAsJsonObjectStrings = saveFieldsAsJsonObjects(classDTO.getFields()); 
		javaImplementation.setFieldsAsJsonObjectStrings(fieldsAsJsonObjectStrings);
		constructorsAsJsonObjectStrings = getConstructorsAsJSONStringList(classDTO.getConstructors());
		javaImplementation.setConstructorsAsJsonObjectStrings(constructorsAsJsonObjectStrings);
		
		return javaImplementation; 
	}

	public void manageClassPersistence(JavaImplementation javaImplementation) {
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
	
	public List<String> getConstructorsAsJSONStringList(List<ConstructorDeclaration> constructors) {
		for (ConstructorDeclaration constructor : constructors) {
			// empty json object
			String constructorAsJsonObject = ""; 
			
			// create constructor entity
			Constructor constructorEntity = new Constructor();
			
			// name, modifiers and annotations
			constructorEntity.setName(constructor.getNameAsString());
			constructorEntity.setModifiers(convertGenericListToString(constructor.getModifiers()));
			constructorEntity.setAnnotations(convertAnnotationListToString(constructor.getAnnotations()));
			
			// body 
			String body = constructorEntity.getBody();
			try {
				body = constructor.getBody().toString();
			} catch (Exception ex) {
				body = ""; 
			}
			constructorEntity.setBody(body);
			
			// creating a nested object for each parameter and adding it to constructor object
			List<PassedParameter> parameters = constructorEntity.getParameters(); 
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
			constructorEntity.setParameters(parameters);
			
			// create constructor json objects and add to string list 
			constructorAsJsonObject = new Gson().toJson(constructorEntity);
			System.out.println("Constructor JSON++++++++++++++++ " + constructorAsJsonObject);
			constructorsAsJsonObjectStrings.add("'" + constructorAsJsonObject + "'");
		}
		return constructorsAsJsonObjectStrings;
	}
	
	public List<String> saveFieldsAsJsonObjects(List<FieldDeclaration> fields){
		for (FieldDeclaration field : fields) {
			// create empty json string for one field 
			String fieldAsJsonObject = "";
			// create an empty Field
			Field fieldEntity = new Field();
			List<String> names = fieldEntity.getNames();
			

			// names and initializer
			for (VariableDeclarator fieldVariable : field.getVariables()) {
				System.out.println("Field variable: " + fieldVariable.getNameAsString());
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
			fieldEntity.setAnnotations(convertAnnotationListToString(field.getAnnotations()));
			
			// create JsonObject from Field
			fieldAsJsonObject = new Gson().toJson(fieldEntity);
			fieldsAsJsonObjectStrings.add("'" + fieldAsJsonObject + "'");
			System.out.println("JSON++++++++++++++++ " + fieldAsJsonObject);
		}
		return fieldsAsJsonObjectStrings;
	}
	
	
	// ++++++++++++++++++++ Helper Methods ++++++++++++++++++++++++++++++++
	public String getCredential(String infrastructure, String key) throws Exception{
		Ini ini = new Ini(new File("src/main/resources/neo4j_conf.ini"));
		return ini.get(infrastructure, key);
	}
	
	public String getEnvironment(String environment, String key_ip, String key_port, String key_portType) throws Exception{
		Ini ini = new Ini(new File("src/main/resources/neo4j_conf.ini"));
		String portType = ini.get(environment, key_portType);
		String ip = ini.get("remote", key_ip);
		String port = ini.get("remote", key_port);
		return portType + "://" + ip + ":" + port;
	}
	
	public List<String> extractPathStructure(String fullPath){
		List<String> modules = new ArrayList(); 
		if(fullPath.contains(".")){
			modules = Arrays.asList(fullPath.split(Pattern.quote(".")));
		}
		else {
			modules.add(fullPath);
		}
		for(String s : modules){
			System.out.println(s);
		}
		return modules; 
	}
	
	public List<String> convertGenericListToString(List list){
		List<String> convertedObjects = new ArrayList();
		for(Object obj : list){
			convertedObjects.add(obj.toString());
		}
		return convertedObjects; 
	}
	
	public List<String> convertAnnotationListToString(List<AnnotationExpr> list){
		List<String> convertedObjects = new ArrayList();
		for(AnnotationExpr annotationExpr : list){
			convertedObjects.add(annotationExpr.getNameAsString()); 
		}
		return convertedObjects; 
	}
}
