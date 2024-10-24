package logic;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import common.TransferrableData;
import common.VisitReservation;
import server.DBController;

public class AlternativeDateHandler {
	private final int startingHour = 8;
	private final int endingHour = 17;

	public TransferrableData getAvailableTimes(TransferrableData transferrableData) {
		List<String> availableDateAndTimes = new ArrayList<>();
		VisitReservation currentVisitReservation = (VisitReservation) transferrableData.getMessage();
		List<VisitReservation> allReservations = DBController.getAllReservations();
		LocalDateTime timeOfDesiredVisit = currentVisitReservation.getTimeofvisit().toLocalDateTime();
		int maxDurationOfDesiredVisit = currentVisitReservation.getMaxDurationInHours();
		int[][] sumForEachVisitForEachDay = new int[8][endingHour - startingHour];
		for(VisitReservation visitReservation : allReservations) {
			LocalDateTime timeOfVisit = visitReservation.getTimeofvisit().toLocalDateTime();
			Duration duration = Duration.between(timeOfDesiredVisit, timeOfVisit);
			if(duration.toDays() <= 7 && duration.toDays() >= 0 && (timeOfVisit.isAfter(timeOfDesiredVisit) || timeOfVisit.isEqual(timeOfDesiredVisit)) && visitReservation.getPark().getParkid() == currentVisitReservation.getPark().getParkid()) {
				sumForEachVisitForEachDay[(int) duration.toDays()][timeOfVisit.getHour() - startingHour] += visitReservation.getNumofvisitors();
			}
		}
		LocalDate startOfDayDate = LocalDate.of(timeOfDesiredVisit.getYear(), timeOfDesiredVisit.getMonth(), timeOfDesiredVisit.getDayOfMonth());
		LocalDateTime startOfDay = startOfDayDate.atTime(8, 0);
		int maxVisitors = currentVisitReservation.getPark().getVisitor_quota() - currentVisitReservation.getPark().getVisitor_gap();
		for(int i = 0; i < sumForEachVisitForEachDay.length; i++) {
			for(int j = 0; j < sumForEachVisitForEachDay[i].length - maxDurationOfDesiredVisit; j += maxDurationOfDesiredVisit) {
				if((sumForEachVisitForEachDay[i][j] < maxVisitors - currentVisitReservation.getNumofvisitors())) {
					LocalDateTime availableTime = startOfDay.plusDays(i);
					LocalDateTime updatedavailableTime = availableTime.plusHours(j);
					String time = updatedavailableTime.toLocalDate().toString() + " " + updatedavailableTime.toLocalTime().toString();
					availableDateAndTimes.add(time);
				}
			}
		}
		return new TransferrableData(transferrableData.getMessageType(), availableDateAndTimes);
	}

	public TransferrableData createVisit(TransferrableData transferrableData) {
		VisitReservation desiredReservation = (VisitReservation) transferrableData.getMessage();
		boolean available = TravelerVisitReservationHandler.checkAvailabilityForReservation(desiredReservation);
		if(available) {
			float price = (float) PaymentHandler.calculatePayment(desiredReservation);
			desiredReservation.setPrice(price);
			long reservationID = DBController.addNewReservation(desiredReservation);
			desiredReservation.setReservationID(reservationID);
		}
		else {
			desiredReservation = null;
		}
		return new TransferrableData(transferrableData.getMessageType(), desiredReservation);
	}
	
}
