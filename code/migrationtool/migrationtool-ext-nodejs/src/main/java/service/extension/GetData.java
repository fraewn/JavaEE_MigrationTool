package service.extension;

import data.TargetTypes;
import model.GraphFoundationDAO;
import model.entities.Constructor;
import model.entities.Field;
import model.entities.PassedParameter;
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
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.TypeParameter;
import com.google.gson.Gson;

import org.ini4j.Ini;
import java.io.File;

public class GetData extends ModelService<List<ClassDTO>, String> {
	
	List<ClassDTO> classDTOList = new ArrayList(); 
			
	// Type means class - jetzt sucht der am Klassenkopf die Annotation und sagt dann ob er da eine gefunden hat
	AnnotationVisitor annotationVisitor = new AnnotationVisitor("javax.persistence.Entity", TargetTypes.TYPE);
	
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
	
	public List<String> saveConstructorsAsJsonObjects(List<ConstructorDeclaration> constructors) {
		List<String> constructorsAsJsonObjects = new ArrayList();
		for (ConstructorDeclaration constructor : constructors) {
			// empty json object
			String constructorAsJsonObject = ""; 
			
			// create constructor entity
			Constructor constructorEntity = new Constructor();
			String name = constructorEntity.getName();
			String body = constructorEntity.getBody(); 
			List<String> modifiersAsStrings = constructorEntity.getModifiers();
			List<String> annotationsAsString = constructorEntity.getAnnotations();
			List<PassedParameter> parameters = constructorEntity.getParameters(); 
			
			// name 
			name = constructor.getNameAsString(); 
			constructorEntity.setName(name);
			System.out.println("Constructor Name: " + constructor.getNameAsString());
			
			// body 
			try {
				body = constructor.getBody().toString();
			} catch (Exception ex) {
				body = ""; 
			}
			constructorEntity.setBody(body);
			
			// modifiers
			constructorModifiers = constructor.getModifiers();
			for (Modifier constructorModifier : constructorModifiers) {
				System.out.println("Constructor Modifier: " + constructorModifier);
				modifiersAsStrings.add(constructorModifier.toString());
			}
			constructorEntity.setModifiers(modifiersAsStrings); 
			
			// creating the parameter object (nested)
			for (Parameter parameter : constructor.getParameters()) {
				// create Parameter object 
				PassedParameter parameterAsJsonObject = new PassedParameter(); 
				String parameterName = parameterAsJsonObject.getName(); 
				String type = parameterAsJsonObject.getType(); 
				List<String> parameterModifiersAsStrings = parameterAsJsonObject.getModifiers();
				List<String> parameterAnnotationsAsStrings = parameterAsJsonObject.getAnnotations();
				
				System.out.println("Paramter complete: " + parameter.toString());
				
				// name and type 
				parameterName = parameter.getNameAsString(); 
				parameterAsJsonObject.setName(parameterName);
				type = parameter.getType().asString();
				parameterAsJsonObject.setType(type);
				
				// modifiers 
				for (Modifier constructorParameterModifier : parameter.getModifiers()) {
					parameterModifiersAsStrings.add(constructorParameterModifier.toString());
				}
				
				// annotations
				for (AnnotationExpr constructorParameterAnnotation : parameter.getAnnotations()) {
					System.out.println("Parameter Annotation: " + constructorParameterAnnotation);
					parameterAnnotationsAsStrings.add(constructorParameterAnnotation.toString());
				}
				
				System.out.println("Parameter type: " + parameter.getType() + "   Parameter name: "
						+ parameter.getName().toString());
				
				// add parameter object to parameter list 
				parameters.add(parameterAsJsonObject); 
			}
			constructorEntity.setParameters(parameters);
			
			constructorAsJsonObject = new Gson().toJson(constructorEntity);
			System.out.println("JSON++++++++++++++++ " + constructorAsJsonObject);
			constructorsAsJsonObjects.add("'" + constructorAsJsonObject + "'");
		}
		return constructorsAsJsonObjects;
	}
	
