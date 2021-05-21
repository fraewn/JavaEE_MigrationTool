package model;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;

// Merge details in graph 
public class GraphRefinementDAO implements AutoCloseable {
	
private Driver neo4j; 
	
	public GraphRefinementDAO(String url, String username, String password){
		neo4j = GraphDatabase.driver(url, AuthTokens.basic(username, password));
	}
	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
