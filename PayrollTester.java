/**
 * @(#)PayrollTester.java
 *
 * PayrollTester application
 *
 * @author 
 * @version 1.00 2017/5/19
 */
 
 import java.time.LocalDateTime;
 
public class PayrollTester {
    
    public static void main(String[] args) 
    {
    	Payroll payrollMngr = new Payroll("data/Employees.txt", "data/hoursWorked.txt");
    	try
    	{
    		payrollMngr.login(1, "welcome");    		
    	    payrollMngr.clockIn();
    	    payrollMngr.generatePayReport(LocalDateTime.now().minusDays(7), LocalDateTime.now());    	    
    	    payrollMngr.generateHoursWorkedReport(1, LocalDateTime.now().minusDays(7), LocalDateTime.now());
    		payrollMngr.logout();
    	}
    	catch(Exception exc)
    	{
    	   System.out.println("Could not login " + exc.toString());
    	}
    }
}
