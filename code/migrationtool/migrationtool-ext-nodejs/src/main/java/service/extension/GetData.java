package service.extension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.javaparser.ast.ImportDeclaration;
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

import model.GraphFoundationDAO;
import model.graph.genericAttributes.PassedParameter;
import model.graph.node.AbstractClassNode;
import model.graph.node.ClassNode;
import model.graph.node.EnumNode;
import model.graph.node.InterfaceNode;
import model.graph.node.JavaImplementation;
import model.graph.node.entityAttributes.Annotation;
import model.graph.node.entityAttributes.AnnotationNameValuePair;
import model.graph.node.entityAttributes.Constructor;
import model.graph.node.entityAttributes.Field;
import model.graph.relation.MethodCallRelation;
import model.graph.relation.entityAttributes.Method;
import model.graph.types.FirstLevelFunctionality;
import model.graph.types.Library;
import model.graph.types.NodeType;
import model.graph.types.SecondLevelFunctionality;
import operations.ModelService;
import operations.dto.AstDTO;
import parser.enums.TargetTypes;
import parser.utils.AnnotationResolver;
import parser.visitors.AnnotationVisitor;
import service.persistence.Neo4jConnection;

// TODO create three classes extractions, transformation, loading
// call each one after the other; this class is going to be extraction
public class GetData extends ModelService<List<AstDTO>, String> {
	private static final Logger LOG = LogManager.getLogger();

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
	public String save(List<AstDTO> astDTOList) {
		LOG.info("STARTING ETL PROCESS");
		orchestrateETL(astDTOList);
		LOG.info("STOPPING ETL PROCESS");
		return null;
	}

