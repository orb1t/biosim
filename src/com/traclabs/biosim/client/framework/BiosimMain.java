package com.traclabs.biosim.client.framework;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import org.apache.log4j.Logger;

import com.traclabs.biosim.client.control.HandController;
import com.traclabs.biosim.client.control.MurderController;
import com.traclabs.biosim.client.control.SimCommandLine;
import com.traclabs.biosim.client.control.SimpleController;
import com.traclabs.biosim.client.control.TestController;
import com.traclabs.biosim.client.framework.apollo13.Apollo13Viewer;
import com.traclabs.biosim.client.sensor.framework.SensorViewer;
import com.traclabs.biosim.client.simulation.environment.EnvironmentGraph;
import com.traclabs.biosim.client.simulation.food.photosynthesis.PhotosynthesisPanel;
import com.traclabs.biosim.client.simulation.framework.SimDesktop;
import com.traclabs.biosim.client.unrealCom.UnrealCom;
import com.traclabs.biosim.client.util.BioHolderInitializer;
import com.traclabs.biosim.util.OrbUtils;

/**
 * @author Scott Bell
 */

public class BiosimMain {

	private Logger myLogger;

	public BiosimMain() {
		OrbUtils.initializeLog();
		myLogger = Logger.getLogger(BiosimMain.class);
	}

	/**
	 * The method to start the BIOSIM client.
	 * 
	 * @param args
	 *            can start TestDriver with -gui or -nogui (optional id=X where
	 *            X is an integer)
	 */
	public static void main(String args[]) {
		BiosimMain myMain = new BiosimMain();
		myMain.checkArgs(args);
	}

	private void checkArgs(String[] myArgs) {
		myLogger.debug("arg length = " + myArgs.length);
		int myID = 0;
		String xmlFile = null;
		boolean wantsToRunCommandLine = false;
		boolean wantsToRunGUI = false;
		boolean wantsToRunController = false;
		boolean wantsToRunUnreal = false;
		boolean wantsToPhotosynthesis = false;
		boolean wantsToRunSensorViewer = false;
		boolean wantsToRunApollo13Viewer = false;
		boolean wantsToRunSimpleController = false;
		boolean wantsToRunMurderController = false;
		boolean wantsToRunTestController = false;
		boolean wantsToRunGraph = false;
		boolean unrealServerGiven = false;
		String unrealServer = "";
		for (int i = 0; i < myArgs.length; i++) {
			if (myArgs[i].equals("gui")) {
				wantsToRunGUI = true;
			} else if (myArgs[i].equals("console")) {
				wantsToRunCommandLine = true;
			} else if (myArgs[i].equals("controller")) {
				wantsToRunController = true;
			} else if (myArgs[i].equals("simple-controller")) {
				wantsToRunSimpleController = true;
			} else if (myArgs[i].equals("unreal")) {
				wantsToRunUnreal = true;
			} else if (myArgs[i].equals("photosynthesis")) {
				wantsToPhotosynthesis = true;
			} else if (myArgs[i].equals("sensor")) {
				wantsToRunSensorViewer = true;
			} else if (myArgs[i].equals("graph")) {
				wantsToRunGraph = true;
			} else if (myArgs[i].equals("apollo")) {
				wantsToRunApollo13Viewer = true;
			} else if (myArgs[i].equals("murder")) {
				wantsToRunMurderController = true;
			} else if (myArgs[i].equals("test")) {
				wantsToRunTestController = true;
			} else if (myArgs[i].startsWith("-xml=")) {
				try {
					xmlFile = myArgs[i].split("=")[1];
				} catch (Exception e) {
					myLogger.error("Problem parsing arguments on arg "
							+ myArgs[i]);
					e.printStackTrace();
				}
			} else if ((myArgs[i].startsWith("-xml")) && (myArgs.length >= i)) {
				try {
					xmlFile = myArgs[i + 1];
				} catch (Exception e) {
					myLogger.error("Problem parsing arguments on arg "
							+ myArgs[i]);
					e.printStackTrace();
				}
			} else if (myArgs[i].startsWith("-id=")) {
				try {
					myID = Integer.parseInt(myArgs[i].split("=")[1]);
				} catch (Exception e) {
					myLogger.error("Problem parsing arguments on arg "
							+ myArgs[i]);
					e.printStackTrace();
				}
			} else if ((myArgs[i].startsWith("-id")) && (myArgs.length >= i)) {
				try {
					myID = Integer.parseInt(myArgs[i + 1]);
				} catch (Exception e) {
					myLogger.error("Problem parsing arguments on arg "
							+ myArgs[i]);
					e.printStackTrace();
				}
			} else if (myArgs[i].startsWith("-utServer=")) {
				try {
					unrealServerGiven = true;
					wantsToRunUnreal = true;
					unrealServer = myArgs[i].split("=")[1];
				} catch (Exception e) {
					myLogger.warn("Problem parsing arguments on arg "
							+ myArgs[i]);
					e.printStackTrace();
				}
			} else if ((myArgs[i].startsWith("-utServer"))
					&& (myArgs.length >= i)) {
				try {
					unrealServerGiven = true;
					wantsToRunUnreal = true;
					unrealServer = myArgs[i + 1];
				} catch (Exception e) {
					myLogger.error("Problem parsing arguments on arg "
							+ myArgs[i]);
					e.printStackTrace();
				}
			}
		}
		if (xmlFile != null)
			BioHolderInitializer.setFileAndID(myID, xmlFile);
		else
			BioHolderInitializer.setID(myID);

		if (wantsToRunCommandLine)
			runCommandLine(myID);
		else if (wantsToRunGUI)
			runGUI();
		else if (wantsToPhotosynthesis)
			runPhotosynthesis();
		else if (wantsToRunController)
			runHandController();
		else if (wantsToRunSimpleController)
			runSimpleController();
		else if (wantsToRunSensorViewer)
			runSensorViewer();
		else if (wantsToRunApollo13Viewer)
			runApollo13Viewer();
		else if (wantsToRunMurderController)
			runMurderController();
		else if (wantsToRunTestController)
			runTestController();
		else if (wantsToRunGraph)
			runGraph();
		else if (wantsToRunUnreal) {
			if (unrealServerGiven) {
				runUnreal(unrealServer);
			} else {
				runUnreal();
			}
		} else {
			myLogger.info("Using default, starting GUI with server ID=" + myID);
			runGUI();
		}
	}

