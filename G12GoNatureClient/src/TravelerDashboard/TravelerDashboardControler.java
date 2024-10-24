package TravelerDashboard;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import TravelerGUi.TravelerGuiControler;
import connectionscreen.ConnectionScreenController;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import common.*;

/**TravelerDashboardControler is a boundary class that shows the board for the
 * traveler. It shows a table of existing and upcoming reservations, the waiting
 * list of the traveler and the possibility to change their phone number and
 * email address.
 */
/**
 * 
 */
public class TravelerDashboardControler implements Initializable{
	
	/** This is the primary stage on which the client's screen are shown on.
	 *
	 */
	private Stage stage;
	/** A list of all upcoming visit reservations that are displayed on the screen.
	 * 
	 */
	private List<VisitReservation> visitReservationList;
	/** A list of all reservations that are waiting to be created.
	 * 
	 */
	private List<VisitReservation> waitingList;
	/** An instance of the visit reservation that the user chose to cancel.
	 * 
	 */
	private VisitReservation chosenVisitReservation;
	/** An instance of the visit reservation that is on the waiting list
	 *  that is chosen by the user to exit from the list.
	 */
	private VisitReservation waitingReservationToExit;
	/** These are buttons that are used in the alerts that confirm some actions
	 *  that the user is capable of doing.
	 */
	private ButtonType continueButton, abortButton;
	
	@FXML
    private TableColumn<String[], String> ReservationsIDColumn;

    @FXML
    private TableColumn<String[], String> ReservationsParkNameColumn;

    @FXML
    private TableColumn<String[], String> ReservationsTimeColumn;

    @FXML
    private TableColumn<String[], String> ReservationsVisitorsColumn;

    @FXML
    private TableColumn<String[], String> WaitingIDColumn;

    @FXML
    private TableColumn<String[], String> WaitingParkNameColumn;

    @FXML
    private TableColumn<String[], String> WaitingTimeColumn;

    @FXML
    private TableColumn<String[], String> WaitingVisitorsColumn;
	
    @FXML
    private Button cancelReservationButton;

    @FXML
    private Button changeEmailAddressButton;

    @FXML
    private Button changePhoneNumberButton;

    @FXML
    private Label emailAddressMessage;

    @FXML
    private TextField emailAddressTextField;

    @FXML
    private Button exitWaitingListTable;

    @FXML
    private Label phoneNumberMessage;
    
    @FXML
    private TextField phoneNumberTextField;

    @FXML
    private ComboBox<String> reservationIDComboBox;

    @FXML
    private Label reservationMessage;

    @FXML
    private TableView<String[]> reservationsTable;

    @FXML
    private Button returnButton;

    @FXML
    private Label userLabel;

    @FXML
    private ComboBox<String> waitingReservationsIDComboBox;

    @FXML
    private Label waitingListMessage;

    @FXML
    private TableView<String[]> waitingListTable;
    
    /** The method shows an alert with a customized method.
     * The alert is from the type "Confirmation" and the user can choose
     * between two choice: continue or abort.
     * @param alertMessage A message that will be displayed on the alert.
     * @return the button that will be clicked on the alert.
     */
    private Optional<ButtonType> showAlert(String alertMessage){
    	Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText(null);
        alert.setContentText(alertMessage);

        continueButton = new ButtonType("Yes, I do");
        abortButton = new ButtonType("No, I don't");
        
        alert.getButtonTypes().setAll(continueButton, abortButton);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.lookupButton(continueButton).setId("continueButton");
        dialogPane.lookupButton(abortButton).setId("abortButton");
        dialogPane.getStylesheets().add(getClass().getResource("alertStyles.css").toExternalForm());
        
        Optional<ButtonType> result = alert.showAndWait();
        return result;
    }
    
