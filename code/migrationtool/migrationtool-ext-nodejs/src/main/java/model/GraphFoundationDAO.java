package model;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.driver.v1.*;

import model.graph.node.ClassNode;
import model.graph.node.JavaImplementation;
import model.graph.relation.MethodCallRelation;
import model.graph.types.FirstLevelFunctionality;
import model.graph.types.NodeType;
import model.graph.types.RelationType;
import model.graph.types.SecondLevelFunctionality;

// create class nodes in neo4j graph 
public class GraphFoundationDAO implements AutoCloseable {
	
	// Singleton 
	private static final GraphFoundationDAO instance = new GraphFoundationDAO(); 
	private GraphFoundationDAO(){}
	public static GraphFoundationDAO getInstance(){
		if(instance == null){
			return new GraphFoundationDAO(); 
		}
		return instance; 
	}
	
	private Driver driver; 
	Session session;
	Record record ;
	String query="" ;
    StatementResult result;
    
    public void setSession(Session session){
    	this.session = session; 
    }
 
	/*public void initConnection(String url, String username, String password){
		driver = GraphDatabase.driver(url, AuthTokens.basic(username, password));
		session = driver.session(); 
		System.out.println("+++++++++++++started connection++++++++");
	}*/

	@Override
	public void close() throws Exception {
		driver.close(); 
		System.out.println("+++++++++++++closed connection++++++++");
	}
	
	// refactoren, sodass die methode die query als parameter annimmt
	// für jede Art des Rückgabe-Werts aus der db (single, array, boolean etc. pp) eine methode 
	/*public boolean execute(){
		String query = "match(c:Class {name:'BatchServiceImpl.java'}) return c.name";
		try ( Session session = driver.session() ) {
			String res = session.writeTransaction( new TransactionWork<String>()
			{
				@Override
				public String execute( Transaction tx ){
					StatementResult result = tx.run(query);
					System.out.println(result.single().get(0).toString());
					return result.single().get(0).toString();
				}
			} );
			System.out.println(res);
			return true; 
		}
	}*/
	
	public boolean getClassNode(String className) throws Exception {
		query = "match(c:Class {name:'" + className + "'}) return c.name";
		result = session.run(query);
		System.out.println(result.single().get(0).toString());
		return true; 
	}
	
	public List<String> getAllMethodCallsPerClass(String path) throws Exception{
		// path = service.SecurityUtils
		List<String> methodCalls = new ArrayList<String>(); 
		query = "Match(callingClass {path:'" + path + "'})<-[r:CALLS_METHOD]-(providingClass) RETURN r.name";
		result = session.run(query);
		while(result.hasNext()){
			methodCalls.add(result.next().get(0).asString());
		}
		return methodCalls; 
	}
	
	public boolean persistClassNode(ClassNode classNode) throws Exception {
		query = "MERGE (" + classNode.getClassName() + ":Class {name:'" + classNode.getJavaClassName() + "'})";
		result = session.run(query);
		if(result.summary() != null){
			return true;
		}
		return false; 
	}
	public boolean persistFullClassNode(JavaImplementation impl) throws Exception {
		String genericNodeType = impl.getGenericNodeType().toString(); 
		String mergeQuery = "MERGE (n:" + impl.getNodeType().toString() + ":" + genericNodeType + " {name:'" + impl.getJavaClassName() + "'})";
		String setQueryBegin = "SET n += { ";
		String setQueryEnd = "}";
		String separator = " ";
		
		String attributes = "path:'" + impl.getPath() + "', moduleDeclaration:'" + impl.getModuleDeclaration() + "', body:'" + impl.getCompleteClassCode() 
		+ "', modules:" + impl.getModules().toString() + ", imports:" + impl.getImports().toString() + ", annotations:" + impl.getAnnotationsAsJsonObjectStrings() 
		+ ", implementedInterfaces:" + impl.getImplementedInterfaces() + ", extensions:" + impl.getExtensions().toString() + ", fields:" + impl.getFieldsAsJsonObjectStrings()
		+ ", methods: " + impl.getMethodsAsJsonObjectStrings(); 
		
		if(impl.getNodeType().equals("Class") || impl.getNodeType().equals("AbstractClass")){
			String classSpecificAttributes = ", constructors:" + impl.getConstructorsAsJsonObjectStrings().toString(); 
			attributes += classSpecificAttributes; 
		}
		
		// execute 
		query = mergeQuery + separator + setQueryBegin + attributes + setQueryEnd;
		result = session.run(query);
		if(result.summary() != null){
			return true;
		}
		return false; 
	}
	
	public boolean persistFullEnumNode(JavaImplementation impl) throws Exception {
		String genericNodeType = impl.getGenericNodeType().toString(); 
		String mergeQuery = "MERGE (n:" + impl.getNodeType().toString() + ":" + genericNodeType + " {name:'" + impl.getJavaClassName() + "'})";
		String setQueryBegin = "SET n += { ";
		String setQueryEnd = "}";
		String separator = " ";
		
		String attributes = "path:'" + impl.getPath() + "', moduleDeclaration:'" + impl.getModuleDeclaration() + "', body:'" + impl.getCompleteClassCode() 
		+ "', modules:" + impl.getModules().toString() + ", imports:" + impl.getImports().toString() + ", annotations:" + impl.getAnnotationsAsJsonObjectStrings() 
		+ ", implementedInterfaces:" + impl.getImplementedInterfaces() + ", constructors:" + impl.getConstructorsAsJsonObjectStrings().toString() + ", fields:" + impl.getFieldsAsJsonObjectStrings()
		+ ", methods: " + impl.getMethodsAsJsonObjectStrings(); 
		
		// execute 
		query = mergeQuery + separator + setQueryBegin + attributes + setQueryEnd;
		System.out.println(query);
		result = session.run(query);
		if(result.summary() != null){
			return true;
		}
		return false; 
	}
	
