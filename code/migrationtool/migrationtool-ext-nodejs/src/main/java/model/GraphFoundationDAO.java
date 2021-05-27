package model;

import java.util.List;

import org.neo4j.driver.v1.*;

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
 
	public void initConnection(String url, String username, String password){
		driver = GraphDatabase.driver(url, AuthTokens.basic(username, password));
		session = driver.session(); 
		System.out.println("+++++++++++++started connection++++++++");
	}

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
	
	public boolean persistClassNode(String className, String javaClassName) throws Exception {
		query = "MERGE (" + className + ":Class {name:'" + javaClassName + "'})";
		result = session.run(query);
		if(result.summary() != null){
			return true;
		}
		return false; 
	}
	
	public boolean setFieldinClassNode(String className, String javaClassName, List<String> fieldsAsJsonObjects) throws Exception {
		System.out.println(fieldsAsJsonObjects.get(0));
		System.out.println(fieldsAsJsonObjects.get(1));
		query = "MATCH (c:Class {name:'" + javaClassName + "'}) SET c.field = " + fieldsAsJsonObjects;
		result = session.run(query);
		if(result.summary() != null){
			return true;
		}
		return false; 
	}
	
	

}
