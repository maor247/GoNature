package client;
import java.io.IOException;
import java.util.List;

import ParkManagerDashboard.ParkManagerDashboardControler;
import ServiceDashboard.ServiceDashboardControler;
import ApprovalScreen.ApprovalScreenController;
import DevisionMangerDashboard.DevisionMangerDashboardControler;
import TravelerDashboard.TravelerDashboardControler;
import TravelerGUi.TravelerGuiControler;
import TravelerVisitReservationScreen.AlternativeDateController;
import TravelerVisitReservationScreen.GuideLoginScreenController;
import TravelerVisitReservationScreen.SummaryPageController;
import TravelerVisitReservationScreen.TravelerVisitReservationScreenControler;
import WorkerDashboard.WorkerDashboardController;
import WorkerLoginPage.WorkerLoginPageControler;
import common.*;
import connectionscreen.ConnectionScreenController;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import notificationScreen.NotificationController;
import notificationScreen.NotificationLauncher;
import ocsf.client.*;

public class GoNatureClient extends AbstractClient{
	public TransferrableData dataToSendInTheStart;
	private ConnectionScreenController cr;
	private TravelerGuiControler travelerGuiControler;
	private Traveler traveler;
	private Worker worker;
	private WorkerLoginPageControler workerLoginPageControler;
	@SuppressWarnings("unused")
	private AlternativeDateController alternativeDateController;
	private ApprovalScreenController approvalScreenController;
	private DevisionMangerDashboardControler divisionManagerDashboardControler;
	private ParkManagerDashboardControler parkManagerDashboardController;
	@SuppressWarnings("unused")
	private NotificationController notificationController;
	private NotificationLauncher notificationLauncher = new NotificationLauncher();
	private ServiceDashboardControler serviceDashboardController;
	private TravelerDashboardControler travelerDashboardControler;
	private TravelerVisitReservationScreenControler travelerVisitReservationController;
	private GuideLoginScreenController guideLoginScreenController;
	private SummaryPageController summaryPageController;
	private WorkerDashboardController workerDashboardController;
//	private WorkerLoginController workerLoginController;
	private int PORT;


	
	public void setWorkerLoginPageControler(WorkerLoginPageControler workerLoginPageControler) {
		this.workerLoginPageControler = workerLoginPageControler;
	}
	public GoNatureClient(String host, int port) throws IOException{
		super(host, port); //Call the superclass constructor
		this.PORT = port;
	}
	public String get_port() {
		return String.valueOf(PORT);
	}
	public Traveler get_traveler() {
		return this.traveler;
	}
	/**
	 * @param traveler the traveler to set
	 */
	public void setTraveler(Traveler traveler) {
		this.traveler = traveler;
	}
	public Worker get_worker() {
		return this.worker;
	}
	