	public List<String> saveFieldsAsJsonObjects(List<FieldDeclaration> fields){
		List<String> fieldsAsJsonObjects = new ArrayList(); 
		
		for (FieldDeclaration field : fields) {
			System.out.println("\nFields:");
			
			// create empty json string for one field 
			String fieldAsJsonObject = "";
			
			// create an empty Field
			Field fieldEntity = new Field();
			List<String> names = fieldEntity.getNames();
			String type = fieldEntity.getType();
			List<String> modifiersAsStrings = fieldEntity.getModifiers();
			List<String> annotationsAsString = fieldEntity.getAnnotations();

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

			// type
			type = field.getElementType().asString();
			fieldEntity.setType(type);
			System.out.println("\nField Element Type: " + type);

			// modifier list
			for (Modifier fieldModifier : field.getModifiers()) {
				modifiersAsStrings.add(fieldModifier.toString());
				System.out.println("Field Modifier: " + fieldModifier);
			}
			fieldEntity.setModifiers(modifiersAsStrings);

			// annotations list
			for (AnnotationExpr fieldAnnotation : field.getAnnotations()) {
				System.out.println("Field Annotation: " + fieldAnnotation.getNameAsString());
				annotationsAsString.add(fieldAnnotation.getNameAsString());
			}
			fieldEntity.setAnnotations(annotationsAsString);

			// create JsonObject from Field
			fieldAsJsonObject = new Gson().toJson(fieldEntity);
			fieldsAsJsonObjects.add("'" + fieldAsJsonObject + "'");
			System.out.println("JSON++++++++++++++++ " + fieldAsJsonObject);
		}
		return fieldsAsJsonObjects;
	}
	
	public void manageClassPersistence(String fullPath, List<String> fieldsAsJsonObjects){
		try {
			Ini ini = new Ini(new File("src/main/resources/neo4j_conf.ini"));
			String portType = ini.get("remote", "portType");
			String ip = ini.get("remote", "ip");
			String port = ini.get("remote", "port");
			String url = portType + "://" + ip + ":" + port;
			String username = ini.get("remote", "username");
			String password = ini.get("remote", "password");
			System.out.println(url + username + password);

			GraphFoundationDAO graphFoundationDAO = GraphFoundationDAO.getInstance();
			
			List<String> modules = extractPathStructure(fullPath);
			System.out.println(modules.size());
			String className = modules.get(modules.size()-1); 
			String javaClassName = className + ".java"; 
			System.out.println("classname:" + className);
			System.out.println("javaclassname:" + javaClassName);
			
			graphFoundationDAO.initConnection(url, username, password);

			try {
				graphFoundationDAO.getClassNode("BatchServiceImpl.java");
				boolean test = graphFoundationDAO.persistClassNode("TestClass", "TestClass.java");
				boolean test2 = graphFoundationDAO.setFieldinClassNode(className, javaClassName, fieldsAsJsonObjects);
				System.out.println(test);
				
			} catch (Exception e) {
				System.out.println(e.getStackTrace());
				System.out.println(e.getMessage());
			}
			graphFoundationDAO.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println(e.getStackTrace());
		}
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

	@Override
	public String save(List<ClassDTO> input) {
		// TODO Auto-generated method stub

		classDTOList = input;
		

		System.out.println("----------starting reading from dto");
		for(ClassDTO classDTO: classDTOList){
			System.out.println("\nClassname: " + classDTO.getFullName());
			System.out.println("Is Interface: " + classDTO.getJavaClass().isInterface());
			System.out.println("Is Abstract: " + classDTO.getJavaClass().isAbstract());
			
			// true wenn die Annotation "javax.persistence.Entity" da drin ist, ansonsten false
			System.out.print("\nHas Javax.Persistence.Entity: ");
			System.out.println(Optional.ofNullable(classDTO.getJavaClass().accept(annotationVisitor, null)).orElse(false));
			
			System.out.print("\nPackage Declaration: ");
			System.out.println(classDTO.getPackageDeclaration());	
			
			// printed die komplette java klasse
			// System.out.println(classDTO.getJavaClass().toString());
			System.out.print("\nModule Declaration or, if non-existent, null: ");
			System.out.println(classDTO.getModuleDeclaration());
			
			importDeclarationList = classDTO.getImports(); 
			System.out.println("\nImports:");
			 for(ImportDeclaration importDeclaration : importDeclarationList){
					System.out.println(importDeclaration.getNameAsString());
			 }
			 
			 implementsList = classDTO.getImplementations();
			 System.out.println("\nInterfaces:");
			 for(ClassOrInterfaceType implementedInterface : implementsList){
					System.out.println(implementedInterface.getNameAsString());
			 }
			 
			 extendsList = classDTO.getExtensions();
			 System.out.println("\nExtends from:");
			 for(ClassOrInterfaceType extendedClass : extendsList){
					System.out.println(extendedClass.getNameAsString());
			 }
			 
			 fields = classDTO.getFields(); 
			 List<String> fieldsAsJsonObjects = saveFieldsAsJsonObjects(fields); 
			 
			 
			 constructors = classDTO.getConstructors(); 
			 List<String> constructorsAsJsonObjects = saveConstructorsAsJsonObjects(constructors);
			 
			 
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
}
