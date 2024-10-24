package common;

import java.io.Serializable;

public class Report implements Serializable{
	private static final long serialVersionUID = 8921306165362278341L;
	private int mounth,year;
	private String reportid, reportType;
	private Park park;
	private byte[] fileByteArray;
	public Report(Park park,int mounth, int year , byte[] fileByteArray , String reportType) {
		this.mounth = mounth;
		this.year = year;
		this.park = park;
		this.fileByteArray = fileByteArray;
		this.reportType = reportType;
	}
	
    // Getter and setter for mounth
    public int getMounth() {
        return mounth;
    }

    public void setMounth(int mounth) {
        this.mounth = mounth;
    }

    // Getter and setter for year
    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    // Getter and setter for reportid
    public String getReportid() {
        return reportid;
    }

    public void setReportid(String reportid) {
        this.reportid = reportid;
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
	 * @return the fileByteArray
	 */
	public byte[] getFileByteArray() {
		return fileByteArray;
	}

	/**
	 * @param fileByteArray the fileByteArray to set
	 */
	public void setFileByteArray(byte[] fileByteArray) {
		this.fileByteArray = fileByteArray;
	}

	/**
	 * @return the reportType
	 */
	public String getReportType() {
		return reportType;
	}

	/**
	 * @param reportType the reportType to set
	 */
	public void setReportType(String reportType) {
		this.reportType = reportType;
	}
	
}
