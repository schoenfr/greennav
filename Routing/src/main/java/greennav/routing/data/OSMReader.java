package greennav.routing.data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.java.plugin.Plugin;
import org.openstreetmap.osmosis.core.TaskRegistrar;
import org.openstreetmap.osmosis.core.pipeline.common.Pipeline;
import org.openstreetmap.osmosis.core.pipeline.common.TaskConfiguration;

/**
 * This class is used to read an OSM file and transform the network to the
 * internal graph structure by using the Osmosis tool.
 */
public class OSMReader {

	public void readOSM(String osmSource) {

		// Add the plugin to Osmosis
		List<String> plugins = new LinkedList<>();
		plugins.add(Plugin.class.getName());
		TaskRegistrar tr = new TaskRegistrar();
		tr.initialize(plugins);

		// Prepare pipeline
		Pipeline p = new Pipeline(tr.getFactoryRegister());
		List<TaskConfiguration> tasks = new LinkedList<>();

		// Reading task
		Map<String, String> pipeArgs = new HashMap<>();
		Map<String, String> taskArgs = new HashMap<>();
		taskArgs.put("file", osmSource);
		TaskConfiguration read = new TaskConfiguration("1", "read-pbf",
				pipeArgs, taskArgs, null);
		tasks.add(read);

		// Extraction task
		pipeArgs = new HashMap<>();
		taskArgs = new HashMap<>();
		TaskConfiguration extract = new TaskConfiguration("2", "extract",
				pipeArgs, taskArgs, null);
		tasks.add(extract);

		// Execute the tasks
		p.prepare(tasks);
		p.execute();
		p.waitForCompletion();

		System.gc();

	}

}
