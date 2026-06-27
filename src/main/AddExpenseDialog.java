package main;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Modal dialog for creating a new expense with multiple creditors and debtors.
 * Uses BorderLayout for main content and GridBagLayout for compact form alignment.
 */
public class AddExpenseDialog extends JDialog {

    private final JTextField descriptionField = new JTextField();
    private final JTextField amountField = new JTextField();
    private final JComboBox<SplittingMethod> methodCombo = new JComboBox<>(new SplittingMethod[]{SplittingMethod.EQUAL, SplittingMethod.UNEQUAL, SplittingMethod.ITEMIZED, SplittingMethod.PERCENTAGE});
    private final JPanel creditorsPanel = new JPanel();
    private final JPanel debtorsPanel = new JPanel();
    private final JLabel debtorHintLabel = new JLabel();
    private final List<ParticipantRow> creditorRows = new ArrayList<>();
    private final List<ParticipantRow> debtorRows = new ArrayList<>();
    private final Group group;
    private final ExpenseSaveListener saveListener;

    public interface ExpenseSaveListener {
        void onSave(Spend spend);
    }

    public AddExpenseDialog(JFrame owner, Group group, ExpenseSaveListener listener) {
        super(owner, "Add Expense", true);
        this.group = group;
        this.saveListener = listener;
        initializeUI();
    }

    private void initializeUI() {
        setSize(760, 560);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(12, 12));
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Expense details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Description"), gbc);
        gbc.gridx = 1;
        formPanel.add(descriptionField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Total amount"), gbc);
        gbc.gridx = 1;
        formPanel.add(amountField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Split method"), gbc);
        gbc.gridx = 1;
        formPanel.add(methodCombo, gbc);

        add(formPanel, BorderLayout.NORTH);

        JPanel mainSplit = new JPanel(new GridLayout(1, 2, 10, 10));

        JPanel creditsContainer = new JPanel(new BorderLayout(10, 10));
        creditsContainer.setBorder(BorderFactory.createTitledBorder("Creditors"));
        creditorsPanel.setLayout(new BoxLayout(creditorsPanel, BoxLayout.Y_AXIS));
        creditsContainer.add(new JScrollPane(creditorsPanel), BorderLayout.CENTER);
        mainSplit.add(creditsContainer);

        JPanel debitsContainer = new JPanel(new BorderLayout(10, 10));
        debitsContainer.setBorder(BorderFactory.createTitledBorder("Debtors"));
        debitsContainer.add(debtorHintLabel, BorderLayout.NORTH);
        debtorsPanel.setLayout(new BoxLayout(debtorsPanel, BoxLayout.Y_AXIS));
        debitsContainer.add(new JScrollPane(debtorsPanel), BorderLayout.CENTER);
        mainSplit.add(debitsContainer);

        add(mainSplit, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        JButton createButton = new JButton("Create Expense");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(cancelButton);
        buttonPanel.add(createButton);
        add(buttonPanel, BorderLayout.SOUTH);

        methodCombo.addActionListener(e -> updateDebtorHints());
        createButton.addActionListener(e -> createExpense());
        cancelButton.addActionListener(e -> dispose());

        buildParticipantRows();
        updateDebtorHints();
    }

    private void buildParticipantRows() {
        creditorsPanel.removeAll();
        debtorsPanel.removeAll();
        creditorRows.clear();
        debtorRows.clear();

        for (Person member : group.getMembers()) {
            creditorRows.add(createParticipantRow(member, true));
            debtorRows.add(createParticipantRow(member, false));
        }
    }

    private ParticipantRow createParticipantRow(Person member, boolean creditorMode) {
        ParticipantRow row = new ParticipantRow();
        row.person = member;
        row.checkBox = new JCheckBox(member.getName());
        row.amountField = new JTextField(8);
        row.amountField.setMaximumSize(new Dimension(Integer.MAX_VALUE, row.amountField.getPreferredSize().height));
        row.amountField.setText("0");

        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        rowPanel.add(row.checkBox);
        rowPanel.add(new JLabel(creditorMode ? "paid" : "owes"));
        rowPanel.add(row.amountField);
        row.amountField.setEnabled(creditorMode || methodCombo.getSelectedItem() != SplittingMethod.EQUAL);
        row.amountField.setToolTipText("Enter a value for this participant");

        if (creditorMode) {
            creditorsPanel.add(rowPanel);
        } else {
            debtorsPanel.add(rowPanel);
        }

        return row;
    }

    private void updateDebtorHints() {
        SplittingMethod method = (SplittingMethod) methodCombo.getSelectedItem();
        switch (method) {
            case UNEQUAL -> debtorHintLabel.setText("Select debtors and enter their owed amounts.");
            case ITEMIZED -> debtorHintLabel.setText("Select debtors and enter itemized amounts.");
            case PERCENTAGE -> debtorHintLabel.setText("Select debtors and enter percentage shares.");
            default -> debtorHintLabel.setText("Select debtors. Amount fields are ignored for Equal split.");
        }

        for (ParticipantRow row : debtorRows) {
            row.amountField.setEnabled(method != SplittingMethod.EQUAL);
        }

        revalidate();
        repaint();
    }

    private void createExpense() {
        String description = descriptionField.getText().trim();
        if (description.isEmpty()) {
            description = "Expense";
        }

        int totalAmount;
        try {
            totalAmount = Integer.parseInt(amountField.getText().trim());
            if (totalAmount <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid total amount.");
            return;
        }

        Map<Person, Integer> creditors = new HashMap<>();
        for (ParticipantRow row : creditorRows) {
            if (row.checkBox.isSelected()) {
                try {
                    int value = Integer.parseInt(row.amountField.getText().trim());
                    creditors.put(row.person, value);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Enter valid amounts for selected creditors.");
                    return;
                }
            }
        }

        if (creditors.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select at least one creditor.");
            return;
        }

        Map<Person, Integer> debtors = new HashMap<>();
        SplittingMethod method = (SplittingMethod) methodCombo.getSelectedItem();
        for (ParticipantRow row : debtorRows) {
            if (row.checkBox.isSelected()) {
                int value = 0;
                if (method != SplittingMethod.EQUAL) {
                    try {
                        value = Integer.parseInt(row.amountField.getText().trim());
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Enter valid values for selected debtors.");
                        return;
                    }
                }
                debtors.put(row.person, value);
            }
        }

        if (debtors.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select at least one debtor.");
            return;
        }

        int creditorsTotal = creditors.values().stream().mapToInt(Integer::intValue).sum();
        if (creditorsTotal != totalAmount) {
            JOptionPane.showMessageDialog(this, "Creditor amounts must add up to the total amount.");
            return;
        }

        Spend spend = new Spend.Builder()
                .spendID(group.getHistory().size())
                .description(description)
                .amount(totalAmount)
                .method(method)
                .creditors(creditors)
                .debtors(debtors)
                .build();

        if (!spend.isValid()) {
            JOptionPane.showMessageDialog(this, "The split is not valid for the selected method.");
            return;
        }

        saveListener.onSave(spend);
        dispose();
    }

    private static class ParticipantRow {
        private Person person;
        private JCheckBox checkBox;
        private JTextField amountField;
    }
}
