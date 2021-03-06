package model;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;

import model.graph.node.ClassNode;
import model.graph.node.JavaImplementation;
import model.graph.relation.MethodCallRelation;
import model.graph.types.FirstLevelFunctionality;
import model.graph.types.Layer;
import model.graph.types.Library;
import model.graph.types.NodeType;
import model.graph.types.RelationType;
import model.graph.types.SecondLevelFunctionality;

// create class nodes in neo4j graph
public class GraphFoundationDAO implements AutoCloseable {

	// Singleton
	private static final GraphFoundationDAO instance = new GraphFoundationDAO();

	private GraphFoundationDAO() {
	}

	public static GraphFoundationDAO getInstance() {
		if (instance == null) {
			return new GraphFoundationDAO();
		}
		return instance;
	}

	private Driver driver;
	Session session;
	Record record;
	String query = "";
	StatementResult result;

	public void setSession(Session session) {
		this.session = session;
	}

	/*
	 * public void initConnection(String url, String username, String password){
	 * driver = GraphDatabase.driver(url, AuthTokens.basic(username, password));
	 * session = driver.session();
	 * System.out.println("+++++++++++++started connection++++++++"); }
	 */

	@Override
	public void close() throws Exception {
		this.driver.close();
		System.out.println("+++++++++++++closed connection++++++++");
	}

	// refactoren, sodass die methode die query als parameter annimmt
	// für jede Art des Rückgabe-Werts aus der db (single, array, boolean etc.
	// pp) eine methode
	/*
	 * public boolean execute(){ String query =
	 * "match(c:Class {name:'BatchServiceImpl.java'}) return c.name"; try ( Session
	 * session = driver.session() ) { String res = session.writeTransaction( new
	 * TransactionWork<String>() {
	 *
	 * @Override public String execute( Transaction tx ){ StatementResult result =
	 * tx.run(query); System.out.println(result.single().get(0).toString()); return
	 * result.single().get(0).toString(); } } ); System.out.println(res); return
	 * true; } }
	 */

	public boolean getClassNode(String className) throws Exception {
		this.query = "match(c:Class {name:'" + className + "'}) return c.name";
		this.result = this.session.run(this.query);
		// System.out.println(result.single().get(0).toString());
		return true;
	}

	public boolean checkForJavaImplementationNodeByName(String name) throws Exception {
		this.query = "match(n:JavaImplementation {name:'" + name + "'}) return n.name";
		this.result = this.session.run(this.query);
		if (this.result.hasNext()) {
			// System.out.println("Found a record: " + result.single().get(0).toString());
			return true;
		}
		return false;
	}

	public boolean persistEntityPersistenceLayerRelations() throws Exception {
		this.query = "match(n:Entity) match(p:Layer {name:'Persistence Layer'}) MERGE(n)-[:BELONGS_TO]->(p)";
		this.result = this.session.run(this.query);
		if (this.result.summary() != null) {
			return true;
		}
		return false;
	}

	public boolean persistDefaultEdgeWeight() throws Exception {
		this.query = "match(n)-[r]->(m) set r.weight = 1";
		this.result = this.session.run(this.query);
		if (this.result.summary() != null) {
			return true;
		}
		return false;
	}

	public List<String> getAllMethodCallsPerClass(String path) throws Exception {
		// path = service.SecurityUtils
		List<String> methodCalls = new ArrayList<>();
		this.query = "Match(callingClass {path:'" + path + "'})<-[r:CALLS_METHOD]-(providingClass) RETURN r.name";
		this.result = this.session.run(this.query);
		while (this.result.hasNext()) {
			methodCalls.add(this.result.next().get(0).asString());
		}
		return methodCalls;
	}

	public boolean persistClassNode(ClassNode classNode) throws Exception {
		this.query = "MERGE (" + classNode.getClassName() + ":Class {name:'" + classNode.getJavaClassName() + "'})";
		this.result = this.session.run(this.query);
		if (this.result.summary() != null) {
			return true;
		}
		return false;
	}

