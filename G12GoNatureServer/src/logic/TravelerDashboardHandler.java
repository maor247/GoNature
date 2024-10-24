package logic;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import common.*;
import server.DBController;

public class TravelerDashboardHandler {

	public TransferrableData cancelAVisit(TransferrableData transferrableData) {
		boolean result = DBController.deleteReservation((VisitReservation) transferrableData.getMessage());
		if(result)
			DBController.addReservationIntoCanceledReservations((VisitReservation) transferrableData.getMessage());
		return new TransferrableData(transferrableData.getMessageType(), result);
	}

	public TransferrableData changeEmailAddress(TransferrableData transferrableData) {
		DBController.updateUserDetails((Traveler) transferrableData.getMessage());
		return transferrableData;
	}
	
	public TransferrableData changePhoneNumber(TransferrableData transferrableData) {
		DBController.updateUserDetails((Traveler) transferrableData.getMessage());
		return new TransferrableData(transferrableData.getMessageType(), (Traveler) transferrableData.getMessage());
	}

	public TransferrableData exitWaitingList(TransferrableData transferrableData) {
		boolean deleted = DBController.deleteReservationFromWaitingList((VisitReservation) transferrableData.getMessage());
		return new TransferrableData(transferrableData.getMessageType(), deleted);
	}

	public TransferrableData showReservations(TransferrableData transferrableData) {
		Traveler traveler = (Traveler) transferrableData.getMessage();
		List<VisitReservation> reservationsOfTraveler = DBController.getReservationsForTraveler(traveler);
		List<VisitReservation> upcomingVisits = new ArrayList<>();
		LocalDateTime today = LocalDateTime.now();
		for(VisitReservation visitReservation : reservationsOfTraveler) {
			LocalDateTime timeOfVisit = visitReservation.getTimeofvisit().toLocalDateTime();
			if(timeOfVisit.isEqual(today) || timeOfVisit.isAfter(today)) {
				upcomingVisits.add(visitReservation);
			}
		}
		return new TransferrableData(transferrableData.getMessageType(), upcomingVisits);
	}

	public TransferrableData showUserDetails(TransferrableData transferrableData) {
		return new TransferrableData(transferrableData.getMessageType(), DBController.getUserDetailsViaUserID(((Traveler) transferrableData.getMessage()).getId_number()));
	}

	public TransferrableData showWaitingList(TransferrableData transferrableData) {
		return new TransferrableData(transferrableData.getMessageType(), DBController.getWaitingList((Traveler) transferrableData.getMessage()));
	}

}
