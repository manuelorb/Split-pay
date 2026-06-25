package tests;

import main.*;
import util.util;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

public class SpendTest {
    Map<Person, Integer> debtors = new HashMap<>();
    Map<Person, Integer> creditors = new HashMap<>();
    Person ana = new Person("ana");
    Person david = new Person("david");
    Person manu = new Person("manu");
    Person rick = new Person("rick");

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

        assertTrue("Test that total adds up", util.addMapEntrys(spend.getSpendBalance()) == 0);
        assertEquals(-400, (int) spend.getSpendBalance().get(rick));
        assertEquals(800, (int) spend.getSpendBalance().get(ana));
        assertEquals(-400, (int) spend.getSpendBalance().get(david));
        
        creditors.clear();
        debtors.clear();

        creditors.put(ana, 1202);
        debtors.put(rick, 0);
        debtors.put(ana, 0);
        debtors.put(david, 0);
        spend = new Spend.Builder()
            .spendID(0)
            .description("description")
            .amount(1202)
            .method(SplittingMethod.EQUAL)
            .creditors(creditors)
            .debtors(debtors)
            .build();

        spend.calculateBalance();

        assertTrue("Test that total adds up", util.addMapEntrys(spend.getSpendBalance()) == 0);
        assertEquals(-400, (int) spend.getSpendBalance().get(rick));
        assertEquals(801, (int) spend.getSpendBalance().get(ana));
        assertEquals(-401, (int) spend.getSpendBalance().get(david));
    }

    @Test
    public void calculateBalancePAYOFF() {
        creditors.clear();
        debtors.clear();

        creditors.put(ana, 1200);
        debtors.put(rick, 0);
        Spend spend = new Spend.Builder()
            .spendID(0)
            .description("description")
            .amount(1200)
            .method(SplittingMethod.PAYOFF)
            .creditors(creditors)
            .debtors(debtors)
            .build();

        spend.calculateBalance();

        assertTrue("Test that total adds up", util.addMapEntrys(spend.getSpendBalance()) == 0);
        assertEquals(1200, (int) spend.getSpendBalance().get(ana));
        assertEquals(-1200, (int) spend.getSpendBalance().get(rick));
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
            .description("description")
            .amount(1200)
            .method(SplittingMethod.PERCENTAGE)
            .creditors(creditors)
            .debtors(debtors)
            .build();

        spend.calculateBalance();
        assertTrue("Test that total adds up", util.addMapEntrys(spend.getSpendBalance()) == 0);
        assertEquals(600, (int) spend.getSpendBalance().get(manu));
        assertEquals(-240, (int) spend.getSpendBalance().get(rick));
        assertEquals(-240, (int) spend.getSpendBalance().get(ana));
        assertEquals(-120, (int) spend.getSpendBalance().get(david));

        creditors.clear();
        debtors.clear();

        creditors.put(manu, 1203);
        debtors.put(manu, 50);
        debtors.put(rick, 20);
        debtors.put(ana, 20);
        debtors.put(david, 10);
        spend = new Spend.Builder()
            .spendID(0)
            .description("description")
            .amount(1203)
            .method(SplittingMethod.PERCENTAGE)
            .creditors(creditors)
            .debtors(debtors)
            .build();

        spend.calculateBalance();
        assertTrue("Test that total adds up", util.addMapEntrys(spend.getSpendBalance()) == 0);
        assertEquals(601, (int) spend.getSpendBalance().get(manu));
        assertEquals(-240, (int) spend.getSpendBalance().get(rick));
        assertEquals(-241, (int) spend.getSpendBalance().get(ana));
        assertEquals(-120, (int) spend.getSpendBalance().get(david));
    }

    @Test
    public void testCalculateBalanceUNEQUAL() {
        creditors.clear();
        debtors.clear();

        creditors.put(manu, 1202);
        debtors.put(manu, 801);
        debtors.put(rick, 100);
        debtors.put(ana, 100);
        debtors.put(david, 201);
        Spend spend = new Spend.Builder()
            .spendID(0)
            .description("description")
            .amount(1202)
            .method(SplittingMethod.UNEQUAL)
            .creditors(creditors)
            .debtors(debtors)
            .build();

        spend.calculateBalance();

        assertTrue("Test that total adds up", util.addMapEntrys(spend.getSpendBalance()) == 0);
        assertEquals(401, (int) spend.getSpendBalance().get(manu));
        assertEquals(-100, (int) spend.getSpendBalance().get(rick));
        assertEquals(-100, (int) spend.getSpendBalance().get(ana));
        assertEquals(-201, (int) spend.getSpendBalance().get(david));
    }

    @Test
    public void testCalculateBalanceSHARE() {
        creditors.clear();
        debtors.clear();

        creditors.put(manu, 1202);
        debtors.put(manu, 1);
        debtors.put(rick, 3);
        debtors.put(ana, 2);
        debtors.put(david, 4);
        Spend spend = new Spend.Builder()
            .spendID(0)
            .description("description")
            .amount(1202)
            .method(SplittingMethod.SHARE)
            .creditors(creditors)
            .debtors(debtors)
            .build();

        spend.calculateBalance();
        
        assertTrue("Test that total adds up", util.addMapEntrys(spend.getSpendBalance()) == 0);
        assertEquals(1081, (int) spend.getSpendBalance().get(manu));
        assertEquals(-360, (int) spend.getSpendBalance().get(rick));
        assertEquals(-241, (int) spend.getSpendBalance().get(ana));
        assertEquals(-480, (int) spend.getSpendBalance().get(david));
    }

    @Test
    public void testCalculateBalanceITEMIZED() {
        creditors.clear();
        debtors.clear();

        creditors.put(manu, 1202);
        debtors.put(manu, 200);
        debtors.put(rick, 0);
        debtors.put(ana, 0);
        debtors.put(david, 200);
        Spend spend = new Spend.Builder()
            .spendID(0)
            .description("description")
            .amount(1202)
            .method(SplittingMethod.ITEMIZED)
            .creditors(creditors)
            .debtors(debtors)
            .build();

        spend.calculateBalance();
        
        assertTrue("Test that total adds up", util.addMapEntrys(spend.getSpendBalance()) == 0);
        assertEquals(801, (int) spend.getSpendBalance().get(manu));
        assertEquals(-200, (int) spend.getSpendBalance().get(rick));
        assertEquals(-201, (int) spend.getSpendBalance().get(ana));
        assertEquals(-400, (int) spend.getSpendBalance().get(david));
    }

    @Test
    public void testCalculateIOUS() {
        creditors.clear();
        debtors.clear();

        creditors.put(manu, 1200);
        debtors.put(manu, 200);
        debtors.put(rick, 0);
        debtors.put(ana, 0);
        debtors.put(david, 200);
        Spend spend = new Spend.Builder()
            .spendID(0)
            .description("description")
            .amount(1200)
            .method(SplittingMethod.ITEMIZED)
            .creditors(creditors)
            .debtors(debtors)
            .build();

        spend.calculateBalance();
        spend.calculateIOUS();

        assertTrue(spend.getIous().get(0).getCreditor() == manu);
        assertTrue(spend.getIous().get(0).getDebtor() == david);
        assertEquals(400, (int) spend.getIous().get(0).getAmount());

        assertTrue(spend.getIous().get(1).getCreditor() == manu);
        assertTrue(spend.getIous().get(1).getDebtor() == ana);
        assertEquals(200, (int) spend.getIous().get(1).getAmount());

        assertTrue(spend.getIous().get(2).getCreditor() == manu);
        assertTrue(spend.getIous().get(2).getDebtor() == rick);
        assertEquals(200, (int) spend.getIous().get(2).getAmount());
    }

    @Test
    public void testIsValid() {
        creditors.clear();
        debtors.clear();

        creditors.put(manu, 1200);
        debtors.put(manu, 200);
        debtors.put(rick, 0);
        debtors.put(ana, 0);
        debtors.put(david, 200);

        assertTrue("test valid spend", new Spend.Builder().spendID(0).description("description").amount(1200).method(SplittingMethod.ITEMIZED).creditors(creditors).debtors(debtors).build().isValid());

        assertFalse("test if description is null", new Spend.Builder().spendID(0).amount(1200).method(SplittingMethod.ITEMIZED).creditors(creditors).debtors(debtors).build().isValid());
        assertFalse("test if amount is null", new Spend.Builder().spendID(0).description("description").method(SplittingMethod.ITEMIZED).creditors(creditors).debtors(debtors).build().isValid());
        assertFalse("test if splitting method is null", new Spend.Builder().spendID(0).description("description").amount(1200).creditors(creditors).debtors(debtors).build().isValid());
        assertFalse("test if creditors is null", new Spend.Builder().spendID(0).description("description").amount(1200).method(SplittingMethod.ITEMIZED).debtors(debtors).build().isValid());
        assertFalse("test if debtors is null", new Spend.Builder().spendID(0).description("description").amount(1200).method(SplittingMethod.ITEMIZED).creditors(creditors).build().isValid());

        assertFalse("test if description is empty string ", new Spend.Builder().spendID(0).description("").amount(1200).method(SplittingMethod.ITEMIZED).creditors(creditors).debtors(debtors).build().isValid());
        assertFalse("test if amount is less or equal to 0 ", new Spend.Builder().spendID(0).description("description").amount(0).method(SplittingMethod.ITEMIZED).creditors(creditors).debtors(debtors).build().isValid());
        assertFalse("test if amount is less or equal to 0 ", new Spend.Builder().spendID(0).description("description").amount(-1).method(SplittingMethod.ITEMIZED).creditors(creditors).debtors(debtors).build().isValid());
        assertFalse("test if creditors are 0 ", new Spend.Builder().spendID(0).description("description").amount(1200).method(SplittingMethod.ITEMIZED).creditors(new HashMap<>()).debtors(debtors).build().isValid());
        assertFalse("test if debtors are 0 ", new Spend.Builder().spendID(0).description("description").amount(1200).method(SplittingMethod.ITEMIZED).creditors(creditors).debtors(new HashMap<>()).build().isValid());

    }

    @Test
    public void testIsValidEQUAL() {
        /*TODO*/

    }

    @Test
    public void testIsValidPAYOFF() {/*TODO*/}

    @Test
    public void testIsValidUNEQUAL() {/*TODO*/}

    @Test
    public void testIsValidPERCENTAGE() {/*TODO*/}

    @Test
    public void testIsValidITEMIZED() {/*TODO*/}

    @Test
    public void testIsValidSHARE() {/*TODO*/}

    @Test
    public void testSendIOUS() {
        creditors.clear();
        debtors.clear();

        creditors.put(manu, 1200);
        debtors.put(manu, 200);
        debtors.put(rick, 0);
        debtors.put(ana, 0);
        debtors.put(david, 200);
        Spend spend = new Spend.Builder()
            .spendID(0)
            .description("description")
            .amount(1200)
            .method(SplittingMethod.ITEMIZED)
            .creditors(creditors)
            .debtors(debtors)
            .build();

        spend.calculateBalance();
        spend.calculateIOUS();
        spend.sendIOUS();

        assertEquals(800, (int) manu.getBalance());
        assertEquals(-400, (int) david.getBalance());
        assertEquals(-200, (int) ana.getBalance());
        assertEquals(-200, (int) rick.getBalance());
    }

    @Test
    public void testRevert() {
        creditors.clear();
        debtors.clear();

        creditors.put(manu, 1200);
        debtors.put(manu, 200);
        debtors.put(rick, 0);
        debtors.put(ana, 0);
        debtors.put(david, 200);
        Spend spend = new Spend.Builder()
            .spendID(0)
            .description("description")
            .amount(1200)
            .method(SplittingMethod.ITEMIZED)
            .creditors(creditors)
            .debtors(debtors)
            .build();

        spend.calculateBalance();
        spend.calculateIOUS();
        spend.revert();

        assertEquals(0, (int) manu.getBalance());
        assertEquals(0, (int) david.getBalance());
        assertEquals(0, (int) ana.getBalance());
        assertEquals(0, (int) rick.getBalance());

    }
}
