package common;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ChangeValueRequest implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int requestID;
	private Worker worker;
	private Park park;
	private LocalDateTime requestDate;
	private boolean isRequestApproved = false;
	private ValueRequested valueRequested;
	private int currentValue, newValue;
	
	
	
	/**
	 * @param worker
	 * @param park
	 * @param requestDate
	 * @param isRequestApproved
	 * @param valueRequested
	 * @param currentValue
	 * @param newValue
	 */
	public ChangeValueRequest(Worker worker, Park park,
			String valueRequested, int currentValue, int newValue) {
		this.worker = worker;
		this.park = park;
		this.requestDate = LocalDateTime.now();
		setValueRequestedByString(valueRequested);;
		this.currentValue = currentValue;
		this.newValue = newValue;
	}



	/**
	 * @return the requestID
	 */
	public int getRequestID() {
		return requestID;
	}



	/**
	 * @param requestID the requestID to set
	 */
	public void setRequestID(int requestID) {
		this.requestID = requestID;
	}



	/**
	 * @return the worker
	 */
	public Worker getWorker() {
		return worker;
	}



	/**
	 * @param worker the worker to set
	 */
	public void setWorker(Worker worker) {
		this.worker = worker;
	}



	/**
	 * @return the park
	 */
	public Park getPark() {
		return park;
	}



	/**
	 * @param park the park to set
	 */
	public void setPark(Park park) {
		this.park = park;
	}



	/**
	 * @return the requestDate
	 */
	public LocalDateTime getRequestDate() {
		return requestDate;
	}

	/**
	 * @param requestDate the requestDate to set
	 */
	public void setRequestDate(LocalDateTime requestDate) {
		this.requestDate = requestDate;
	}



	/**
	 * @return the isRequestApproved
	 */
	public boolean isRequestApproved() {
		return isRequestApproved;
	}



	/**
	 * @param isRequestApproved the isRequestApproved to set
	 */
	public void setRequestApproved(boolean isRequestApproved) {
		this.isRequestApproved = isRequestApproved;
	}



	/**
	 * @return the valueRequested
	 */
	public ValueRequested getValueRequested() {
		return valueRequested;
	}
	
	public String getValueRequestedString() {
		return valueRequested.toString();
	}

	/**
	 * @param valueRequested the valueRequested to set
	 */
	public void setValueRequested(ValueRequested valueRequested) {
		this.valueRequested = valueRequested;
	}
	
	public void setValueRequestedByString(String valueRequestedString) {
		switch(valueRequestedString) {
		case "MaxCapacity":
			valueRequested = ValueRequested.MaxCapacity;
			break;
		case "GapToMaxCapacity":
			valueRequested = ValueRequested.GapToMaxCapacity;
			break;
		case "MaxStayDurationHours":
			valueRequested = ValueRequested.MaxStayDurationHours;
			break;
		default: break;
		}
	}



	/**
	 * @return the currentValue
	 */
	public int getCurrentValue() {
		return currentValue;
	}



	/**
	 * @param currentValue the currentValue to set
	 */
	public void setCurrentValue(int currentValue) {
		this.currentValue = currentValue;
	}



	/**
	 * @return the newValue
	 */
	public int getNewValue() {
		return newValue;
	}



	/**
	 * @param newValue the newValue to set
	 */
	public void setNewValue(int newValue) {
		this.newValue = newValue;
	}


	public enum ValueRequested{
		MaxCapacity, GapToMaxCapacity, MaxStayDurationHours;
	}
}
