package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountTest {


    @Test
    public void testIncome(){
        Account acc = new Account();
        acc.setAmmount(100);

        //Run test
        acc.income(100);

        assertEquals(200,acc.getAmmount());
    }
}
