package greennav.visualization.view;

import greennav.model.computations.interfaces.Problem;
import greennav.routing.server.Server;
import greennav.visualization.data.TraceObserver;

public interface IView extends TraceObserver {

	public void message(String message);

	public void managerSet(Server server);

	public void problemSet(Problem problem);

	public void algorithmSet(String algorithm);

	public void update();

}
