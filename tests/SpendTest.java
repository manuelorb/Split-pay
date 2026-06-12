package tests;

import splitpay.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class SpendTest {
    Group group = new Group();

    Map<Person, Integer> debtors = new HashMap<>();
    Map<Person, Integer> creditors = new HashMap<>();

    Person ana = new Person("ana");
    Person david = new Person("david");
    Person manu = new Person("manu");
    Person rick = new Person("rick");

    group.addMember(ana);
    group.addMember(david);
    group.addMember(manu);
    group.addMember(rick);

    @Test
    public void testCalculateBalance() {
        
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
