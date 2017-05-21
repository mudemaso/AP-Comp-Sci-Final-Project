import java.util.Hashtable;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.io.FileReader;
import java.util.Scanner;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.TreeMap;
import java.time.format.DateTimeFormatter;

public class Payroll
{
  private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm a");	
	
  // currently logged in employee;
  private Employee loggedInEmployee;
  
  // hash of employee id -> employee
  private TreeMap<Integer, Employee> employees;
  
  // filename that contains the list of employees
  private String employeeFilename;
  
  // filename that contains all clock-in/clock-out activity of employees
  private String hoursWorkedFilename;
  
  /**
   *Creates a Payroll manager object and loads files
   *@param name of file with all employees
   *@param name of file with history of hours worked for each employee
   */
  public Payroll(String employeeFilename, String hoursWorkedFilename)
  {
  	this.employeeFilename = employeeFilename;
  	this.hoursWorkedFilename = hoursWorkedFilename;
  	this.employees = new TreeMap<Integer, Employee>();
  	
  	// load the list of employees and create appropropriate employee objects
  	try
  	{
  		loadEmployees();	
  	}
  	catch(FileNotFoundException exc)
  	{
  		System.out.println("Could not find employees file: " + this.employeeFilename);
  	}
  	catch(InvalidEmployeeTypeException exc)
  	{
  		System.out.println("err= " + exc.toString());
  	}
  	
  	// load the list of employees and create appropropriate employee objects
  	try
  	{
  		loadHoursWorked();	
  	}
  	catch(FileNotFoundException exc)
  	{
  		System.out.println("Could not find employees file: " + this.employeeFilename);
  	}
  }
  
  /**
   * Loads all of the employees and creates the employees 
   */
  private void loadEmployees()
  	throws FileNotFoundException, InvalidEmployeeTypeException
  {
  	FileReader reader = new FileReader(this.employeeFilename);
  	Scanner in = new Scanner(reader);
  	
  	while (in.hasNextLine())
  	{
  		String line = in.nextLine();
  		if (line.charAt(0) == '#')
  			continue;
  			 		
  		String[] fields = line.split(",");
  		// check to make sure we have a valid row	
  		if (fields.length != 7)
  			continue;
 
  		String type = fields[0].trim();
  		String fname = fields[1].trim();
  		String lname = fields[2].trim();
  		int id = Integer.parseInt(fields[3].trim());
  		boolean changePassword = (Integer.parseInt(fields[4].trim()) == 1);
  		String password = fields[5].trim();
  		double wageRate = Double.parseDouble(fields[6].trim());
  		
  		Employee emp = null;
  		if (type.equals("Administrator") )
  			// construct an Administrator object
  			emp = new Administrator(fname, lname, id, changePassword, password, wageRate);
  		else if (type.equals("Worker"))
  			// construct a Worker object
  			emp = new Worker(fname, lname, id, changePassword, password, wageRate);
  		else
  			// throw exception
  			throw new InvalidEmployeeTypeException("Unsupported employee type: " + type);
  			
  		// stores employees in a map
  		if (emp != null)
  		{
  		  this.employees.put(id, emp);
  		}		
  	}
  	
  	System.out.println(String.format("Finished loading %d employees", this.employees.size()) );
  }
  
  /**
   * Loads the historical hours worked for each employee
   */
  private void loadHoursWorked() throws FileNotFoundException
  {
  	FileReader reader = new FileReader(this.hoursWorkedFilename);
  	Scanner in = new Scanner(reader);
  	
  	while (in.hasNextLine())
  	{
  		String line = in.nextLine();
  		if ( (!line.equals("")) && line.charAt(0) == '#')
  			continue;
  		
  		String[] fields = line.split(",");
  		// check for a valid row
  		if (fields.length != 3)
  			continue;
  		int empId = Integer.parseInt(fields[0].trim());
  		String actionType = fields[1].trim();
  		String actionTimeStr = fields[2].trim();
  		LocalDateTime actionTime = LocalDateTime.parse(actionTimeStr);
  		Employee emp = this.employees.get(empId);
  		System.out.println(String.format("%s: %s, %s", emp.toString(), actionType, actionTime.toString()));
  		
  		// create the history of clock-in/clock-out for each employee
  		try
  		{
  			if (actionType.equalsIgnoreCase("clockIn"))
  				emp.clockIn(actionTime);
  			else
  				emp.clockOut(actionTime);
  		}
  		catch(HoursWorkedExceededException exc)
  		{
  			// this should never happen since the file should always contain valid entries
  			System.out.println("Invalid entry in hours worked file: " + line);
  		}
  	}
  }
  
