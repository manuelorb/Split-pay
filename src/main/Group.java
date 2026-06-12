package main;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Group {
    private ArrayList<Person> members;
    private ArrayList<Spend> SpendHistory;

    public Group() {
        this.members = new ArrayList<>();
        this.SpendHistory = new ArrayList<>();
    }

    public void addMember(Person person) {
        members.add(person);
    }

    public void addSpend(Spend spend) {
        if (!spend.isValid()) return;
        SpendHistory.add(spend);
        spend.calculateBalance();
        spend.calculateIOUS();
        spend.sendIOUS();
    }

    public void simplifyDebts() {
        ArrayList<Person> creditors = new ArrayList<>();
        ArrayList<Person> debtors = new ArrayList<>();
        ArrayList<IOU> ious = new ArrayList<>();
        Map<Person, Integer> balanceCopy = new HashMap<>();
        int c = 0, d = 0;

        // 1. Separate people into debtors and creditors
        for (Person member : members) {
            if (member.getBalance() > 0) creditors.add(member);
            if (member.getBalance() < 0) debtors.add(member);
            balanceCopy.put(member, member.getBalance());
        }

        // 2. Sort by amount to settle largest debts/credits first
        debtors.sort(Comparator.comparingInt(p -> p.getBalance()));
        creditors.sort(Comparator.comparingInt(p -> -p.getBalance()));

        // 3. Match debtors and creditors
        while (d < debtors.size() && c < creditors.size()) {
            Person debtor = debtors.get(d);
            Person creditor = creditors.get(c);

            Integer transactionAmount = Math.min(-balanceCopy.get(debtor), balanceCopy.get(creditor));

            ious.add(new IOU(creditor, debtor, transactionAmount));

            balanceCopy.put(debtor, balanceCopy.get(debtor) + transactionAmount);
            balanceCopy.put(creditor, balanceCopy.get(creditor) - transactionAmount);

            if (Math.abs(balanceCopy.get(debtor)) < 0.01) d++;
            if (Math.abs(balanceCopy.get(creditor)) < 0.01) c++;
        }

        // 4. Clear balance details of each member and add new IOUS
        for (Person person : members) {
            person.clearBalanceDetails();
        }

        // 5. Add new IOUS of each member
        for (IOU iou : ious) {
            Person creditor = iou.getCreditor();
            Person debtor = iou.getDebtor();
            
            creditor.addDetail(debtor, iou.getAmount());
            debtor.addDetail(creditor, - iou.getAmount());
        }
    }

    //GETTERS & SETTERS

    public ArrayList<Person> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<Person> persons) {
        this.members = persons;
    }

    public ArrayList<Spend> getSpendHistory() {
        return SpendHistory;
    }

    public void setSpendHistory(ArrayList<Spend> spendHistory) {
        SpendHistory = spendHistory;
    }
}