	@Override
	protected synchronized void handleMessageFromServer(Object msg) {
		if(!(msg instanceof TransferrableData))
			return;
		TransferrableData transferrableData = (TransferrableData) msg;
		MessageType operation = transferrableData.getMessageType();
		switch(operation) {
			case ApprovalScreenChooseReservationOptions: 
				approvalScreenController.updateParkAndDateDetails(transferrableData);
				break;
//			case ApprovalScreenUpdateStatusOfReservation:
//				ApprovalScreenController.showMessage(transferrableData);
//				break;
//			case ApprovalScreenShowReservationDetailsAfterChoosingReservation:
//				ApprovalScreenController.showDetailsOfReservation(transferrableData);
//				break;
			case ApprovalScreenApproveVisit:
				approvalScreenController.showApprovalVisitMessage(transferrableData);
				break;
			case ApprovalScreenCancelVisit:
				approvalScreenController.showCanceledVisitMessage(transferrableData);
				break;
			case DivisionManagerChangeStatusOfRequest:
				//DivisionManagerDashboardController.updateRequestStatus(transferrableData);
				break;
			case DivisionManagerChoosePark: 
				divisionManagerDashboardControler.showParks(transferrableData);
				break;
			case DivisionManagerDashboardShowDetails:
				divisionManagerDashboardControler.updateTable(transferrableData);
				break;
			case DivisionManagerDashboardAcceptRequest:
				String msg2 = (String)transferrableData.getMessage();
				divisionManagerDashboardControler.approve_deny_message(msg2);
				break;
			case DivisionManagerDashboardDenyRequest:
				divisionManagerDashboardControler.approve_deny_message("Request denied successfuly");
				break;
			case DivisionManagerDashboardCreateReport:
				if((boolean)transferrableData.getMessage())
				divisionManagerDashboardControler.updateComboBoxReport();
				break;
			case DivisionManagerViewReport: {
				Object data[] = (Object[]) transferrableData.getMessage();
				divisionManagerDashboardControler.getAllReservations(data[2]);
				divisionManagerDashboardControler.getAllCanceledReservations(data[0], data[1]);
				break;
			}
			case GetAllReportsDivisionManager:
				if(transferrableData.getMessage() != null)
					divisionManagerDashboardControler.UpdateReportDentails((List<Report>)transferrableData.getMessage());
				break;
			case NotificationScreenShowMessage:{
				notificationLauncher.launchAvailableNotifications(transferrableData);
				break;
			}
			case ParkManagerDahsboardPreviewReport: 
				//ParkManagerDashboardController.openReportView(transferrableData);
				break;
			case ParkManagerDashboardCreateReport:
				parkManagerDashboardController.setReportStatusMassege((boolean)transferrableData.getMessage());
				//ParkManagerDashboardController.showMessageReportCreated(transferrableData);
				break;
			case GetAllReservationsParkManagerDashboard:{
				parkManagerDashboardController.getAllReservations(transferrableData.getMessage());
				break;
			}
			case ParkManagerDashboardGetAllReportsForPark:{
				if(transferrableData.getMessage() != null)
				parkManagerDashboardController.UpdrateReporstDetails((List<Report>)transferrableData.getMessage());
				break;
			}
			case ParkManagerDashboardRequestQuotaInParkChange: 
				//ParkManagerDashboardController.showMessageRequestSent(transferrableData);
				break;
			case ParkManagerDashboardRequestVisitorChange:
				Object data2[] = (Object[]) transferrableData.getMessage();
				String requestType = (String) data2[0];
				boolean status = (boolean) data2[1];
				parkManagerDashboardController.choose_answer(requestType,status);
				break;
			case ParkManagerDashboardRequestVisitTimeInMinutesChange: 
				//ParkManagerDashboardController.showMessageRequestSent(transferrableData);
				break;
			case ParkManagerDashboardShowDetails: 
				parkManagerDashboardController.showParks(transferrableData);
				break;
			case ParkManagerDashboardShowDetailsAfterChoosingPark: 
				//ParkManagerDashboardController.showCurrentParkDetails(transferrableData);
				break;
			case ServiceDashboardRegisterAGuide: 
				serviceDashboardController.showMessageGuideIsRegistered(transferrableData);
				break;
			case ServiceDashboardShowDetails: 
				//ServiceDashboardController.showUserName(transferrableData);
				break;
			case TravelerDashboardCancelReservation: 
				travelerDashboardControler.cancelReservation(transferrableData);
				break;
			case TravelerDashboardChangeEmail: 
				travelerDashboardControler.updateEmailField(transferrableData);
				break;
			case TravelerDashboardExitWaitingList: 
				travelerDashboardControler.cancelWaitingListReservation(transferrableData);
				break;
			case TravelerDashboardReservations:
				travelerDashboardControler.updateReservationsTable(transferrableData);
				break;
			case TravelerDashboardShowUserDetails:
				travelerDashboardControler.showTravelerDetails(transferrableData);
				break;
			case TravelerDashboardWaitingList: 
				travelerDashboardControler.updateWaitingListTable(transferrableData);
				break;
			case TravlerIsConnected:{
				travelerGuiControler.Error_traveler_is_loggedIn();
				break;
			}
			case TravlerAddNew:
				traveler = (Traveler) transferrableData.getMessage();
				if(traveler == null) {
					travelerGuiControler.new_traveler_reservetion(false);
				}
				else {
					travelerGuiControler.new_traveler_reservetion(true);
				}
				break;
			case TravelerVisitReservationCreateReservation: 
				travelerVisitReservationController.showMessageOfCreatingReservation(transferrableData);
				break;
			case TravelerVisitReservationJoinWaitingList: 
				travelerVisitReservationController.showMessageJoinedWaitingList(transferrableData);
				break;
			case TravelerVisitReservationScreenUpdateDetails: 
				travelerVisitReservationController.setParks(transferrableData);
				break;
			case SummaryPageUpdateEmailANdPhoneNumber:
				summaryPageController.showUpdatedMessage(transferrableData);
				break;
			case GetAllReservations:
				travelerVisitReservationController.getAlternativeDateController().setOptionsForNewDate(transferrableData);
				break;
			case AlternativeDateScreenCreateVisit:
				travelerVisitReservationController.getAlternativeDateController().showMessageForVisit(transferrableData);
				break;
			case TravlerDashboardChangePhoneNumber: 
				travelerDashboardControler.updatePhoneNumber(transferrableData);
				break;
			case TravlerGUILogin:
				traveler = (Traveler) transferrableData.getMessage();
				if(traveler == null) {
					travelerGuiControler.last_button_selection(false);
				}
				else {
					travelerGuiControler.last_button_selection(true);
				}
				break;
			case WorkerDashboardMakeAReservation: 
				workerDashboardController.startCreatingReservation(transferrableData);
				break;
			case WorkerDashboardPaymentOnReservation: 
				workerDashboardController.setSum(transferrableData);
				break;
			case TravlerLogOut:
				break;
			case WorkerDashboardShowDetails: 
				workerDashboardController.setChoosePark(transferrableData);
				break;
			case WorkerDashboardEnterPark:
				workerDashboardController.enteredPark(transferrableData);
				break;
			case WorkerDashboardExitPark:
				workerDashboardController.exitedPark(transferrableData);
				break;
			case WorkerDashboardShowDetailsAfterChoosingPark: 
				//workerDashboardController.showDetailsOfAPark(transferrableData);
				break;
			case WorkerLogin: 
				worker = (Worker) transferrableData.getMessage();
				if(worker == null) {
					workerLoginPageControler.wrong_worker_login();
				}
				else {
					workerLoginPageControler.success_worker_login();
				}
				break;
			case GuideLoginScreenLogin:
				guideLoginScreenController.loginGuide(transferrableData);
				break;
			case NewClientConnected:
				boolean usersAreImported = (Boolean) transferrableData.getMessage();
				if(!usersAreImported) {
					showAlertUsersAreNotImported();
				}
				cr.set_connectionstatus(usersAreImported);
				break;
			case TravlerLogOutALL:
				System.exit(1);
				break;
			default: break;
		}
	}
	
	
	private void showAlertUsersAreNotImported() {
		Platform.runLater(() -> {
			Alert alert = new Alert(AlertType.WARNING);
	        alert.setTitle("Warning Dialog");
	        alert.setHeaderText(null);
	        alert.setContentText("Users are not imported yet to the system, thus it"
	        		+ " is currently unavailable");

	        ButtonType continueButton = new ButtonType("Ok");
	        
	        alert.getButtonTypes().setAll(continueButton);
	        alert.showAndWait();
		});
	}
	
