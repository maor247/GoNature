package common;

import java.io.Serializable;

public class Worker extends Traveler implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = -4246670281665736305L;
	private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String workerId;
    private String permission;
    
    public Worker(String idNumber, String workerId) {
    	super(idNumber);
    	this.workerId = workerId;
    }

    public Worker(String id_number, String email_addres, String phone_number, String username, String password, String firstName, String lastName, String workerId) {
    	super(id_number, email_addres, phone_number);
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.workerId = workerId;
    }

	// Setter and getter for username
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Setter and getter for password
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Setter and getter for firstName
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    // Setter and getter for lastName
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    // Setter and getter for workerId
    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }
    /*
    // Setter and getter for permission
    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    // Enum for permission levels
    public enum Permission {
        Regular,
        Service,
        Park_Manager,
        Devision_Manager
    }*/
    
    public void setRegularPermission() {
    	this.permission = "Regular";
    }
    public void setServicePermission() {
    	this.permission = "Service";
    }
	public void setPark_ManagerPermission() {
		this.permission = "Park_Manager";
	}
	public void setDevision_ManagerPermission() {
		this.permission = "Devision_Manager";
	}
	public void setPermissionAsNone() {
		this.permission = "None";
	}
	public String get_permmision() {
		return permission;
	}

	public static Worker findWorkerByID(int int1) {
		// TODO Auto-generated method stub
		return null;
	}
}
