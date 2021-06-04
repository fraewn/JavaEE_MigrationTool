package service.utils;

import java.io.File;

import org.ini4j.Ini;

public class EnvironmentUtils {
	public String getCredential(String infrastructure, String key) throws Exception {
		Ini ini = new Ini(new File("src/main/resources/neo4j_conf.ini"));
		return ini.get(infrastructure, key);
	}

	public String getEnvironment(String environment, String key_ip, String key_port, String key_portType)
			throws Exception {
		Ini ini = new Ini(new File("src/main/resources/neo4j_conf.ini"));
		String portType = ini.get(environment, key_portType);
		String ip = ini.get("remote", key_ip);
		String port = ini.get("remote", key_port);
		return portType + "://" + ip + ":" + port;
	}
}
