package greennav.visualization.view;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.LayoutManager;

import javax.swing.JPanel;

/**
 * The image panel is for the control panel, it extends the usual
 * <code>JPanel</code> with a background image.
 */
public class ImagePanel extends JPanel {

	/**
	 * A serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The background image.
	 */
	private Image img;

	/**
	 * The constructor takes a layout manager and the background image.
	 * 
	 * @param layout
	 *            A layout manager.
	 * @param img
	 *            The background image.
	 */
	public ImagePanel(LayoutManager layout, Image img) {
		super(layout);
		this.img = img;
		Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		setSize(size);
	}

	/**
	 * The paint component method is overridden in order to paint the background
	 * image.
	 */
	public void paintComponent(Graphics g) {
		g.drawImage(img, 0, 0, null);
	}

}
