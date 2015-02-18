package greennav.routing.data;

import greennav.routing.data.osmosis.Extractor;
import greennav.routing.data.osmosis.Plugin;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openstreetmap.osmosis.core.TaskRegistrar;
import org.openstreetmap.osmosis.core.pipeline.common.Pipeline;
import org.openstreetmap.osmosis.core.pipeline.common.TaskConfiguration;

/**
 * This class is used to read an OSM file and transform the network to the
 * internal graph structure by using the Osmosis tool.
 */
public class OSMReader {

	public Graph readOSM(String osmSource) {

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

		// Filter highways
		pipeArgs = new HashMap<>();
		taskArgs = new HashMap<>();
		taskArgs.put(
				"highway",
				"motorway,motorway_link,trunk,trunk_link,primary,primary_link,secondary,secondary_link,tertiary,tertiary_link,unclassified,living_street,residential");

		TaskConfiguration tf1 = new TaskConfiguration("2", "tag-filter",
				pipeArgs, taskArgs, "accept-ways");
		tasks.add(tf1);

		// Reject relations highways
		pipeArgs = new HashMap<>();
		taskArgs = new HashMap<>();
		TaskConfiguration tf2 = new TaskConfiguration("3", "tag-filter",
				pipeArgs, taskArgs, "reject-relations");
		tasks.add(tf2);

		// Reject relations highways
		pipeArgs = new HashMap<>();
		taskArgs = new HashMap<>();
		TaskConfiguration usednode = new TaskConfiguration("4", "used-node",
				pipeArgs, taskArgs, null);
		tasks.add(usednode);

		// Extraction task
		pipeArgs = new HashMap<>();
		taskArgs = new HashMap<>();
		TaskConfiguration extract = new TaskConfiguration("5", "extract",
				pipeArgs, taskArgs, null);
		tasks.add(extract);

		// Execute the tasks
		p.prepare(tasks);
		p.execute();
		p.waitForCompletion();

		System.gc();

		Extractor f = (Extractor) tr.getFactoryRegister()
				.getInstance("extract");
		return f.getGraph();
	}
}
