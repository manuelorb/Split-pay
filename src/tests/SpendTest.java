package tests;

import main.*;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

/*
Map<Person, Integer> debtors = new HashMap<>();
Map<Person, Integer> creditors = new HashMap<>();
Person ana = new Person("ana");
Person david = new Person("david");
Person manu = new Person("manu");
Person rick = new Person("rick");

creditors.put(ana, 1200);
debtors.put(rick, 0);
debtors.put(ana, 0);
debtors.put(david, 0);
Spend spend = new Spend.Builder()
    .spendID(0)
    .description("description")
    .amount(1200)
    .method(SplittingMethod.EQUAL)
    .creditors(creditors)
    .debtors(debtors)
    .build();

spend.calculateBalance();

*/

public class SpendTest {
    Map<Person, Integer> debtors = new HashMap<>();
    Map<Person, Integer> creditors = new HashMap<>();
    Person ana = new Person("ana");
    Person david = new Person("david");
    Person manu = new Person("manu");
    Person rick = new Person("rick");

    public static int addMapEntry(Map<Person, Integer> map) {
        int total = 0;
        for (Map.Entry<Person, Integer> entry : map.entrySet()) {
            total += entry.getValue();
        }
        return total;
    }

    @Test
    public void testCalculateBalanceEQUAL() {
        creditors.clear();
        debtors.clear();

        creditors.put(ana, 1200);
        debtors.put(rick, 0);
        debtors.put(ana, 0);
        debtors.put(david, 0);
        Spend spend = new Spend.Builder()
            .spendID(0)
            .description("description")
            .amount(1200)
            .method(SplittingMethod.EQUAL)
            .creditors(creditors)
            .debtors(debtors)
            .build();

        spend.calculateBalance();

        assertEquals("splitting method equal", (int) -400, (int) spend.getSpendBalance().get(rick));
        assertEquals("splitting method equal", (int) 800, (int) spend.getSpendBalance().get(ana));
        assertEquals("splitting method equal", (int) -400, (int) spend.getSpendBalance().get(david));        
    }

    @Test
    public void calculateBalancePAYOFF() {
        creditors.clear();
        debtors.clear();

        creditors.put(ana, 1200);
        debtors.put(rick, 0);
        Spend spend = new Spend.Builder()
            .spendID(0)
            .description("description for equal splitting method")
            .amount(1200)
            .method(SplittingMethod.PAYOFF)
            .creditors(creditors)
            .debtors(debtors)
            .build();

        spend.calculateBalance();

        assertEquals("Test calculateBalance for splitting method equals", (int) 1200, (int) spend.getSpendBalance().get(ana));
        assertEquals("Test calculateBalance for splitting method equals", (int) -1200, (int) spend.getSpendBalance().get(rick));
    }

    @Test
    public void testCalculateBalancePERCENTAGE() {
        creditors.clear();
        debtors.clear();

        creditors.put(manu, 1200);
        debtors.put(manu, 50);
        debtors.put(rick, 20);
        debtors.put(ana, 20);
        debtors.put(david, 10);
        Spend spend = new Spend.Builder()
            .spendID(0)
            .description("description for equal splitting method")
            .amount(1200)
            .method(SplittingMethod.PERCENTAGE)
            .creditors(creditors)
            .debtors(debtors)
            .build();

        spend.calculateBalance();

        assertEquals("Test percentage", (int) 600, (int) spend.getSpendBalance().get(manu));
        assertEquals("Test percentage", (int) -240, (int) spend.getSpendBalance().get(rick));
        assertEquals("Test percentage", (int) -240, (int) spend.getSpendBalance().get(ana));
        assertEquals("Test percentage", (int) -120, (int) spend.getSpendBalance().get(david));
    }

    @Test
    public void testCalculateBalanceUNEQUAL() {

    }

    @Test
    public void testCalculateBalanceSHARE() {

    }

    @Test
    public void testCalculateBalanceITEMIZED() {

    }

    @Test
    public void testCalculateIOUS() {

    }

    @Test
    public void testIsValid() {

    }

    @Test
    public void testRevert() {

    }

    @Test
    public void testSendIOUS() {

    }
}