    /** The method handles what will happen when Cancel Reservation button
     * will be clicked. It checks whether the user chose a reservation to cancel
     * and then calls the client to pass a message to the server to remove
     * the reservation (if pressed "Ok" in the alert).
     * @param event The event of clicking on Cancel Reservation button.
     */
    @FXML
    void cancelReservationClicked(ActionEvent event) {
    	reservationMessage.setText("");
    	reservationMessage.setTextFill(Color.RED);
    	try{
    		Long reservationID = Long.parseLong(reservationIDComboBox.getValue());
    		chosenVisitReservation = findReservationFromList(reservationID, visitReservationList);
    		if(chosenVisitReservation == null)
    			reservationMessage.setText("The reservation doesn't exist.");
    		else {
    	        Optional<ButtonType> result = showAlert(String.format("Are you sure you want to cancel reservation %d?", reservationID));
    	        if (result.isPresent() && result.get() == continueButton) {
    	        	ConnectionScreenController.client.handleMessageFromClientUI(new TransferrableData(MessageType.TravelerDashboardCancelReservation, chosenVisitReservation));
    	        }
    		}
    	} catch(NumberFormatException exception) {
    		reservationMessage.setText("Please choose an ID for canceling.");
    	}
    }

    /** This method handles the event of clicking on changing the phone number.
     * It validates that the input is valid and then sends a message to the server
     * to change the phone number to the inserted value.
     * @param event The event of clicking on "Change Phone Number" button.
     */
    @FXML
    void changePhoneNumberClicked(ActionEvent event) {
    	phoneNumberMessage.setText("");
    	phoneNumberMessage.setTextFill(Color.RED);
    	String newPhoneNumber = phoneNumberTextField.getText();
    	if(!newPhoneNumber.startsWith("+"))
    		phoneNumberMessage.setText("Phone number should start with a \'+\'");
    	else if(newPhoneNumber.length() < 13)
    		phoneNumberMessage.setText("Phone number should have at least 12 digits");
    	else {
    		Traveler traveler = copyOfTraveler();
    		traveler.setPhone_number(newPhoneNumber);
    		ConnectionScreenController.client.handleMessageFromClientUI(new TransferrableData(MessageType.TravlerDashboardChangePhoneNumber, traveler));
    	}
    }

    /** The method handles the event of changing email address. 
     * @param event The event of clicking on "Change Email Address" button.
     */
    @FXML
    void changeEmailAddressClicked(ActionEvent event) {
    	emailAddressMessage.setText("");
    	emailAddressMessage.setTextFill(Color.RED);
    	String newEmailAddress = emailAddressTextField.getText();
    	if(!newEmailAddress.contains("@"))
    		emailAddressMessage.setText("Email address should contain a \'@\'");
    	else if(!newEmailAddress.endsWith(".com"))
    		emailAddressMessage.setText("Email address should have \".com\" at the end");
    	else {
    		Traveler traveler = copyOfTraveler();
    		traveler.setEmail_addres(newEmailAddress);
    		ConnectionScreenController.client.handleMessageFromClientUI(new TransferrableData(MessageType.TravelerDashboardChangeEmail, traveler));
    	}
    }

    @FXML
    void exitWaitingListClicked(ActionEvent event) {
    	waitingListMessage.setText("");
    	waitingListMessage.setTextFill(Color.RED);
    	try {
    		long waitingListReservationID = Long.parseLong(waitingReservationsIDComboBox.getValue());
    		waitingReservationToExit = findReservationFromList(waitingListReservationID, waitingList);
    		if(waitingReservationToExit == null)
    			waitingListMessage.setText("The reservation doesn't exist.");
    		else {
    			Optional<ButtonType> result = showAlert(String.format("Are you sure you want reservation no. %d to exit the waiting list?", waitingListReservationID));
    	        if (result.isPresent() && result.get() == continueButton) {
    	        	ConnectionScreenController.client.handleMessageFromClientUI(new TransferrableData(MessageType.TravelerDashboardExitWaitingList, waitingReservationToExit));
    	        }
    		}
    	} catch(NumberFormatException exception) {
    		waitingListMessage.setText("Please choose an ID for exiting.");
    	}
    }

    @FXML
    void returnToPreviousScreen(ActionEvent event) {
    	ConnectionScreenController.client.handleMessageFromClientUI(new TransferrableData(MessageType.TravlerLogOut, ConnectionScreenController.client.get_traveler()));
    	openNextScene("/TravelerGUi/TravelerGui.fxml", event);
    }
    
