package main;

import java.util.HashMap;
import java.util.Map;

public class Person {
    private String name;
    private Integer balance;
    private Map<Person, Integer> balanceDetails;

    public Person(String name) {
        this.name = name;
        this.balance = 0;
        this.balanceDetails = new HashMap<>();
    }

    /**
     * Adds a balance detail without changing the balance (used internaly and on simplifyDebts)
     * @param person
     * @param amount
     */
    public void addDetailWithoutChangingBalance(Person person, Integer amount) {
        balanceDetails.merge(person, amount, Integer::sum);
        if(balanceDetails.get(person) == 0) balanceDetails.remove(person);
    }

    /**
     * Adds a balance detail AND change the balance (used when addSpend is called)
     * @param person
     * @param amount
     */
    public void addDetail(Person person, Integer amount) {
        addDetailWithoutChangingBalance(person, amount);
        balance += amount;
    }

    /**
     * Erase all the balance detail map
     */
    public void clearBalanceDetails() {
        balanceDetails.clear();
    }

    // Print for testing
    @Override
    public String toString() {
        return name;
    }

    // GETTERS & SETTERS

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public Map<Person, Integer> getBalanceDetails() {
        return balanceDetails;
    }

    public void setBalanceDetails(Map<Person, Integer> balanceDetails) {
        this.balanceDetails = balanceDetails;
    }
}