	public boolean setListAttributeInClassNode(String className, String javaClassName, String nodeAttribute, List<String> jsonObjectListAsString) throws Exception {
		System.out.println(jsonObjectListAsString.get(0));
		System.out.println(jsonObjectListAsString.get(1));
		query = "MATCH (c:Class {name:'" + javaClassName + "'}) SET c." + nodeAttribute + "= " + jsonObjectListAsString;
		result = session.run(query);
		if(result.summary() != null){
			return true;
		}
		return false; 
	}
	
	public boolean persistMethodCallRelation(MethodCallRelation methodCallRelation){
		String relationType = RelationType.CALLS_METHOD.toString(); 
		String callingJavaImplementation = methodCallRelation.getCallingJavaImplementation();
		String callingJavaImplementationType = methodCallRelation.getCallingJavaImplementationType().toString(); 
		String providingJavaImplementation = methodCallRelation.getProvidingJavaImplementation(); 
		query = "MATCH (callingClass:" + callingJavaImplementationType + " {name:'" + callingJavaImplementation + "'}) "
				+ "MATCH (providingClass {name:'" + providingJavaImplementation + "'}) "
				+ "MERGE (callingClass)-[:" + relationType + " {name:'" + methodCallRelation.getMethod() + "'}]->(providingClass)";
		System.out.println(query);
		result = session.run(query);
		if(result.summary() != null){
			return true;
		}
		return false; 
	}
	
	public boolean persistEnumImportRelation(String enumPath, String implPath, String implType){
		String relationType = RelationType.USES_ENUM.toString(); 
		query = "MATCH (class:" + implType + " {path:'" + implPath + "'}) "
				+ "MATCH (enum {path:'" + enumPath + "'}) "
				+ "MERGE (class)-[:" + relationType + "]->(enum)";
		System.out.println(query);
		result = session.run(query);
		if(result.summary() != null){
			return true;
		}
		return false; 
	}
	
	public boolean persistEntity(String className, String entityName){
		String relationType = RelationType.IS_ENTITY.toString(); 
		query = "MATCH (entityClass {name:'" + className + "'}) MERGE (entityClass)-[:" + relationType + "]-(entity:Entity {name:'" + entityName + "'})"; 
		result = session.run(query);
		if(result.summary() != null){
			return true;
		}
		return false; 
	}
	
	public boolean persistDependencyInjection(String dependentClass, String injectedClass){
		String relationType = RelationType.INJECTS.toString(); 
		String genericNodeType = NodeType.JavaImplementation.toString(); 
		// check if injectedClass is part of the project already and exists as JavaImplementaiton node 
		query = "MATCH(injectedClass {name:'" + injectedClass + "'}) return injectedClass.name";
		result = session.run(query);
		if(result.hasNext()){
			// create a new edge to the class that's already there (with type JavaImplementation)
			query = "MATCH (dependentClass:" + genericNodeType + " {name:'" + dependentClass + "'}) MATCH(injectedClass {name:'" + injectedClass + "'}) MERGE (dependentClass)-[:" + relationType + "]->(injectedClass)";
			result = session.run(query);
			System.out.println("node was already there: " + query); 
			if(result.summary() != null){
				return true;
			}
		}
		else {
			// create a new node and an edge to this node (with type InjectedExternal)
			String injectedNodeType = NodeType.InjectedExternal.toString(); 
			query = "MATCH (dependentClass:" + genericNodeType + " {name:'" + dependentClass + "'}) MERGE (dependentClass)-[:" + relationType + "]->(injectedClass:" + injectedNodeType + " {name:'" + injectedClass + "'})";
			result = session.run(query);
			System.out.println("added new node: " + query);
			if(result.summary() != null){
				return true;
			}
		}
		return false; 
	}
	
	public boolean persistSecondLevelFunctionality(SecondLevelFunctionality secondLevelFunc){
		// check for FirstLevelFunc
		String nodeType = NodeType.Functionality.toString();
		String query = "MERGE(secondLevel:"  + nodeType + " {name: '" + secondLevelFunc.toString() + "'})";
		String addFirstLevelQuery = "";
		for(FirstLevelFunctionality firstLevelFunc : FirstLevelFunctionality.values()){
			if(secondLevelFunc.contains(firstLevelFunc.toString())){
				addFirstLevelQuery = " MERGE (firstLevel:" + nodeType + " {name:'" + firstLevelFunc.toString() + "'}) MERGE (secondLevel)-[r:" + RelationType.BELONGS_TO.toString() + "]->(firstLevel)";
			}
		}
		query = query + addFirstLevelQuery; 
		result = session.run(query);
		if(result.summary() != null){
			return true;
		}
		return false; 
	}
	
	public boolean persistFirstLevelFunctionality(FirstLevelFunctionality firstLevelFunc){
		String nodeType = NodeType.Functionality.toString();
		query = "MERGE (firstLevel:" + nodeType + " {name:'" + firstLevelFunc.toString() + "'})";
		result = session.run(query);
		if(result.summary() != null){
			return true;
		}
		return false; 
	}
	
	public boolean associateJavaImplWithFunctionality(String javaImplementationPath, String functionality, String fullImportPath){
		query = "MATCH (j:JavaImplementation) WHERE j.path='" + javaImplementationPath + "' MATCH (f:Functionality) WHERE f.name='" + functionality + "' MERGE (j)-[r:" + RelationType.USES_FUNCTIONALITY.toString() + " {name:" + fullImportPath + "}]->(f)"; 
		result = session.run(query);
		if(result.summary() != null){
			return true;
		}
		return false; 
	}
	

}
