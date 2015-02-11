package greennav.visualization.controller;

import greennav.visualization.view.View;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * ChangeListener to perform the change event for the show start destination
 * check box. Currently not used!
 */
public class ShowStartDestChangeListener implements ActionListener {

	private final View gui;

	public ShowStartDestChangeListener(Controller parent) {
		gui = parent.getView();
		gui.getObservationPanel().getStartDestCheckBox()
				.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// if (gui.getStartDestCheckBox().isSelected()) {
		// gui.viewStartDest();
		// } else {
		// gui.hideStartDest();
		// }
	}
}
