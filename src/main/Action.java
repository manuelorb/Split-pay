package main;

import java.util.ArrayList;
import java.util.Map;

abstract class Action {
    private int actionID;
    private String description;
    private Integer totalAmount;
    private Map<Person, Integer> balance;
    private ArrayList<IOU> ious;

    abstract void calculateBalance();

    /**
     * this method has to calculate how much each person owe each other in this particular spend AFTER you called calculateBalance
     */
    abstract void calculateIOUS();

    /**
     * this method validate if the action is ok or not
     * it returns true when the action is valid otherwise it returns false
     * @return
     */
    abstract boolean isValid();

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
        for (IOU iou : getIous()) {
            Person creditor = iou.getCreditor();
            Person debtor = iou.getDebtor();
            
            creditor.addDetail(debtor, - iou.getAmount());
            debtor.addDetail(creditor, iou.getAmount());
        }
    }

    // GETTERS & SETTERS

    public int getActionID() {
        return actionID;
    }
    public void setActionID(int actionID) {
        this.actionID = actionID;
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
    public ArrayList<IOU> getIous() {
        return ious;
    }
    public void setIous(ArrayList<IOU> ious) {
        this.ious = ious;
    }
    public Map<Person, Integer> getBalance() {
        return balance;
    }
    public void setBalance(Map<Person, Integer> balance) {
        this.balance = balance;
    }
}
