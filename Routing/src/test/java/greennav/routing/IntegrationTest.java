package greennav.routing;

import greennav.routing.data.Graph.Vertex;
import greennav.routing.data.path.IPath;
import greennav.routing.server.Server;

import org.junit.Test;

public class IntegrationTest {

	@Test
	public void testRouting1() throws Exception {

		Server s = new Server();

		System.out.println("server loaded");

		long from = 269723938;
		long to = 271032295;
		IPath<Vertex> l = s
				.route(null, 100, from, to, "distance", "any", false);

		for (Vertex v : l.toVertexList()) {
			System.out.println(v.getID());
		}

	}
}
