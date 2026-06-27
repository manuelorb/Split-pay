package main;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Modal dialog for recording a payout repayment between two members.
 * Uses GridLayout for consistent data entry fields.
 */
public class PayoffDialog extends JDialog {

    private final JComboBox<Person> payerCombo;
    private final JComboBox<Person> receiverCombo;
    private final JTextField amountField = new JTextField();
    private final PayoffListener listener;

    public interface PayoffListener {
        void onPayoff(Person payer, Person receiver, int amount);
    }

    public PayoffDialog(JFrame owner, Group group, PayoffListener listener) {
        super(owner, "Record Payout", true);
        this.listener = listener;

        payerCombo = new JComboBox<>(group.getMembers().toArray(new Person[0]));
        receiverCombo = new JComboBox<>(group.getMembers().toArray(new Person[0]));

        initializeUI();
    }

    private void initializeUI() {
        setSize(420, 260);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(12, 12));
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.add(new JLabel("Payer"));
        formPanel.add(payerCombo);
        formPanel.add(new JLabel("Receiver"));
        formPanel.add(receiverCombo);
        formPanel.add(new JLabel("Amount"));
        formPanel.add(amountField);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton cancelButton = new JButton("Cancel");
        JButton saveButton = new JButton("Record payout");
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        add(buttonPanel, BorderLayout.SOUTH);

        cancelButton.addActionListener(e -> dispose());
        saveButton.addActionListener(e -> recordPayoff());
    }

    private void recordPayoff() {
        Person payer = (Person) payerCombo.getSelectedItem();
        Person receiver = (Person) receiverCombo.getSelectedItem();

        if (payer == null || receiver == null || payer == receiver) {
            JOptionPane.showMessageDialog(this, "Choose two different members.");
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(amountField.getText().trim());
            if (amount <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a valid positive amount.");
            return;
        }

        listener.onPayoff(payer, receiver, amount);
        dispose();
    }
}
