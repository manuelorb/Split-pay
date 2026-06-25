package main;

import util.util;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Spend {
    private int spendID;
    private String description;
    private Integer totalAmount;
    private SplittingMethod type;
    private Map<Person, Integer> creditors;
    private Map<Person, Integer> debtors;
    private Map<Person, Integer> spendBalance;
    private ArrayList<IOU> ious;

    public Spend(Builder builder) {
        this.spendID = builder.spendID;
        this.description = builder.description;
        this.totalAmount = builder.totalAmount;
        this.type = builder.method;
        this.creditors = builder.creditors;
        this.debtors = builder.debtors;
        this.spendBalance = new HashMap<>();
        this.ious = new ArrayList<>();
    }

    public static class Builder {
        private int spendID;
        private String description;
        private Integer totalAmount;
        private SplittingMethod method;
        private Map<Person, Integer> creditors;
        private Map<Person, Integer> debtors;

        public Builder spendID(int spendID) {
            this.spendID = spendID;
            return this;
        }
        public Builder description(String description) {
            this.description = description;
            return this;
        }
        public Builder amount(Integer totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }
        public Builder method(SplittingMethod transactionType) {
            this.method = transactionType;
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
            case SplittingMethod.PAYOFF:
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

        aux = totalAmount - totalItemized;
        costPerPerson = aux / debtors.size();
        remainder = aux % debtors.size();

        for (Person debtor : debtors.keySet()) {
            spendBalance.put(debtor, - (costPerPerson + debtors.get(debtor)));
        }

        addRemainderToBalance(remainder);
        addCreditToBalance();
    }


    private void calculateBalanceSHARE() {
        int sum = 0, sharePrice, totalShares = util.addMapEntrys(debtors);

        try {
            sharePrice = totalAmount / totalShares;
        } catch (Exception e) {
            sharePrice = 1;
        }
        
        for (Map.Entry<Person, Integer> entry : debtors.entrySet()) {
            int n = - entry.getValue() * sharePrice;
            spendBalance.put(entry.getKey(), n);
            sum += n;
        }

        addRemainderToBalance(totalAmount + sum);
        addCreditToBalance();
    }


    private void calculateBalancePERCENTAGE() {
        int sum = 0;
        for (Person debtor : debtors.keySet()) {
            int n = - (totalAmount * debtors.get(debtor) / 100);
            sum += n;
            spendBalance.put(debtor, n);
        }

        addRemainderToBalance(totalAmount + sum);
        addCreditToBalance();
    }


    private void calculateBalanceEQUAL() {
        int costPerPerson = totalAmount / debtors.size();
        int remainder = totalAmount % debtors.size();

        for (Person debtor : debtors.keySet()) {
            spendBalance.put(debtor, - costPerPerson);
        }

        addRemainderToBalance(remainder);
        addCreditToBalance();
    }

    private void calculateBalanceUNEQUAL() {
        for (Map.Entry<Person, Integer> entry : debtors.entrySet()) {
            spendBalance.put(entry.getKey(), - entry.getValue());
        }

        addCreditToBalance();
    }

    private void addRemainderToBalance(int remainder) {
        if (remainder <= 0) return;
        int d = 0;
        Object[] debtorsArray = debtors.keySet().toArray();
        while (remainder > 0) {
            Person person = (Person) debtorsArray[d % debtorsArray.length];
            spendBalance.merge(person, -1, Integer::sum);
            d++;
            remainder--;
        }
    }

    private void addCreditToBalance() {
        for (Map.Entry<Person, Integer> entry : creditors.entrySet()) {
            spendBalance.merge(entry.getKey(), entry.getValue(), Integer::sum);
        }
    }

    /**
     * this method has to calculate how much each person owe each other in this particular spend AFTER you called calculateBalance
     */
    public void calculateIOUS() {
        ArrayList<Person> persons = new ArrayList<>(spendBalance.keySet());
        Map<Person, Integer> copy = new HashMap<>(spendBalance);

        while (1 < persons.size()) {

            persons.sort(Comparator.comparingInt(p -> copy.get(p)));

            Person debtor = persons.getFirst();
            Person creditor = persons.getLast();

            Integer IOUAmount = Math.min(- copy.get(debtor), copy.get(creditor));

            ious.add(new IOU(creditor, debtor, IOUAmount));

            copy.put(debtor, copy.get(debtor) + IOUAmount);
            copy.put(creditor, copy.get(creditor) - IOUAmount);

            if (Math.abs(copy.get(debtor)) < 1) persons.remove(debtor);
            if (Math.abs(copy.get(creditor)) < 1) persons.remove(creditor);
        }
    }

    /**
     * this method Apply the IOUS to each persons balance details
     */
    public void sendIOUS() {
        for (IOU iou : ious) {
            Person creditor = iou.getCreditor();
            Person debtor = iou.getDebtor();
            
            creditor.addDetail(debtor, iou.getAmount());
            debtor.addDetail(creditor, - iou.getAmount());
        }
    }

    /**
     * UnApply the IOUS balance, basicly an "undo" button
     * It's used when the transaction gets deleted or updated
     */
    public void revert() {
        for (IOU iou : ious) {
            Person creditor = iou.getCreditor();
            Person debtor = iou.getDebtor();
            
            creditor.addDetail(debtor, - iou.getAmount());
            debtor.addDetail(creditor, iou.getAmount());
        }
    }

    /**
     * this method validate if the transaction is ok or not
     * it returns true when the transaction is valid otherwise it returns false
     * @return
     */
    public boolean isValid() {
        int checkCredit = util.addMapEntrys(creditors);
        int checkDebit = util.addMapEntrys(debtors);

        if (
            description == null ||
            totalAmount == null ||
            type == null ||
            creditors == null ||
            debtors == null
        ) return false;

        if (
            description.equals("") ||
            totalAmount <= 0 ||
            creditors.size() < 1 ||
            debtors.size() < 1
        ) return false;

        if (checkCredit != totalAmount) return false;

        switch (type) {
            case SplittingMethod.PAYOFF:
                if (creditors.size() != 1 || debtors.size() != 1) return false;
            break;

            case SplittingMethod.UNEQUAL:
                if (checkDebit != totalAmount) return false;
            break;

            case SplittingMethod.PERCENTAGE:
                if (checkDebit != 100) return false;
            break;

            case SplittingMethod.SHARE:
                if (checkDebit <= 0) return false;
            break;

            case SplittingMethod.ITEMIZED:
                if (checkDebit > totalAmount) return false;
            break;

            default:
            break;
        }

        return true;
    }

    // for testing
    public void print() {
        System.out.println("\nBALANCE: ");
        for (Person person : spendBalance.keySet()) {
            System.out.println(person.getName() + ": " + spendBalance.get(person));
        }
    }


    //GETTERS & SETTERS

    public int getSpendID() {
        return spendID;
    }


    public void setSpendID(int transactionID) {
        this.spendID = transactionID;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public Integer getTotalAmount() {
        return totalAmount;
    }


    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }


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


    public Map<Person, Integer> getSpendBalance() {
        return spendBalance;
    }


    public void setSpendBalance(Map<Person, Integer> transactionBalance) {
        this.spendBalance = transactionBalance;
    }


    public ArrayList<IOU> getIous() {
        return ious;
    }


    public void setIous(ArrayList<IOU> ious) {
        this.ious = ious;
    }

    
}
