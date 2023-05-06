package biz;

import db.dao.DAO;
import model.Account;
import model.User;
import model.operations.PaymentIn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;


import java.lang.reflect.Field;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AccountManagerTest {

    AccountManager aManager;
    DAO dao;
    BankHistory bHistory;

    @BeforeEach
    public void initTest() throws SQLException {
        aManager = new AccountManager();
        dao = mock(DAO.class);
        bHistory = mock(BankHistory.class);
        Field field;
        try {
            field = AccountManager.class.getDeclaredField("dao");
            field.setAccessible(true);
            field.set(aManager,dao); //aManager.dao = dao;
            field.setAccessible(false);
            field = AccountManager.class.getDeclaredField("history");
            field.setAccessible(true);
            field.set(aManager,bHistory); //aManager.history = bHistory;
            field.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void paymentInTest1() throws SQLException {
        // Konto o id 12 istnieje, można na nie wpłacić pieniądze wszytsko powinno działać
        // stan początkowy konta 100 pln
        // wpłata 100 pln
        // stan końcowy końcowy 200 pln
        // SET TEST CASE
        Account acc = mock(Account.class);
        when(dao.findAccountById(12)).thenReturn(acc);
        double quota = 0.0;
        when(acc.income(100)).thenReturn(true);
        //System.out.println(dao.findAccountById(12));
        when(dao.updateAccountState(acc)).thenReturn(true);

        //RUN TEST
        User user = new User();
        boolean resp = aManager.paymentIn(user, 100, "Wpłata", 12 );

        //VERIFY BEHAVIOUR
        assertTrue(resp);
        verify(acc, times(1)).income(eq(100.0));
        verify(bHistory, times(1))
                .logOperation(any(PaymentIn.class),eq(true));

    }

    @Test
    public void paymentInTest2() throws SQLException {
        // Konto o id 12 nie istnieje, nie można na nie wpłacić pieniędzy
        // zwraca fałsz
        when(dao.findAccountById(12)).thenReturn(null);

        User user = new User();
        boolean resp = aManager.paymentIn(user, 100, "Wpłata", 12 );

        assertFalse(resp);

    }

    @Test
    public void paymentInTest3() throws SQLException {
        // Konto o id 12 istnieje, ale baza danych nie pozwala zaktualizować stanu, zwraca wyjątek
        // metoda paymentInWyrzuca wyjątek SQLException
        Account acc = mock(Account.class);
        when(dao.findAccountById(12)).thenReturn(acc);
        when(acc.income(100)).thenReturn(true);
        //System.out.println(dao.findAccountById(12));
        when(dao.updateAccountState(acc)).thenThrow(new SQLException(""));
        User user = null;
        assertThrows(SQLException.class,
                ()->{
                aManager.paymentIn(user, 100, "Wpłata", 12 );
        });

    }


}
