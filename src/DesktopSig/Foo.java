package DesktopSig;

public class Foo 
{
    public static void main(String[] args) 
    {
    	//test9@test.com
    	//123Aa123
    	
    	
    	//test10@test.com
    	//1234
    	
    	SignService signService = new SignService();
//    	String token = signService.getToken("aa@aa.com", "1234");
    	String token = signService.getToken("test9@test.com", "123Aa123");
//    	System.out.println(token);
    	signService.getSignature("AYOUB", token);
    	System.out.println("End");
    	
    	
//    	String s1 = "wwrewrewrewreewr";
//    	System.out.println(s1.hashCode());
//    	
//    	String s2 = "wwrewrewrewreewr";
//    	System.out.println(s2.hashCode());
    }
}
