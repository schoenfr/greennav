package greennav.routing.data.osmosis;

import greennav.routing.data.Graph;

import java.util.List;
import java.util.Map;

import javax.management.relation.Relation;

import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Entity;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;
import org.openstreetmap.osmosis.core.pipeline.common.TaskConfiguration;
import org.openstreetmap.osmosis.core.pipeline.common.TaskManager;
import org.openstreetmap.osmosis.core.pipeline.common.TaskManagerFactory;
import org.openstreetmap.osmosis.core.pipeline.v0_6.SinkManager;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;

/**
 * This class is used to create a task manager, that contains the sink for
 * constructing the graph.
 */
public class Extractor extends TaskManagerFactory {

	private Graph graph = new Graph();

	/**
	 * This method retrieves the task parameters from the task configuration and
	 * creates a GreenNavWriter object to handle the task.
	 */
	protected TaskManager createTaskManagerImpl(TaskConfiguration taskConfig) {

		// Connect the Osmosis task chain with the following sink
		Sink sink = new Sink() {

			@Override
			public void complete() {
				graph.cleanup();
			}

			@Override
			public void initialize(Map<String, Object> metaData) {

			}

			@Override
			public void process(EntityContainer entityContainer) {
				Entity entity = entityContainer.getEntity();
				if (entity instanceof Node) {
					onNode((Node) entity);
				} else if (entity instanceof Way) {
					onWay((Way) entity);
				} else if (entity instanceof Relation) {
					// do nothing particular for relations
				}
			}

			private void onNode(Node node) {
				graph.addVertex(node.getId(), node.getLatitude(),
						node.getLongitude());
			}

			private void onWay(Way way) {

				List<WayNode> nodes = way.getWayNodes();

				WayNode from = nodes.get(0);
				for (int i = 1; i < nodes.size(); i++) {
					WayNode to = nodes.get(i);
					graph.addEdge(from.getNodeId(), to.getNodeId(), false);
					from = to;
				}
				if (way.isClosed()) {
					graph.addEdge(from.getNodeId(), nodes.get(0).getNodeId(),
							false);
				}
			}

			@Override
			public void release() {

			}
		};

		// Return a sink manager with constructed sink
		return new SinkManager(taskConfig.getId(), sink,
				taskConfig.getPipeArgs());
	}

	public Graph getGraph() {
		return graph;
	}

}
