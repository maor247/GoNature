package common;

import java.io.Serializable;
import java.util.ArrayList;

public class Traveler implements Serializable{
	private static final long serialVersionUID = 7148366960751683639L;
	private String id_number, email_addres, phone_number;
	private ArrayList<VisitReservation> visit_list = new ArrayList<VisitReservation>();
	private ArrayList<VisitReservation> waiting_list = new ArrayList<VisitReservation>();
	private boolean isguide = false;
	
	public Traveler(String id_number, String email_addres, String phone_number) {
		this.id_number = id_number;
		this.email_addres = email_addres;
		this.phone_number = phone_number;
	}

	public Traveler(String id_number) {
    	this.id_number = id_number;
	}

	// Getters
    public String getId_number() {
        return id_number;
    }

    public String getEmail_addres() {
        return email_addres;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public boolean isIsguide() {
        return isguide;
    }

    // Setters
    public void setId_number(String id_number) {
        this.id_number = id_number;
    }

    public void setEmail_addres(String email_addres) {
        this.email_addres = email_addres;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public void setIsguide(boolean isguide) {
        this.isguide = isguide;
    }

    // Methods for manipulating visit_list ArrayList

	public void addVisitReservation(VisitReservation visitReservation) {
        visit_list.add(visitReservation);
    }

    public void removeVisitReservation(VisitReservation visitReservation) {
        visit_list.remove(visitReservation);
    }

    public boolean containsVisitReservationByDateTime(String dateTime) {
        for (VisitReservation reservation : visit_list) {
            if (reservation.getTimeofvisit().equals(dateTime)) {
                return true;
            }
        }
        return false;
    }

    // Methods for manipulating waiting_list ArrayList

    public void addWaitingList(VisitReservation waitingList) {
        waiting_list.add(waitingList);
    }

    public void removeWaitingList(VisitReservation waitingList) {
        waiting_list.remove(waitingList);
    }

    public boolean containsWaitingListByDateTime(String dateTime) {
        for (VisitReservation waiting : waiting_list) {
            if (waiting.getTimeofvisit().equals(dateTime)) {
                return true;
            }
        }
        return false;
    }
}
