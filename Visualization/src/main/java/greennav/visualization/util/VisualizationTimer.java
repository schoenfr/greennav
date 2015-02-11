package greennav.visualization.util;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.concurrent.atomic.AtomicLong;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 * Timer class for displaying a timer during visualization
 * 
 * @author Christian
 */
public class VisualizationTimer extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 112552462L;
	private final Font borderFont = new Font("Helvetica",
			Font.LAYOUT_LEFT_TO_RIGHT, 12);
	/**
	 * Reference startpoint for calculation of current timer value
	 */
	private AtomicLong start = new AtomicLong(System.currentTimeMillis());

	/**
	 * Layout, labels and panel for timer visualization
	 */
	private FlowLayout layout;
	private JLabel lHours, lHoursSeperator;
	private JLabel lMinutes, lMinutesSeperator;
	private JLabel lSeconds;

	/**
	 * Font for the timer
	 */
	private final Font timerFont = new Font("Helvetica",
			Font.LAYOUT_LEFT_TO_RIGHT, 14);

	/**
	 * Thread updating the timer
	 */
	private Thread timerThread;

	/**
	 * Constants in seconds
	 */
	private final long HOUR = 3600l;
	private final long MINUTE = 60l;
	private final String ZERO = "00";

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            parent panel that should contain the timer
	 */
	public VisualizationTimer() {
		initializeGUIComponents();
		initializeTimerThread();
	}

	/**
	 * Initializes the timer GUI components
	 */
	private void initializeGUIComponents() {
		lHoursSeperator = new JLabel(":");
		lHours = new JLabel(ZERO);
		lMinutesSeperator = new JLabel(":");
		lMinutes = new JLabel(ZERO);
		lSeconds = new JLabel(ZERO);
		layout = new FlowLayout();
		layout.setVgap(0);

		/* Modify the textcolor */
		lHours.setForeground(Color.BLACK);
		lHoursSeperator.setForeground(Color.BLACK);
		lMinutes.setForeground(Color.BLACK);
		lMinutesSeperator.setForeground(Color.BLACK);
		lSeconds.setForeground(Color.BLACK);

		/* Modify the font */
		lHours.setFont(timerFont);
		lHoursSeperator.setFont(timerFont);
		lMinutes.setFont(timerFont);
		lMinutesSeperator.setFont(timerFont);
		lSeconds.setFont(timerFont);

		setLayout(layout);

		add(lHours);
		add(lHoursSeperator);
		add(lMinutes);
		add(lMinutesSeperator);
		add(lSeconds);

		Border border = BorderFactory.createLineBorder(Color.BLACK);
		setBorder(BorderFactory.createTitledBorder(border,
				"Time since launch:", 0, 0, borderFont, Color.BLACK));
		setBounds(25, 640, 200, 50);
		setVisible(true);

		// parent.setBackground(Color.GRAY);

		// /* load background image */
		// JLabel bg_small = new JLabel(new ImageIcon(
		// VisualizationImages.class.getResource("background_small.jpg")));
		// bg_small.setBounds(0, 0, 200, 40);
		//
		// /* add the background to the JPanel */
		// parent.add(bg_small);
	}

	/**
	 * Method to initialize the timer thread
	 */
	private void initializeTimerThread() {
		timerThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					long seconds = (long) ((System.currentTimeMillis() - start
							.get()) / 1000);
					updateGUI(seconds);
				}
			}

		});
	}

	/**
	 * Method to update the gui
	 * 
	 * @param seconds
	 *            new value for the timer (seconds since start)
	 */
	private void updateGUI(long seconds) {
		long remainingSeconds = seconds;
		if (remainingSeconds >= HOUR) {
			lHours.setText(String.format("%02d", remainingSeconds / HOUR));
			lMinutes.setText(ZERO);
			remainingSeconds %= HOUR;
		}
		if (remainingSeconds >= MINUTE) {
			lMinutes.setText(String.format("%02d", remainingSeconds / MINUTE));
			lSeconds.setText(ZERO);
			remainingSeconds %= MINUTE;
		}
		lSeconds.setText(String.format("%02d", remainingSeconds));
	}

	/**
	 * Resets the timer
	 */
	public void reset() {
		start.set(System.currentTimeMillis());
		updateGUI(0);
	}

	/**
	 * Starts the timer
	 */
	public void start() {
		timerThread.start();
	}

}
