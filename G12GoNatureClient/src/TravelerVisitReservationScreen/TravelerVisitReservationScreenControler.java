package TravelerVisitReservationScreen;

import common.*;
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


import connectionscreen.ConnectionScreenController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import notificationScreen.NotificationLauncher;

public class TravelerVisitReservationScreenControler implements Initializable{
	
	private List<Park> parks;
	private boolean guideScreenActive = false;
	private boolean guideLoggedIn;
	private VisitReservation currentVisitReservation;
	private String reservationType;
	private NotificationLauncher notificationLauncher = new NotificationLauncher();
	private Thread makeNotification;
	private String locationToReturn;
	private AlternativeDateController alternativeDateController;
	
	@SuppressWarnings("unused")
	private Stage stage; // Reference to the stage of the current scene
	
	private final String privateGroupType = "Private";
	private final String familyGroupType = "Family";
	private final String organizedGroupType = "Organized";
	
	
  	@FXML
    private Button BackButton;

    @FXML
    private Button JoinWaitingListButton;

    @FXML
    private ChoiceBox<Integer> amountChoiceBox;

    @FXML
    private Button chooseAnotherDate;

    @FXML
    private Button createReservationButton;

    @FXML
    private Text createReservationMessageLabel;
    
    @FXML
    private Label dateMessage;

    @FXML
    private DatePicker datePicker;

    @FXML
    private ChoiceBox<String> groupTypeChoiceBox;

    @FXML
    private Label groupTypeMessage;

    @FXML
    private ChoiceBox<String> parkChoiceBox;

    @FXML
    private Label parkNameMessage;

    @FXML
    private Label timeMessage;
    
    @FXML
    private ChoiceBox<String> timeChoiceBox;

    @FXML
    private Label userIDLabel;

    @FXML
    private Label visitorsAmountMessage;
	

    public void setLocationToReturn(String locationToReturn) {
		this.locationToReturn = locationToReturn;
	}

