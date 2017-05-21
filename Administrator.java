
public class Administrator extends Employee {
	
  //Gives the employee Administrative permissions
  private boolean isAdmin;	
  
  /**
   *Creates an Administrator object
   *@param first name
   *@param last name
   *@param ID
   *@param whether or not they need to change password
   *@param password
   *@param wage
   */	
  public Administrator(String fname, String lname, int idNum, boolean changePassword, String password, double wage)
  {
  	super(fname, lname, idNum, changePassword, password, wage);
    isAdmin = true;
  }
}
