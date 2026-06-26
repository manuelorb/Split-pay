package main;

import util.util;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Spend extends Action {
    private SplittingMethod type;
    private Map<Person, Integer> creditors;
    private Map<Person, Integer> debtors;

    public Spend(Builder builder) {
        setActionID(builder.actionID);
        setDescription(builder.description);
        setTotalAmount(builder.amount);
        setBalance(new HashMap<>());
        setIous(new ArrayList<>());

        this.type = builder.type;
        this.creditors = builder.creditors;
        this.debtors = builder.debtors;
    }

    public static class Builder {
        private int actionID;
        private String description;
        private Integer amount;
        private SplittingMethod type;
        private Map<Person, Integer> creditors;
        private Map<Person, Integer> debtors;

        public Builder spendID(int spendID) {
            this.actionID = spendID;
            return this;
        }
        public Builder description(String description) {
            this.description = description;
            return this;
        }
        public Builder amount(Integer amount) {
            this.amount = amount;
            return this;
        }
        public Builder method(SplittingMethod transactionType) {
            this.type = transactionType;
            return this;
        }
        public Builder creditors(Map<Person, Integer> creditors) {
            this.creditors = creditors;
            return this;
        }
        public Builder debtors(Map<Person, Integer> debtors) {
            this.debtors = debtors;
            return this;
        }
        public Spend build() {
            return new Spend(this);
        }
    }

    /**
     * This method choose between Splitting Methods and call "calculate Balance" for that method
     */
    public void calculateBalance() {
        switch (type) {
            case SplittingMethod.EQUAL:
                calculateBalanceEQUAL();
            break;

            case SplittingMethod.UNEQUAL:
                calculateBalanceUNEQUAL();
            break;

            case SplittingMethod.PERCENTAGE:
                calculateBalancePERCENTAGE();
            break;

            case SplittingMethod.SHARE:
                calculateBalanceSHARE();
            break;

            case SplittingMethod.ITEMIZED:
                calculateBalanceITEMIZED();
            break;

            default:
                System.out.println("Invalid Transaction");
            break;
        }
    }

    
    private void calculateBalanceITEMIZED() {
        int aux, costPerPerson, remainder, totalItemized = util.addMapEntrys(debtors);

        aux = getTotalAmount() - totalItemized;
        costPerPerson = aux / debtors.size();
        remainder = aux % debtors.size();

        for (Person debtor : debtors.keySet()) {
            getBalance().put(debtor, - (costPerPerson + debtors.get(debtor)));
        }

        addRemainderToBalance(remainder);
        addCreditToBalance();
    }


    private void calculateBalanceSHARE() {
        int sum = 0, sharePrice, totalShares = util.addMapEntrys(debtors);

        try {
            sharePrice = getTotalAmount() / totalShares;
        } catch (Exception e) {
            sharePrice = 1;
        }
        
        for (Map.Entry<Person, Integer> entry : debtors.entrySet()) {
            int n = - entry.getValue() * sharePrice;
            getBalance().put(entry.getKey(), n);
            sum += n;
        }

        addRemainderToBalance(getTotalAmount() + sum);
        addCreditToBalance();
    }


    private void calculateBalancePERCENTAGE() {
        int sum = 0;
        for (Person debtor : debtors.keySet()) {
            int n = - (getTotalAmount() * debtors.get(debtor) / 100);
            sum += n;
            getBalance().put(debtor, n);
        }

        addRemainderToBalance(getTotalAmount() + sum);
        addCreditToBalance();
    }


    private void calculateBalanceEQUAL() {
        int costPerPerson = getTotalAmount() / debtors.size();
        int remainder = getTotalAmount() % debtors.size();

        for (Person debtor : debtors.keySet()) {
            getBalance().put(debtor, - costPerPerson);
        }

        addRemainderToBalance(remainder);
        addCreditToBalance();
    }

    private void calculateBalanceUNEQUAL() {
        for (Map.Entry<Person, Integer> entry : debtors.entrySet()) {
            getBalance().put(entry.getKey(), - entry.getValue());
        }

        addCreditToBalance();
    }

    private void addRemainderToBalance(int remainder) {
        if (remainder <= 0) return;
        int d = 0;
        Object[] debtorsArray = debtors.keySet().toArray();
        while (remainder > 0) {
            Person person = (Person) debtorsArray[d % debtorsArray.length];
            getBalance().merge(person, -1, Integer::sum);
            d++;
            remainder--;
        }
    }

    private void addCreditToBalance() {
        for (Map.Entry<Person, Integer> entry : creditors.entrySet()) {
            getBalance().merge(entry.getKey(), entry.getValue(), Integer::sum);
        }
    }

    @Override
    public void calculateIOUS() {
        ArrayList<Person> persons = new ArrayList<>(getBalance().keySet());
        Map<Person, Integer> copy = new HashMap<>(getBalance());

        while (1 < persons.size()) {

            persons.sort(Comparator.comparingInt(p -> copy.get(p)));

            Person debtor = persons.getFirst();
            Person creditor = persons.getLast();

            Integer IOUAmount = Math.min(- copy.get(debtor), copy.get(creditor));

            getIous().add(new IOU(creditor, debtor, IOUAmount));

            copy.put(debtor, copy.get(debtor) + IOUAmount);
            copy.put(creditor, copy.get(creditor) - IOUAmount);

            if (Math.abs(copy.get(debtor)) < 1) persons.remove(debtor);
            if (Math.abs(copy.get(creditor)) < 1) persons.remove(creditor);
        }
    }

    
    @Override
    public boolean isValid() {
        if (
            getDescription() == null ||
            getTotalAmount() == null ||
            type == null ||
            creditors == null ||
            debtors == null
        ) return false;

        if (
            getDescription().equals("") ||
            getTotalAmount() <= 0 ||
            creditors.size() < 1 ||
            debtors.size() < 1
        ) return false;

        int checkCredit = util.addMapEntrys(creditors);
        int checkDebit = util.addMapEntrys(debtors);

        if (checkCredit != getTotalAmount()) return false;

        switch (type) {
            case SplittingMethod.UNEQUAL:
                if (checkDebit != getTotalAmount()) return false;
            break;

            case SplittingMethod.PERCENTAGE:
                if (checkDebit != 100) return false;
            break;

            case SplittingMethod.SHARE:
                if (checkDebit <= 0) return false;
            break;

            case SplittingMethod.ITEMIZED:
                if (checkDebit > getTotalAmount()) return false;
            break;

            default:
            break;
        }

        return true;
    }

    // for testing
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getActionID() + " " + getDescription() + " " + getTotalAmount() + "$\n");

        for (Person person : getBalance().keySet()) {
            sb.append(person.getName() + ": " + getBalance().get(person) + "\n");
        }

        sb.append("\n");
        return sb.toString();
    }

    // GETTERS & SETTERS

    public SplittingMethod getType() {
        return type;
    }


    public void setType(SplittingMethod type) {
        this.type = type;
    }


    public Map<Person, Integer> getCreditors() {
        return creditors;
    }


    public void setCreditors(Map<Person, Integer> creditors) {
        this.creditors = creditors;
    }


    public Map<Person, Integer> getDebtors() {
        return debtors;
    }


    public void setDebtors(Map<Person, Integer> debtors) {
        this.debtors = debtors;
    }

}
