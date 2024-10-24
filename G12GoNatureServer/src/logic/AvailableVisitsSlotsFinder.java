package logic;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import ServerGUI.Server_GUI;
import common.Reminder;
import common.VisitReservation;
import server.DBController;

public class AvailableVisitsSlotsFinder implements Runnable{
	private Thread thread;
	
	public void setThread(Thread thread) {
		this.thread = thread;
	}
	
	@Override
	public void run(){
		while(Server_GUI.serverInstance.isListening()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
			List<VisitReservation> waitingLists = DBController.getWaitingList();
			for(VisitReservation visitReservation : waitingLists) {
				boolean available = TravelerVisitReservationHandler.checkAvailabilityForReservation(visitReservation);
				if(available) {
					visitReservation.setPricediscountforreservation(-1);
					visitReservation.setStatus("Active");
					LocalDateTime exitTime = visitReservation.getTimeofvisit().toLocalDateTime();
					exitTime = exitTime.plusHours(visitReservation.getMaxDurationInHours());
					visitReservation.setExitTime(Timestamp.valueOf(exitTime));
					boolean deleted = DBController.deleteReservationFromWaitingList(visitReservation);
					if(deleted) {
						long reservationID = DBController.addNewReservation(visitReservation);
						Reminder reminder = new Reminder();
						reminder.setReservationID(reservationID);
						reminder.setUserID(Integer.parseInt(visitReservation.getTraveler().getId_number()));
						reminder.setReminderType("Reservation Added");
						reminder.setDateTimeSent(Timestamp.valueOf(LocalDateTime.now()));
						visitReservation.setReservationID(reservationID);
						DBController.addReminder(reminder);
					}
				}
			}
		}
		thread.interrupt();
	}

}
