package server;
import ocsf.server.*;
import java.io.IOException;
import java.util.List;

import ServerGUI.Server_Controller;
import ServerGUI.Server_GUI;
import common.*;
import javafx.application.Platform;
import logic.*;

public class GoNatureServer extends AbstractServer{
	final public static int DEFAULT_PORT = 5555;
	
	private Server_Controller serverController;
	private divisionManagerDashboardHandler divisionManagerDashboardHandler = new divisionManagerDashboardHandler();
	private parkManagerDashboardHandler parkManagerDashboardHandler = new parkManagerDashboardHandler();
	private ApprovalHandler approvalHandler = new ApprovalHandler();
	private NotificationHandler notificationHandler = new NotificationHandler();
	private ServiceDashboardHandler serviceDashboardHandler = new ServiceDashboardHandler();
	private TravelerDashboardHandler travelerDashboardHandler = new TravelerDashboardHandler();
	private TravelerVisitReservationHandler travelerVisitReservationHandler = new TravelerVisitReservationHandler();
//	private TravelerGUILoginHandler travelerGUILoginHandler;
	private WorkerDashboardHandler workerDashboardHandler = new WorkerDashboardHandler();
//	private WorkerLoginHandler workerLoginHandler;
	private AlternativeDateHandler alternativeDateHandler = new AlternativeDateHandler();
	private GuideLoginHandler guideLoginHandler = new GuideLoginHandler();
	
	private Thread notificationsThread;

	public GoNatureServer(int port) {
		super(port);
		Server_GUI.serverInstance = this;
	}

