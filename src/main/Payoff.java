package main;

import java.util.ArrayList;
import java.util.HashMap;

public class Payoff extends Action {
    private Person creditor;
    private Person debtor;

    public Payoff(Builder builder) {
        setActionID(builder.actionID);
        setDescription(builder.description);
        setTotalAmount(builder.totalAmount);
        setBalance(new HashMap<>());
        setIous(new ArrayList<>());

        this.creditor = builder.creditor;
        this.debtor = builder.debtor;
    }

    public static class Builder {
        private int actionID;
        private String description;
        private Integer totalAmount;
        private Person creditor;
        private Person debtor;

        public Builder spendID(int actionID) {
            this.actionID = actionID;
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
        public Builder creditor(Person creditor) {
            this.creditor = creditor;
            return this;
        }
        public Builder debtor(Person debtor) {
            this.debtor = debtor;
            return this;
        }
        public Payoff build() {
            return new Payoff(this);
        }
    }

    @Override
    public void calculateBalance() {
        getBalance().put(creditor, getTotalAmount());
        getBalance().put(debtor, - getTotalAmount());
    }

    @Override
    public void calculateIOUS() {
        getIous().add(new IOU(creditor, debtor, getTotalAmount()));
    }

    @Override
    public boolean isValid() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isValid'");
    }

    // GETTERS & SETTERS

    public Person getCreditor() {
        return creditor;
    }

    public void setCreditor(Person creditor) {
        this.creditor = creditor;
    }

    public Person getDebtor() {
        return debtor;
    }

    public void setDebtor(Person debtor) {
        this.debtor = debtor;
    } 
}
