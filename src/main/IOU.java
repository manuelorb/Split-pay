package main;

public class IOU {
    private Person creditor;
    private Person debtor;
    private Integer amount;

    public IOU(Person creditor, Person debtor, Integer amount) {
        this.creditor = creditor;
        this.debtor = debtor;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return creditor.getName() + ", " + debtor.getName() + ", " + amount;
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

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}
