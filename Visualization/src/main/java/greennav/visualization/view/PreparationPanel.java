package greennav.visualization.view;

import greennav.model.computations.interfaces.Problem;
import greennav.routing.server.Server;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class PreparationPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The parent view.
	 */
	private View parent;

	/**
	 * The combo box used for choosing the routing problem.
	 */
	private final JComboBox<String> problemComboBox = new JComboBox<>();

	/**
	 * The combo box used for choosing the routing algorithm.
	 */
	private final JComboBox<String> algorithmComboBox = new JComboBox<>();

	/**
	 * The start button is used to allow choosing the start vertex from the map.
	 */
	private final JButton chooseStartButton = new JButton("Choose Start");

	/**
	 * On entering coordinates, a corresponding vertex is searched and marked as
	 * the start. This is an alternative input method to choosing the start
	 * vertex by clicking on the map.
	 */
	private final JTextField chooseStartField = new JTextField(
			"53.8344, 10.7042");

	/**
	 * The destination button is used to allow choosing the destination vertex
	 * from the map.
	 */
	private final JButton chooseDestinationButton = new JButton(
			"Choose Destination");

	/**
	 * On entering coordinates, a corresponding vertex is searched and marked as
	 * the destination. This is an alternative input method to choosing the
	 * destination vertex by clicking on the map.
	 */
	private final JTextField chooseDestinationField = new JTextField(
			"53.9641, 10.8796");

	/**
	 * The start button initializes the algorithm and switches to debugging
	 * perspective.
	 */
	private final JButton startVisualizationButton = new JButton(
			"Start Visualization");

	public PreparationPanel(View parent) {
		super(new GridBagLayout());
		this.parent = parent;
		GridBagConstraints c = new GridBagConstraints();
		c.weighty = 0;
		c.ipady = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;

		c.gridy = 0;
		add(problemComboBox, c);

		c.insets = new Insets(10, 0, 0, 0); // top padding

		c.gridy++;
		add(algorithmComboBox, c);

		c.insets = new Insets(40, 0, 0, 0); // top padding
		c.gridy++;
		add(chooseStartButton, c);

		c.insets = new Insets(10, 0, 0, 0); // top padding
		c.gridy++;
		add(chooseStartField, c);

		c.insets = new Insets(40, 0, 0, 0); // top padding
		c.gridy++;
		add(chooseDestinationButton, c);

		c.insets = new Insets(10, 0, 0, 0); // top padding
		c.gridy++;
		add(chooseDestinationField, c);

		c.insets = new Insets(40, 0, 0, 0); // top padding
		c.gridy++;
		add(startVisualizationButton, c);

		// preparationPanel.add(loadXMLButton);

		setOpaque(false);
	}

	public void managerSet(Server server) {
		// cardLayout.show(controlPanel, model.getState().toString());
		problemComboBox.removeAllItems();
		List<Problem> sortedProblems = new ArrayList<>();
		Collections.sort(sortedProblems, new Comparator<Problem>() {
			public int compare(Problem o1, Problem o2) {
				return o1.getIdentifier().compareTo(o2.getIdentifier());
			}
		});
		for (Problem prob : sortedProblems) {
			problemComboBox.addItem(prob.getIdentifier());
		}
	}

	public void problemSet(Problem problem) {
		algorithmComboBox.removeAllItems();
		Collection<String> algorithms = parent.getModel().getServer()
				.getAlgorithmList();
		List<String> sortedAlgorithms = new ArrayList<>(algorithms);
		for (String alg : sortedAlgorithms) {
			algorithmComboBox.addItem(alg);
		}
	}

	public JTextField getChooseStartField() {
		return chooseStartField;
	}

	public JTextField getChooseDestinationField() {
		return chooseDestinationField;
	}

	public JComboBox<String> getAlgorithmComboBox() {
		return algorithmComboBox;
	}

	public JComboBox<String> getProblemComboBox() {
		return problemComboBox;
	}

	public JButton getStartVisualizationButton() {
		return startVisualizationButton;
	}

	public JButton getChooseStartButton() {
		return chooseStartButton;
	}

	public JButton getChooseDestinationButton() {
		return chooseDestinationButton;
	}
}
