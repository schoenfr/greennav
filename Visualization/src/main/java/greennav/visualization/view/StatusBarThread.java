package greennav.visualization.view;

/**
 * Thread der die Status Bar aktualisiert und ein Drehkreuz beim laden anzeigt
 * 
 * @author Ulf
 * 
 */
public class StatusBarThread extends Thread {

	View gui;
	String status;
	boolean loading = false;

	public StatusBarThread(View gui) {
		this.gui = gui;
	}

	public void setStatus(String status, boolean l) {
		this.status = status;
		this.loading = l;
		System.out.println("set status " + status);
	}

	@Override
	public void run() {
		try {
			while (!isInterrupted()) {
				if (loading) {
					gui.setStatusWithoutThread(status + " -");
					sleep(500);
					gui.setStatusWithoutThread(status + " \\");
					sleep(500);
					gui.setStatusWithoutThread(status + " |");
					sleep(500);
					gui.setStatusWithoutThread(status + " /");
					sleep(500);
				} else {
					gui.setStatusWithoutThread(status);
					sleep(10000);
				}
			}
		} catch (InterruptedException e) {
			// Do nothing in particular
		}
		gui.setStatusWithoutThread("");
	}
}
