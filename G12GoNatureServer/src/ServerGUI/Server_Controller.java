package ServerGUI;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import common.MessageType;
import common.TransferrableData;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import logic.AvailableVisitsSlotsFinder;
import logic.DeleteNotVisitedReservations;
import logic.RequestsForComingVisits;
import logic.WaitingListChecker;
import logic.ReminderForApprovingVisits;
import server.DBController;
import server.GoNatureServer;

public class Server_Controller {
	private final String DEFAULT_PORT = "5555";
	private static GoNatureServer sv;
	private DBController DB_controller;
	@SuppressWarnings("unused")
	private Stage stage;
	protected static Thread findAvailableVisitsSlots;
	protected static Thread comingVisitsRequests;
	protected static Thread approvingVisitsRemindersChecking;
	protected static Thread checkWaitingLists;
	protected static Thread deleteReservationThatPassed;
	
	 // Setter method to set the stage reference
	 public void setStage(Stage stage) {
	      this.stage = stage;
	 }
	 
	 public static GoNatureServer get_server() {
		 return Server_Controller.sv;
	 }
	 
	@FXML
	private Text notification, log_data;
	
	@FXML
	private TextField ip, port, DB_name, DB_login;
	
	@FXML
	private PasswordField DB_password;
	
	@FXML
	private GridPane gridPane;
	
	@FXML
	private ImageView logo;
	
	@FXML
    private HBox buttons;
	
	@FXML
    private VBox fields_and_logo;
	
	@FXML
	private TableColumn<String[], String> ipCol, hostCol, statusCol;
	
	@FXML
	private TableView<String[]> table;
	
	private ObservableList<String[]> tableData = FXCollections.observableArrayList();
	
