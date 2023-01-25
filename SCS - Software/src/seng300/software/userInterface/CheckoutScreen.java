package seng300.software.userInterface;

import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.lsmr.selfcheckout.devices.EmptyException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.PLUCodedProduct;
import org.lsmr.selfcheckout.products.Product;

import seng300.software.selfcheckout.product.ProductDatabase;
import seng300.software.selfcheckout.station.SelfCheckoutStationLogic;


public class CheckoutScreen extends Screen implements ActionListener, DocumentListener{
	private JFrame frame;
	private JFrame checkoutFrame;
    private JFrame PLUFrame;
    private JFrame SearchFrame;
    private JFrame PayFrame;
    private JFrame PayCashFrame;
    private JFrame PayCardFrame;
    private boolean isBlocked = false;
    private JFrame MemberFrame;
    private JFrame blockFrame;
    private SelfCheckoutStationLogic checkout;
    private JPanel checkoutPanel;
    private JFrame removeFrame;
    
    static JTextArea text;
    static JLabel label;
    static JTextArea Mtext;
    static JLabel Mlabel;
    static JLabel member;
    static JLabel totalAmount;
    static JTextArea itemsList;
    
    private String productName;
	private BigDecimal productPrice;
    private String updateString;
    private BigDecimal total;
    private Document itemCode;
    
	
	//constructor
	public CheckoutScreen(SelfCheckoutStationLogic sl) {
		position = new Point(0, 0);
		frame = new JFrame("Checkout");
    	checkoutFrame = new JFrame("Checkout");
    	PLUFrame = new JFrame("PriceLookUp");
    	SearchFrame = new JFrame("Search");
    	PayFrame = new JFrame("Pay");
    	PayCashFrame = new JFrame("PayCash");
    	PayCardFrame = new JFrame("PayCard");
    	MemberFrame = new JFrame("Member");
    	checkout = sl;
    	itemsList = new JTextArea(10,15);
	}
	
	//helper function
	//given PLUCode, find the product
	public PLUCodedProduct findPLUCodedProduct(String code) {
		PLUCodedProduct foundItem = null;
		ArrayList<Product> products = checkout.getProductDatabase().getProducts();
		for (int i = 0; i< products.size(); i++) {
			if (products.get(i) instanceof PLUCodedProduct) {
				PLUCodedProduct pluP = (PLUCodedProduct) products.get(i);
				if (pluP.getPLUCode().toString().equals(code)) {
					foundItem = pluP;
				}
			}
		}
		return foundItem;
	}
	
	//given name, find the product in pluCart
	public PLUCodedProduct findPLUProductByName(String name) {
		PLUCodedProduct foundItem = null;
		ArrayList<Product> products = checkout.getProductDatabase().getProducts();
		for (int i = 0; i< products.size(); i++) {
			if (products.get(i) instanceof PLUCodedProduct) {
				PLUCodedProduct pluP = (PLUCodedProduct) products.get(i);
				if (pluP.getDescription().equals(name)) {
					foundItem = pluP;
				}
			}
		}
		return foundItem;
	}
	//given name, find the product in cart
		public BarcodedProduct findProductByName(String name) {
			BarcodedProduct foundItem = null;
			ArrayList<Product> products = checkout.getProductDatabase().getProducts();
			for (int i = 0; i< products.size(); i++) {
				if (products.get(i) instanceof BarcodedProduct) {
					BarcodedProduct barP = (BarcodedProduct) products.get(i);
					if (barP.getDescription().equals(name)) {
						foundItem = barP;
					}
				}
			}
			return foundItem;
		}
	
	//Initialize the welcome screen
    public void initialScreen() {
        frame.setSize(maxX, maxY);
        frame.setLocation(position);
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        
        //start button
        JButton startButton = new JButton("Start");
        startButton.setFont(startButton.getFont().deriveFont((float)36));
        startButton.setActionCommand(START);
        startButton.addActionListener(this);
        
        panel.add(startButton);
        
        frame.getContentPane().add(panel, BorderLayout.CENTER);
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.setVisible(true);
    }
    