	public boolean persistFullClassNode(JavaImplementation impl) throws Exception {
		String genericNodeType = impl.getGenericNodeType().toString();
		String mergeQuery = "MERGE (n:" + impl.getNodeType().toString() + ":" + genericNodeType + " {name:'"
				+ impl.getJavaClassName() + "'})";
		String setQueryBegin = "SET n += { ";
		String setQueryEnd = "}";
		String separator = " ";

		StringBuilder attributes = new StringBuilder("path:'").append(impl.getPath()).append("', moduleDeclaration:'")
				.append(impl.getModuleDeclaration()).append("', body:'").append(impl.getCompleteClassCode())
				.append("', modules:")
				.append(impl.getModules().toString()).append(", imports:").append(impl.getImports().toString())
				.append(", annotations:").append(impl.getAnnotationsAsJsonObjectStrings())
				.append(", implementedInterfaces:")
				.append(impl.getImplementedInterfaces()).append(", extensions:").append(impl.getExtensions().toString())
				.append(", fields:").append(impl.getFieldsAsJsonObjectStrings()).append(", methods: ")
				.append(impl.getMethodsAsJsonObjectStrings());

		if (impl.getNodeType().equals("Class") || impl.getNodeType().equals("AbstractClass")) {
			String classSpecificAttributes = ", constructors:" + impl.getConstructorsAsJsonObjectStrings().toString();
			attributes.append(classSpecificAttributes);
		}

		// execute
		this.query = mergeQuery + separator + setQueryBegin + attributes.append(setQueryEnd).toString();
		this.result = this.session.run(this.query);
		if (this.result.summary() != null) {
			return true;
		}
		return false;
	}

	public boolean persistFullEnumNode(JavaImplementation impl) throws Exception {
		String genericNodeType = impl.getGenericNodeType().toString();
		String mergeQuery = "MERGE (n:" + impl.getNodeType().toString() + ":" + genericNodeType + " {name:'"
				+ impl.getJavaClassName() + "'})";
		String setQueryBegin = "SET n += { ";
		String setQueryEnd = "}";
		String separator = " ";

		String attributes = "path:'" + impl.getPath() + "', moduleDeclaration:'" + impl.getModuleDeclaration()
				+ "', body:'" + impl.getCompleteClassCode() + "', modules:" + impl.getModules().toString()
				+ ", imports:" + impl.getImports().toString() + ", annotations:"
				+ impl.getAnnotationsAsJsonObjectStrings() + ", implementedInterfaces:"
				+ impl.getImplementedInterfaces() + ", constructors:"
				+ impl.getConstructorsAsJsonObjectStrings().toString() + ", fields:"
				+ impl.getFieldsAsJsonObjectStrings() + ", methods: " + impl.getMethodsAsJsonObjectStrings();

		// execute
		this.query = mergeQuery + separator + setQueryBegin + attributes + setQueryEnd;
		// System.out.println(query);
		this.result = this.session.run(this.query);
		if (this.result.summary() != null) {
			return true;
		}
		return false;
	}

	public boolean setListAttributeInClassNode(String className, String javaClassName, String nodeAttribute,
			List<String> jsonObjectListAsString) throws Exception {
		// System.out.println(jsonObjectListAsString.get(0));
		// System.out.println(jsonObjectListAsString.get(1));
		this.query = "MATCH (c:Class {name:'" + javaClassName + "'}) SET c." + nodeAttribute + "= "
				+ jsonObjectListAsString;
		this.result = this.session.run(this.query);
		if (this.result.summary() != null) {
			return true;
		}
		return false;
	}

	public boolean persistMethodCallRelation(MethodCallRelation methodCallRelation) {
		String relationType = RelationType.CALLS_METHOD.toString();
		String callingJavaImplementation = methodCallRelation.getCallingJavaImplementation();
		String callingJavaImplementationType = methodCallRelation.getCallingJavaImplementationType().toString();
		String providingJavaImplementation = methodCallRelation.getProvidingJavaImplementation();
		this.query = "MATCH (callingClass:" + callingJavaImplementationType + " {name:'" + callingJavaImplementation
				+ "'}) "
				+ "MATCH (providingClass {name:'" + providingJavaImplementation + "'}) " + "MERGE (callingClass)-[:"
				+ relationType + " {name:'" + methodCallRelation.getMethod() + "'}]->(providingClass)";
		// System.out.println(query);
		this.result = this.session.run(this.query);
		if (this.result.summary() != null) {
			return true;
		}
		return false;
	}

