package greennav.visualization.view;

import greennav.model.computations.interfaces.Problem;
import greennav.routing.server.Server;
import greennav.visualization.data.TraceEvent;
import greennav.visualization.data.TraceEvent.ExceptionThrownEvent;
import greennav.visualization.images.VisualizationImages;
import greennav.visualization.model.Model;
import greennav.visualization.model.Model.State;
import greennav.visualization.util.VisualizationTimer;
import info.clearthought.layout.TableLayout;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * This class represents the graphical user interface of the application.
 */
public class View extends JFrame implements IView {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * DataVisualizationManager with GraphManager and VisualisationData.
	 */
	private Model model;

	/**
	 * Used to animate the status bar.
	 */
	private StatusBarThread statusBarThread = new StatusBarThread(this);

	/**
	 * The clock at the bottom of the control panel.
	 */
	private VisualizationTimer timer = new VisualizationTimer();

	/**
	 * A card layout is used for the control panel on the left side.
	 */
	private final CardLayout cardLayout = new CardLayout();

	/**
	 * The control panel shows controls depending on the current state.
	 */
	private final JPanel controlPanel = new ImagePanel(cardLayout,
			new ImageIcon(VisualizationImages.class
					.getResource("background.jpg")).getImage());

	/**
	 * The JMapViewer component displaying the map and its markers.
	 */
	private ObservationMap observationMap = new ObservationMap(this);

	/**
	 * The loading panel is shown in the beginning while network data is
	 * loading.
	 */
	private final JPanel loadingPanel = new JPanel(null);

	/**
	 * The preparation panel allows you to choose a start and a destination for
	 * debugging.
	 */
	private final PreparationPanel preparationPanel = new PreparationPanel(this);

	/**
	 * The debug panel is shown while the algorithm is running.
	 */
	private final ObservationPanel observationPanel = new ObservationPanel(this);

	/**
	 * The status bar is used to present status information from the model.
	 */
	private final JPanel statusPanel = new JPanel(new FlowLayout(
			FlowLayout.CENTER));

	/* Declare fonts */
	private final Font buttonFont = new Font("Helvetica",
			Font.LAYOUT_LEFT_TO_RIGHT, 13);
	private final Font labelFont = new Font("Helvetica",
			Font.LAYOUT_LEFT_TO_RIGHT, 14);
	private final Font statusFont = new Font("Helvetica",
			Font.LAYOUT_LEFT_TO_RIGHT, 14);

	Thread statusThread;
	JLabel statusLabel = new JLabel(
			"Welcome to the GreenNav-Visualization-Project");

