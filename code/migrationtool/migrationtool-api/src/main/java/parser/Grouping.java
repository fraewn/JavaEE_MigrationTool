package parser;

import java.util.Map;

public interface Grouping {

	void setGroups(Map<Integer, String> list);

	Map<Integer, String> getGroups();
}
