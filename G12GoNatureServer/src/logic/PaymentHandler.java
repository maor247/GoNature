package logic;

import common.VisitReservation;

public class PaymentHandler {
	private static final double familyOnlineDiscount = 0.85;
	private static final double organizedOnlineDiscount = 0.7;
	private static final double organizedOfflineDiscount = 0.9;
	
	public static double calculatePayment(VisitReservation visitReservation) {
		double price = APIController.getFullPrice();
	    switch (visitReservation.getGroupType()) {
	        case "Private":
	        case "Family":
	            if (visitReservation.getReservationType().equals("Offline")) {
	                return visitReservation.getNumofvisitors() * price;
	            }
	            else if (visitReservation.getReservationType().equals("Online")) {
	            	return visitReservation.getNumofvisitors() * price * familyOnlineDiscount;
	            }
	            break;
	        case "Organized":
	            if (visitReservation.getReservationType().equals("Offline")) {
	                return visitReservation.getNumofvisitors() * price * organizedOfflineDiscount;
	            } else if (visitReservation.getReservationType().equals("Online")) {
	                // Drop the price by 12% because the payment is paid in advance
	                return (visitReservation.getNumofvisitors() - 1) * price * organizedOnlineDiscount;
	            }
	            break;
	        default:
	            // Handle other reservation types if needed
	            break;
	    }

	    // Default return value.
	    return price;
	}

	


}