    //Membership Screen
    public void membershipScreen() {
    	frame.setVisible(false);
    	JPanel memberPanel = new JPanel();
        memberPanel.setLayout(new GridBagLayout());

        MemberFrame.setSize(maxX, maxY);
        MemberFrame.setLocation(frame.getLocation());    	
    	Mlabel = new JLabel("Please type in your membership number");        
        Mlabel.setFont(Mlabel.getFont().deriveFont((float)24));
        memberPanel.add(Mlabel);
        
        Mtext = new JTextArea(1,10);
        Mtext.setFont(Mtext.getFont().deriveFont((float)24));
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.fill = GridBagConstraints.HORIZONTAL;
        gbc2.gridx = 0;
        gbc2.gridy = 1;
        memberPanel.add(Mtext, gbc2);
        
        //Submit member number
        JButton SubmitButton = new JButton("Submit");
        SubmitButton.setFont(SubmitButton.getFont().deriveFont((float)24));
        SubmitButton.setActionCommand(MEMBERSUBMIT);
        SubmitButton.addActionListener(this);
        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.fill = GridBagConstraints.HORIZONTAL;
        gbc1.gridx = 1;
        gbc1.gridy = 1;
        
        memberPanel.add(SubmitButton, gbc1);
        
        //None button
        JButton NoneButton = new JButton("None Member");
        NoneButton.setFont(NoneButton.getFont().deriveFont((float)24));
        NoneButton.setActionCommand(NONEMEMBER);
        NoneButton.addActionListener(this);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1;
        gbc.gridy = 2;
        memberPanel.add(NoneButton, gbc);
        
        
        MemberFrame.getContentPane().add(memberPanel, BorderLayout.CENTER);
    	MemberFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	MemberFrame.setVisible(true);
    }
    
