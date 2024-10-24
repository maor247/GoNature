package logic;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.TransferrableData;
import common.Traveler;
import common.VisitReservation;
import common.Park;
import server.DBController;

public class WorkerDashboardHandler {
	
	public TransferrableData detailsOfParks(TransferrableData transferrableData) {
		LocalDateTime currentTime = (LocalDateTime) transferrableData.getMessage();
		List<VisitReservation> allReservations = DBController.getAllReservations();
		Map<Park, Integer> parkWithAvailableSpace = new HashMap<>();
		List<Park> parks = DBController.getParks();
		for(Park park : parks) {
			int availableSpace = park.getVisitor_quota() - park.getVisitor_gap();
			availableSpace -= getAvailableSpaceForPark(park.getParkid(), currentTime, allReservations);
			parkWithAvailableSpace.put(park, availableSpace);
			
		}
		return new TransferrableData(transferrableData.getMessageType(), parkWithAvailableSpace);
	}
	
	public int getAvailableSpaceForPark(int parkID, LocalDateTime currentTime, List<VisitReservation> allReservations) {
		int takenSpace = 0;
		for(VisitReservation visitReservation : allReservations) {
			LocalDateTime timeOfVisit = visitReservation.getTimeofvisit().toLocalDateTime();
			if(visitReservation.getPark().getParkid() == parkID 
					&& timeOfVisit.isBefore(currentTime) 
					&& timeOfVisit.plusHours(visitReservation.getMaxDurationInHours()).isAfter(currentTime) 
					&& !visitReservation.getStatus().toLowerCase().equals("closed")) {
				takenSpace += visitReservation.getNumofvisitors();
			}
		}
		return takenSpace;
	}

	public TransferrableData payOnReservation(TransferrableData transferrableData) {
		long reservationID = (Long) transferrableData.getMessage();
		List<VisitReservation> allReservations = DBController.getAllReservations();
		for(VisitReservation visitReservation : allReservations) {
			if(visitReservation.getReservationID() == reservationID) {
				float priceToPay = (float) PaymentHandler.calculatePayment(visitReservation);
				visitReservation.setPrice(priceToPay);
				return new TransferrableData(transferrableData.getMessageType(), visitReservation);
			}
		}
		return new TransferrableData(transferrableData.getMessageType(), null);
	}

	public TransferrableData getTravelerForReservation(TransferrableData transferrableData) {
		String travelerID = (String) transferrableData.getMessage();
		Traveler traveler = DBController.getUserDetailsViaUserID(travelerID);
		boolean travelerExists = false;
		if(traveler == null) {
			traveler = new Traveler(travelerID);
			travelerExists = DBController.addTraveler(traveler);
		}
		else {
			travelerExists = true;
		}
		if(travelerExists)
			return new TransferrableData(transferrableData.getMessageType(), traveler);
		return new TransferrableData(transferrableData.getMessageType(), null);
	}

	public TransferrableData enterPark(TransferrableData transferrableData) {
		long reservationID = (Long) transferrableData.getMessage();
		List<VisitReservation> allReservations = DBController.getAllReservations();
		VisitReservation foundVisitReservation = null;
		for(VisitReservation visitReservation : allReservations) {
			if(visitReservation.getReservationID() == reservationID) {
				foundVisitReservation = visitReservation;
				break;
			}
		}
		if(foundVisitReservation == null) {
			return new TransferrableData(transferrableData.getMessageType(), false);
		}
		LocalDateTime timeOfVisit = foundVisitReservation.getTimeofvisit().toLocalDateTime();
		if(!(LocalDateTime.now().isAfter(timeOfVisit) && LocalDateTime.now().isBefore(timeOfVisit.plusHours(foundVisitReservation.getMaxDurationInHours()))) 
				|| !foundVisitReservation.isApprovedbythetraveler()) {
			return new TransferrableData(transferrableData.getMessageType(), false);
		}
		foundVisitReservation.setStatus("In Progress");
		DBController.updateReservation(foundVisitReservation);
		return new TransferrableData(transferrableData.getMessageType(), true);
	}

	public TransferrableData exitPark(TransferrableData transferrableData) {
		long reservationID = (Long) transferrableData.getMessage();
		List<VisitReservation> allReservations = DBController.getAllReservations();
		VisitReservation foundVisitReservation = null;
		for(VisitReservation visitReservation : allReservations) {
			if(visitReservation.getReservationID() == reservationID) {
				foundVisitReservation = visitReservation;
				break;
			}
		}
		if(foundVisitReservation == null) {
			return new TransferrableData(transferrableData.getMessageType(), false);
		}
		if(!foundVisitReservation.getStatus().equals("In Progress")) {
			return new TransferrableData(transferrableData.getMessageType(), false);
		}
		foundVisitReservation.setStatus("Closed");
		foundVisitReservation.setExitTime(Timestamp.valueOf(LocalDateTime.now()));
		DBController.updateReservation(foundVisitReservation);
		return new TransferrableData(transferrableData.getMessageType(), true);
	}
	
}
