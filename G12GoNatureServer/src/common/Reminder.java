package common;

import java.io.Serializable;
import java.sql.Timestamp;

public class Reminder implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1135412376558616043L;
	private int reminderID, userID;
	private long reservationID;
	String reminderType, status;
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	Timestamp dateTimeSent;
	
	public Reminder() {
		
	}

	/**
	 * @return the reminderID
	 */
	public int getReminderID() {
		return reminderID;
	}

	/**
	 * @param reminderID the reminderID to set
	 */
	public void setReminderID(int reminderID) {
		this.reminderID = reminderID;
	}

	/**
	 * @return the userID
	 */
	public int getUserID() {
		return userID;
	}

	/**
	 * @param userID the userID to set
	 */
	public void setUserID(int userID) {
		this.userID = userID;
	}

	/**
	 * @return the reservationID
	 */
	public long getReservationID() {
		return reservationID;
	}

	/**
	 * @param reservationID the reservationID to set
	 */
	public void setReservationID(long reservationID) {
		this.reservationID = reservationID;
	}

	/**
	 * @return the reminderType
	 */
	public String getReminderType() {
		return reminderType;
	}

	/**
	 * @param reminderType the reminderType to set
	 */
	public void setReminderType(String reminderType) {
		this.reminderType = reminderType;
	}

	/**
	 * @return the dateTimeSent
	 */
	public Timestamp getDateTimeSent() {
		return dateTimeSent;
	}

	/**
	 * @param dateTimeSent the dateTimeSent to set
	 */
	public void setDateTimeSent(Timestamp dateTimeSent) {
		this.dateTimeSent = dateTimeSent;
	}

	
}
