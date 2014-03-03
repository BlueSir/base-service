package test.hystrix.annotation;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 12/5/13
 * Time: 4:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class MyException extends Exception{
    public MyException(String message){
        super(message);
    }
}
