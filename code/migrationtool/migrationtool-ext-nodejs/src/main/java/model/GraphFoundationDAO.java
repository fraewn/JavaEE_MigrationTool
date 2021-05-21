package model;

import org.neo4j.driver.v1.*;

// create class nodes in neo4j graph 
public class GraphFoundationDAO implements AutoCloseable {
	
	private Driver neo4j; 
	
	public GraphFoundationDAO(String url, String username, String password){
		neo4j = GraphDatabase.driver(url, AuthTokens.basic(username, password));
	}

	@Override
	public void close() throws Exception {
		neo4j.close(); 
	}
	
	// refactoren, sodass die methode die query als parameter annimmt
	// für jede Art des Rückgabe-Werts aus der db (single, array, boolean etc. pp) eine methode 
	public boolean execute(){
		try ( Session session = neo4j.session() ) {
			String res = session.writeTransaction( new TransactionWork<String>()
			{
				@Override
				public String execute( Transaction tx )
				{
					StatementResult result = tx.run( "match(c:Class {name:'BatchServiceImpl.java'}) return c.name");
					System.out.println(result.single().get(0).toString());
					return result.single().get(0).toString();
				}
			} );
			System.out.println(res);
			return true; 
		}
	}

}