	// persist javaImplementations (class, Abstract, Interface nodes)
	public void orchestrateETL(List<AstDTO> astDTOList) {
		List<JavaImplementation> javaImplementationsList = new ArrayList<>();
		List<JavaImplementation> enumList = new ArrayList<>();

		try {
			Neo4jConnection connection = Neo4jConnection.getInstance();
			GraphFoundationDAO graphFoundationDAO = GraphFoundationDAO.getInstance();
			graphFoundationDAO.setSession(connection.initConnection());

			// ETL base information
			System.out.println("");
			LOG.info("+++++++++ START persisting base information +++++++++");
			LOG.info("+++++++++ Persisting classes, abstract classes, interfaces and enums +++++++++");
			for (AstDTO classDTO : astDTOList) {
				if (classDTO.getEnumClass() != null) {
					// astDTO represents Enum
					JavaImplementation javaImplementation = transformClassDTOtoEnumJavaImplementation(classDTO);
					enumList.add(javaImplementation);

					graphFoundationDAO.persistFullEnumNode(javaImplementation);

				} else {
					// astDTO represents Class/AbstractClass/Interface
					JavaImplementation javaImplementation;
					javaImplementation = transformClassDTOtoJavaImplementation(classDTO);
					javaImplementationsList.add(javaImplementation);
					graphFoundationDAO.persistFullClassNode(javaImplementation);
				}
			}

			LOG.info("+++++++++ Persisting method calls +++++++++");
			etlMethodCallRelations(astDTOList, graphFoundationDAO);
			LOG.info("+++++++++ Persisting implement relations +++++++++");
			etlImplementations(javaImplementationsList, graphFoundationDAO);
			LOG.info("+++++++++ Persisting extend relations +++++++++");
			etlExtensions(javaImplementationsList, graphFoundationDAO);
			LOG.info("+++++++++ Persisting enum imports +++++++++");
			etlEnumImportRelations(javaImplementationsList, enumList, graphFoundationDAO);
			LOG.info("+++++++++ STOP persisting base information +++++++++");
			System.out.println("");

			// ETL generated knowledge
			LOG.info("+++++++++ START persisting generated knowledge +++++++++");
			LOG.info("+++++++++ Persisting Dependency Injections +++++++++");
			etlDependencyInjections(astDTOList, graphFoundationDAO);
			LOG.info("+++++++++ Persisting Resources +++++++++");
			etlResources(astDTOList, graphFoundationDAO);
			LOG.info("+++++++++ Persisting Entities +++++++++");
			etlEntities(astDTOList, graphFoundationDAO);
			LOG.info("+++++++++ Persisting Functionalities and Libraries +++++++++");
			etlFunctionalitiesAndLibraries(javaImplementationsList, graphFoundationDAO);
			LOG.info("+++++++++ Persisting Layers +++++++++");
			graphFoundationDAO.persistEntityPersistenceLayerRelations();
			LOG.info("+++++++++ Persisting Default Edge Weight +++++++++");
			graphFoundationDAO.persistDefaultEdgeWeight();
			LOG.info("+++++++++ STOP persisting generated knowledge +++++++++");
			System.out.println("");

			// close neo4j connection
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void etlResources(List<AstDTO> astDTOList, GraphFoundationDAO graphFoundationDAO) {
		for (AstDTO classDTO : astDTOList) {
			// exclude enums
			if (classDTO.getEnumClass() == null) {
				// check if class is connected to any resources
				List<FieldDeclaration> fields = classDTO.getJavaClass().getFields();
				for (FieldDeclaration field : fields) {
					List<AnnotationExpr> annoList = field.getAnnotations();
					for (AnnotationExpr annoExpr : annoList) {
						if (annoExpr.toString().contains("Resource")) {
							// System.out.println("Found a resource in the field: " + field.toString() + "
							// called " + annoExpr.toString());
							graphFoundationDAO.persistRessource(annoExpr.toString(), classDTO.getFullName());
						}
					}
				}
			}
		}
	}

	public void etlEntities(List<AstDTO> astDTOList, GraphFoundationDAO graphFoundationDAO) {
		for (AstDTO classDTO : astDTOList) {
			// exclude enums
			if (classDTO.getEnumClass() == null) {
				JavaImplementation javaImplementation;
				javaImplementation = transformClassDTOtoJavaImplementation(classDTO);

				// check if class is entity
				AnnotationVisitor entityAnnotationVisitor = new AnnotationVisitor("javax.persistence.Entity",
						TargetTypes.TYPE);
				if (Optional.ofNullable(classDTO.getJavaClass().accept(entityAnnotationVisitor, null))
						.orElse(false)) {
					String className = javaImplementation.getJavaClassName();
					String entityName = javaImplementation.getClassName();
					graphFoundationDAO.persistEntity(className, entityName);
				}
			}
		}
	}

	public void etlMethodCallRelations(List<AstDTO> astDTOList, GraphFoundationDAO graphFoundationDAO) {
		// persist method dependencies between java implementations
		for (AstDTO classDTO : astDTOList) {
			if (classDTO.getEnumClass() != null) {
				JavaImplementation javaImplementation = transformClassDTOtoEnumJavaImplementation(classDTO);
				List<MethodCallRelation> methodCallRelationList = getEnumMethodCallRelationList(classDTO);

			} else {
				// enthält alle ausgehenden Kanten des javaImplementation
				// Knotens
				List<MethodCallRelation> methodCallRelationList = getMethodCallRelationList(classDTO);
				for (MethodCallRelation methodCallRelation : methodCallRelationList) {
					graphFoundationDAO.persistMethodCallRelation(methodCallRelation);
				}
			}
		}
	}

	public void etlDependencyInjections(List<AstDTO> astDTOList, GraphFoundationDAO graphFoundationDAO) {
		for (AstDTO classDTO : astDTOList) {
			// check all fields in class if they are injected
			for (FieldDeclaration fieldDeclaration : classDTO.getFields()) {
				JavaImplementation javaImplementation = transformClassDTOtoJavaImplementation(classDTO);
				if (fieldDeclaration.toString().contains("@Inject")) {
					String dependentClass = javaImplementation.getJavaClassName();
					String injectedClass = fieldDeclaration.getElementType().asString() + ".java";
					// System.out.println(dependentClass + " is
					// dependent
					// on: " + injectedClass);
					graphFoundationDAO.persistDependencyInjection(dependentClass, injectedClass);
				}
			}
		}
	}

	public void etlFunctionalitiesAndLibraries(List<JavaImplementation> javaImplementationList,
			GraphFoundationDAO graphFoundationDAO) {
		for (JavaImplementation javaImplementation : javaImplementationList) {
			for (String imp : javaImplementation.getImports()) {
				boolean functionalityFound = false;
				if (imp.contains("javax")) {
					// System.out.println("imp: " + imp);
					// first check if we can find a detailed
					// functionality
					boolean secondLevelFunctionalityFound = false;
					for (SecondLevelFunctionality func : SecondLevelFunctionality.values()) {
						if (imp.contains(func.toString())) {
							// System.out.println("imp is the same as second level func: " + imp + " " +
							// func);
							secondLevelFunctionalityFound = true;
							functionalityFound = true;
							graphFoundationDAO.persistSecondLevelFunctionality(func);
							graphFoundationDAO.associateJavaImplWithFunctionality(javaImplementation.getPath(),
									func.toString(), imp);
						}
					}
					// if we could not find a detailed functionality,
					// check
					// if we match a more generic functionality
					if (!secondLevelFunctionalityFound) {
						for (FirstLevelFunctionality func : FirstLevelFunctionality.values()) {
							if (imp.contains(func.toString())) {
								// System.out.println("imp is the same as First Level func: " + imp + " " +
								// func);
								functionalityFound = true;
								graphFoundationDAO.persistFirstLevelFunctionality(func);
								graphFoundationDAO.associateJavaImplWithFunctionality(javaImplementation.getPath(),
										func.toString(), imp);
							}
						}
					}
					if (!functionalityFound) {
						System.out.println("FUNCTIONALITY WAS NOT FOUND**************************!");
					}
				} else {
					for (Library lib : Library.values()) {
						if (imp.contains(lib.toString())) {
							// System.out.println("found a library: " + lib);
							graphFoundationDAO.persistLibrary(lib, javaImplementation.getPath());
						}
					}
				}
			}

		}
	}

	public void etlEnumImportRelations(List<JavaImplementation> javaImplementationList,
			List<JavaImplementation> enumList, GraphFoundationDAO dao) {
		for (JavaImplementation javaImplementation : javaImplementationList) {
			for (String imp : javaImplementation.getImports()) {
				for (JavaImplementation enumImpl : enumList) {
					if (imp.contains(enumImpl.getClassName())) {
						// System.out.println("Found a connection from: " +
						// javaImplementation.getClassName() + " to enum: " + enumImpl.getClassName());
						dao.persistEnumImportRelation(enumImpl.getPath(), javaImplementation.getPath(),
								javaImplementation.getNodeType().toString());
					}
				}
			}
		}
	}

	public void etlImplementations(List<JavaImplementation> javaImplementationList, GraphFoundationDAO dao) {
		for (JavaImplementation impl : javaImplementationList) {
			if (!impl.getImplementedInterfaces().isEmpty()) {
				for (String implementedInterface : impl.getImplementedInterfaces()) {
					String implementedInterfaceName = implementedInterface.replaceAll("'", "") + ".java";
					String javaImplementationPath = impl.getPath();
					// System.out.println(javaImplementationPath + " implementes: " +
					// implementedInterfaceName);
					dao.persistImplementation(javaImplementationPath, implementedInterfaceName);
				}
			}
		}
	}

	public void etlExtensions(List<JavaImplementation> javaImplementationList, GraphFoundationDAO dao) {
		for (JavaImplementation impl : javaImplementationList) {
			if (!impl.getExtensions().isEmpty()) {
				for (String extension : impl.getExtensions()) {
					String extensionName = extension.replaceAll("'", "") + ".java";
					String javaImplementationPath = impl.getPath();
					// System.out.println(javaImplementationPath + " extends: " + extensionName);
					dao.persistExtension(javaImplementationPath, extensionName);
				}
			}
		}
	}

	// GRAPH_NODES: Class, Abstract Class or Interface
	public JavaImplementation transformClassDTOtoJavaImplementation(AstDTO classDTO) {
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
		List<String> implementedInterfaces = new ArrayList<>();
		List<String> imports = new ArrayList<>();
		List<String> extensions = new ArrayList<>();
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
		javaImplementation.setMethodsAsJsonObjectStrings(getMethodsAsJSONStringList(classDTO.getMethods()));
		return javaImplementation;
	}

	public JavaImplementation transformClassDTOtoEnumJavaImplementation(AstDTO classDTO) {
		JavaImplementation javaImplementation = new EnumNode();

		// set String variables
		javaImplementation.setPath(classDTO.getFullName());
		javaImplementation.setModules(convertGenericListToString(extractPathStructure(javaImplementation.getPath())));
		javaImplementation.setClassName(
				extractPathStructure(javaImplementation.getPath()).get(javaImplementation.getModules().size() - 1));
		javaImplementation.setJavaClassName(javaImplementation.getClassName() + ".java");
		javaImplementation.setCompleteClassCode(classDTO.getEnumClass().toString());
		javaImplementation.cleanBody();
		String moduleDeclaration = classDTO.getModuleDeclaration() == null ? "" : classDTO.getModuleDeclaration();
		javaImplementation.setModuleDeclaration(moduleDeclaration);

		// set List<String> variables containing plain Strings
		List<String> implementedInterfaces = new ArrayList<>();
		List<String> imports = new ArrayList<>();
		for (ImportDeclaration importDeclaration : classDTO.getImports()) {
			imports.add("'" + importDeclaration.getNameAsString() + "'");
		}
		javaImplementation.setImports(imports);
		for (ClassOrInterfaceType implementedInterface : classDTO.getImplementations()) {
			implementedInterfaces.add("'" + implementedInterface.getNameAsString() + "'");
		}
		javaImplementation.setImplementedInterfaces(implementedInterfaces);

		// set List<String> variables containing JSON Objects which were
		// converted to Strings
		javaImplementation.setFieldsAsJsonObjectStrings(getFieldsAsJSONStringList(classDTO.getFields()));
		javaImplementation
				.setConstructorsAsJsonObjectStrings(getConstructorsAsJSONStringList(classDTO.getConstructors()));
		javaImplementation.setAnnotationsAsJsonObjectStrings(
				getAnnotationsAsJSONStringList(classDTO.getAnnotationDeclarationList()));
		javaImplementation.setMethodsAsJsonObjectStrings(getMethodsAsJSONStringList(classDTO.getMethods()));
		return javaImplementation;
	}

	// GRAPH_RELATION: MethodCall between Class, Interface and AbstractClass
	public List<MethodCallRelation> getMethodCallRelationList(AstDTO classDTO) {
		List<MethodCallRelation> methodCallRelationList = new ArrayList<>();

		// get calling class
		List<String> modules = convertGenericListToString(extractPathStructure(classDTO.getFullName()));
		String className = modules.get(modules.size() - 1).replaceAll("'", "") + ".java";
		// get type of calling class
		NodeType implementationType;
		if (classDTO.getJavaClass().isInterface()) {
			implementationType = NodeType.Interface;
		} else if (classDTO.getJavaClass().isAbstract()) {
			implementationType = NodeType.AbstractClass;
		} else {
			implementationType = NodeType.Class;
		}

		// analyse method call expressions in java implementation
		/*
		 * example source code from project: private MailEvent createMailEvent(User c) {
		 * MailEvent event = new MailEvent(); event.setTitle("New User created");
		 * event.setMsg("New User created with following settings: " + "\n Username: " +
		 * c .getUserExtID() + "\n Password: " + c.getPassword());
		 * event.setTo(c.getMail()); return event; } used methods in this class are:
		 * setTitle, setMsg, getUserExtId, getPassword, setTo, getMail some of them are
		 * nested into each other The nesting is ignored and just all edges from this
		 * class (calling class) to the method providing classes are added:
		 * service.user.UserMgmtServiceImpl.createMailEvent(beans.user.User)
		 * UserMgmtServiceImpl.java uses createMailEvent(beans.user.User) from
		 * UserMgmtServiceImpl service.mail.MailEvent.setTitle(java.lang.String)
		 * UserMgmtServiceImpl.java uses setTitle(java.lang.String) from MailEvent
		 * service.mail.MailEvent.setMsg(java.lang.String) UserMgmtServiceImpl.java uses
		 * setMsg(java.lang.String) from MailEvent beans.user.User.getUserExtID()
		 * UserMgmtServiceImpl.java uses getUserExtID() from User ...
		 */
		for (MethodCallExpr n : classDTO.getJavaClass().findAll(MethodCallExpr.class)) {
			MethodCallRelation methodCallRelation = new MethodCallRelation();
			methodCallRelation.setCallingJavaImplementation(className);
			methodCallRelation.setCallingJavaImplementationType(implementationType);
			try {
				String resolvedMethodSignature = n.resolve().getQualifiedSignature();
				// get method and providing class
				String method = resolveMethodFromMethodCall(resolvedMethodSignature);
				String providingClass = resolveClassFromMethodCall(resolvedMethodSignature);
				// save in methodCallRelation object
				methodCallRelation.setProvidingJavaImplementation(providingClass + ".java");
				methodCallRelation.setMethod(method);
				// System.out.println(className + " uses " + method + " from " +
				// providingClass);
			} catch (Exception e) {
				// e.printStackTrace();
			}
			methodCallRelationList.add(methodCallRelation);
		}
		return methodCallRelationList;
	}

	// currently, there are no methods in any enums or method calls from any enums
	// this method is still included for future projects
	public List<MethodCallRelation> getEnumMethodCallRelationList(AstDTO classDTO) {
		List<MethodCallRelation> methodCallRelationList = new ArrayList<>();

		// get calling class
		List<String> modules = convertGenericListToString(extractPathStructure(classDTO.getFullName()));
		String className = modules.get(modules.size() - 1).replaceAll("'", "") + ".java";
		// get type of calling class
		NodeType implementationType = NodeType.Enum;
		List<MethodCallExpr> enumMethodList = classDTO.getEnumClass().findAll(MethodCallExpr.class);
		// analyse method call expressions in java implementation
		for (MethodCallExpr n : enumMethodList) {
			MethodCallRelation methodCallRelation = new MethodCallRelation();
			methodCallRelation.setCallingJavaImplementation(className);
			methodCallRelation.setCallingJavaImplementationType(implementationType);
			try {
				String resolvedMethodSignature = n.resolve().getQualifiedSignature();
				// get method and providing class
				String method = resolveMethodFromMethodCall(resolvedMethodSignature);
				String providingClass = resolveClassFromMethodCall(resolvedMethodSignature);
				// save in methodCallRelation object
				methodCallRelation.setProvidingJavaImplementation(providingClass + ".java");
				methodCallRelation.setMethod(method);
				// System.out.println(
				// "ENUM METHOD CALL RELATION: " + className + " uses " + method + " from " +
				// providingClass);
			} catch (Exception e) {
				e.printStackTrace();
			}
			methodCallRelationList.add(methodCallRelation);
		}
		return methodCallRelationList;
	}

	public String resolveMethodFromMethodCall(String methodCall) {
		// input: ui.service.data.ReportObject.getCompanyId()
		// output: getCompanyId()
		String completeMethod = null;
		try {
			String[] method = methodCall.split(Pattern.quote("("));
			String methodEnd = "(" + method[1];
			String[] modules = method[0].split(Pattern.quote("."));
			String methodBegin = modules[modules.length - 1];
			completeMethod = methodBegin + methodEnd;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return completeMethod;
	}

	public String resolveClassFromMethodCall(String methodCall) {
		// input: ui.service.data.ReportObject.getCompanyId()
		// output: ReportObject
		String method = resolveMethodFromMethodCall(methodCall);
		methodCall = methodCall.replace(method, "");
		String[] modules = methodCall.split(Pattern.quote("."));
		return modules[modules.length - 1];

	}

	// // ++++++++++++++++++++ Transformation Methods
	// ++++++++++++++++++++++++++++++++
	// TODO: move to Transformation.java (T in ETL)

	public List<String> getMethodsAsJSONStringList(List<MethodDeclaration> methodDeclarations) {

		List<String> methodsAsJsonObjectStrings = new ArrayList<>();
		List<Method> methods = new ArrayList<>();
		for (MethodDeclaration methodDeclaration : methodDeclarations) {
			Method method = new Method();
			method = transformDeclarationToMethod(methodDeclaration);
			methods.add(method);
		}
		for (Method method : methods) {
			String methodAsJsonObject = new Gson().toJson(method);
			methodsAsJsonObjectStrings.add("'" + methodAsJsonObject + "'");
		}
		return methodsAsJsonObjectStrings;

	}

	public Method transformDeclarationToMethod(MethodDeclaration methodDeclaration) {
		Method method = new Method();
		method.setType(methodDeclaration.getTypeAsString());
		method.setName(methodDeclaration.getNameAsString());
		method.setModifiers(convertGenericListToString(methodDeclaration.getModifiers()));
		method.setAnnotations(convertAnnotationListToString(methodDeclaration.getAnnotations()));
		method.setParameters(extractParameters(methodDeclaration.getParameters()));
		method.setExceptions(convertGenericListToString(methodDeclaration.getThrownExceptions()));

		/*
		 * parameterList = methodDeclaration.getParameters(); for (Parameter parameter :
		 * parameterList) { System.out.println("Paramter complete: " +
		 * parameter.toString()); methodParameterAnnotations =
		 * parameter.getAnnotations(); for (AnnotationExpr methodParameterAnnotation :
		 * methodParameterAnnotations) { System.out.println("Parameter Annotation: " +
		 * methodParameterAnnotation); }
		 *
		 * System.out.println( "Parameter type: " + parameter.getType() +
		 * "   Parameter name: " + parameter.getName().toString()); }
		 */

		/*
		 * exceptionList = methodDeclaration.getThrownExceptions(); for (ReferenceType
		 * exception : exceptionList) { System.out.println("Thrown Exception: " +
		 * exception.toString()); }
		 */

		String body;
		try {
			body = methodDeclaration.getBody().get().toString();
		} catch (Exception ex) {
			// ex.printStackTrace();
			body = null;
		}

		method.setBody(body);
		method.cleanBody();
		return method;
	}

	public List<String> getAnnotationsAsJSONStringList(List<AnnotationExpr> annotationExpressions) {
		List<String> annotationsAsJsonObjectStrings = new ArrayList<>();
		for (Annotation annotation : getAnnotationList(annotationExpressions)) {
			String annotationAsJsonObject = new Gson().toJson(annotation);
			annotationsAsJsonObjectStrings.add("'" + annotationAsJsonObject + "'");
		}
		return annotationsAsJsonObjectStrings;
	}

	public List<Annotation> getAnnotationList(List<AnnotationExpr> annotationExpressions) {
		List<Annotation> annotationList = new ArrayList<>();
		for (AnnotationExpr annotationExpr : annotationExpressions) {
			Annotation annotation = new Annotation();
			annotation.setAnnotation(annotationExpr.getNameAsString());
			List<AnnotationNameValuePair> parameterList = new ArrayList<>();
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
		List<Field> fieldList = new ArrayList<>();
		for (FieldDeclaration fieldDeclaration : fields) {
			// create an empty Field
			Field field = new Field();
			List<String> names = field.getNames();
			// names and initializer
			for (VariableDeclarator fieldVariable : fieldDeclaration.getVariables()) {
				names.add(fieldVariable.getNameAsString());
				fieldVariable.getInitializer().ifPresent(init -> {
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
		List<String> constructorsAsJsonObjectStrings = new ArrayList<>();
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
			constructor.cleanBody();

			// create constructor Json object string and add to list in the
			// right format
			String constructorAsJsonObject = new Gson().toJson(constructor);
			constructorsAsJsonObjectStrings.add("'" + constructorAsJsonObject + "'");
		}
		return constructorsAsJsonObjectStrings;
	}

	public List<String> getFieldsAsJSONStringList(List<FieldDeclaration> fields) {
		List<String> fieldsAsJsonObjectStrings = new ArrayList<>();
		for (Field field : getFieldList(fields)) {
			fieldsAsJsonObjectStrings.add("'" + new Gson().toJson(field) + "'");
		}
		return fieldsAsJsonObjectStrings;
	}

	public List<PassedParameter> extractParameters(List<Parameter> parameterList) {
		List<PassedParameter> passedParameterList = new ArrayList<>();
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
		List<String> modules = new ArrayList<>();
		if (fullPath.contains(".")) {
			modules = Arrays.asList(fullPath.split(Pattern.quote(".")));
		} else {
			modules.add(fullPath);
		}
		return modules;
	}

	// TODO: move to ConverterUtils.java class
	public List<String> convertGenericListToString(List list) {
		List<String> convertedObjects = new ArrayList<>();
		for (Object obj : list) {
			String tmp = "'" + obj.toString() + "'";
			convertedObjects.add(tmp);
		}
		return convertedObjects;
	}

	// TODO: move to Converter.java class
	public List<String> convertAnnotationListToString(List<AnnotationExpr> list) {
		List<String> convertedObjects = new ArrayList<>();
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
