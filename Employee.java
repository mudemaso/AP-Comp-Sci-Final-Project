import java.util.Date;
import java.util.ArrayList;
import java.time.LocalDateTime;

public class Employee{
	
	//instance fields
	private int id;
	private String firstName;
	private String lastName;
	private double wageRate;
	private boolean changePassword;
	private String password;
	private LocalDateTime lastClockInTime;
	
	//Checks to see if the user is currently clocked-in
	private boolean isActive;
	
	//The date the employee joined the company
	private Date startDate;
	
	//hours worked by this employee
	private ArrayList<TimeWorked> timeWorkedList;
			
	/**
	 *Constructs an Employee object
	 *@param first name
	 *@param last name
	 *@param ID
	 *@param whether or not the employee has to change their password
	 *@param password
	 *@param wage rate
	 */
	public Employee(String fname, String lname, int id, boolean changePassword, String password, double wageRate)
	{
		this.firstName = fname;
		this.lastName = lname;
		this.id = id;
		this.changePassword = changePassword;
		this.password = password;
		this.wageRate = wageRate;
		
		this.isActive = false;
		timeWorkedList = new ArrayList<TimeWorked>();
	}
	
	/**
	 *Prints the employee ID, first name, and last name
	 *@return ID, first name, and last name
	 */
    public String toString()
    {
    	String outStr = String.format("%d, %s %s", this.id, this.firstName, this.lastName);
    	return outStr;
    }
		
	/**
	 *Returns ID of Employee
	 *@return ID
	 */
	public int getId()
	{
		return id;
	}	
	
	/**
	 *Logs in the employee
	 *@param password
	 */
	public boolean login(String password) throws ChangePasswordException, WrongPasswordException
	{
		if (this.changePassword)
			throw new ChangePasswordException("Please change password");
			
		if (! password.equals(this.password))
			throw new WrongPasswordException("Incorrect password");
		
		System.out.println(String.format("User (id=%d, fname=%s) logged in", this.id, this.firstName) );
		return true;
	}
	
	/**
	 *Logs out the employee
	 */
	public void logout()
	{
		System.out.println(String.format("User (id=%d, fname=%s) logged out", this.id, this.firstName) );
	}
	
	/**
	 *Clocks in the employee
	 */
	public void clockIn(LocalDateTime clockInTime) throws HoursWorkedExceededException
	{
		if (isActive)
			throw new HoursWorkedExceededException("Last clock-in was at " + this.lastClockInTime.toString());
				
		isActive = true;
		this.lastClockInTime = clockInTime;
	}
	
	/**
	 *Returns the clockOut - this may be different from the currentTime if the employee forgot
	 *to clockout or worked more than the max number of hours permissible
	 *@return the clock out time
	 */
	public LocalDateTime clockOut(LocalDateTime clockOutTime)
	{
		isActive = false;
		TimeWorked timeWorked = new TimeWorked(this.lastClockInTime, clockOutTime);
		
		// get the actual clock-out time based on company policy
		clockOutTime = timeWorked.getClockOutTime();
		
		// add the timeWorked to the list of all previous timeWorked
		timeWorkedList.add(timeWorked);
		
		return clockOutTime;
	}
	
	/**
	 *Lets the employee see how many hours they have worked
	 *@param start time
	 *@param end time
	 *@return total hours worked
	 */
	public long getTotalHoursWorked(LocalDateTime startTime, LocalDateTime endTime)
	{
		ArrayList<TimeWorked> twList = this.getTimeWorked(startTime, endTime);
		long totalHours = 0;
		for(int i=0; i<twList.size(); i++)
		{
			totalHours += twList.get(i).getHoursWorked();
		}	
		return totalHours;
	}
	
	/**
	 *@param start time
	 *@param end time
	 *@return total pay
	 */
	public double getTotalPay(LocalDateTime startTime, LocalDateTime endTime)
	{
		double totalPay = this.wageRate * this.getTotalHoursWorked(startTime, endTime);
		return totalPay;
	}
	
	/**
	 *Sets a new password for the employee
	 *@param password
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}
	 	
  	/**
  	 *Returns a list of times worked from the given startTime
  	 *@param start time
  	 *@param end time
  	 *@return list of times worked that lie within the interval
  	 */
  	public ArrayList<TimeWorked> getTimeWorked(LocalDateTime startTime, LocalDateTime endTime)
  	{
  		ArrayList<TimeWorked> resultSet = new ArrayList();
  		
  		for(int i=0; i<this.timeWorkedList.size(); i++)
  		{
  			TimeWorked tw = this.timeWorkedList.get(i);
  			if ( (tw.getClockInTime().isEqual(startTime) || tw.getClockInTime().isAfter(startTime) )
  				 && tw.getClockInTime().isBefore(endTime) )
  				resultSet.add(tw);
  		}
  		return resultSet;
  	}
}