	public boolean persistEnumImportRelation(String enumPath, String implPath, String implType) {
		String relationType = RelationType.USES_ENUM.toString();
		this.query = "MATCH (class:" + implType + " {path:'" + implPath + "'}) " + "MATCH (enum {path:'" + enumPath
				+ "'}) "
				+ "MERGE (class)-[:" + relationType + "]->(enum)";
		// System.out.println(query);
		this.result = this.session.run(this.query);
		if (this.result.summary() != null) {
			return true;
		}
		return false;
	}

	public boolean persistEntity(String className, String entityName) {
		String relationType = RelationType.IS_ENTITY.toString();
		this.query = "MATCH (entityClass {name:'" + className + "'}) MERGE (entityClass)-[:" + relationType
				+ "]-(entity:Entity {name:'" + entityName + "'})";
		this.result = this.session.run(this.query);
		if (this.result.summary() != null) {
			return true;
		}
		return false;
	}

	public boolean persistDependencyInjection(String dependentClass, String injectedClass) {
		String relationType = RelationType.INJECTS.toString();
		String genericNodeType = NodeType.JavaImplementation.toString();
		// check if injectedClass is part of the project already and exists as
		// JavaImplementaiton node
		this.query = "MATCH(injectedClass {name:'" + injectedClass + "'}) return injectedClass.name";
		this.result = this.session.run(this.query);
		if (this.result.hasNext()) {
			// create a new edge to the class that's already there (with type
			// JavaImplementation)
			this.query = "MATCH (dependentClass:" + genericNodeType + " {name:'" + dependentClass
					+ "'}) MATCH(injectedClass {name:'" + injectedClass + "'}) MERGE (dependentClass)-[:" + relationType
					+ "]->(injectedClass)";
			this.result = this.session.run(this.query);
			// System.out.println("node was already there: " + query);
			if (this.result.summary() != null) {
				return true;
			}
		} else {
			// create a new node and an edge to this node (with type
			// InjectedExternal)
			String injectedNodeType = NodeType.InjectedExternal.toString();
			this.query = "MATCH (dependentClass:" + genericNodeType + " {name:'" + dependentClass
					+ "'}) MERGE (dependentClass)-[:" + relationType + "]->(injectedClass:" + injectedNodeType
					+ " {name:'" + injectedClass + "'})";
			this.result = this.session.run(this.query);
			// System.out.println("added new node: " + query);
			if (this.result.summary() != null) {
				return true;
			}
		}
		return false;
	}

	public boolean persistSecondLevelFunctionality(SecondLevelFunctionality secondLevelFunc) {
		// check for FirstLevelFunc
		String nodeType = NodeType.Functionality.toString();
		StringBuilder query = new StringBuilder("MERGE(secondLevel:").append(nodeType).append(" {name: '")
				.append(secondLevelFunc.toString()).append("'})");
		String addFirstLevelQuery = "";
		String addLayerQuery = "";
		for (FirstLevelFunctionality firstLevelFunc : FirstLevelFunctionality.values()) {
			if (secondLevelFunc.contains(firstLevelFunc.toString())) {
				addFirstLevelQuery = " MERGE (firstLevel:" + nodeType + " {name:'" + firstLevelFunc.toString()
						+ "'}) MERGE (secondLevel)-[r:" + RelationType.BELONGS_TO.toString() + "]->(firstLevel)";
				for (Layer layer : Layer.values()) {
					if (layer.getFuncList().contains(firstLevelFunc)) {
						String nodeTypeLayer = NodeType.Layer.toString();
						addLayerQuery = " MERGE (layer:" + nodeTypeLayer + " {name: '" + layer.toString()
								+ "'}) MERGE (layer)<-[s: " + RelationType.BELONGS_TO.toString() + "]-(firstLevel)";
					}
				}

			}
		}
		query.append(addFirstLevelQuery).append(addLayerQuery);
		this.result = this.session.run(query.toString());
		if (this.result.summary() != null) {
			return true;
		}
		return false;
	}

