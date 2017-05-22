import java.util.Map;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FullPayReport implements Report
{
	private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm a");
	
	private Map<Integer, Employee> employees;
	private int employeeId;
	
	/**
	 *Creates a Full Pay Report for all employees
	 *@param map of all employees
	 */
	public FullPayReport(Map<Integer, Employee> employees)
	{
		this.employees = employees;
	}
	
	/**
	 *Returns the complete pay report for a given time interval
	 *@param start time
	 *@parma end time
	 *@return the complete pay report 
	 */
	public String generateReport(LocalDateTime startTime, LocalDateTime endTime)
	{
	  	String output = String.format("PayRoll Report for period: %s - %s \n", startTime.format(dateTimeFormatter), endTime.format(dateTimeFormatter));
	  	for(Integer id: this.employees.keySet())
	  	{
	  		Employee emp = this.employees.get(id);
	  		long totalHours = emp.getTotalHoursWorked(startTime, endTime);
	  		double totalPay = emp.getTotalPay(startTime, endTime);
	  		output += String.format("Employee: %-20s | HoursWorked: %d, TotalPay: %f\n", emp.toString(), totalHours, totalPay);
	    }
	  	return output;
	}
}
