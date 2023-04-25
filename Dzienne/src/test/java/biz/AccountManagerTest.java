package biz;


import db.dao.DAO;
import model.Account;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.lang.reflect.Field;
import java.sql.SQLException;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;

public class AccountManagerTest {

    AccountManager aManager;

    DAO daoMock;
    AuthenticationManager authMock;
    BankHistory histMock;

    @BeforeEach
    public void TestPreparation() throws NoSuchFieldException, IllegalAccessException {
        aManager = new AccountManager();
        Field field = AccountManager.class.getDeclaredField("dao");
        field.setAccessible(true);
        daoMock = mock(DAO.class);
        field.set(aManager,daoMock);
        field.setAccessible(false);
        authMock = mock(AuthenticationManager.class);
        aManager.auth = authMock;
        histMock = mock(BankHistory.class);
        aManager.history = histMock;
    }

    /*
        wpłata na kotno o id 13, kwota 100 pln
        takie konto istnieje i ma stan 150 pln
        operacja powinna się udać, baza danych powinna być zaktualizowana
        powinno nastąpić logowanie operacji do historii
        stan końcowy konta 13 ==> 250 pln -- tego nie możemy sprawdzić, tu trzeba testów klasy Account
        operacje zostały wykonane odpowiednią ilość razy na odpowiednich obiektach
     */
    @Test
    public void TestCase1() throws SQLException {
        int accId =13;
        double amount = 100.0;
        //State preparation
        Account accountMock = mock(Account.class);
        when(daoMock.findAccountById(accId)).thenReturn(accountMock);
        when(daoMock.updateAccountState(any(Account.class))).thenReturn(true);
        when(accountMock.income(anyDouble())).thenReturn(true);
        //Do operation
        User user = new User();
        boolean rest = aManager.paymentIn(user,amount,"wpłata",accId);
        //Check outcome
        assertTrue(rest);
        verify(accountMock,times(1)).income(amount);
        verify(accountMock,times(1)).income(anyDouble());
        verify(daoMock,times(1)).updateAccountState(accountMock);
        verify(daoMock,times(1)).updateAccountState(any());
        verify(histMock,times(1)).logOperation(any(),eq(true));
    }

    /*
        wpłata na kotno o id 13, kwota 100 pln
        takie konto istnieje i ma stan 150 pln
        operacja income na koncie nie powiedzie się,
        baza danych powinna być zaktualizowana
        powinno nastąpić logowanie operacji do historii
        stan końcowy konta 13 ==> 100 pln -- tego nie możemy sprawdzić, tu trzeba testów klasy Account
        operacje zostały wykonane odpowiednią ilość razy na odpowiednich obiektach
     */
    @Test
    public void TestCase2() throws SQLException {
        int accId =13;
        double amount = 100.0;
        //State preparation
        Account accountMock = mock(Account.class);
        when(daoMock.findAccountById(accId)).thenReturn(accountMock);
        when(daoMock.updateAccountState(any(Account.class))).thenReturn(true);
        when(accountMock.income(anyDouble())).thenReturn(false);
        //Do operation
        User user = new User();
        boolean rest = aManager.paymentIn(user,amount,"wpłata",accId);
        //Check outcome
        assertFalse(rest);
        verify(accountMock,times(1)).income(amount);
        verify(accountMock,times(1)).income(anyDouble());
        verify(histMock,times(1)).logOperation(any(),eq(false));
    }
}
