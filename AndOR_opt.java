package com.test.rajkumar;

public class AndOR_opt {
    public static void main(String []args){
        int x,y;
        x = 10;
        y = -10;
        //&& n || operation
        if (x > 0 && y > 0) {
            System.out.println("Both the numbers are +ve");
            }
        else if (x>0 || y>0){
            System.out.println("Atleast one number is +ve");
        }
        else{
            System.out.println("Both the numbes are -ve");
        }

    }

}
