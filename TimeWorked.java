import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;

public class TimeWorked 
{
	// maximum allowed hours
	private static final int MAX_MINUTES = 8*60;
	
	// scale for computer minute : actual minute
	private static final int TIME_SCALE = 60; 
	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm a");
	
	private LocalDateTime clockInTime;
	private LocalDateTime clockOutTime;
	private int hoursWorked;
	
	/**
	 *Creates a time worked manager object
	 *@param time when an employee clocks in
	 *@param time when an employee clocks out
	 */
	public TimeWorked(LocalDateTime clockInTime, LocalDateTime clockOutTime)
	{
		this.clockInTime = clockInTime;
		this.clockOutTime = clockOutTime;
		
		// clock-out time may need to be modified based on max hours allowed
		this.setClockOutTime();
		
		// set the duration
		this.calculateHoursWorked();
	}
	
	/**
	 *Returns the clock in and clock out time in a more readable format
	 *@return the clock in and clock out time
	 */
	public String toString()
	{
		String output = clockInTime.format(formatter) + " - " + clockOutTime.format(formatter);
		return output;
	}
	
	/**
	 *Returns the clock-in time
	 *@return clock-in time
	 */
	public LocalDateTime getClockInTime()
	{
		return clockInTime;
	}
	
	/**
	 *Returns the clock-out time
	 *@return clock-out time
	 */
	public LocalDateTime getClockOutTime()
	{
		return clockOutTime;
	}
	
	/**
	 *Returns the hours worked
	 *@return the number of hours worked 
	 */
	public double getHoursWorked()
	{
		return this.hoursWorked;
	}
	
	/**
	 *Calculates the hours worked
	 */
	private void calculateHoursWorked()
	{
		long computedMinutes = ChronoUnit.MINUTES.between(this.clockInTime, this.clockOutTime);
		
		// scale up the computed minutes by the scale
		long actualMinutes = computedMinutes * TIME_SCALE;
		
		// convert to hours (round to nearest)
		this.hoursWorked = (int)Math.round(actualMinutes/60.0);
	}
	
	/*
	 * Calculate the clockout time
	 */
	private void setClockOutTime()
	{
		// calculate the max. clockoutTime
		LocalDateTime maxClockOutTime = this.clockInTime.plusMinutes(MAX_MINUTES/TIME_SCALE);
		
		if (maxClockOutTime.isBefore(this.clockOutTime) )
		{
			System.out.println(String.format("Max. hours worked exceeded: prevClockoutTime=%s, newClockoutTime=%s", 
					this.clockOutTime.toString(), maxClockOutTime.toString()));
			this.clockOutTime = maxClockOutTime;
		}
			
	}
}
