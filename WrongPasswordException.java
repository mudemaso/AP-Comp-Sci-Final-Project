public class WrongPasswordException extends IllegalArgumentException{
  public WrongPasswordException(String s)
  {
    super(s);
  }
}
