package service.extension;

import data.TargetTypes;
import operations.ModelService;
import operations.dto.ClassDTO;
import operations.dto.GenericDTO;
import parser.visitors.AnnotationVisitor;

import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.TypeParameter;

public class GetData extends ModelService {
	@Override
	public void save() {

	}

	@Override
	public void setDTO(GenericDTO<?> dto) {
		List<ClassDTO> classDTOList = (List<ClassDTO>) dto.getObject();
		// Type means class - jetzt sucht der am Klassenkopf die Annotation und sagt dann ob er da eine gefunden hat
		AnnotationVisitor annotationVisitor = new AnnotationVisitor("javax.persistence.Entity", TargetTypes.TYPE);
		List<ImportDeclaration> importDeclarationList;
		List<ClassOrInterfaceType> implementsList; 
		List<ClassOrInterfaceType> extendsList;
		List<FieldDeclaration> fields;
		List<ConstructorDeclaration> constructors;
		List<MethodDeclaration> methods;
		List<AnnotationExpr> annotationExprs;
		List<TypeParameter> typeParameterList;
		
		String fieldElementType; 
		List<VariableDeclarator> fieldVariables; 
		List<Modifier> fieldModifiers; 
		
		List<AnnotationExpr> fieldAnnotations; 
		List<AnnotationExpr> methodAnnotations; 

		System.out.println("----------starting reading from dto");
		for(ClassDTO classDTO: classDTOList){
			System.out.print("\nClassname: ");
			// printed Optional[ui.config.CustomAuthentification]
			System.out.println(classDTO.getFullName());
			
			// wenn der true zurück gibt gibt es in der klasse die annotation; wenn der null zurück gibt wird das zu false gemacht und es
			// gab sie nicht
			// printed true: wenn die Annotation "javax.persistence.Entity" da drin ist, ansonsten false
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
			 System.out.println("\nExtended Classes:");
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
				 		System.out.println("Field Annotation: " + fieldAnnotation.getNameAsString() + " ");
				 	}	
			 } 
			 
			 /*methods = classDTO.getMethods();  
			 System.out.println("\nMethods:");
			 for(MethodDeclaration method : methods){
					System.out.println(method.toString());
			 } */
		}
		System.out.println("----------stopping reading from dto");
	}

	@Override
	public GenericDTO<?> buildDTO() {
		return null;
	}
}