	public void setReservationType(String reservationType) {
		this.reservationType = reservationType;
		
		if(reservationType.toLowerCase().equals("offline")) {
			datePicker.setValue(LocalDate.now());
			datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
				if(newValue != null) {
					datePicker.setValue(LocalDate.now());
				}
			});
		}
	}

	@FXML
    void pressChooseAnotherDate(ActionEvent event) {
    	openNextScene("/TravelerVisitReservationScreen/AlternativeDate.fxml", event, false);
    }

	@FXML
    void pressCreateReservation(ActionEvent event) {
		setEmptyLabels();
    	VisitReservation visitReservation = getReservation();
    	if(visitReservation == null)
    		return;
		ConnectionScreenController.client.handleMessageFromClientUI(new TransferrableData(MessageType.TravelerVisitReservationCreateReservation, visitReservation));	
	}
    
	private Park findParkByName(String parkName) {
		for(Park park : parks)
			if(park.getParkname().equals(parkName))
				return park;
		return null;
	}
	
    private void setEmptyLabels() {
    	parkNameMessage.setText("");
    	createReservationMessageLabel.setText("");
    	groupTypeMessage.setText("");
    	dateMessage.setText("");
    	timeMessage.setText("");
    	visitorsAmountMessage.setText("");
    }

	@FXML
	void pressJoinWaitingList(ActionEvent event) {
		ConnectionScreenController.client.handleMessageFromClientUI(new TransferrableData(MessageType.TravelerVisitReservationJoinWaitingList, getReservation()));
	}

	public VisitReservation getReservation() {
		String parkName = parkChoiceBox.getValue();
    	String groupType = groupTypeChoiceBox.getValue();
    	LocalDate date = datePicker.getValue();
		if(parkName == null) {
			parkNameMessage.setText("Please choose a park.");
			return null;
		}
		if(date == null) {
			dateMessage.setText("Please choose a date.");
			return null;
		}
		if(timeChoiceBox.getValue() == null || timeChoiceBox.getValue().isEmpty()) {
			timeMessage.setText("Please choose time.");
			return null;
		}
		String[] time = timeChoiceBox.getValue().split(":");
		int hours = Integer.parseInt(time[0]);
		LocalDateTime selectedDateTime = date.atTime(hours, 0);
		Park chosenPark = findParkByName(parkName);
		if((selectedDateTime.isBefore(LocalDateTime.now()) && reservationType.toLowerCase().equals("online")) 
				|| (reservationType.toLowerCase().equals("offline") && !(LocalDateTime.now().isAfter(selectedDateTime) && LocalDateTime.now().isBefore(selectedDateTime.plusHours(chosenPark.getVisitorTimeInMinut()))))) {
			timeMessage.setText("We are sorry but this time has passed. Choose another time please.");
			return null;
		}
		VisitReservation visitReservation = new VisitReservation(-1, ConnectionScreenController.client.get_traveler(), chosenPark, Timestamp.valueOf(selectedDateTime), amountChoiceBox.getValue(), -1, -1, "Active", reservationType, groupType);
		visitReservation.setMaxDurationInHours(chosenPark.getVisitorTimeInMinut());
		visitReservation.setApprovedbythetraveler(reservationType.toLowerCase().equals("offline"));
		visitReservation.setExitTime(Timestamp.valueOf(selectedDateTime.plusHours(chosenPark.getVisitorTimeInMinut())));
		return visitReservation;
	}
	
	// Setter method to set the stage reference
    public void setStage(Stage stage) {
	  this.stage = stage;
    }
    
    @FXML
    void pressReturn(ActionEvent event) {
    	ConnectionScreenController.client.handleMessageFromClientUI(new TransferrableData(MessageType.TravlerLogOut, ConnectionScreenController.client.get_traveler()));
    	openNextScene(locationToReturn, event, true); // Open scene
    }
    
    private void openNextScene(String location, ActionEvent event, boolean toHide) { //return
	      try {
	          FXMLLoader loader = new FXMLLoader(getClass().getResource(location));
	          Parent root = loader.load();
	          Scene scene = new Scene(root);
	          Stage newStage = new Stage();
	          if(toHide)
	        	  ((Node)event.getSource()).getScene().getWindow().hide();
	          if(location.equals("/TravelerVisitReservationScreen/GuideLoginScreen.fxml")) {
	        	  newStage.initStyle(StageStyle.UNDECORATED);
	        	  GuideLoginScreenController guideLoginScreenController = loader.getController();
	        	  guideLoginScreenController.setTravelerVisitReservationScreenControler(this);
	        	  guideLoginScreenController.setStage(newStage);
	          }
	          else if(location.equals("/TravelerVisitReservationScreen/AlternativeDate.fxml")) {
	        	  alternativeDateController = loader.getController();
	        	  alternativeDateController.setTravelerVisitReservationScreen(this);
	        	  alternativeDateController.setVisitReservation(getReservation());
	          }
	          else if(location.equals("/TravelerVisitReservationScreen/SummaryPage.fxml")) {
	        	  newStage.initStyle(StageStyle.UNDECORATED);
	        	  SummaryPageController summaryPageController = loader.getController();
	        	  summaryPageController.setVisitReservation(currentVisitReservation);
	        	  summaryPageController.setTravelerVisitReservationScreenControler(this);
	          }
	          newStage.setScene(scene);
	          newStage.show();
	      } 
	      catch (IOException e) {
	          e.printStackTrace();
	          // Handle the exception if unable to load or show the next scene
	      }
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ConnectionScreenController.client.setTravelerVisitReservationController(this);
		userIDLabel.setAlignment(Pos.CENTER);
		userIDLabel.setText(ConnectionScreenController.client.get_traveler().getId_number());
		ObservableList<String> groupTypes = FXCollections.observableArrayList();
		groupTypeChoiceBox.setItems(groupTypes);
		groupTypes.add(privateGroupType);
		groupTypes.add(familyGroupType);
		if(ConnectionScreenController.client.get_traveler().isIsguide())
			groupTypes.add(organizedGroupType);
		groupTypeChoiceBox.getSelectionModel().clearAndSelect(0);
		setAmountOfPeopleValues(privateGroupType);
		groupTypeChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			groupTypeMessage.setText("");
			if(newValue != null) {
				if(newValue.equals(organizedGroupType) && !guideLoggedIn) {
					continueVisitReservationProcess(true);
					if(!guideScreenActive) {
						guideScreenActive = true;
						groupTypeChoiceBox.setDisable(true);
						openNextScene("/TravelerVisitReservationScreen/GuideLoginScreen.fxml", null, false);
					}
				}
				setAmountOfPeopleValues(newValue);
			}
		});
		datePicker.setDayCellFactory(picker -> new DateCell() {
			@Override
	        public void updateItem(LocalDate date, boolean empty) {
	            super.updateItem(date, empty);
	            LocalDate today = LocalDate.now();
	            setDisable(date.isBefore(today));
	        }
		});
		datePicker.setStyle("-fx-control-inner-background: #FFC371;");
		ConnectionScreenController.client.handleMessageFromClientUI(new TransferrableData(MessageType.TravelerVisitReservationScreenUpdateDetails, null));
	}

	@SuppressWarnings("unchecked")
	public void setParks(TransferrableData transferrableData) {
		parks = (List<Park>) transferrableData.getMessage();
		ObservableList<String> parkNames = FXCollections.observableArrayList();
		parkChoiceBox.setItems(parkNames);
		for(Park park : parks) {
			parkNames.add(park.getParkname());
		}
		parkChoiceBox.getSelectionModel().clearSelection();
		parkChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if(newValue != null) {
				parkNameMessage.setText("");
				for(Park park : parks) {
					if(newValue.equals(park.getParkname())) {
						try {
							setTimeSpinnerValues(park.getVisitorTimeInMinut());
						} catch (Exception e) {
							e.printStackTrace();
						}
						createReservationMessageLabel.setText("");
						groupTypeChoiceBox.setDisable(false);
						continueVisitReservationProcess(false);
					}
				}
			}
		});
	}
	
	private void setTimeSpinnerValues(int maxDurationInHoursInPark) throws Exception{
		if(maxDurationInHoursInPark > 9)
			throw new Exception("Max duration exceeds the maximum (9 hours)");
		ObservableList<String> timeOptions = FXCollections.observableArrayList();
		for(int i = 8; i <= 17 - maxDurationInHoursInPark; i += maxDurationInHoursInPark) {
			timeOptions.add(String.format("%d:00", i));
		}
		timeChoiceBox.setItems(timeOptions);
		timeChoiceBox.getSelectionModel().clearSelection();
	}
	
	public void continueVisitReservationProcess(boolean disability) {
		groupTypeChoiceBox.setDisable(disability);
		datePicker.setDisable(disability);
		timeChoiceBox.setDisable(disability);
		amountChoiceBox.setDisable(disability);
	}

	public void setDefaultChoiceOfGroupType() {
		groupTypeChoiceBox.getSelectionModel().clearAndSelect(0);
		groupTypeChoiceBox.setDisable(false);
		guideScreenActive = false;
		continueVisitReservationProcess(false);
	}
	
	public void setDefaultValues() {
		parkChoiceBox.getSelectionModel().clearSelection();
		groupTypeChoiceBox.getSelectionModel().clearAndSelect(0);
		if(reservationType.toLowerCase().equals("offline"))
			datePicker.setValue(LocalDate.now());
		else
			datePicker.setValue(null);
		timeChoiceBox.getSelectionModel().clearSelection();
		amountChoiceBox.getSelectionModel().clearAndSelect(0);
	}
	
	private void setAmountOfPeopleValues(String newValue) {
		ObservableList<Integer> visitorsAmountValues = FXCollections.observableArrayList();
		amountChoiceBox.setItems(visitorsAmountValues);
		switch(newValue) {
		case privateGroupType:
			visitorsAmountValues.add(1);
			break;
		case familyGroupType:
			for(int i = 2; i <= 10; i++)
				visitorsAmountValues.add(i);
			break;
		case organizedGroupType:
			for(int i = 2; i <= 15; i++)
				visitorsAmountValues.add(i);
			break;
		default: break;
		}
		amountChoiceBox.getSelectionModel().clearAndSelect(0);
	}

	public void setGuideIsLoggedIn(boolean guideLoggedIn) {
		this.guideLoggedIn = guideLoggedIn;
	}

	public void showMessageOfCreatingReservation(TransferrableData transferrableData) {
		currentVisitReservation = (VisitReservation) transferrableData.getMessage();
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				if(currentVisitReservation != null) {
					createReservationMessageLabel.setLayoutX(303);
					createReservationMessageLabel.setLayoutY(401);
					createReservationMessageLabel.setFill(Color.GREEN);
					createReservationMessageLabel.setText("Reservation is created.");
					sendSMSForCreatingVisit();
					openNextScene("/TravelerVisitReservationScreen/SummaryPage.fxml", null, false);
				}
				else {
					createReservationMessageLabel.setFill(Color.RED);
					createReservationMessageLabel.setLayoutX(446);
					createReservationMessageLabel.setLayoutY(216);
					createReservationMessageLabel.setText("There is no available space."
							+ "\nPlease choose another date via the button in the bottom left corner"
							+ "\nor join the waiting list via the button in the bottom right corner.");
					disableReservationFields(true);
				}
			}

		});
	}
	
	private void sendSMSForCreatingVisit() {
		class VisitCreatedNotification implements Runnable{
			private Thread thread;
			@Override
			public void run() {
				List<String> message = new ArrayList<>();
				message.add(ConnectionScreenController.client.get_traveler().getPhone_number());
				message.add("Dear traveler,\nReservation number " + currentVisitReservation.getReservationID()
						+ " is created.\nYour visit will be in the park " + currentVisitReservation.getPark().getParkname()
						+ "\nand will happen in " + currentVisitReservation.getTimeofvisit()
						+ ".\nPlease note that one day before the visit, a reminder will be sent to"
						+ " you for approving this visit.\nWith regards, Go Nature team.");
				List<List<String>> allMessages = new ArrayList<>();
				allMessages.add(message);
				notificationLauncher.launchAvailableNotifications(new TransferrableData(null, allMessages));
				thread.interrupt();
			}
			private void setThread(Thread thread) {
				this.thread = thread;
			}
		}
		
		VisitCreatedNotification visitCreatedNotification = new VisitCreatedNotification();
		makeNotification = new Thread(visitCreatedNotification);
		visitCreatedNotification.setThread(makeNotification);
		if(ConnectionScreenController.client.get_traveler().getPhone_number() != null &&
				!ConnectionScreenController.client.get_traveler().getPhone_number().isEmpty())
			makeNotification.start();
	}
	
	/**
	 * @return the makeNotification
	 */
	public Thread getMakeNotification() {
		return makeNotification;
	}

	void disableReservationFields(boolean disability) {
		parkChoiceBox.setDisable(disability);
		continueVisitReservationProcess(disability);
		createReservationButton.setDisable(disability);
		JoinWaitingListButton.setDisable(!disability);
		chooseAnotherDate.setDisable(!disability);
	}

	public void showMessageJoinedWaitingList(TransferrableData transferrableData) {
		boolean addedToWaitingList = (Boolean) transferrableData.getMessage();
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				createReservationMessageLabel.setLayoutX(303);
				createReservationMessageLabel.setLayoutY(401);
				if(addedToWaitingList) {
					createReservationMessageLabel.setFill(Color.GREEN);
					createReservationMessageLabel.setText("The reservation has been to added to the waiting list");
				}
				else {
					createReservationMessageLabel.setFill(Color.RED);
					createReservationMessageLabel.setText("The reservation couldn't been added to the waiting list");
				}
				disableReservationFields(false);
				continueVisitReservationProcess(true);
				setDefaultValues();
			}
			
		});
	}

	/**
	 * @return the currentVisitReservation
	 */
	public VisitReservation getCurrentVisitReservation() {
		return currentVisitReservation;
	}

	public AlternativeDateController getAlternativeDateController() {
		return alternativeDateController;
	}
}
