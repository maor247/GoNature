package WorkerDashboard; 

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;


import TravelerVisitReservationScreen.TravelerVisitReservationScreenControler;
import client.GoNatureClient;
import common.MessageType;
import common.Park;
import common.TransferrableData;
import common.Traveler;
import common.VisitReservation;
import connectionscreen.ConnectionScreenController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class WorkerDashboardController implements Initializable{
	private final int CARD_NUM_LEN = 16, SECRET_NUM_LEN = 3, ID_LEN = 9;
	
	private GoNatureClient client;
	private List<Park> parkList;
	private ActionEvent createReservationEvent;
	
	private Stage stage; // Reference to the stage of the current scene
	  
	@FXML
	private TextField availableSpace;

	@FXML
	private Button calculateBtn;

	@FXML
	private TextField cardDate;

	@FXML
	private TextField cardNum;

	@FXML
	private ComboBox<String> choosePark;

	@FXML
	private Button createReservation;

	@FXML
	private Button enterParkButton;

	@FXML
	private Label enterParkLabel;

	@FXML
	private Button exitParkButton;

	@FXML
	private Label exitParkLabel;

	@FXML
	private Text notification;

	@FXML
	private Button paymentBtn;

	@FXML
	private TextField paymentID;

	@FXML
	private TextField reservationID;

	@FXML
	private TextField reservationIDForExiting;

	@FXML
	private TextField reservationIDforEntering;

	@FXML
	private Button returnBtn;

	@FXML
	private TextField secretNum;

	@FXML
	private TextField sum;

	@FXML
	private TextField travelerIDTextField;

	@FXML
	private Text userName;

	public void setStage(Stage stage) {
		this.stage=stage;
	}
	  
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Fetch data from the database
		// Populate the ChoiceBox with the fetched data
		client = ConnectionScreenController.client;
		client.setWorkerDashboardController(this);
		client.handleMessageFromClientUI(new TransferrableData(MessageType.WorkerDashboardShowDetails, LocalDateTime.now()));
		userName.setText(client.get_worker().getFirstName());
	}
	
	// this method is called from GoNatureClient after receiving the message from the server due to initialize activation
	@SuppressWarnings("unchecked")
	public void setChoosePark(TransferrableData transferrableData) {
		Object obj = transferrableData.getMessage();
		if (!(obj instanceof Map<?, ?>)) {
			notification.setText("Unknown problem while attempting setting combo box values");
			return;
		}
		Map<Park, Integer> parksWithAvailableSpace = (Map<Park, Integer>) transferrableData.getMessage();
		parkList = Arrays.asList(parksWithAvailableSpace.keySet().toArray(new Park[3]));
		List<String> parkNamesList = new ArrayList<>();
		for (int i = 0; i < parkList.size(); i++) {
			parkNamesList.add(parkList.get(i).getParkname());
		}
		choosePark.getItems().addAll(parkNamesList);
		notification.setText("");
		setListener(parksWithAvailableSpace);
	}
	
	public void setListener(Map<Park, Integer> parksWithAvailableSpace) {
		choosePark.getSelectionModel().clearSelection();
		choosePark.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if(newValue != null) {
				for(Park park : parkList) {
					if(newValue.equals(park.getParkname())) {
						try {
							availableSpace.setText(String.valueOf(parksWithAvailableSpace.get(park)));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
    }
	
	@FXML
	void pressCalculate(ActionEvent event) {
		if (!isStringAnInteger(reservationID.getText())) {
			notification.setText("Invalid reservation ID, please enter a correct value");
			return;
		}
		client.handleMessageFromClientUI(new TransferrableData(MessageType.WorkerDashboardPaymentOnReservation, Long.parseLong(reservationID.getText())));
		notification.setText("");
	}
	
	// this method is called from GoNatureClient after receiving the message from the server due to pressCalculate activation
	public void setSum(TransferrableData transferrableData) {
		VisitReservation visitReservation = (VisitReservation) transferrableData.getMessage();
		if(visitReservation == null) {
			notification.setText("No reservation exists with this ID.");
			return;
		}
		Object obj = visitReservation.getPrice();
		if (!(obj instanceof Float)) {
			notification.setText("Unknown problem while attempting calculation");
			return;
		}
		float sumToPay = (Float) obj;
		sum.setText(String.valueOf(sumToPay));
		notification.setText("");
	}
	
	@FXML
	void pressPayment(ActionEvent event) {
		openReceipt();
	}
	  
	private void openReceipt() {
		try {
//			if (!validation()) { // checks if all fields are correctly filled in order to continue
//				notification.setText("Invalid values, please enter values correctly");
//				return;
//			}
			
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("Receipt.fxml")); // loads the fxml file
			VBox vbox = null;
			vbox = loader.load(); // saves the root Node in 'vbox'
			if (vbox == null) { // handles the case of not finding the root
		        System.out.println("VBox not found");
		        return;
		    }
			
			receiptController controller = loader.getController();
			
			String[] values = new String[] {choosePark.getValue(), reservationID.getText(), sum.getText()};
			controller.setValues(values);
			
			Scene receiptScene = new Scene(vbox);
			Stage receiptStage = new Stage();
			receiptStage.initModality(Modality.APPLICATION_MODAL); // Block events to other windows
			receiptStage.setTitle("Receipt Window");
			receiptStage.setScene(receiptScene);
			receiptStage.show(); // display
			notification.setText("");
	    } 
	    catch (IOException e) {
	    	e.printStackTrace();
	    }
	}
	
	@SuppressWarnings("unused")
	private boolean validation() {
		boolean condition = cardNumValidation() && cardDateValidation() && secretNumValidation() && idValidation() && sumValidation(); 
		return condition;
	}
	
	private boolean cardNumValidation() {
		if (cardNum.getText().length() != CARD_NUM_LEN) return false;
		return isStringAnInteger(cardNum.getText());
	}
	
	private boolean cardDateValidation() {
		if (cardDate.getText().length() != 5) return false;
		if (cardDate.getText().charAt(2) != '/') return false;
		return isStringAnInteger(cardDate.getText().substring(0, 1)) && isStringAnInteger(cardDate.getText().substring(3, 4)); 
	}
	
	private boolean secretNumValidation() {
		if (secretNum.getText().length() != SECRET_NUM_LEN) return false;
		return isStringAnInteger(secretNum.getText());
	}
	
	private boolean idValidation() {
		if (paymentID.getText().length() != ID_LEN) return false;
		return isStringAnInteger(paymentID.getText());
	}
	
	private boolean sumValidation() {
		return isStringAnInteger(sum.getText());
	}
	
	private boolean isStringAnInteger(String str) {
		for(int i = 0; i < str.length(); i++) {
			if (str.charAt(i) < '0' || str.charAt(i) > '9') {
				return false;
			}
		}
		return true;
	}
	
	@FXML
	void pressReturn(ActionEvent event) {
		openNextScene("/WorkerLoginPage/WorkerLoginPage.fxml",event); // Open scene
	}
	    
	private void openNextScene(String location, ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(location));
			Scene scene = new Scene(loader.load());
			Stage newStage = new Stage();
			if(location.equals("/TravelerVisitReservationScreen/TravelerVisitReservationScreen.fxml")) {
				TravelerVisitReservationScreenControler controller = loader.getController();
		        controller.setStage(stage);   
		        controller.setReservationType("Offline");
		        controller.setLocationToReturn("/WorkerDashboard/WorkerDashboard.fxml");
			}
			newStage.setScene(scene);
			newStage.show();
			((Node)event.getSource()).getScene().getWindow().hide();
	    } 
	    catch (IOException e) {
	    	e.printStackTrace();
	    }
	}
	
	@FXML
    void pressCreateReservation(ActionEvent event) {
		createReservationEvent = event;
		notification.setText("");
		String travelerID = travelerIDTextField.getText();
		if(travelerID.length() != 9) {
			notification.setText("ID should contain 9 digits");
		}
		else {
			ConnectionScreenController.client.handleMessageFromClientUI(new TransferrableData(MessageType.WorkerDashboardMakeAReservation, travelerID));
		}
//		ConnectionScreenController.client.setTraveler(new Traveler(travelerID));
    }
	
	public void startCreatingReservation(TransferrableData transferrableData) {
		ConnectionScreenController.client.setTraveler((Traveler) transferrableData.getMessage());
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				openNextScene("/TravelerVisitReservationScreen/TravelerVisitReservationScreen.fxml", createReservationEvent);
			}
		});
	}
	
    @FXML
    void pressEnterPark(ActionEvent event) {
    	enterParkLabel.setText("");
    	String reservationIDString = reservationIDforEntering.getText();
    	try {
    		long reservationID = Long.parseLong(reservationIDString);
    		client.handleMessageFromClientUI(new TransferrableData(MessageType.WorkerDashboardEnterPark, reservationID));
    	} catch(NumberFormatException e) {
    		enterParkLabel.setTextFill(Color.RED);
    		enterParkLabel.setText("ID should include only numbers.");
    		return;
    	}
    }

    @FXML
    void pressExitPark(ActionEvent event) {
    	exitParkLabel.setText("");
    	String reservationIDString = reservationIDForExiting.getText();
    	try {
    		long reservationID = Long.parseLong(reservationIDString);
    		client.handleMessageFromClientUI(new TransferrableData(MessageType.WorkerDashboardExitPark, reservationID));
    	} catch(NumberFormatException e) {
    		exitParkLabel.setTextFill(Color.RED);
    		exitParkLabel.setText("ID should include only numbers.");
    		return;
    	}
    }

	public void enteredPark(TransferrableData transferrableData) {
		boolean entered = (Boolean) transferrableData.getMessage();
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				if(entered) {
					enterParkLabel.setTextFill(Color.GREEN);
					enterParkLabel.setText("Entered Park");
				}
				else {
					enterParkLabel.setTextFill(Color.RED);
					enterParkLabel.setText("Can't enter park or reservation doesn't exist.");
				}
			}
		});
	}

	public void exitedPark(TransferrableData transferrableData) {
		boolean exited = (Boolean) transferrableData.getMessage();
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				if(exited) {
					exitParkLabel.setTextFill(Color.GREEN);
					exitParkLabel.setText("Exited Park");
				}
				else {
					exitParkLabel.setTextFill(Color.RED);
					exitParkLabel.setText("Can't exit because didn't enter park.");
				}
			}
		});
	}
    
    

}