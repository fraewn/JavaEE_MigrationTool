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
import model.graph.types.NodeType;
import operations.ModelService;
import operations.dto.ClassDTO;
import parser.utils.AnnotationResolver;
import service.persistence.Neo4jConnection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.google.gson.Gson;

import org.ini4j.Ini;
import java.io.File;

// TODO create three classes extractions, transformation, loading 
// call each one after the other; this class is going to be extraction 
public class GetData extends ModelService<List<ClassDTO>, String> {

	// method that inits the ETL and persistence processes
	// hier kommt eine liste an dtos rein
	// für jedes dto wird zunächst ein klassen knoten erstellt
	// (=javaImplementation)
	// danach wird geprüft ob es irgendwelche methoden/interface/extends
	// abhängigkeiten gibt und die entsprechenden Kanten und fehlenden
	// Knoten werden ebenfalls eingefügt
	// danach wird auf entitäten und funktionalitäten geprüft und diese
	// mittels Knoten und Kanten eingefügt
	@Override
	public String save(List<ClassDTO> classDTOList) {
		System.out.println("----------starting reading from dto");
		persistFoundation(classDTOList);
		/*List<JavaImplementation> javaImplementationsList = new ArrayList<JavaImplementation>();
		for (ClassDTO classDTO : classDTOList) {
			JavaImplementation javaImplementation = transformClassDTOtoJavaImplementation(classDTO);
			javaImplementationsList.add(javaImplementation);
			
		}*/
		System.out.println("----------stopping reading from dto");
		return null;
	}

	
	
	
	// persist javaImplementations (class, Abstract, Interface nodes) and methods calls (edges)
	public boolean persistFoundation(List<ClassDTO> classDTOList){
		try {
			Neo4jConnection connection = Neo4jConnection.getInstance(); 
			GraphFoundationDAO graphFoundationDAO = GraphFoundationDAO.getInstance();
			graphFoundationDAO.setSession(connection.initConnection()); 
			System.out.println("+++++++++Start persisting java implementations +++++++++");
			/*for (ClassDTO classDTO : classDTOList) {
				// enthält den javaImplementation knoten (Class/AbstractClass/Interface) mit allen Attributen
				JavaImplementation javaImplementation = transformClassDTOtoJavaImplementation(classDTO);
				System.out.println(javaImplementation.toString());
				graphFoundationDAO.persistFullClassNode(javaImplementation);
			}*/
			System.out.println("+++++++++Start persisting method call relations +++++++++");
			for (ClassDTO classDTO : classDTOList) {
				// enthält alle ausgehenden Kanten des javaImplementation Knotens
				List<MethodCallRelation> methodCallRelationList = getMethodCallRelationList(classDTO);
				/*for(MethodCallRelation methodCallRelation : methodCallRelationList){
					graphFoundationDAO.persistMethodCallRelation(methodCallRelation);
				}*/
				
				// persist sceleton node 
				// String type = javaImplementation.getNodeType().toString(); 
				// String javaClassName = javaImplementation.getJavaClassName(); 
				
				//javaImplementationsList.add(javaImplementation);
			}
			connection.close();

			// for(JavaImplementation javaImplementation:
			// javaImplementationsList){
			// System.out.println(javaImplementation);
			// graphFoundationDAO.persistFullClassNode(javaImplementation);
			// }

			// graphFoundationDAO.getClassNode("BatchServiceImpl.java");
			// boolean test = graphFoundationDAO.persistClassNode("TestClass",
			// "TestClass.java");
			// boolean test2 =
			// graphFoundationDAO.setListAttributeInClassNode(className,
			// javaClassName, "field",
			// fieldsAsJsonObjects);
			// System.out.println(test);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
		return false; 
		
	}
	
	// Persistence Management
		public void orchestratePersistenceProcess(List<JavaImplementation> javaImplementationList) {
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
		javaImplementation.setClassName(
				extractPathStructure(javaImplementation.getPath()).get(javaImplementation.getModules().size() - 1));
		javaImplementation.setJavaClassName(javaImplementation.getClassName() + ".java");
		javaImplementation.setCompleteClassCode(classDTO.getJavaClass().toString());
		javaImplementation.cleanBody();
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
	public List<MethodCallRelation> getMethodCallRelationList(ClassDTO classDTO) {
		List<MethodCallRelation> methodCallRelationList = new ArrayList<MethodCallRelation>(); 
		// methodCallRelation.setProvidingJavaImplementation(methodDeclaration.getClass().getName().toString());
		List<MethodCallExpr> methodCallExprs = classDTO.getJavaClass().findAll(MethodCallExpr.class);
		List<String> modules = convertGenericListToString(extractPathStructure(classDTO.getFullName()));
		String className = modules.get(modules.size()-1).replaceAll("'", "") + ".java";
		NodeType implementationType; 
		if (classDTO.getJavaClass().isInterface()) {
			implementationType = NodeType.Interface; 
		} else if (classDTO.getJavaClass().isAbstract()) {
			implementationType = NodeType.AbstractClass; 
		} else {
			implementationType = NodeType.Class; 
		}
		for(MethodCallExpr n : methodCallExprs){
			MethodCallRelation methodCallRelation = new MethodCallRelation();
			methodCallRelation.setCallingJavaImplementation(className);
			methodCallRelation.setCallingJavaImplementationType(implementationType);
			try{
			String method = n.getNameAsString();
			//System.out.println(method); 
			//String callingClass = javaImplementation.getClassName();
			List<String> moduless = convertGenericListToString(extractPathStructure(n.resolve().getQualifiedSignature()));
			String providingClass = moduless.get(moduless.size()-2).replaceAll("'", "");
			/*if(providingClass.equals("null")){
				continue; 
			}*/
			String fullMethod = moduless.get(moduless.size()-1).replaceAll("'", "");
			methodCallRelation.setProvidingJavaImplementation(providingClass + ".java");
			// Typ von der providing brauche ich ja eig nicht; name müsste als identifier reichen 
			System.out.println(methodCallRelation.getCallingJavaImplementation() + " uses " + fullMethod  + "from " + methodCallRelation.getProvidingJavaImplementation());
			methodCallRelation.setMethod(fullMethod);
			}
			catch(Exception e){
				//System.out.println("Exception with method: " + n.getNameAsString());
				//e.printStackTrace();
				// do nothing
			}
			methodCallRelationList.add(methodCallRelation); 
		}
		return methodCallRelationList;
	}

	// // ++++++++++++++++++++ Transformation Methods
	// ++++++++++++++++++++++++++++++++
	// TODO: move to Transformation.java (T in ETL)
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
			ex.printStackTrace();
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
	public void visit() {
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
	}
}
