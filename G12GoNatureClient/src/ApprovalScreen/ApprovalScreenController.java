package ApprovalScreen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import common.MessageType;
import common.TransferrableData;
import common.VisitReservation;
import connectionscreen.ConnectionScreenController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ApprovalScreenController {

    @FXML
    private Label alertMessageTextField;

    @FXML
    private Button approveButton;

    @FXML
    private Button backButton;

    @FXML
    private Label dateAndTime;

    @FXML
    private TextField dateAndTimeText;

    @FXML
    private Button declineButton;

    @FXML
    private Label groupType;

    @FXML
    private TextField groupTypeText;

    @FXML
    private ImageView imageView;

    @FXML
    private Label numberOfVisitors;

    @FXML
    private TextField numberOfVisitorsText;

    @FXML
    private Label parkName;

    @FXML
    private TextField parkText;

    @FXML
    private Label reservationApprovalTitle;

    @FXML
    private Label reservationNumber;

    @FXML
    private ChoiceBox<String> reservationNumberBox;

    @FXML
    private Label userID;

    @FXML
    private TextField userIDtext;

	@SuppressWarnings("unused")
	private Stage stage;
    
    
    private static ArrayList<VisitReservation> VisitReservationList=new ArrayList<>();
    
    // Method to approve a visit
    public void approveVisit(Object message) {
        try {
            TransferrableData data = new TransferrableData(MessageType.ApprovalScreenApproveVisit, message);
            ConnectionScreenController.client.handleMessageFromClientUI(data);// Display message on text field
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to cancel a visit
    public void cancelVisit(Object message) {
        try {
            TransferrableData data = new TransferrableData(MessageType.ApprovalScreenCancelVisit, message);
            ConnectionScreenController.client.handleMessageFromClientUI(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to display approval visit message on the text field
    public void showApprovalVisitMessage(TransferrableData transferrableData) {
    	boolean approved = (Boolean) transferrableData.getMessage();
    	Platform.runLater(new Runnable() {

			@Override
			public void run() {
				if(approved) {
		    		alertMessageTextField.setTextFill(Color.GREEN);
		    		alertMessageTextField.setText("Visit is approved.");
		    		ConnectionScreenController.client.handleMessageFromClientUI(new TransferrableData(MessageType.ApprovalScreenChooseReservationOptions, ConnectionScreenController.client.get_traveler()));
		    	}
		    	else {
		    		alertMessageTextField.setTextFill(Color.RED);
		    		alertMessageTextField.setText("Visit isn't approved.");
		    	}
			}
    		
    	});
    	
    }

    // Method to display canceled visit message on the text field
    public void showCanceledVisitMessage(TransferrableData transferrableData) {
    	boolean canceled = (Boolean) transferrableData.getMessage();
    	Platform.runLater(new Runnable() {

			@Override
			public void run() {
				if(canceled) {
		    		alertMessageTextField.setTextFill(Color.GREEN);
		    		alertMessageTextField.setText("Visit is canceled.");
		    		ConnectionScreenController.client.handleMessageFromClientUI(new TransferrableData(MessageType.ApprovalScreenChooseReservationOptions, ConnectionScreenController.client.get_traveler()));
		    	}
		    	else {
		    		alertMessageTextField.setTextFill(Color.RED);
		    		alertMessageTextField.setText("Visit isn't canceled.");
		    	}
			}
    		
    	});
    }

    @FXML
    void PressApproveButton(ActionEvent event) {
    	String selectedReservation = reservationNumberBox.getValue(); // Get the selected reservation
	    if (selectedReservation != null) {
	        VisitReservation selectedVisitReservation = getChosenReservation(Long.parseLong(selectedReservation));
	        approveVisit(selectedVisitReservation); // Call the method to approve the visit
	    } else {
	        // Display a message indicating that no reservation is selected
	        alertMessageTextField.setText("Please select a reservation to approve.");
	    }
    }

    @FXML
    void PressBackButton(ActionEvent event) {
    	ConnectionScreenController.client.handleMessageFromClientUI(new TransferrableData(MessageType.TravlerLogOut, ConnectionScreenController.client.get_traveler()));
    	openNextScene("/TravelerGUi/TravelerGui.fxml",event); // Open scene
    }

    @FXML
    void PressDeclineButton(ActionEvent event) {
    	String selectedReservation = reservationNumberBox.getValue(); // Get the selected reservation
        if (selectedReservation != null) {
            VisitReservation selectedVisitReservation = getChosenReservation(Long.parseLong(selectedReservation));
            cancelVisit(selectedVisitReservation); // Call the method to cancel the visit
        } else {
            // Display a message indicating that no reservation is selected
            alertMessageTextField.setText("Please select a reservation to decline.");
        }
    }
    
    
    @FXML
    public void initialize() {
    	imageView.setImage(new Image(getClass().getResourceAsStream("ApprovalScreenBackgroundImage.jpeg")));
    	reservationNumberBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
    		if(newValue != null) {
    			VisitReservation visitReservation =  getChosenReservation(Long.parseLong(newValue));
    			Platform.runLater(new Runnable() {

					@Override
					public void run() {
						userIDtext.setText(visitReservation.getTraveler().getId_number());
		    			parkText.setText(visitReservation.getPark().getParkname());
		    			dateAndTimeText.setText(visitReservation.getTimeofvisit().toString());
		    			numberOfVisitorsText.setText(String.valueOf(visitReservation.getNumofvisitors()));
		    			groupTypeText.setText(visitReservation.getGroupType());
					}
    				
    			});
    		}
    	});
    	ConnectionScreenController.client.setApprovalScreenController(this);
    	ConnectionScreenController.client.handleMessageFromClientUI(new TransferrableData(MessageType.ApprovalScreenChooseReservationOptions, ConnectionScreenController.client.get_traveler()));
        // Fetch data from the database
        // Populate the ChoiceBox with the fetched data
//    	if (data1 instanceof List && data2 instanceof List) {
//        chooseReservationChoose.getItems().addAll((List)data1);
//        parkChoose.getItems().addAll((List)data2);
//    	}
    }
    
    private void openNextScene(String loaction,ActionEvent event) { //return
	      try {
	          FXMLLoader loader = new FXMLLoader(getClass().getResource(loaction));
	          Scene scene = new Scene(loader.load());
	          Stage newStage = new Stage();
	          ((Node)event.getSource()).getScene().getWindow().hide();
	          newStage.setScene(scene);
	          newStage.show();
	      } 
	      catch (IOException e) {
	          e.printStackTrace();
	          // Handle the exception if unable to load or show the next scene
	      }
	 }

	@SuppressWarnings("unchecked")
	public void updateParkAndDateDetails(TransferrableData transferrableData) {
		VisitReservationList = (ArrayList<VisitReservation>)transferrableData.getMessage();
		List<String> lst = new ArrayList<>();
		for(VisitReservation vr: VisitReservationList) {
			String details = String.valueOf(vr.getReservationID());
			lst.add(details);
		}
		ObservableList<String> ol = FXCollections.observableArrayList(lst);
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				userIDtext.clear();
				parkText.clear();
				dateAndTimeText.clear();
				numberOfVisitorsText.clear();
				groupTypeText.clear();
				reservationNumberBox.setItems(ol);
				reservationNumberBox.getSelectionModel().clearSelection();
			}
			
		});
		
	}
	
	private VisitReservation getChosenReservation(Long reservationID) {
		for(VisitReservation vr: VisitReservationList) {
			if(vr.getReservationID() == reservationID)
				return vr;
		}
		return null;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

}
