package seng300.software.userInterface;
import java.awt.Point;
import java.awt.event.*;

abstract class Screen extends WindowAdapter{
    protected final int maxX = 800;
    protected final int maxY = 480;
    protected Point position;
  
    //constants for action commands
    protected final static String START = "start";
    protected final static String PAY = "pay";
    protected final static String FINDITEM = "find_item";
    protected final static String PLU = "PLU";
    protected final static String PLUEXIT = "PLU_exit";
    protected final static String MEMBERSHIP = "membership";
    protected final static String BLOCK = "block";
    protected final static String UNBLOCK = "unblock";
    protected final static String PAYCARD = "payCard";
    protected final static String PAYCASH = "payCash";
    protected final static String SEARCHEXIT = "Search_Exit";
    protected final static String SEARCHSUBMIT = "Search_Submit";
    protected final static String MEMBERSUBMIT = "Member_Submit";
    protected final static String NONEMEMBER = "None_Member";
    protected final static String LOGIN = "log_in";
    protected final static String LOGOUT = "log_out";
    protected final static String REMOVE = "remove";
    
    
    //constructor
    public Screen() {
    }
    
    abstract void initialScreen();
}
