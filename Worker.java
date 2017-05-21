public class Worker extends Employee
{
  /**
   *Creates a Worker object
   *@param first name
   *@param last name
   *@param ID number
   *@param whether or not they have to change their password
   *@param password
   *@param wage rate
   */	
  public Worker(String fname, String lname, int id, boolean changePassword, String password, double wageRate)
  {
    super(fname, lname, id, changePassword, password, wageRate);
  }
}