    private void openNextScene(String location, ActionEvent event) {
	      try {
	    	  FXMLLoader loader = new FXMLLoader(getClass().getResource(location));
	    	  Parent root = loader.load();
	          Scene scene = new Scene(root);
	          Stage newStage = new Stage();
	          newStage.setScene(scene);
	          TravelerGuiControler controller = loader.getController();
	          controller.setStage(stage); 
	          newStage.show();
	          // Hide the current stage
		      ((Node)event.getSource()).getScene().getWindow().hide();
	      } 
	      catch (IOException e) {
	          e.printStackTrace();
	          // Handle the exception if unable to load or show the next scene
	      }
	 }
    
    private VisitReservation findReservationFromList(long reservationID, List<VisitReservation> reservationsList) {
    	for(VisitReservation visitReservation : reservationsList) {
    		if(visitReservation.getReservationID() == reservationID) {
    			return visitReservation;
    		}
    	}
    	return null;
    }
    
    private Traveler copyOfTraveler() {
    	Traveler traveler = new Traveler(ConnectionScreenController.client.get_traveler().getId_number());
    	traveler.setEmail_addres(ConnectionScreenController.client.get_traveler().getEmail_addres());
    	traveler.setPhone_number(ConnectionScreenController.client.get_traveler().getPhone_number());
    	traveler.setIsguide(ConnectionScreenController.client.get_traveler().isIsguide());
    	return traveler;
    }
    
