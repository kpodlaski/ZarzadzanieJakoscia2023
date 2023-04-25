package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountTest {

    Account account;

    @BeforeEach
    public void PrepareTest() throws NoSuchFieldException {
        account = new Account();
    }
    /*
        operacja income z wartością 100.
        stan początkowy 150
        stan oczekiwany 250
     */
    @Test
    public void TestCase3() throws IllegalAccessException, NoSuchFieldException {
        Field field = Account.class.getDeclaredField("ammount");
        field.setAccessible(true);
        field.set(account,150.0);
        field.setAccessible(false);
        account.income(100);
        assertEquals(account.getAmmount(),250.0,.01);
    }
}
