package greennav.visualization.view;

import greennav.model.computations.ComputationManager;
import greennav.model.computations.interfaces.Problem;
import greennav.visualization.data.TraceObserver;

public interface IView extends TraceObserver {

	public void message(String message);

	public void managerSet(ComputationManager computationManager);

	public void problemSet(Problem problem);

	public void algorithmSet(String algorithm);

	public void update();

}
