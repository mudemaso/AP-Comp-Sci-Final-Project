import java.util.Map;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HoursWorkedReport implements Report
{
	private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm a");
	
	private Map<Integer, Employee> employees;
	private int employeeId;
	
	/**
	 *Creates an Hours Worked Report for a single employee
	 *@param map of all employees
	 *@param ID of employee
	 */
	public HoursWorkedReport(Map<Integer, Employee> employees, int employeeId)
	{
		this.employees = employees;
		this.employeeId = employeeId;
	}
	
	/**
	 *Returns the hours worked report in a given time interval
	 *@param start time
	 *@parma end time
	 *@return the hours worked report 
	 */
	public String generateReport(LocalDateTime startTime, LocalDateTime endTime)
	{
		Employee emp = this.employees.get(employeeId);
	  	if (emp == null)
	  		throw new WrongIDException(String.format("Could not find employee for id=%d", this.employeeId));
	  	ArrayList<TimeWorked> timeWorkedList = emp.getTimeWorked(startTime, endTime);
	  	String output = String.format("TimeWorkedReport for %s. Period: %s - %s \n", emp.toString(), 
	  		startTime.format(dateTimeFormatter), endTime.format(dateTimeFormatter));
	  	for(TimeWorked tw: timeWorkedList)
	  	{
	  		output += tw.toString() + "\n";
	  	}
	  	return output;
	}
}
