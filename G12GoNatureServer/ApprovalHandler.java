package logic;

import java.util.ArrayList;


import common.MessageType;
import common.VisitReservation;
import common.TransferrableData;
import common.Traveler;
import connectionscreen.ConnectionScreenController;
import server.DBController;

public class ApprovalHandler {
	
	public TransferrableData AprovePressedHandller(TransferrableData transferrableData) {
		VisitReservation visit=(VisitReservation)transferrableData.getMessage();
		visit.setApprovedbythetraveler(true);
		DBController.updateReservation(visit);
		return new TransferrableData(transferrableData.getMessageType() ,true);
	}
	
	public TransferrableData DeclinePressedHandller(TransferrableData transferrableData) {
		VisitReservation visit=(VisitReservation)transferrableData.getMessage();
		visit.setApprovedbythetraveler(false);
		DBController.deleteReservation(visit);
		return new TransferrableData(transferrableData.getMessageType() ,false);
	}
	
	//this method receives travelerID as TransferrableData
	public TransferrableData ChooseReservationHndler(TransferrableData transferrableData) {
		ArrayList<Reminder> allReminders = DBController.getAllReminders();
		ArrayList<VisitReservation> reservationsFromDB=new ArrayList<>();
		ArrayList<Long> reservationIDList=new ArrayList<>();
		reservationsFromDB=(ArrayList<VisitReservation>) DBController.getReservationsForTraveler((Traveler)transferrableData.getMessage());
		for(VisitReservation res:reservationsFromDB) {
			for(Reminder rem : allReminders) {
				if(rem.getReservationID() == res.getReservationID() && rem.getReminderType().equals("Approve Visit")&&!reservationIDList.contains(res.getReservationID())) {
					reservationIDList.add(res.getReservationID());
				}	
			}
		}
		return new TransferrableData(transferrableData.getMessageType() ,reservationIDList);
	}

}
