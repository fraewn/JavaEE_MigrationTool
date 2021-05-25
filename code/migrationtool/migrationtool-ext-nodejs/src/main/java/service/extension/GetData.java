package service.extension;

import data.TargetTypes;
import model.GraphFoundationDAO;
import operations.ModelService;
import operations.dto.ClassDTO;
import operations.dto.GenericDTO;
import parser.visitors.AnnotationVisitor;

import java.util.List;
import java.util.Optional;

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

import org.ini4j.Ini;
import java.io.File;

public class GetData extends ModelService {
	@Override
	public void save() {

	}

	@Override
	public void setDTO(GenericDTO<?> dto) {
		
		/*try{
			Ini ini = new Ini(new File("src/main/resources/neo4j_conf.ini"));
			String portType = ini.get("remote", "portType");
			String ip = ini.get("remote", "ip");
			String port = ini.get("remote", "port");
			String url = portType + "://" + ip + ":" + port;
			String username = ini.get("remote", "username");
			String password = ini.get("remote", "password");
			System.out.println(url + username + password);
			
			GraphFoundationDAO graphFoundationDAO = GraphFoundationDAO.getInstance(); 
			
			graphFoundationDAO.initConnection(url, username, password);
			
			
			try{
			graphFoundationDAO.getClassNode("BatchServiceImpl.java");
			}
			catch(Exception e){
				System.out.println(e.getStackTrace());
				System.out.println(e.getMessage());
			}
			graphFoundationDAO.close(); 
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}*/
		
		
		
		List<ClassDTO> classDTOList = (List<ClassDTO>) dto.getObject();
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
			 System.out.println("\nFields:");
			 for(FieldDeclaration field : fields){
					 fieldElementType = field.getElementType().asString(); 
					 System.out.println("\nField Element Type: " + fieldElementType);
					 
					 fieldModifiers = field.getModifiers(); 
					 for(Modifier fieldModifier : fieldModifiers){
						 System.out.println("Field Modifier: " + fieldModifier);
					 }
				 
				 	fieldVariables = field.getVariables();
				 	for(VariableDeclarator fieldVariable: fieldVariables){
				 		System.out.println("Field variable: " + fieldVariable.getNameAsString());
				 		fieldVariable.getInitializer().ifPresent((i) -> { 
				 			System.out.println("Field Initializer: " + i);
				 			});
				 	}
				 	
				 	fieldAnnotations = field.getAnnotations(); 
				 	for(AnnotationExpr fieldAnnotation: fieldAnnotations){
				 		System.out.println("Field Annotation: " + fieldAnnotation.getNameAsString());
				 	}	
			 } 
			 
			 constructors = classDTO.getConstructors(); 
			 for(ConstructorDeclaration constructor : constructors){
				 constructorModifiers = constructor.getModifiers(); 
				 for(Modifier constructorModifier : constructorModifiers){
				 		System.out.println("Constructor Modifier: " + constructorModifier);
				 }
				 parameterList = constructor.getParameters(); 
				 for(Parameter parameter : parameterList){
					 System.out.println("Paramter complete: " + parameter.toString());
					 constructorParameterAnnotations = parameter.getAnnotations(); 
					 for(AnnotationExpr constructorParameterAnnotation : constructorParameterAnnotations){
						 System.out.println("Parameter Annotation: " + constructorParameterAnnotation);
					 }
					 System.out.println("Parameter type: " + parameter.getType() + "   Parameter name: " + parameter.getName().toString());
				 }
				 constructorName = constructor.getNameAsString();
				 System.out.println("Constructor Name: " + constructorName);
				 try{
					 body = constructor.getBody().toString();
				 }
				 catch(Exception ex){
					 body = null; 
				 }
				 System.out.println("Constructor Body: " + body);
				 System.out.println("Constructor complete: " + constructor.toString());
			 }
			 
			 
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
				 System.out.println("Body: " + body);
				 System.out.println(method.toString());
				 
			 } 
		}
		System.out.println("----------stopping reading from dto");
	}

	@Override
	public GenericDTO<?> buildDTO() {
		return null;
	}
}
