package greennav.routing;

import greennav.routing.server.Server;

import org.junit.Test;

public class IntegrationTest {

	@Test
	public void testRouting1() throws Exception {

		Server s = new Server();

		System.out.println("server loaded");

		long from = 269723938;
		long to = 271032295;
		s.route(null, 100, from, to, "distance", "any", false);

	}
}
