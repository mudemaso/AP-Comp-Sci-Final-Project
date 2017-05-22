import java.time.LocalDateTime;

public interface Report 
{
	String generateReport(LocalDateTime startTime, LocalDateTime endTime);
}
