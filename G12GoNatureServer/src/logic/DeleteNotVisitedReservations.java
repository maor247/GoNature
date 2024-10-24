package logic;

import java.time.LocalDateTime;
import java.util.List;

import ServerGUI.Server_GUI;
import common.VisitReservation;
import server.DBController;

public class DeleteNotVisitedReservations implements Runnable{
	private Thread thread;
	
	@Override
	public void run() {
		while(Server_GUI.serverInstance.isListening()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
			List<VisitReservation> allExistingReservations = DBController.getAllReservations();
			for(VisitReservation visitReservation : allExistingReservations) {
				LocalDateTime timeOfVisit = visitReservation.getTimeofvisit().toLocalDateTime();
				if(timeOfVisit.plusHours(visitReservation.getMaxDurationInHours()).isBefore(LocalDateTime.now())
						&& visitReservation.getStatus().toLowerCase().equals("active")) {
					boolean deletedReservation = DBController.deleteReservation(visitReservation);
					if(deletedReservation) {
						DBController.addReservationIntoNotVisited(visitReservation);
					}
				}
			}
		}
		thread.interrupt();
	}
	
	public void setThread(Thread thread) {
		this.thread = thread;
	}
}
