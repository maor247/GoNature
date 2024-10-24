package common;

import java.io.Serializable;
import java.sql.Timestamp;

public class VisitReservation implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8412366937254200437L;
	//additions:
	private long reservationID;
	private String status, reservationType, groupType;
	//end of additions.
	private Park park;
	private Timestamp timeofvisit, exitTime;
	private Traveler traveler ;
	private int numofvisitors, maxDurationInHours;
	private float pricediscountforreservation, price;
	private boolean approvedbythetraveler = false;

	public VisitReservation(long reservationID, Traveler traveler, Park park, Timestamp timeofvisit, int numofvisitors, float pricediscountforreservation, float price , String status, String reservationType, String groupType) {
		this.reservationID = reservationID;
		this.status = status;
		this.reservationType = reservationType;
		this.groupType = groupType;
		this.park = park;
		this.timeofvisit = timeofvisit;
		this.traveler = traveler;
		this.numofvisitors = numofvisitors;
		this.pricediscountforreservation = pricediscountforreservation;
		this.price = price;
	}
	
    // Getters
    public Park getPark() {
        return park;
    }

    public Timestamp getTimeofvisit() {
        return timeofvisit;
    }

    public Traveler getTraveler() {
        return traveler;
    }

    public int getNumofvisitors() {
        return numofvisitors;
    }

    public float getPricediscountforreservation() {
        return pricediscountforreservation;
    }

    public float getPrice() {
        return price;
    }

    public boolean isApprovedbythetraveler() {
        return approvedbythetraveler;
    }

    public long getReservationID() {
		return reservationID;
	}

	public void setReservationID(long reservationID) {
		this.reservationID = reservationID;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getReservationType() {
		return reservationType;
	}

	public void setReservationType(String reservationType) {
		this.reservationType = reservationType;
	}

	public String getGroupType() {
		return groupType;
	}

	public void setGroupType(String groupType) {
		this.groupType = groupType;
	}

	// Setters
    public void setPark(Park park) {
        this.park = park;
    }

    public void setTimeofvisit(Timestamp timeofvisit) {
        this.timeofvisit = timeofvisit;
    }

    public void setTraveler(Traveler traveler) {
        this.traveler = traveler;
    }

    public void setNumofvisitors(int numofvisitors) {
        this.numofvisitors = numofvisitors;
    }

    public void setPricediscountforreservation(float pricediscountforreservation) {
        this.pricediscountforreservation = pricediscountforreservation;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setApprovedbythetraveler(boolean approvedbythetraveler) {
        this.approvedbythetraveler = approvedbythetraveler;
    }

	public int getMaxDurationInHours() {
		return maxDurationInHours;
	}

	public void setMaxDurationInHours(int maxDurationInHours) {
		this.maxDurationInHours = maxDurationInHours;
	}

	public Timestamp getExitTime() {
		return exitTime;
	}

	public void setExitTime(Timestamp exitTime) {
		this.exitTime = exitTime;
	}
}
