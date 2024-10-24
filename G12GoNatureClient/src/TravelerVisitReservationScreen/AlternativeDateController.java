package TravelerVisitReservationScreen;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.sql.Timestamp;

import common.MessageType;
import common.TransferrableData;
import common.VisitReservation;
import connectionscreen.ConnectionScreenController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class AlternativeDateController implements Initializable{
	
	@SuppressWarnings("unused")
	private Stage stage; // Reference to the stage of the current 
	private VisitReservation visitReservation;
	private boolean wasCreatedOnce = false;
	
	public void setStage(Stage stage) {
	      this.stage = stage;
    }
	
	@FXML
    private Label createReservationMessage;
	
    @FXML
    private ComboBox<String> datePicker;
    
    @FXML
    private Button returnButton;

	private TravelerVisitReservationScreenControler travelerVisitReservationScreenControler;

    @FXML
    void pressChoose(ActionEvent event) {
    	VisitReservation visitReservation = travelerVisitReservationScreenControler.getReservation();
    	visitReservation.setTimeofvisit(Timestamp.valueOf(datePicker.getValue() + ":00"));
    	this.visitReservation = visitReservation;
    	if(!wasCreatedOnce)
    		ConnectionScreenController.client.handleMessageFromClientUI(new TransferrableData(MessageType.AlternativeDateScreenCreateVisit, visitReservation));
    	else {
    		createReservationMessage.setTextFill(Color.RED);
    		createReservationMessage.setText("Can't create because you already created one visit.");
    	}
    }
    
    @FXML
    void pressReturn(ActionEvent event) {
    	((Node)event.getSource()).getScene().getWindow().hide();
    	travelerVisitReservationScreenControler.disableReservationFields(false);
    	travelerVisitReservationScreenControler.continueVisitReservationProcess(true);
    	travelerVisitReservationScreenControler.setDefaultValues();
    }
    
    
	@SuppressWarnings("unchecked")
	public void setOptionsForNewDate(TransferrableData transferrableData) {
		List<String> availableDatesAndTimes = (List<String>) transferrableData.getMessage();
		ObservableList<String> optionsForDates = FXCollections.observableArrayList();
		for(String time : availableDatesAndTimes) {
			optionsForDates.add(time);
		}
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				datePicker.setItems(optionsForDates);
				datePicker.getSelectionModel().clearSelection();
			}
			
		});
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ConnectionScreenController.client.setAlternativeDateController(this);
	}


	public void showMessageForVisit(TransferrableData transferrableData) {
		VisitReservation createdVisit = (VisitReservation) transferrableData.getMessage();
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				if(createdVisit != null) {
					visitReservation = createdVisit;
					createReservationMessage.setTextFill(Color.GREEN);
					createReservationMessage.setText("Created a Visit Reservation.");
					wasCreatedOnce = true;
					travelerVisitReservationScreenControler.showMessageOfCreatingReservation(new TransferrableData(null, visitReservation));
				}
				else {
					createReservationMessage.setTextFill(Color.RED);
					createReservationMessage.setText("Couldn't create a reservation.");
				}
			}
		});
	}

	public void setTravelerVisitReservationScreen(
			TravelerVisitReservationScreenControler travelerVisitReservationScreenControler) {
		this.travelerVisitReservationScreenControler = travelerVisitReservationScreenControler;
	}

	public void setVisitReservation(VisitReservation visitReservation) {
		this.visitReservation = visitReservation;
		ConnectionScreenController.client.handleMessageFromClientUI(new TransferrableData(MessageType.GetAllReservations, visitReservation));
	}
	
}