	/**
	 * Runs the SimDesktop front end for the simulation.
	 */
	private void runGUI() {
		SimDesktop newDesktop = new SimDesktop();
		newDesktop.setSize(1024, 768);
		newDesktop.setVisible(true);
	}

	/**
	 * Runs the sensor viewer
	 */
	private void runSensorViewer() {
		SensorViewer.main(new String[] {});
	}

	private void runGraph() {
		EnvironmentGraph plotLive = new EnvironmentGraph();
		JFrame frame = new JFrame("Environment Graph");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add("Center", plotLive);
		plotLive.setButtons(true);
		frame.pack();
		frame.setVisible(true);
		plotLive.start();
	}

	/**
	 * Runs the commandline front end for the simulation.
	 */
	private void runCommandLine(int myID) {
		SimCommandLine newCommandLine = new SimCommandLine(myID);
		newCommandLine.runCommandLine();
	}

	private void runHandController() {
		HandController myController = new HandController();
		myController.runSim();
	}

	private void runSimpleController() {
		SimpleController myController = new SimpleController();
		myController.runSim();
	}

	/**
	 * Runs the UnrealCom interface to communicate with UnrealTournament 2004
	 */
	private void runUnreal() {
		UnrealCom myUnrealCom = new UnrealCom();
		myUnrealCom.initUnrealComm();
	}

	/**
	 * Runs the UnrealCom interface to communicate with UnrealTournament 2004
	 * 
	 * @param unServer
	 *            The name of the unreal Server to connect to.
	 */
	private void runUnreal(String unServer) {
		UnrealCom myUnrealCom = new UnrealCom(unServer);
		myUnrealCom.initUnrealComm();
	}

	/**
	 * Runs the Photosynthesis GUI
	 */
	private void runPhotosynthesis() {
		BioFrame myFrame = new BioFrame("Photosynthesis Model", false);
		myFrame.getContentPane().add(new PhotosynthesisPanel());
		myFrame.pack();
		myFrame.setSize(800, 600);
		ImageIcon biosimIcon = new ImageIcon(BiosimMain.class.getClassLoader()
				.getResource("com/traclabs/biosim/client/food/food.png"));
		myFrame.setIconImage(biosimIcon.getImage());
		myFrame.setVisible(true);
	}

	private void runApollo13Viewer() {
		Apollo13Viewer.main(new String[] {});
	}

	private void runMurderController() {
		MurderController.main(new String[] {});
	}

	private void runTestController() {
		TestController.main(new String[] {});
	}
}