  /**
   *logs an employee into the system
   *@param employee's ID number
   *@param employee's password
   */
  public void login(int id, String password) 
  	throws WrongIDException, WrongPasswordException, ChangePasswordException
  {
  	// check if another employee is logged in and log them out
  	if (this.loggedInEmployee != null)
  		logout();
  	
  	Employee emp = 	employees.get(id);
  	if (emp == null)
  		throw new WrongIDException(String.format("Could not find id=%d", id));
  	
  	emp.login(password);
  	this.loggedInEmployee = emp;
  }

  /**
   *logs an employee out of the system
   */	
  public void logout()
  {
     this.loggedInEmployee.logout();
     this.loggedInEmployee = null;
  }
  
  /**
   * Clocks in the currently logged in employee
   */
  public void clockIn()
  {
	this.clockIn(LocalDateTime.now());
  }
  
   /**
   * Clocks in the currently logged in employee
   */
  public void clockIn(LocalDateTime clockInTime)
  {
  	try
  	{
  	  	this.loggedInEmployee.clockIn(clockInTime);	
  		recordClockInTime(clockInTime);
  	}
    catch (HoursWorkedExceededException exc)
    {
    	System.out.println("Need to force clockout");
    	// force a clockout - the clockout time is calculated internally and returned
    	LocalDateTime clockOutTime = this.loggedInEmployee.clockOut(clockInTime);
    	recordClockOutTime(clockOutTime);
    }
  }
  
  /**
   * Clocks out the currently logged in employee
   */
  public void clockOut()
  {
  	this.clockOut(LocalDateTime.now());
  }
  
  /**
   * Clocks out the currently logged in employee
   *@param time when employee clocks out
   */
  public void clockOut(LocalDateTime clockOutTime)
  {
  	this.loggedInEmployee.clockOut(clockOutTime);
  	recordClockOutTime(clockOutTime);
  }
  
  /**
   *Writes the employee clock-in time to a file
   *@param time at which the employee clocks in
   */
  private void recordClockInTime(LocalDateTime clockInTime)
  {
  	try
  	{
  		PrintWriter out = new PrintWriter(new FileWriter(this.hoursWorkedFilename, true));
  		String clockInEntry = String.format("%d, clockIn, %s", this.loggedInEmployee.getId(), clockInTime.toString());
  		out.println(clockInEntry);
  		out.close();
  	}
  	catch(FileNotFoundException exc)  	
  	{
  		System.out.println("Could not open file");
  	}
  	catch(IOException exc)  	
  	{
  		System.out.println("Could not open file");
  	}
  }
  
  /**
   *Writes the employee clock-out time to a file
   *@param time when the employee clocks out
   */  
  private void recordClockOutTime(LocalDateTime clockOutTime)
  {
  	try
  	{
  		PrintWriter out = new PrintWriter(new FileWriter(this.hoursWorkedFilename, true));
  		String clockOutEntry = String.format("%d, clockOut, %s", this.loggedInEmployee.getId(), clockOutTime.toString());
  		out.println(clockOutEntry);
  		out.close();
  	}
  	catch (FileNotFoundException exc)
  	{
  		System.out.println("Could not open file");
  	}
  	catch(IOException exc)  	
  	{
  		System.out.println("Could not open file");
  	}
  }
  
  /**
   *Generates the hours worked by a given employee in the specified time period
   *@param Employee's ID
   *@param time when the employee started work
   *@param time when the employee ended work
   */
  public String generateHoursWorkedReport(int id, LocalDateTime startTime, LocalDateTime endTime) throws WrongIDException
  {
  	// make sure only an admin can do this
  	Administrator admin = (Administrator) this.loggedInEmployee;
  	if (admin == null)
  	{
  		throw new IllegalStateException("Only administrators can generate pay report");
  	}
  	
  	Report reportGenerator = new HoursWorkedReport(this.employees, id);
  	String output = reportGenerator.generateReport(startTime, endTime);
  	System.out.println(output);
  	return output;

  }
  
  /**
   *Generates the pay for a given employee in the specified time period
   *@param time when the employee started work
   *@param time when the employee ended work
   */
  public String generatePayReport(LocalDateTime startTime, LocalDateTime endTime) throws IllegalStateException
  {
  	// make sure only an admin can do this
  	Administrator admin = (Administrator) this.loggedInEmployee;
  	if (admin == null)
  	{
  		throw new IllegalStateException("Only administrators can generate pay report");
  	}
  	
  	Report reportGenerator = new FullPayReport(this.employees);
  	String output = reportGenerator.generateReport(startTime, endTime);
  	System.out.println(output);
  	return output;
  } 
}