	@Override
	protected synchronized void handleMessageFromClient(Object msg, ConnectionToClient client) {
		if(!(msg instanceof TransferrableData))
			return;
		TransferrableData transferrableData = (TransferrableData) msg, dataToReturn = null;
		MessageType operation = transferrableData.getMessageType();
		serverController.updateLog("Recive: " + transferrableData.getMessageType().toString());
		switch(operation) {
			case ApprovalScreenChooseReservationOptions: 
				dataToReturn = approvalHandler.ChooseReservationHndler(transferrableData);
				break;
//			case ApprovalScreenUpdateStatusOfReservation: 
//				dataToReturn = approvalHandler.updateStatusOfReservation(transferrableData);
//				break;
//			case ApprovalScreenShowReservationDetailsAfterChoosingReservation:
//				dataToReturn = approvalHandler.showDetailsOfReservation(transferrableData);
//				break;
			case ApprovalScreenApproveVisit:
				dataToReturn = approvalHandler.AprovePressedHandller(transferrableData);
				break;
			case ApprovalScreenCancelVisit:
				dataToReturn = approvalHandler.DeclinePressedHandller(transferrableData);
				break;
//			case DivisionManagerChangeStatusOfRequest: 
//				dataToReturn = divisionManagerDashboardHandler.updateStatusOfRequest(transferrableData);
//				break;
			case DivisionManagerChoosePark:
				dataToReturn = divisionManagerDashboardHandler.showUserDetails(transferrableData);
				break;
			case GetAllReportsDivisionManager:
				dataToReturn = divisionManagerDashboardHandler.getAllReportsFromAll(transferrableData);
				break;
			case DivisionManagerDashboardShowDetails:
				dataToReturn = divisionManagerDashboardHandler.showRequestsUpdateTableRequest(transferrableData);
				break;
			case DivisionManagerDashboardAcceptRequest:
				dataToReturn = divisionManagerDashboardHandler.AcceptRequest(transferrableData);
				break;
			case DivisionManagerDashboardDenyRequest:
				dataToReturn = divisionManagerDashboardHandler.DenyRequest(transferrableData);
				break;
			case GetAllReservationsParkManagerDashboard:
				dataToReturn = divisionManagerDashboardHandler.GetAllReservesionMD(transferrableData);
				break;
			case ParkManagerDashboardGetAllReportsForPark:
				dataToReturn = parkManagerDashboardHandler.GetAllReportsDetails(transferrableData);
				break;
//				,DivisionManagerDashboardAcceptRequest, DivisionManagerDashboardDenyRequest,
			case DivisionManagerViewReport:
				dataToReturn = divisionManagerDashboardHandler.getAllReports(transferrableData);
				break;
//			case NotificationScreenShowMessage:
//				break;
//			case ParkManagerDahsboardPreviewReport:
//				dataToReturn = parkManagerDashboardHandler.showPreviewOfAReport(transferrableData);
//				break;
			case DivisionManagerDashboardCreateReport:
			case ParkManagerDashboardCreateReport:
				dataToReturn = parkManagerDashboardHandler.createAReport(transferrableData);
				break;
//			case ParkManagerDashboardRequestQuotaInParkChange:
//				dataToReturn = parkManagerDashboardHandler.requestQuotaInParkChange(transferrableData);
//				break;
			case ParkManagerDashboardRequestVisitorChange:
				dataToReturn = parkManagerDashboardHandler.requestVisitorChange(transferrableData);
				break;
//			case ParkManagerDashboardRequestVisitTimeInMinutesChange:
//				dataToReturn = parkManagerDashboardHandler.requestVisitTimeInMinutesChange(transferrableData);
//				break;
				case ParkManagerDashboardShowDetails:
				dataToReturn = parkManagerDashboardHandler.showUserDetails(transferrableData);
				break;
//			case ParkManagerDashboardShowDetailsAfterChoosingPark:
//				dataToReturn = parkManagerDashboardHandler.showDetailsOfPark(transferrableData);
//				break;
			case ServiceDashboardRegisterAGuide:
				dataToReturn = serviceDashboardHandler.registerAGuide(transferrableData);
				break;
//			case ServiceDashboardShowDetails:
//				dataToReturn = serviceDashboardHandler.showDetails(transferrableData);
//				break;
			case TravelerDashboardCancelReservation:
				dataToReturn = travelerDashboardHandler.cancelAVisit(transferrableData);
				break;
			case TravelerDashboardChangeEmail:
				dataToReturn = travelerDashboardHandler.changeEmailAddress(transferrableData);
				break;
			case TravelerDashboardExitWaitingList:
				dataToReturn = travelerDashboardHandler.exitWaitingList(transferrableData);
				break;
			case TravelerDashboardReservations:
				dataToReturn = travelerDashboardHandler.showReservations(transferrableData);
				break;
			case TravelerDashboardShowUserDetails: 
				dataToReturn = travelerDashboardHandler.showUserDetails(transferrableData);
				break;
			case TravelerDashboardWaitingList: 
				dataToReturn = travelerDashboardHandler.showWaitingList(transferrableData);
				break;
			case TravlerAddNew:
				Traveler user1 = (Traveler)transferrableData.getMessage();
				boolean result = DBController.addTraveler(user1);
				if(result == true) {
					DBController.logInTraveler(user1);
					dataToReturn = new TransferrableData(transferrableData.getMessageType(), user1);
				}
				else {
					dataToReturn = new TransferrableData(transferrableData.getMessageType(), null);
				}
				break;
			case TravelerVisitReservationCreateReservation: 
				dataToReturn = travelerVisitReservationHandler.approveReservation(transferrableData);
				break;
			case TravelerVisitReservationJoinWaitingList: 
				dataToReturn = travelerVisitReservationHandler.joinWaitingList(transferrableData);
				break;
			case TravlerLogOut:
				Traveler user2 = (Traveler)transferrableData.getMessage();
				if(user2 != null) {
					DBController.logOutTraveler(user2);
				}
				dataToReturn = new TransferrableData(transferrableData.getMessageType(), null);
				break;
			case TravelerVisitReservationScreenUpdateDetails: 
				dataToReturn = travelerVisitReservationHandler.updateDetails(transferrableData);
				break;
			case SummaryPageUpdateEmailANdPhoneNumber:
				travelerDashboardHandler.changePhoneNumber(transferrableData).getMessage();
				travelerDashboardHandler.changeEmailAddress(transferrableData).getMessage();
				dataToReturn = new TransferrableData(MessageType.SummaryPageUpdateEmailANdPhoneNumber, true);
				break;
			case TravlerDashboardChangePhoneNumber: 
				dataToReturn = travelerDashboardHandler.changePhoneNumber(transferrableData);
				break;
			case GetAllReservations:
				dataToReturn = alternativeDateHandler.getAvailableTimes(transferrableData);
				break;
			case AlternativeDateScreenCreateVisit:
				dataToReturn = alternativeDateHandler.createVisit(transferrableData);
				break;
			case TravlerGUILogin: 
				String userid = (String)transferrableData.getMessage();
				Traveler user;
				user = DBController.getUserDetailsViaUserID(userid);
				List <VisitReservation> rs= DBController.getReservationsForTraveler(user);
				if(user == null || rs == null) {
					dataToReturn = new TransferrableData(transferrableData.getMessageType(), null);
				} else {
					try {
						if(DBController.isTravelerLoggedIn(user)) {
							dataToReturn = new TransferrableData(MessageType.TravlerIsConnected, null);
						}
						else {
							DBController.logInTraveler(user);
							dataToReturn = new TransferrableData(transferrableData.getMessageType(), user);
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				//getUserDetailsViaUserID
//				dataToReturn = travelerGUILoginHandler.loginTraveler(transferrableData);
				break;
			case WorkerDashboardMakeAReservation: 
				dataToReturn = workerDashboardHandler.getTravelerForReservation(transferrableData);
				break;
			case WorkerDashboardPaymentOnReservation: 
				dataToReturn = workerDashboardHandler.payOnReservation(transferrableData);
				break;
			case WorkerDashboardShowDetails: 
				dataToReturn = workerDashboardHandler.detailsOfParks(transferrableData);
				break;
			case WorkerDashboardEnterPark:
				dataToReturn = workerDashboardHandler.enterPark(transferrableData);
				break;
			case WorkerDashboardExitPark:
				dataToReturn = workerDashboardHandler.exitPark(transferrableData);
				break;
//			case WorkerDashboardShowDetailsAfterChoosingPark: 
//				dataToReturn = workerDashboardHandler.showParkDetails(transferrableData);
//				break;
			case WorkerLogin: 
				String data[] = (String[]) transferrableData.getMessage();
				boolean found = false;
				String username = data[0];
				String password = data[1];
				List<Worker> workers =  DBController.getWorkersDetails();
				for (Worker work : workers) {
					if(work.getUsername().contentEquals(username) && work.getPassword().contentEquals(password)) {
						dataToReturn = new TransferrableData(transferrableData.getMessageType(), work);
						found = true;
						break;
					}
				}
				if(!found) {
					dataToReturn = new TransferrableData(transferrableData.getMessageType(), null);
				}
//				dataToReturn = workerLoginHandler.identifyInTheSystem(transferrableData);
				break;
			case GuideLoginScreenLogin:
				dataToReturn = guideLoginHandler.loginGuide(transferrableData);
				break;
			case NewClientConnected:
				boolean usersAreImported = checkIfUseresAreImported();
				if(usersAreImported) {
					String details[] = (String[]) transferrableData.getMessage();
					serverController.updateTable(details[0], details[1], details[2]);
					if(notificationsThread == null) {
						notificationsThread = new Thread(notificationHandler);
						notificationsThread.start();
					}
				}
				dataToReturn = new TransferrableData(transferrableData.getMessageType(), usersAreImported);
				break;
			case ClientDisconnected:
				break;
			case CloseClient:
				String details2[] = (String[]) transferrableData.getMessage();
				Platform.runLater(() -> {
					serverController.removeFromTable(details2[0], details2[1]);
				});
				dataToReturn = new TransferrableData(MessageType.CloseClient, null);
				break;
			default:
				dataToReturn = new TransferrableData(null, null);
				break;
		}
		try {
			serverController.updateLog("Send: " + dataToReturn.getMessageType().toString());
			client.sendToClient(dataToReturn);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private boolean checkIfUseresAreImported() {
		if(DBController.getConnection() == null)
			return false;
		List<Worker> users = DBController.getWorkersDetails();
		boolean usersAreImported = true;
		for(Worker user : users) {
			if(user.getUsername() == null && user.getPassword() == null) {
				usersAreImported = false;
				break;
			}
		}
		return usersAreImported;
	}

	protected void serverStarted() {
		System.out.println("Server has started listening for connections.");
	}
	
	protected void serverStopped()  {
		if(notificationsThread != null)
			notificationsThread.interrupt();
	    System.out.println("Server has stopped listening for connections.");
	}

	/**
	 * @param serverController the serverController to set
	 */
	public void setServerController(Server_Controller serverController) {
		this.serverController = serverController;
	}

	/**
	 * @return the workerDashboardHandler
	 */
	public WorkerDashboardHandler getWorkerDashboardHandler() {
		return workerDashboardHandler;
	}

}
