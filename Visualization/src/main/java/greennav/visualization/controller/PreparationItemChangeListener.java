package greennav.visualization.controller;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * A controller for listening to changes in both the algorithm combo box and the
 * problem combo box.
 */
public class PreparationItemChangeListener implements ItemListener {

	/**
	 * The parent controller.
	 */
	private final Controller parent;

	/**
	 * The constructor registers itself to the corresponding combo boxes.
	 * 
	 * @param parent
	 *            The parent controller.
	 */
	public PreparationItemChangeListener(Controller parent) {
		this.parent = parent;
		parent.getView().getPreparationPanel().getProblemComboBox()
				.addItemListener(this);
		parent.getView().getPreparationPanel().getAlgorithmComboBox()
				.addItemListener(this);
	}

	/**
	 * If one of the combo boxes was used to select a particular item, then the
	 * corresponding value is set within the model. The model will then tell the
	 * view to update its information about the current problem/algorithm.
	 */
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() != ItemEvent.SELECTED)
			return;
		String item = (String) e.getItem();
		if (e.getSource().equals(
				parent.getView().getPreparationPanel().getProblemComboBox())) {
//			parent.getModel().setProblem(item);
		} else if (e.getSource().equals(
				parent.getView().getPreparationPanel().getAlgorithmComboBox())) {
			parent.getModel().setAlgorithm(item);
		}
	}

}
