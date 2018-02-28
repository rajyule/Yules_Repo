package com.test.rajkumar;

public class Execise {
    public static void main(String []args){
        double salary = 20554.12;
        double tax = 0.0;

        if(salary <= 15000){
            tax = salary * .10;
        }else if(salary <= 4000){
            tax = salary * .20;
        }else{
             tax = salary * .30;
        }
        System.out.println("tax = " + tax);
    }
}
