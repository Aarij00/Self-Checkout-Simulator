package seng300.software.userInterface;

import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;

import javax.swing.*;

import org.lsmr.selfcheckout.devices.SelfCheckoutStation;

import java.util.*;

import seng300.software.attendant.AttendantLogic;

public class AttendantScreen extends Screen implements ActionListener{
	private JFrame frame;
	private AttendantLogic attendant;
	private JFrame blockFrame;
	private JFrame unblockFrame;
	private IdentityHashMap<SelfCheckoutStation, Integer> stationList= new IdentityHashMap<SelfCheckoutStation, Integer>();
	private ArrayList<SelfCheckoutStation> supervisedStations;
	private JPanel panel;
	private ArrayList<CheckoutScreen> screens;
	private JFrame loginFrame;
	
	//constant for station status
	protected final static Integer BLOCKED = 0;
	protected final static Integer FREE = 1;
	
	
	//This is a attendant station that can control at most 4 checkout stations
	//constructor
	public AttendantScreen(AttendantLogic al, ArrayList<CheckoutScreen> sc) {
		position = new Point(0, 0);
		loginFrame = new JFrame("Attendant");
		frame = new JFrame("Attendant");
		blockFrame = new JFrame("Stations");
		unblockFrame = new JFrame("BlockedStations");
		panel = new JPanel();
		attendant = al;
		supervisedStations = attendant.getSupervisedStations();
		screens = sc;

		for (int i = 0; i < supervisedStations.size(); i++) {
			stationList.put(supervisedStations.get(i), FREE);
		}
	}
	
	
	//helper function
	public int findStation(String s) {
		char stationNmuber = s.charAt(8);
		int intStationNum = Character.getNumericValue(stationNmuber) - 1;
		return intStationNum;
	}
	
	//getter
	public ArrayList<CheckoutScreen> getScreens() {
		return screens;
	};
	
	//log in screen
	public void loginScreen() {
		loginFrame.setSize(maxX, maxY);
		loginFrame.setLocation(position);
		JPanel loginPanel = new JPanel();
		loginPanel.setLayout(new GridBagLayout());
		
		//login button
        JButton loginButton = new JButton("Log in");
        loginButton.setFont(loginButton.getFont().deriveFont((float)36));
        loginButton.setActionCommand(LOGIN);
        loginButton.addActionListener(this);
        
        loginPanel.add(loginButton);
        loginFrame.getContentPane().add(loginPanel, BorderLayout.CENTER);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setVisible(true);
	}
	
	
	//Initialize attendant screen
    public void initialScreen() {
    	frame.setSize(maxX, maxY);
    	frame.setLocation(position);
    	panel = new JPanel();
	    panel.setLayout(new GridLayout(3, 1));
        
        //block button
        JButton blockButton = new JButton("Block");
        blockButton.setFont(blockButton.getFont().deriveFont((float)36));
        blockButton.setActionCommand(BLOCK);
        blockButton.addActionListener(this);
        
        //unblock button
        JButton unblockButton = new JButton("Unblock");
        unblockButton.setFont(unblockButton.getFont().deriveFont((float)36));
        unblockButton.setActionCommand(UNBLOCK);
        unblockButton.addActionListener(this);
        
        //logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(logoutButton.getFont().deriveFont((float)36));
        logoutButton.setActionCommand(LOGOUT);
        logoutButton.addActionListener(this);
        
        panel.add(blockButton);
        panel.add(unblockButton);
        panel.add(logoutButton);
        
        frame.getContentPane().add(panel, BorderLayout.NORTH);
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.setVisible(true);
    }
    
    //display a list of stations and let attendant choose to block
    public void displayBlock() {
    	blockFrame = new JFrame();
    	blockFrame.setSize(maxX, maxY);
    	blockFrame.setLocation(frame.getLocation());
        JPanel blockPanel = new JPanel();
        blockPanel.setLayout(new FlowLayout());
        
        //go through the list of stations, display if the station is not blocked
        System.out.println(supervisedStations.size());
        for (int i = 0; i < supervisedStations.size(); i++) {
        	String str = "Station ";
        	if (stationList.get(supervisedStations.get(i)) == FREE) {
        		String stationNumber = Integer.toString(i + 1);
        		str += stationNumber;
        		JButton stationButton = new JButton(str);
        		stationButton.setFont(stationButton.getFont().deriveFont((float)24));
        		stationButton.setActionCommand(str);
        		stationButton.addActionListener(this);
        		blockPanel.add(stationButton);
        	}
        }
        
        blockFrame.getContentPane().add(blockPanel, BorderLayout.LINE_START);
    	blockFrame.setVisible(true);
    }
    
    //display a list of blocked stations and let attendant choose to unblock
    public void displayUnblock() {
    	unblockFrame = new JFrame();
    	unblockFrame.setSize(maxX, maxY);
    	unblockFrame.setLocation(frame.getLocation());
        JPanel unblockPanel = new JPanel();
        unblockPanel.setLayout(new FlowLayout());
        
        //go through the list of stations, display if the station is not blocked
        for (int i = 0; i < supervisedStations.size(); i++) {
        	String str = "Station ";
        	if (stationList.get(supervisedStations.get(i)) == BLOCKED) {
        		String stationNumber = Integer.toString(i + 1);
        		str += stationNumber;
        		JButton stationButton = new JButton(str);
        		stationButton.setFont(stationButton.getFont().deriveFont((float)24));
        		stationButton.setActionCommand(str + "_unblock");
        		stationButton.addActionListener(this);
        		unblockPanel.add(stationButton);
        	}
        }
        
        unblockFrame.getContentPane().add(unblockPanel, BorderLayout.LINE_START);
    	unblockFrame.setVisible(true);
    }
    