	@SuppressWarnings("unchecked")
	public void updateReservationsTable(TransferrableData transferrableData) {
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				visitReservationList = (List<VisitReservation>) transferrableData.getMessage();
				ObservableList<String[]> reservationsObservableList = FXCollections.observableArrayList();
				ObservableList<String> reservationIDs = FXCollections.observableArrayList();
				reservationsTable.setItems(reservationsObservableList);
				reservationIDComboBox.setItems(reservationIDs);
				for(VisitReservation visitReservation : visitReservationList) {
		    		String[] details = new String[4];
		    		String reservationID = new Long(visitReservation.getReservationID()).toString();
		    		details[0] = reservationID;
		    		reservationIDs.add(reservationID);
		    		details[1] = visitReservation.getPark().getParkname();
		    		details[2] = visitReservation.getTimeofvisit().toString();
		    		details[3] = new Integer(visitReservation.getNumofvisitors()).toString();
		    		reservationsObservableList.add(details);
		    	}
				reservationIDComboBox.setPromptText("Choose ID");
				reservationIDComboBox.getSelectionModel().clearSelection();
				reservationsTable.refresh();
			}
		});
	}

	public void updateEmailField(TransferrableData transferrableData) {
		ConnectionScreenController.client.get_traveler().setEmail_addres(((Traveler) transferrableData.getMessage()).getEmail_addres());
		try {
			emailAddressTextField.setText(ConnectionScreenController.client.get_traveler().getEmail_addres());
			Platform.runLater(() -> {
				emailAddressMessage.setTextFill(Color.GREEN);
				emailAddressMessage.setText("Email updated successfully");
			});
		} catch(IllegalStateException e) {}
		
	}

	@SuppressWarnings("unchecked")
	public void updateWaitingListTable(TransferrableData transferrableData) {
		waitingList = (List<VisitReservation>) transferrableData.getMessage();
		waitingListTable.getItems().clear();
		ObservableList<String[]> reservationsObservableList = FXCollections.observableArrayList();
		ObservableList<String> reservationIDs = FXCollections.observableArrayList();
		waitingListTable.setItems(reservationsObservableList);
		waitingReservationsIDComboBox.setItems(reservationIDs);
		for(VisitReservation visitReservation : waitingList) {
    		String[] details = new String[4];
    		String reservationID = new Long(visitReservation.getReservationID()).toString();
    		waitingReservationsIDComboBox.setValue(reservationID);
    		reservationIDs.add(reservationID);
    		details[0] = reservationID;
    		details[1] = visitReservation.getPark().getParkname();
    		details[2] = visitReservation.getTimeofvisit().toString();
    		details[3] = new Integer(visitReservation.getNumofvisitors()).toString();
    		reservationsObservableList.add(details);
    	}
		waitingReservationsIDComboBox.setPromptText("Choose ID");
		waitingReservationsIDComboBox.getSelectionModel().clearSelection();
		waitingListTable.refresh();
	}

	public void showTravelerDetails(TransferrableData transferrableData) {
		userLabel.setText(ConnectionScreenController.client.get_traveler().getId_number());
		emailAddressTextField.setText(ConnectionScreenController.client.get_traveler().getEmail_addres());
		phoneNumberTextField.setText(ConnectionScreenController.client.get_traveler().getPhone_number());
	}

	public void updatePhoneNumber(TransferrableData transferrableData) {
		ConnectionScreenController.client.get_traveler().setPhone_number(((Traveler) transferrableData.getMessage()).getPhone_number());
		try {
			phoneNumberTextField.setText(ConnectionScreenController.client.get_traveler().getPhone_number());
			Platform.runLater(() -> {
				phoneNumberMessage.setTextFill(Color.GREEN);
				phoneNumberMessage.setText("Phone number updated successfully");
			});
		} catch(IllegalStateException e) {}
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	private void initializeTableViews() {
		ReservationsIDColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[0]));
		ReservationsParkNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[1]));
		ReservationsTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[2]));
		ReservationsVisitorsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[3]));
		WaitingIDColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[0]));
		WaitingParkNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[1]));
		WaitingTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[2]));
		WaitingVisitorsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[3]));
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initializeTableViews();
		ConnectionScreenController.client.setTravelerDashboardController(this);
		ConnectionScreenController.client.handleMessageFromClientUI(new TransferrableData(MessageType.TravelerDashboardReservations, ConnectionScreenController.client.get_traveler()));
		ConnectionScreenController.client.handleMessageFromClientUI(new TransferrableData(MessageType.TravelerDashboardWaitingList, ConnectionScreenController.client.get_traveler()));
    	showTravelerDetails(null);
    	reservationsTable.setRowFactory(tableView -> {
    		TableRow<String[]> row = new TableRow<>();
    		row.setOnMouseClicked(event -> {
    			String[] rowData = row.getItem();
    			if(rowData != null) {
    				reservationIDComboBox.setValue(rowData[0]);
    			}
    		});
    		return row;
    	});
    	waitingListTable.setRowFactory(tableView -> {
    		TableRow<String[]> row = new TableRow<>();
    		row.setOnMouseClicked(event -> {
    			String[] rowData = row.getItem();
    			if(rowData != null) {
    				waitingReservationsIDComboBox.setValue(rowData[0]);
    			}
    		});
    		return row;
    	});
	}

	public void cancelReservation(TransferrableData transferrableData) {
		boolean canceled = (Boolean) transferrableData.getMessage();
			if(canceled) {
				try {
					reservationMessage.setTextFill(Color.GREEN);
					Platform.runLater(() -> {
						reservationMessage.setText("Reservation is canceled.");
					});
				} catch(IllegalStateException e) {}
				visitReservationList.remove(chosenVisitReservation);
				updateReservationsTable(new TransferrableData(null, visitReservationList));
			}
			else {
				reservationMessage.setTextFill(Color.RED);
				reservationMessage.setText("Reservation couldn't be canceled.");
			}
	}

	public void cancelWaitingListReservation(TransferrableData transferrableData) {
		boolean canceled = (Boolean) transferrableData.getMessage();
		if(canceled) {
			try {
				Platform.runLater(() -> {
					waitingListMessage.setTextFill(Color.GREEN);
					waitingListMessage.setText("Exited from waiting list.");
				});
			} catch(IllegalStateException e) {}
			waitingList.remove(waitingReservationToExit);
			updateWaitingListTable(new TransferrableData(null, waitingList));
		}
		else {
			waitingListMessage.setTextFill(Color.RED);
			waitingListMessage.setText("Reservation couldn't be canceled.");
		}
	}
}