	/**
	 * Constructor for the GUI
	 */
	public View() {
		super("GreenNav - Visualization");

		buildMainFrame();
		buildControlPanel();
		observationMap.setBounds(250, 0, 950, 700);
		buildStatusPanel();

		/* Insert JPanels to Frame window */
		getContentPane().add(controlPanel, "0, 0, ");
		getContentPane().add(observationMap, "1, 0, 1, 1");
		getContentPane().add(statusPanel, "0, 2, 1, 2");
		getContentPane().add(timer, "0, 1");

		/* set default close operation to kill all parent tasks */
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				dispose();
			}
		});

		setVisible(true);
		timer.start();
	}

	/**
	 * Creates the window for the application.
	 */
	private void buildMainFrame() {
		/*
		 * see
		 * http://www.clearthought.info/sun/products/jfc/tsc/articles/tablelayout
		 * /index.html
		 */
		double[][] layoutValues = new double[][] { { 250.0, TableLayout.FILL },
				{ TableLayout.FILL, 50, 25 } };
		setLayout(new TableLayout(layoutValues));
		setBounds(50, 50, 1200, 750);
		setResizable(true);
	}

	/**
	 * Creates the left side containing the controls.
	 */
	private void buildControlPanel() {
		controlPanel.setBounds(0, 0, 250, 700);
		loadingPanel.setOpaque(false);
		controlPanel.add(loadingPanel, State.LOADING.toString());
		controlPanel.add(preparationPanel, State.PREPARATION.toString());
		controlPanel.add(observationPanel, State.OBSERVATION.toString());
		cardLayout.show(controlPanel, State.LOADING.toString());
		controlPanel.setVisible(true);
	}

	/**
	 * Creates the StatusPanel at the bottom of the GUI.
	 */
	private void buildStatusPanel() {
		statusLabel.setFont(statusFont);
		statusPanel.setBackground(Color.YELLOW);
		statusPanel.add(statusLabel);
	}

	/**
	 * Sets the text of the statuspanel. It is shown for ten seconds.
	 * 
	 * @param status
	 *            The statustext
	 */
	public void setStatus(String status) {
		if (statusThread != null)
			statusThread.interrupt();
		statusLabel.setText(status);
		if (!status.equals("")) {
			statusThread = new Thread() {
				@Override
				public void run() {
					try {
						sleep(10000);
						setStatusWithoutThread("");
					} catch (InterruptedException e) {
						// Do nothing in particular
					}
				}
			};
			statusThread.start();
		}
	}

	public void setModel(Model model) {
		if (this.model != null) {
			model.removeObserver(this);
			model.getTraceObservers().removeObserver(observationMap);
		}
		this.model = model;
		if (this.model != null) {
			model.addObserver(this);
			model.getTraceObservers().addObserver(observationMap);
			cardLayout.show(controlPanel, model.getState().toString());
		}
	}

	@Override
	public void message(String message) {
		setStatus(message);
	}

	@Override
	public void update() {
		cardLayout.show(controlPanel, model.getState().toString());
		redraw();
	}

	private void redraw() {
		observationPanel.getQueueSizeLabel().setText(
				String.valueOf(model.getQueueSize()));
		observationPanel.getMaxQueueSizeLabel().setText(
				String.valueOf(model.getMaxQueueSize()));
		observationMap.updateTraceMarkers();
		observationMap.checkEndMarker();
		repaint();
	}

	@Override
	public void traceEvent(TraceEvent message) {
		if (message instanceof ExceptionThrownEvent) {
			setStatusWithoutThread(((ExceptionThrownEvent) message).getCause()
					.getMessage());
		}
		redraw();
	}

	@Override
	public void managerSet(Server server) {
		cardLayout.show(controlPanel, model.getState().toString());
		preparationPanel.managerSet(server);
	}

	@Override
	public void problemSet(Problem problem) {
		preparationPanel.problemSet(problem);
	}

	@Override
	public void algorithmSet(String algorithm) {
		// TODO
	}

	public Model getModel() {
		return model;
	}

	public ObservationPanel getObservationPanel() {
		return observationPanel;
	}

	/**
	 * Sets the text of the statuspanel. It is shown forever.
	 * 
	 * @param status
	 *            The statustext
	 */
	public void setStatusWithoutThread(final String status) {
		statusLabel.setText(status);
	}

	/**
	 * Get the status bar thread
	 * 
	 * @return
	 */
	public StatusBarThread getStatusBarThread() {
		return statusBarThread;
	}

	/**
	 * Set the status bar Thread
	 * 
	 * @param status
	 */
	public void setStatusBarThread(StatusBarThread status) {
		this.statusBarThread = status;
	}

	public PreparationPanel getPreparationPanel() {
		return preparationPanel;
	}

	public VisualizationTimer getTimer() {
		return timer;
	}

	public JPanel getControlPanel() {
		return controlPanel;
	}

	public JPanel getStatusPanel() {
		return statusPanel;
	}

	public Font getButtonFont() {
		return buttonFont;
	}

	public Font getLabelFont() {
		return labelFont;
	}

	public Font getStatusFont() {
		return statusFont;
	}

	public ObservationMap getObservationMap() {
		return observationMap;
	}

}

/* initialize textfields */
// goToStepField.setBounds(110, 250, 50, 35);

/* initialize slider */
// Position on the SliderPanel
// stepSizeSlider.setBounds(25, 170, 150, 80);
// stepSizeSlider.setForeground(Color.WHITE);
// stepSizeSlider.setMinorTickSpacing(1);
// stepSizeSlider.setPaintTicks(false);
// stepSizeSlider.setPaintLabels(false);
// stepSizeSlider.setValue(1);
// stepSizeSlider.setSnapToTicks(true);

// modify the slider (min- and max-value)
// modifySlider();

// debugPanel.add(stepSizeSlider);
// stepSizeSlider.setVisible(false);

/* initialize label */
// String step = "Current step: "
// + dataVisualizationManager.getStepCount();
// stepLabel = new JLabel(step);
// stepLabel.setHorizontalAlignment(JLabel.CENTER);
// stepLabel.setFont(labelFont);
// stepLabel.setForeground(Color.WHITE);
// stepLabel.setBounds(25, 500, 200, 30);

// goToLabel.setFont(labelFont);
// goToLabel.setForeground(Color.WHITE);
// goToLabel.setBounds(25, 250, 80, 35);

// titleLabel.setBounds(25, 170, 200, 30);
// titleLabel.setFont(labelFont);
// titleLabel.setForeground(Color.WHITE);

// stepSizeLabel.setText(String.valueOf(stepSizeSlider.getValue()));
// stepSizeLabel.setBounds(190, 170, 50, 80);
// stepSizeLabel.setFont(labelFont);
// stepSizeLabel.setForeground(Color.WHITE);