    //prompt attendant to investigate and render assistance
    public void promptAttendant(SelfCheckoutStation station) {
    	String str = "Station ";
    	for (int i = 0; i < supervisedStations.size(); i++) {
    		if (supervisedStations.get(i) == station) {
    			String stationNum = Integer.toString(i + 1);
    			str += stationNum + " needs investigation";
    			break;
    		}
    	}
    	JLabel promptLabel = new JLabel(str);
    	promptLabel.setFont(promptLabel.getFont().deriveFont((float)36));
    	
    	position = frame.getLocation();
		panel.add(promptLabel);
		frame.setVisible(false);
		frame = new JFrame("Attendant");
		frame.setSize(maxX, maxY);
		frame.setLocation(position);
		frame.getContentPane().add(panel, BorderLayout.NORTH);
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.setVisible(true);
    }
    
    //hide prompt message once resolved
    public void hidePrompt() {
    	frame.setVisible(false);
    	initialScreen();
    }
    
  //handle action events
    public void actionPerformed(ActionEvent e) {
    	String command = e.getActionCommand();
    	int index = -1;
    	switch(command) {
    	case BLOCK:
    		unblockFrame.setVisible(false);
    		displayBlock();
    		System.out.println("Block is clicked");
    		break;
    	case UNBLOCK:
    		blockFrame.setVisible(false);
    		displayUnblock();
    		System.out.println("Unblock is clicked");
    		break;
    	case "Station 1":
    		index = findStation(command);
    		stationList.replace(supervisedStations.get(index), BLOCKED);
    		blockFrame.setVisible(false);
    		blockFrame = new JFrame("Stations");
    		displayBlock();
    		screens.get(index).blockScreen();
    		attendant.attendantBlocksStation(supervisedStations.get(index));
    		System.out.println("Block station 1");
    		break;
    	case "Station 2":
    		index = findStation(command);
    		stationList.replace(supervisedStations.get(index), BLOCKED);
    		blockFrame.setVisible(false);
    		blockFrame = new JFrame("Stations");
    		displayBlock();
    		screens.get(index).blockScreen();
    		attendant.attendantBlocksStation(supervisedStations.get(index));
    		System.out.println("Block station 2");
    		break;
    	case "Station 3":
    		index = findStation(command);
    		stationList.replace(supervisedStations.get(index), BLOCKED);
    		blockFrame.setVisible(false);
    		blockFrame = new JFrame("Stations");
    		displayBlock();
    		screens.get(index).blockScreen();
    		attendant.attendantBlocksStation(supervisedStations.get(index));
    		System.out.println("Block station 3");
    		break;
    	case "Station 4":
    		index = findStation(command);
    		stationList.replace(supervisedStations.get(index), BLOCKED);
    		blockFrame.setVisible(false);
    		blockFrame = new JFrame("Stations");
    		displayBlock();
    		screens.get(index).blockScreen();
    		attendant.attendantBlocksStation(supervisedStations.get(index));
    		System.out.println("Block station 4");
    		break;
    	case "Station 1_unblock":
    		index = findStation(command);
    		stationList.replace(supervisedStations.get(index), FREE);
    		unblockFrame.setVisible(false);
    		unblockFrame = new JFrame("BlockedStations");
    		displayUnblock();
    		screens.get(index).unblockScreen();
    		attendant.attendantUnblocksStation(supervisedStations.get(index));
    		System.out.println("Unblock station 1");
    		break;
    	case "Station 2_unblock":
    		index = findStation(command);
    		stationList.replace(supervisedStations.get(index), FREE);
    		unblockFrame.setVisible(false);
    		unblockFrame = new JFrame("BlockedStations");
    		displayUnblock();
    		screens.get(index).unblockScreen();
    		attendant.attendantUnblocksStation(supervisedStations.get(index));
    		System.out.println("Unblock station 2");
    		break;
    	case "Station 3_unblock":
    		index = findStation(command);
    		stationList.replace(supervisedStations.get(index), FREE);
    		unblockFrame.setVisible(false);
    		unblockFrame = new JFrame("BlockedStations");
    		displayUnblock();
    		screens.get(index).unblockScreen();
    		attendant.attendantUnblocksStation(supervisedStations.get(index));
    		System.out.println("Unblock station 3");
    		break;
    	case "Station 4_unblock":
    		index = findStation(command);
    		stationList.replace(supervisedStations.get(index), FREE);
    		unblockFrame.setVisible(false);
    		unblockFrame = new JFrame("BlockedStations");
    		displayUnblock();
    		screens.get(index).unblockScreen();
    		attendant.attendantUnblocksStation(supervisedStations.get(index));
    		System.out.println("Unblock station 4");
    		break;
    	case LOGIN:
    		loginFrame.setVisible(false);
    		initialScreen();
    		attendant.consoleLogin();
    		System.out.println("Log in is clicked");
    		break;
    	case LOGOUT:
    		frame.setVisible(false);
    		loginScreen();
    		attendant.consoleLogout();
    		System.out.println("Log out is clicked");
    		break;
    	}
    }
}
