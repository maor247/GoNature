package logic;

import java.util.ArrayList;

import common.Reminder;
import common.VisitReservation;
import common.TransferrableData;
import common.Traveler;
import server.DBController;

public class ApprovalHandler {
	
	public TransferrableData AprovePressedHandller(TransferrableData transferrableData) {
		VisitReservation visit=(VisitReservation)transferrableData.getMessage();
		visit.setApprovedbythetraveler(true);
		DBController.updateReservation(visit);
		return new TransferrableData(transferrableData.getMessageType(), true);
	}
	
	public TransferrableData DeclinePressedHandller(TransferrableData transferrableData) {
		VisitReservation visit=(VisitReservation)transferrableData.getMessage();
		boolean deleted = DBController.deleteReservation(visit);
		DBController.addReservationIntoCanceledReservations(visit);
		return new TransferrableData(transferrableData.getMessageType(), deleted);
	}
	
	//this method receives travelerID as TransferrableData
	public TransferrableData ChooseReservationHndler(TransferrableData transferrableData) {
		ArrayList<Reminder> allReminders = (ArrayList<Reminder>) DBController.getAllReminders();
		ArrayList<VisitReservation> reservationsToApprove = new ArrayList<>();
		ArrayList<VisitReservation> reservationsFromDB = (ArrayList<VisitReservation>) DBController.getReservationsForTraveler((Traveler)transferrableData.getMessage());
		for(VisitReservation res : reservationsFromDB) {
			for(Reminder rem : allReminders) {
				if(rem.getReservationID() == res.getReservationID() && rem.getReminderType().equals("Approve Visit") && !res.isApprovedbythetraveler()) {
					reservationsToApprove.add(res);
				}	
			}
		}
		return new TransferrableData(transferrableData.getMessageType() ,reservationsToApprove);
	}

}