/* initialize Buttons */
/*
 * HTML-Code listed here: http://de.selfhtml.org/html/referenz/zeichen.htm
 * http://www.code-knacker.de/ansi.htm
 */
// startButton.setFont(buttonFont);
// startButton.setEnabled(false);

// loadXMLButton.setFont(buttonFont);
// loadXMLButton.setEnabled(false);

// autoButton.setFont(buttonFont);
// autoButton.setEnabled(false);

// nextButton.setFont(buttonFont);

// previousButton.setFont(buttonFont);

// goToButton.setFont(buttonFont);

// showAllButton = new JButton("Show All ("
// + dataVisualizationManager.getShapes().size() + ")");
// showAllButton.setFont(buttonFont);

/* initialize Checkboxes */
// highlightRouteCheckBox.setBounds(25, 340, 200, 25);
// highlightRouteCheckBox.setOpaque(false);
// highlightRouteCheckBox.setFont(labelFont);
// highlightRouteCheckBox.setForeground(Color.WHITE);

// showRouteCheckBox.setBounds(25, 360, 200, 25);
// showRouteCheckBox.setOpaque(false);
// showRouteCheckBox.setFont(labelFont);
// showRouteCheckBox.setForeground(Color.WHITE);

// startDestCheckBox.setBounds(25, 380, 200, 25);
// startDestCheckBox.setOpaque(false);
// startDestCheckBox.setFont(labelFont);
// startDestCheckBox.setForeground(Color.WHITE);

// heightInfoCheckBox.setBounds(25, 400, 200, 25);
// heightInfoCheckBox.setOpaque(false);
// heightInfoCheckBox.setFont(labelFont);
// heightInfoCheckBox.setForeground(Color.WHITE);

/* Button size */
// autoButton.setBounds(25, 440, 200, 35);
// nextButton.setBounds(25, 80, 200, 35);
// previousButton.setBounds(25, 120, 200, 35);
// goToButton.setBounds(175, 250, 50, 35);
// showAllButton.setBounds(25, 290, 200, 35);
// loadXMLButton.setBounds(25, 80, 200, 35);

/* Insert buttons to the controlPanel */
// loadXMLButton.setVisible(true);
// debugPanel.add(autoButton);
// autoButton.setVisible(false);
// debugPanel.add(getNextButton());
// startButton.setVisible(false);
// previousButton.setVisible(false);
// goToButton.setVisible(false);
// controlPanel.add(showAllButton);
// showAllButton.setVisible(false);
// highlightRouteCheckBox.setVisible(false);
// showRouteCheckBox.setVisible(false);
// startDestCheckBox.setVisible(false);
// heightInfoCheckBox.setVisible(false);

// debugPanel.add(previousButton);
// debugPanel.add(goToButton);
// debugPanel.add(highlightRouteCheckBox);
// debugPanel.add(showRouteCheckBox);
// debugPanel.add(startDestCheckBox);
// debugPanel.add(heightInfoCheckBox);
// debugPanel.add(goToLabel);
// debugPanel.add(titleLabel);
// debugPanel.add(stepSizeLabel);
// debugPanel.add(goToStepField);

// controlPanel.add(stepLabel);
// stepLabel.setVisible(false);
// goToLabel.setVisible(false);
// titleLabel.setVisible(false);
// stepSizeLabel.setVisible(false);
// goToStepField.setVisible(false);
/**
 * Paints a specific number of MapShapes.
 * 
 * @param start
 *            boolean if printing should be started at the beginning
 * @param num
 *            Number of MapShapes
 */
// public void paintMapShapes(boolean start, int num) {
// if (start) {
// dataVisualizationManager.getShapes().resetCursor();
// }
// for (int i = 0; i < num; i++) {
// dataVisualizationManager.getShapes().cursorForward();
// }
// mapViewer.repaint();
// }

// /**
// * Enables and disables the next- and the previous-button according to the
// * stepCount
// */
// public void testBoundaries() {
// if (model.getStepCount() == shapes.size()) {
// /* stepCount = MAX */
// autoButton.setEnabled(false);
// getNextButton().setEnabled(false);
// showAllButton.setText("Hide all (" + shapes.size() + ")");
// previousButton.setEnabled(true);
// } else if (model.getStepCount() == 0) {
// /* stepCount = 0 */
// autoButton.setEnabled(true);
// getNextButton().setEnabled(true);
// showAllButton.setText("Show all (" + shapes.size() + ")");
// previousButton.setEnabled(false);
// } else {
// /* 0 < stepCount < MAX */
// autoButton.setEnabled(true);
// getNextButton().setEnabled(true);
// showAllButton.setText("Show all (" + shapes.size() + ")");
// previousButton.setEnabled(true);
// }
// }