   //Checkout Screen
    public void checkoutScreen() {
    	MemberFrame.setVisible(false);
    	PLUFrame.setVisible(false);
    	SearchFrame.setVisible(false);
    	checkoutPanel = new JPanel();
    	checkoutPanel.setLayout(new GridBagLayout());
    	
    	//Show member info
    	member = new JLabel("Membership:");
        member.setFont(member.getFont().deriveFont((float)24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        checkoutPanel.add(member, gbc);
        
        JButton removeButton = new JButton("Remove item");
        removeButton.setFont(removeButton.getFont().deriveFont((float)24));
        removeButton.setActionCommand(REMOVE);
        removeButton.addActionListener(this);
        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.fill = GridBagConstraints.HORIZONTAL;
        gbc1.gridx = 2;
        gbc1.gridy = 1;
        checkoutPanel.add(removeButton, gbc1);
        
    	//Permit customer to scan
        checkoutFrame.setSize(maxX, maxY);
        checkoutFrame.setLocation(frame.getLocation());
        JLabel scanPermit = new JLabel("Please scan your items");
        scanPermit.setFont(scanPermit.getFont().deriveFont((float)24));
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.fill = GridBagConstraints.HORIZONTAL;
        gbc2.gridx = 0;
        gbc2.gridy = 1;
        checkoutPanel.add(scanPermit,gbc2);
        
        //find item without code
        JButton findItemButton = new JButton("Find items without barcode");
        findItemButton.setFont(findItemButton.getFont().deriveFont((float)24));
        findItemButton.setActionCommand(FINDITEM);
        findItemButton.addActionListener(this);
        GridBagConstraints gbc3 = new GridBagConstraints();
        gbc3.fill = GridBagConstraints.HORIZONTAL;
        gbc3.gridx = 0;
        gbc3.gridy = 2;
        checkoutPanel.add(findItemButton, gbc3);
        
        //PLU
        JButton PLUButton = new JButton("Look up items price here");
        PLUButton.setFont(PLUButton.getFont().deriveFont((float)24));
        PLUButton.setActionCommand(PLU);
        PLUButton.addActionListener(this);
        GridBagConstraints gbc4 = new GridBagConstraints();
        gbc4.fill = GridBagConstraints.HORIZONTAL;
        gbc4.gridx = 0;
        gbc4.gridy = 3;
        checkoutPanel.add(PLUButton, gbc4);
        
        //Items' list
        itemsList = new JTextArea(10,15);
        itemsList.setEditable(false);
        itemsList.setFont(itemsList.getFont().deriveFont((float)24));
        GridBagConstraints gbc5 = new GridBagConstraints();
        gbc5.fill = GridBagConstraints.HORIZONTAL;
        gbc5.gridx = 1;
        gbc5.gridy = 1;
        checkoutPanel.add(itemsList, gbc5);
        
        //Total amount
        totalAmount = new JLabel("Total: ");
        totalAmount.setFont(totalAmount.getFont().deriveFont((float)24));
        GridBagConstraints gbc6 = new GridBagConstraints();
        gbc6.fill = GridBagConstraints.HORIZONTAL;
        gbc6.gridx = 1;
        gbc6.gridy = 5;
        checkoutPanel.add(totalAmount, gbc6);
        
        //Pay
        JButton payButton = new JButton("Pay");
        payButton.setFont(payButton.getFont().deriveFont((float)24));
        payButton.setActionCommand(PAY);
        payButton.addActionListener(this);
        GridBagConstraints gbc8 = new GridBagConstraints();
        gbc8.fill = GridBagConstraints.HORIZONTAL;
        gbc8.gridx = 2;
        gbc8.gridy = 5;
        checkoutPanel.add(payButton, gbc8);

        checkoutFrame.getContentPane().add(checkoutPanel, BorderLayout.LINE_START);
        checkoutFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        checkoutFrame.setVisible(true);
    }
    
    //Search items without barcode screen
    public void SearchScreen() {
    	checkoutFrame.setVisible(false);
    	JPanel SearchPanel = new JPanel();
    	SearchPanel.setLayout(new GridBagLayout());
    	
    	SearchFrame.setSize(maxX, maxY);
    	SearchFrame.setLocation(frame.getLocation());
    	   	
    	label = new JLabel("Type the item's name");
        label.setFont(label.getFont().deriveFont((float)24));
        SearchPanel.add(label);
        
        text = new JTextArea(1,10);
        text.setFont(text.getFont().deriveFont((float)24));
        text.getDocument().addDocumentListener(this);
        text.getDocument().putProperty("name", "Text Area");
        
        GridBagConstraints gbc2 = new GridBagConstraints();
    	gbc2.fill = GridBagConstraints.HORIZONTAL;
        gbc2.gridx = 0;
        gbc2.gridy = 2;
        SearchPanel.add(text, gbc2);
        
        JButton EnterButton = new JButton("Submit");
        EnterButton.setFont(EnterButton.getFont().deriveFont((float)24));
        EnterButton.setActionCommand(SEARCHSUBMIT);
        EnterButton.addActionListener(this);
        GridBagConstraints gbc = new GridBagConstraints();
    	gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 2;
        gbc.gridy = 3;
        SearchPanel.add(EnterButton, gbc);
        
        JButton SearchExitButton = new JButton("Exit");
        SearchExitButton.setFont(SearchExitButton.getFont().deriveFont((float)24));
        SearchExitButton.setActionCommand(SEARCHEXIT);
        SearchExitButton.addActionListener(this);
        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.fill = GridBagConstraints.HORIZONTAL;
        gbc1.gridx = 3;
        gbc1.gridy = 3;
        SearchPanel.add(SearchExitButton, gbc1);
    	
        SearchFrame.getContentPane().add(SearchPanel, BorderLayout.LINE_START);
        SearchFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        SearchFrame.setVisible(true);
    }
    
    //Price look up screen
    public void PLUScreen() {
    	checkoutFrame.setVisible(false);
    	JPanel PLUPanel = new JPanel();
    	PLUPanel.setLayout(new GridBagLayout());
    	GridBagConstraints gbc = new GridBagConstraints();
    	
    	gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 2;
        gbc.gridy = 2;
    	
    	PLUFrame.setSize(maxX, maxY);
    	PLUFrame.setLocation(frame.getLocation());
        JLabel scanPermit = new JLabel("Please scan your items");
        scanPermit.setFont(scanPermit.getFont().deriveFont((float)24));
        PLUPanel.add(scanPermit);
        
        JButton PLUExitButton = new JButton("Exit");
        PLUExitButton.setFont(PLUExitButton.getFont().deriveFont((float)24));
        PLUExitButton.setActionCommand(PLUEXIT);
        PLUExitButton.addActionListener(this);
        PLUPanel.add(PLUExitButton, gbc);
    	
        PLUFrame.getContentPane().add(PLUPanel, BorderLayout.LINE_START);
        PLUFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        PLUFrame.setVisible(true);
    	
    }
    
    public void insertCashScreen() {
    	JPanel cashPanel = new JPanel();
    	cashPanel.setLayout(new GridBagLayout());
    	
    	PayCashFrame.setSize(maxX, maxY);
    	PayCashFrame.setLocation(frame.getLocation());
    	JLabel insertCash = new JLabel("Please insert cash");
        insertCash.setFont(insertCash.getFont().deriveFont((float)24));
        cashPanel.add(insertCash);
        
        PayCashFrame.getContentPane().add(cashPanel, BorderLayout.LINE_START);
        PayCashFrame.setVisible(true);
    }
    
    public void insertCardScreen() {
    	JPanel cardPanel = new JPanel();
    	cardPanel.setLayout(new GridBagLayout());
    	
    	PayCardFrame.setSize(maxX, maxY);
    	PayCardFrame.setLocation(frame.getLocation());
    	JLabel insertCard = new JLabel("Please insert, swipe or tap your card");
        insertCard.setFont(insertCard.getFont().deriveFont((float)24));
        cardPanel.add(insertCard);
        
        PayCardFrame.getContentPane().add(cardPanel, BorderLayout.LINE_START);
        PayCardFrame.setVisible(true);
    }
  //Payment screen
    public void paymentScreen() {
    	JPanel payPanel = new JPanel();
    	payPanel.setLayout(new GridBagLayout());
    	
    	PayFrame.setSize(maxX, maxY);
    	PayFrame.setLocation(frame.getLocation());
    	JButton payCardButton = new JButton("Pay with card");
    	payCardButton.setFont(payCardButton.getFont().deriveFont((float)24));
    	payCardButton.setActionCommand(PAYCARD);
    	payCardButton.addActionListener(this);
    	payPanel.add(payCardButton);
        
        JButton payCashButton = new JButton("Pay with cash");
        payCashButton.setFont(payCashButton.getFont().deriveFont((float)24));
        payCashButton.setActionCommand(PAYCASH);
        payCashButton.addActionListener(this);
        payPanel.add(payCashButton);
        
        PayFrame.getContentPane().add(payPanel, BorderLayout.LINE_START);
        PayFrame.setVisible(true);
    	//ask number of bag used -> ask for membership card/gift card -> ask for payment method -> print receipt
    }
    
    //update screen after adding/removing items
    public void updateScreen() {
    	checkoutFrame.setVisible(false);
    	itemsList.setText(null);
    	for (int i = 0; i < checkout.pluCart.size(); i++) {
    		productName = checkout.pluCart.get(i).getDescription();
    		productPrice = checkout.pluCart.get(i).getPrice();
    		
    		JLabel itemLabel = new JLabel(productName + " 	" + productPrice.toString());
    		itemLabel.setFont(itemLabel.getFont().deriveFont((float)12));
    		checkoutFrame = new JFrame("Checkout");
    		checkoutFrame.setSize(maxX, maxY);
            checkoutFrame.setLocation(frame.getLocation());
            itemsList.append(itemLabel.getText() + "\n");
    	}
    	total = checkout.getFinalPrice();
    	totalAmount.setText("Total:   " + total.toString());
    	checkoutFrame.getContentPane().add(checkoutPanel, BorderLayout.LINE_START);
    	checkoutFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	checkoutFrame.setVisible(true);
    }
    
  //remove screen
    public void removeScreen() {
    	removeFrame = new JFrame("Remove");
    	removeFrame.setSize(maxX, maxY);
        removeFrame.setLocation(position);
        JPanel removePanel = new JPanel();
        removePanel.setLayout(new GridBagLayout());
        
      //bar code
    	for (int i = 0; i < checkout.cart.size(); i++) {
    		productName = checkout.cart.get(i).getDescription();
    		productPrice = checkout.cart.get(i).getPrice();
    		
    		JButton item = new JButton(productName + " 	" + productPrice.toString());
    		item.setFont(item.getFont().deriveFont((float)12));
            item.setActionCommand(productName);
            item.addActionListener(this);
            removePanel.add(item);
    	}
    	//PLU code
    	for (int i = 0; i < checkout.pluCart.size(); i++) {
    		productName = checkout.pluCart.get(i).getDescription();
    		productPrice = checkout.pluCart.get(i).getPrice();
    		
    		JButton item = new JButton(productName + " 	" + productPrice.toString());
    		item.setFont(item.getFont().deriveFont((float)12));
            item.setActionCommand(productName);
            item.addActionListener(this);
            removePanel.add(item);
    	}
        
        removeFrame.getContentPane().add(removePanel, BorderLayout.NORTH);
    	removeFrame.setVisible(true);
    }
    
    //setter
    public void block() {
    	isBlocked = true;
    }
    
    public void unblock() {
    	isBlocked = false;
    }

    //Screen when the station is blocked
    public void blockScreen() {
    	blockFrame = new JFrame("Blocked");
    	blockFrame.setSize(maxX, maxY);
    	blockFrame.setLocation(frame.getLocation());
    	JPanel blockPanel = new JPanel();
    	blockPanel.setLayout(new GridBagLayout());
    	JLabel block = new JLabel("This station is blocked. An attendant is on the way.");
    	block.setFont(block.getFont().deriveFont((float)24));
    	blockPanel.add(block);
    	blockFrame.getContentPane().add(blockPanel, BorderLayout.CENTER);
    	block();
    	blockFrame.setAlwaysOnTop(true);
    	blockFrame.setVisible(true);
    }
    
    //unblock
    public void unblockScreen() {
    	blockFrame.setVisible(false);
    	unblock();
    }
    
    //print receipt
    //for debugging only
    public void printReceipt() {
    	System.out.println("");
    	System.out.println("Receipt: ");
    	System.out.println(itemsList.getText());
    	System.out.println("Total:   " + total.toString());
    }
    
   //handle action events
    public void actionPerformed(ActionEvent e) {
    	String command = e.getActionCommand();
    	if (!isBlocked) {
    		switch(command) {
        	case START:
        		membershipScreen();
        		System.out.println("Start is clicked");
        		break;
        	case PAY:
        		paymentScreen();
        		System.out.println("Pay is clicked");
        		break;
        	case FINDITEM:
        		SearchScreen();
        		System.out.println("Find item without barcode is clicked");
        		break;
        	case PLU:
        		PLUScreen();
        		System.out.println("PLU is clicked");
        		break;
        	case PLUEXIT:
        		checkoutScreen();
        		System.out.println("PLUExit is clicked");
        		break;
        	case SEARCHEXIT:
        		checkoutScreen();
        		text.setText(null);
        		System.out.println("SearchExit is clicked");
        		break;
        	case SEARCHSUBMIT:
        		System.out.println("SearchSubmit is clicked");
        		//label.setText(text.getText());
        		PLUCodedProduct p;
				try {
					p = findPLUCodedProduct(itemCode.getText(0, 4));
					checkout.PLUCodedProductAdded(p);
					System.out.println(p.getDescription() + " is added to the cart.");
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}
				text.setText(null);
        		updateScreen();
        		break;
        	case PAYCARD:
        		insertCardScreen();
        		System.out.println("PayCard is clicked");
        		printReceipt();
        		checkout.printReceipt();
        		break;
        	case PAYCASH:
        		insertCashScreen();
        		System.out.println("PayCash is clicked");
        		printReceipt();
        		checkout.printReceipt();
        		break;
        	case MEMBERSUBMIT:
        		if(Mtext.getText().length() != 0) {
        			checkoutScreen();
        			member.setText("Membership:  " + Mtext.getText());
        			System.out.println("MemberSubmit is clicked");
        		}
    			break;
    		case NONEMEMBER:
    			checkoutScreen();
    			member.setText("Membership:  None");
    			System.out.println("NoneMember is clicked");
    			break;
    		case REMOVE:
    			removeScreen();
    			break;
    		default:
    			PLUCodedProduct pluProduct = null;
    			BarcodedProduct barProduct = null;
    			pluProduct = findPLUProductByName(command);
    			barProduct = findProductByName(command);
    			
    			if (pluProduct != null) {
    				checkout.removePLUcodedItem(pluProduct);
    			} else if (barProduct != null) {
    				checkout.removeBarcodedItem(barProduct);
    			}
    			updateScreen();
    			break;
        	}
    	}
    }
    
    public void insertUpdate(DocumentEvent e) {
        updateLog(e);
    }
    public void removeUpdate(DocumentEvent e) {
        updateLog(e);
    }
    public void changedUpdate(DocumentEvent e) {
        //Plain text components do not fire these events
    }
    public void updateLog(DocumentEvent e) {
        itemCode = (Document)e.getDocument();
    }
}