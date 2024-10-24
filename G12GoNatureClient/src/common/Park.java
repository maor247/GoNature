
package common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;


public class Park implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3427090741822686806L;
	private int visitor_quota,visitor_gap,visitorTimeInMinut;
	private ArrayList<Report> reports = new ArrayList<Report>();
	private String parkname;
	private int parkid;
	private int parkManagerId;
	private int devisionManagerID;


	public Park(int parkid,String parkname,int visitor_quota ,int visitor_gap,int visitorTimeInMinut) {
		this.parkid = parkid;
		this.parkname = parkname;
		this.visitor_quota = visitor_quota;
		this.visitor_gap = visitor_gap;
		this.visitorTimeInMinut = visitorTimeInMinut;
	}
	
    public Park() {
		// TODO Auto-generated constructor stub
	}

	// Getters and setters
    public int getVisitor_quota() {
        return visitor_quota;
    }

    public void setVisitor_quota(int visitor_quota) {
        this.visitor_quota = visitor_quota;
    }

    public int getVisitor_gap() {
        return visitor_gap;
    }

    public void setVisitor_gap(int visitor_gap) {
        this.visitor_gap = visitor_gap;
    }

    public int getVisitorTimeInMinut() {
        return visitorTimeInMinut;
    }

    public void setVisitorTimeInMinut(int visitorTimeInMinut) {
        this.visitorTimeInMinut = visitorTimeInMinut;
    }

    public String getParkname() {
        return parkname;
    }

    public void setParkname(String parkname) {
        this.parkname = parkname;
    }

    public int getParkid() {
        return parkid;
    }

    public void setParkid(int parkid) {
        this.parkid = parkid;
    }
    
	public int getParkManagerId() {
		return parkManagerId;
	}

	public void setParkManagerId(int parkManagerId) {
		this.parkManagerId = parkManagerId;
	}

	public int getDevisionManagerID() {
		return devisionManagerID;
	}

	public void setDevisionManagerID(int devisionManagerID) {
		this.devisionManagerID = devisionManagerID;
	}

    // Methods for manipulating reports ArrayList

    // Add report to the ArrayList
    public void addReport(Report report) {
        reports.add(report);
    }

    // Remove report by year and month
    public void removeReportByYearAndMonth(int year, int month) {
        Iterator<Report> iterator = reports.iterator();
        while (iterator.hasNext()) {
            Report report = iterator.next();
            if (report.getYear() == year && report.getMounth() == month) {
                iterator.remove();
            }
        }
    }

    // Get the size of reports ArrayList
    public int getReportsSize() {
        return reports.size();
    }

    // Check if a report exists for a specific year and month
    public boolean containsReportByYearAndMonth(int year, int month) {
        for (Report report : reports) {
            if (report.getYear() == year && report.getMounth() == month) {
                return true;
            }
        }
        return false;
    }

	@Override
	public int hashCode() {
		return Objects.hash(parkid);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Park)) {
			return false;
		}
		Park other = (Park) obj;
		return parkid == other.parkid;
	}
    
    
}
