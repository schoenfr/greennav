package greennav.routing.data.osmosis;

import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.osmosis.core.pipeline.common.TaskManagerFactory;
import org.openstreetmap.osmosis.core.plugin.PluginLoader;

public class Plugin implements PluginLoader {
	
	public Map<String, TaskManagerFactory> loadTaskFactories() {
		HashMap<String, TaskManagerFactory> map = new HashMap<String, TaskManagerFactory>();
		map.put("extract", new Extractor());
		return map;
	}

}