	public void setCr(ConnectionScreenController cr) {
		this.cr = cr;
	}
	

	public void setTravelerGuiControler(TravelerGuiControler travelerGuiControler) {
		this.travelerGuiControler = travelerGuiControler;
	}

	public synchronized void handleMessageFromClientUI(TransferrableData transferrableData) {
		try {
			openConnection();
			sendToServer(transferrableData);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void quit() {
	    try {
	      closeConnection();
	    } catch(IOException e) {
	    	e.printStackTrace();
	    }
	    System.exit(0);
	  }

	/**
	 * @param dataToSendInTheStart the dataToSendInTheStart to set
	 */
	public void setDataToSendInTheStart(TransferrableData dataToSendInTheStart) {
		this.dataToSendInTheStart = dataToSendInTheStart;
	}
	
	/**
	 * @param approvalScreenController the approvalScreenController to set
	 */
	public void setApprovalScreenController(ApprovalScreenController approvalScreenController) {
		this.approvalScreenController = approvalScreenController;
	}
//
	/**
	 * @param divisionManagerDashboardController the divisionManagerDashboardController to set
	 */
	public void setDivisionManagerDashboardController(
			DevisionMangerDashboardControler divisionManagerDashboardController) {
		this.divisionManagerDashboardControler = divisionManagerDashboardController;
	}
//
	/**
	 * @param notificationController the notificationController to set
	 */
	public void setNotificationController(NotificationController notificationController) {
		this.notificationController = notificationController;
	}
//
	/**
	 * @param parkManagerDashboardController the parkManagerDashboardController to set
	 */
	public void setParkManagerDashboardController(ParkManagerDashboardControler parkManagerDashboardController) {
		this.parkManagerDashboardController = parkManagerDashboardController;
	}

	/**
	 * @param serviceDashboardController the serviceDashboardController to set
	 */
	public void setServiceDashboardController(ServiceDashboardControler serviceDashboardController) {
		this.serviceDashboardController = serviceDashboardController;
	}
//
	/**
	 * @param travelerDashboardController the travelerDashboardController to set
	 */
	public void setTravelerDashboardController(TravelerDashboardControler travelerDashboardControler) {
		this.travelerDashboardControler = travelerDashboardControler;
	}
//
	/**
	 * @param travelerVisitReservationController the travelerVisitReservationController to set
	 */
	public void setTravelerVisitReservationController(
			TravelerVisitReservationScreenControler travelerVisitReservationController) {
		this.travelerVisitReservationController = travelerVisitReservationController;
	}
//
	/**
	 * @param workerDashboardController the workerDashboardController to set
	 */
	public void setWorkerDashboardController(WorkerDashboardController workerDashboardController) {
		this.workerDashboardController = workerDashboardController;
	}
//
//	/**
//	 * @param workerLoginHandler the workerLoginHandler to set
//	 */
//	public void setWorkerLoginController(WorkerLoginController workerLoginController) {
//		this.workerLoginController = workerLoginController;
//	}
	/**
	 * @param guideLoginScreenController the guideLoginScreenController to set
	 */
	public void setGuideLoginScreenController(GuideLoginScreenController guideLoginScreenController) {
		this.guideLoginScreenController = guideLoginScreenController;
	}
	/**
	 * @param alternativeDateController the alternativeDateController to set
	 */
	public void setAlternativeDateController(AlternativeDateController alternativeDateController) {
		this.alternativeDateController = alternativeDateController;
	}
	public void setSummaryPageController(SummaryPageController summaryPageController) {
		this.summaryPageController = summaryPageController;
	}
}
