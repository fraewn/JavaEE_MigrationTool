package service.persistence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;

import service.utils.EnvironmentUtils;

public class Neo4jConnection implements AutoCloseable {
	private static final Logger LOG = LogManager.getLogger();
	private Driver driver;

	// Singleton
	private static final Neo4jConnection instance = new Neo4jConnection();

	private Neo4jConnection() {
	}

	public static Neo4jConnection getInstance() {
		if (instance == null) {
			return new Neo4jConnection();
		}
		return instance;
	}

	public Session initConnection() {
		String url = "";
		String username = "";
		String password = "";
		try {
			EnvironmentUtils envUtils = new EnvironmentUtils();
			url = envUtils.getEnvironment("remote", "ip", "port", "portType");
			username = envUtils.getCredential("remote", "username");
			password = envUtils.getCredential("remote", "password");
			// System.out.println(url + username + password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// TODO Exception Handling verbessern
		this.driver = GraphDatabase.driver(url, AuthTokens.basic(username, password));
		System.out.println("");
		LOG.info("Initialising Neo4j Connection");
		System.out.println("");
		return this.driver.session();
	}

	@Override
	public void close() throws Exception {
		this.driver.close();
		System.out.println("");
		LOG.info("Closing Neo4j Connection");
		System.out.println("");
	}

}