	public boolean persistFirstLevelFunctionality(FirstLevelFunctionality firstLevelFunc) {
		String nodeType = NodeType.Functionality.toString();
		this.query = "MERGE (firstLevel:" + nodeType + " {name:'" + firstLevelFunc.toString() + "'})";
		String addLayerQuery = "";
		for (Layer layer : Layer.values()) {
			if (layer.getFuncList().contains(firstLevelFunc)) {
				String nodeTypeLayer = NodeType.Layer.toString();
				addLayerQuery = " MERGE (layer:" + nodeTypeLayer + " {name: '" + layer.toString()
						+ "'}) MERGE (layer)<-[s: " + RelationType.BELONGS_TO.toString() + "]-(firstLevel)";
			}
		}
		this.query = this.query + addLayerQuery;
		this.result = this.session.run(this.query);
		if (this.result.summary() != null) {
			return true;
		}
		return false;
	}

	public boolean persistImplementation(String javaImplementationPath, String implementedInterfaceName) {
		String relationType = RelationType.IMPLEMENTS.toString();
		// service.GenericDAOAdapter
		// service.GenericDAO
		String query = "";
		try {
			if (checkForJavaImplementationNodeByName(implementedInterfaceName)) {
				// if interface exists, connect javaImplementation with Interface
				query = "MATCH(n:JavaImplementation {path:'" + javaImplementationPath + "'}) MATCH (m:Interface {name:'"
						+ implementedInterfaceName + "'}) MERGE (n)-[:" + relationType + "]->(m)";
			} else {
				// if interface does not yet exist, create a new one and connect
				query = "MATCH(n:JavaImplementation {path:'" + javaImplementationPath + "'}) MERGE (m:Interface {name:'"
						+ implementedInterfaceName + "'}) MERGE (n)-[:" + relationType + "]->(m)";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.result = this.session.run(query);
		if (this.result.summary() != null) {
			return true;
		}
		// System.out.println(query);

		return false;
	}

	public boolean persistExtension(String javaImplementationPath, String extensionName) {
		String relationType = RelationType.EXTENDS.toString();
		String query = "";
		try {
			if (checkForJavaImplementationNodeByName(extensionName)) {
				// if extension exists, connect javaImplementation with Interface
				query = "MATCH(n:JavaImplementation {path:'" + javaImplementationPath
						+ "'}) MATCH (m:JavaImplementation {name:'"
						+ extensionName + "'}) MERGE (n)-[:" + relationType + "]->(m)";
			} else {
				// if abstract extension does not yet exist, create a new one and connect
				query = "MATCH(n:JavaImplementation {path:'" + javaImplementationPath
						+ "'}) MERGE (m:AbstractClass {name:'"
						+ extensionName + "'}) MERGE (n)-[:" + relationType + "]->(m)";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.result = this.session.run(query);
		if (this.result.summary() != null) {
			return true;
		}
		return false;
	}

	public boolean associateJavaImplWithFunctionality(String javaImplementationPath, String functionality,
			String fullImportPath) {
		this.query = "MATCH (j:JavaImplementation) WHERE j.path='" + javaImplementationPath
				+ "' MATCH (f:Functionality) WHERE f.name='" + functionality + "' MERGE (j)-[r:"
				+ RelationType.USES_FUNCTIONALITY.toString() + " {name:" + fullImportPath + "}]->(f)";
		this.result = this.session.run(this.query);
		if (this.result.summary() != null) {
			return true;
		}
		return false;
	}

	public boolean persistRessource(String resource, String javaImplementationPath) {
		String nodeType = NodeType.Resource.toString();
		this.query = "MATCH (j:JavaImplementation) WHERE j.path='" + javaImplementationPath + "' MERGE (r:" + nodeType
				+ " {name: '" + resource + "'}) MERGE (r)<-[:" + RelationType.USES_RESOURCE.toString() + "]-(j)";
		this.result = this.session.run(this.query);
		if (this.result.summary() != null) {
			return true;
		}
		return false;
	}

	public boolean persistLibrary(Library lib, String javaImplementationPath) {
		String nodeType = NodeType.Library.toString();
		this.query = "MATCH (j:JavaImplementation) WHERE j.path='" + javaImplementationPath + "' MERGE (l:" + nodeType
				+ " {name: '" + lib.getName() + "', description: '" + lib.getDescription() + "'}) MERGE (l)<-[:"
				+ RelationType.USES_LIBRARY.toString() + "]-(j)";
		this.result = this.session.run(this.query);
		if (this.result.summary() != null) {
			return true;
		}
		return false;
	}

}