	@FXML
     void initialize() {
        log_data.setText("");
        notification.setText("");
        setFields();
        gridPane.getColumnConstraints().get(1).setPrefWidth(150);
        
        // sets cell value factories for table columns
        ipCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[0]));
        hostCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[1]));
        statusCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[2]));
        
        // sets items for table
        table.setItems(tableData);
        
        // rounds the corners of the logo
        double radius = 50;
        Rectangle clip = new Rectangle(logo.getFitWidth() - radius, logo.getFitHeight());
        clip.setArcWidth(radius);
        clip.setArcHeight(radius);
        logo.setClip(clip);
    }
	
	// sets all TextFields and PasswordField to default values
	private void setFields() {
		try {
			ip.setText(InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			ip.setText("Unknown");
			e.printStackTrace();
		}
        port.setText(DEFAULT_PORT);
        DB_name.setText(DBController.DBName);
        DB_login.setText(DBController.DBRoot);
        DB_password.setText(DBController.DBPassword);
	}
	@FXML
	 void pressedStart(ActionEvent event) {
		String message = "";
		
		// handles DB connection
		if (null == DB_controller) // if there isn't a DB controller yet
			setDB_controller();
		else if (!DB_controller.getName().equals(DB_name.getText())) // if server-user tries to connect to a different DB
			setDB_controller();
		if (!DB_controller.isConnected()) { // if DB isn't connected yet
			DB_controller.connectToDB(); // connect to DB
			if (DB_controller.isConnected()) // if connection succeed
				message += "Connected to DataBase\n";
			else {
				notification.setText("Couldn't connected to DataBase");
				return;
			}
		}
		
		// handles server connection
		if (null == sv) { // if server doesn't exist yet
			message += startListening();
		}
		// if server isn't listening yet or server-user tries connecting to a different port
		else if (!sv.isListening() || (sv.getPort() != Integer.parseInt(port.getText()))) {
			sv.stopListening();
			try {
				sv.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			message += startListening();
		}
		else // server is already listening using the desired port 
			message += "Already listening";
		notification.setText(message);
	}
	
	// returns whether the server started listening or not
	private String startListening() {
		return runServer(port.getText())? "Start listening" : "Couldn't start listening";
	}
	
	// sets DB controller
	private void setDB_controller() {
		DB_controller = new DBController(DB_name.getText(), DB_login.getText(), DB_password.getText());
	}
	// stops listening, closes server and exits the program
	@FXML
	void pressedStop(ActionEvent event) {
		if (null != sv) {
			Server_Controller.get_server().sendToAllClients(new TransferrableData(MessageType.TravlerLogOutALL, null));
    		DBController.logOutEveryone();
			sv.stopListening();
			this.setEmptyTable();
			try {
				sv.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		notification.setText("Stopped listening");
		//System.exit(0);
	}
	
	// adds a new line to record log
	public void updateLog(String message) {
		log_data.setText(log_data.getText() + message + "\n");
	}
	
	// adds a new row to table
	public void updateTable(String ip, String host, String status) {
		tableData.add(new String[] {ip, host, status});
		table.refresh();
	}
	
	public void removeFromTable(String ip, String host) {
		boolean flag = true;
		ObservableList<String[]> newData = FXCollections.observableArrayList();
	    for (String[] row : tableData) {
	    	if(!row[0].equals(ip) && !row[1].equals(host) || flag == false) {
	    		newData.add(row);
	    	}
	    	else {
	    		flag = false;
	    	}
//	        if (!row[0].equals(ip) && !row[1].equals(host)&& flag==false) {
//	            newData.add(row);
//	            flag = true;
//	        }
	    }
	    tableData.setAll(newData);
	}
	
	public void setEmptyTable() {
		ObservableList<String[]> newData = FXCollections.observableArrayList();
		tableData.setAll(newData);
	}
	
	// returns true if server started listening else returns false
	public boolean runServer(String p)
	{
		int port = 0; //port to listen on
        try
        {
        	port = Integer.parseInt(p); //sets port to 5555
        }
        catch(Throwable t)
        {
        	this.updateLog("ERROR - Could not connect!");
//        	System.out.println("ERROR - Could not connect!");
        }
        this.port.setText(Integer.toString(port));
        sv = new GoNatureServer(port);
        sv.setServerController(this);
        try 
        {
          sv.listen(); //starts listening for connections
          runThreads();
          return true;
        } 
        catch (Exception ex) 
        {
        	this.updateLog("ERROR - Could not listen for clients!");
          //System.out.println("ERROR - Could not listen for clients!");
          return false;
        }
	}
	
	private void runThreads() {
		AvailableVisitsSlotsFinder availableVisitsSlotsFinder = new AvailableVisitsSlotsFinder();
        findAvailableVisitsSlots = new Thread(availableVisitsSlotsFinder);
        availableVisitsSlotsFinder.setThread(findAvailableVisitsSlots);
        
        RequestsForComingVisits requestsForComingVisits = new RequestsForComingVisits();
        comingVisitsRequests = new Thread(requestsForComingVisits);
        requestsForComingVisits.setThread(comingVisitsRequests);
		
        ReminderForApprovingVisits reminderForApprovingVisits = new ReminderForApprovingVisits();
        approvingVisitsRemindersChecking = new Thread(reminderForApprovingVisits);
        reminderForApprovingVisits.setThread(approvingVisitsRemindersChecking);
        
        WaitingListChecker waitingListChecker = new WaitingListChecker();
        checkWaitingLists = new Thread(waitingListChecker);
        waitingListChecker.setThread(checkWaitingLists);
        
        DeleteNotVisitedReservations deleteNotVisitedReservations = new DeleteNotVisitedReservations();
        deleteReservationThatPassed = new Thread(deleteNotVisitedReservations);
        deleteNotVisitedReservations.setThread(deleteReservationThatPassed);
        
		findAvailableVisitsSlots.start();
		comingVisitsRequests.start();
		approvingVisitsRemindersChecking.start();
		checkWaitingLists.start();
		deleteReservationThatPassed.start();
	}
	
}
