package logic;

import java.util.List;

import common.*;
import server.DBController;

public class TravelerVisitReservationHandler {

	public TransferrableData approveReservation(TransferrableData transferrableData) {
		VisitReservation desiredReservation = (VisitReservation) transferrableData.getMessage();
		boolean available = checkAvailabilityForReservation(desiredReservation);
		if(available) {
			double price = PaymentHandler.calculatePayment(desiredReservation);
			desiredReservation.setPrice(new Float(price));
			long reservationID = DBController.addNewReservation(desiredReservation);
			desiredReservation.setReservationID(reservationID);
		}
		else {
			desiredReservation = null;
		}
		return new TransferrableData(transferrableData.getMessageType(), desiredReservation);
	}
	
	public static boolean checkAvailabilityForReservation(VisitReservation desiredReservation) {
		List<VisitReservation> allReservations = DBController.getAllReservations();
		int hourOfDesiredVisit = desiredReservation.getTimeofvisit().toLocalDateTime().getHour();
		long currentPark = desiredReservation.getPark().getParkid();
		int maxVisitors = desiredReservation.getPark().getVisitor_quota() - desiredReservation.getPark().getVisitor_gap();
		int visitorsSumForEachHour[] = new int[desiredReservation.getMaxDurationInHours()];
		int maxDurationOfDesiredVisit = desiredReservation.getMaxDurationInHours();
		for(VisitReservation visitReservation : allReservations) {
			if((visitReservation.getPark().getParkid() == currentPark) && (visitReservation.getTimeofvisit().equals(desiredReservation.getTimeofvisit())) && !visitReservation.getStatus().toLowerCase().equals("closed")) {
				int hourOfCurrentVisit = visitReservation.getTimeofvisit().toLocalDateTime().getHour();
				if((hourOfCurrentVisit >= hourOfDesiredVisit) && (hourOfCurrentVisit < hourOfDesiredVisit + maxDurationOfDesiredVisit)) {
					for(int i = hourOfCurrentVisit - hourOfDesiredVisit; i < visitorsSumForEachHour.length; i++) {
						visitorsSumForEachHour[i] += visitReservation.getNumofvisitors();
					}
				}
			}
		}
		for(int visitorsInHour : visitorsSumForEachHour) {
			if(visitorsInHour > (maxVisitors - desiredReservation.getNumofvisitors())) {
				return false;
			}
		}
		return true;
	}

	public TransferrableData joinWaitingList(TransferrableData transferrableData) {
		boolean addedToWaitingList = DBController.addReservationIntoWaitingList((VisitReservation) transferrableData.getMessage());
		return new TransferrableData(transferrableData.getMessageType(), addedToWaitingList);
	}

	public TransferrableData updateDetails(TransferrableData transferrableData) {
		List<Park> parks = DBController.getParks();
		return new TransferrableData(transferrableData.getMessageType(), parks);
	}
	
}
