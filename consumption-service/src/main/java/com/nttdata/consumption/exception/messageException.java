package com.nttdata.consumption.exception;

public class messageException {

    public static String cardNotAssociated(){
        return "The card does not have an associated account";
    }

    public static String cardWithManyAssociatedAccounts(){
        return "The card has many associated accounts";
    }

    public static String noCreditAvailable(){
        return "The card does not have enough credit available";
    }
}
