package main;

import java.util.HashMap;
import java.util.Map;

public class App {
    public static void main(String[] args) throws Exception {
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

        creditors.put(rick, 1000);

        debtors.put(rick, 0);
        debtors.put(david, 0);
        debtors.put(manu, 0);

        group.addSpend(
            new Spend.Builder()
            .spendID(1)
            .amount(1000)
            .description("prueba1")
            .method(SplittingMethod.EQUAL)
            .creditors(creditors)
            .debtors(debtors)
            .build()
        );

        creditors.clear();
        debtors.clear();

        creditors.put(manu, 1000);
        creditors.put(ana, 202);

        debtors.put(rick, 0);
        debtors.put(david, 0);
        debtors.put(manu, 0);

        group.addSpend(
            new Spend.Builder()
            .spendID(2)
            .amount(1202)
            .description("prueba2")
            .method(SplittingMethod.EQUAL)
            .creditors(creditors)
            .debtors(debtors)
            .build()
        );

        printSpendHistory(group);
        printGroupStatus(group);

        group.simplifyDebts();

        printSpendHistory(group);
        printGroupStatus(group);

        System.out.println("TEST PAYOFF");

        creditors.clear();
        debtors.clear();

        creditors.put(david, 265);
        debtors.put(manu, 0);

        group.addSpend(
            new Spend.Builder()
            .spendID(2)
            .amount(265)
            .description("pruebaPAYOFF")
            .method(SplittingMethod.PAYOFF)
            .creditors(creditors)
            .debtors(debtors)
            .build()
        );

        printGroupStatus(group);
    }

    public static void printSpendHistory(Group group) {
         for (Spend s : group.getSpendHistory()) {
            for (IOU iou : s.getIous()) {
                System.out.println("Creditor: " + iou.getCreditor().getName() + " Debtor: " + iou.getDebtor().getName() + " Amount: " + iou.getAmount());
            }
        }
    }

    public static void printGroupStatus(Group group) {
        for (Person person : group.getMembers()) {
            person.print();
        }
    }
}
