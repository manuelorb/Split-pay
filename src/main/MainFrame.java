package main;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;

/**
 * Main application window.
 * Uses BorderLayout for the top-level split and BoxLayout for vertical dashboard composition.
 */
public class MainFrame extends JFrame {

    private final Group group;
    private final DefaultListModel<Person> memberModel;
    private final JList<Person> membersList;
    private final JTextField memberNameField;
    private final JTextArea balanceArea;
    private final JTextArea detailsArea;
    private final JTextArea historyArea;

    public MainFrame() {
        group = new Group();
        memberModel = new DefaultListModel<>();
        membersList = new JList<>(memberModel);
        memberNameField = new JTextField();
        balanceArea = new JTextArea();
        detailsArea = new JTextArea();
        historyArea = new JTextArea();

        setTitle("Split Pay");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        configureMemberList();
        JPanel membersPanel = buildMembersPanel();
        JPanel dashboardPanel = buildDashboardPanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, membersPanel, dashboardPanel);
        splitPane.setDividerLocation(300);
        add(splitPane, BorderLayout.CENTER);

        refreshMemberControls();
        refreshDetailsPanel();
        setVisible(true);
    }

    private void configureMemberList() {
        membersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        membersList.setCellRenderer(createPersonRenderer());
        membersList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                refreshDetailsPanel();
            }
        });
    }

    private JPanel buildMembersPanel() {
        JPanel membersPanel = new JPanel(new BorderLayout(10, 10));
        membersPanel.setBorder(new EmptyBorder(10, 10, 10, 5));

        JLabel membersLabel = new JLabel("Members");
        membersLabel.setFont(membersLabel.getFont().deriveFont(Font.BOLD, 14f));
        membersPanel.add(membersLabel, BorderLayout.NORTH);

        membersPanel.add(new JScrollPane(membersList), BorderLayout.CENTER);

        JPanel addMemberPanel = new JPanel(new GridLayout(2, 2, 6, 6));
        addMemberPanel.setBorder(BorderFactory.createTitledBorder("Add member"));
        addMemberPanel.add(new JLabel("Name"));
        addMemberPanel.add(memberNameField);
        JButton addMemberButton = new JButton("Add member");
        addMemberButton.addActionListener(e -> addMember());
        addMemberPanel.add(addMemberButton);
        addMemberPanel.add(new JPanel());

        membersPanel.add(addMemberPanel, BorderLayout.SOUTH);
        return membersPanel;
    }

    private JPanel buildDashboardPanel() {
        JPanel dashboardPanel = new JPanel();
        dashboardPanel.setLayout(new BoxLayout(dashboardPanel, BoxLayout.Y_AXIS));
        dashboardPanel.setBorder(new EmptyBorder(10, 5, 10, 10));

        dashboardPanel.add(buildBalancePanel());
        dashboardPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        dashboardPanel.add(buildDetailsPanel());
        dashboardPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        dashboardPanel.add(buildHistoryPanel());
        dashboardPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        dashboardPanel.add(buildActionPanel());

        return dashboardPanel;
    }

    private JPanel buildBalancePanel() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(BorderFactory.createTitledBorder("Current balance"));

        balanceArea.setEditable(false);
        balanceArea.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
        balanceArea.setBackground(getBackground());
        panel.add(balanceArea, BorderLayout.CENTER);

        return panel;
    }

    private JPanel buildDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(BorderFactory.createTitledBorder("Member details"));

        detailsArea.setEditable(false);
        detailsArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        panel.add(new JScrollPane(detailsArea), BorderLayout.CENTER);

        return panel;
    }

    private JPanel buildHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(BorderFactory.createTitledBorder("Recent transaction history"));

        historyArea.setEditable(false);
        historyArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        panel.add(new JScrollPane(historyArea), BorderLayout.CENTER);

        return panel;
    }

    private JPanel buildActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));

        JButton revertButton = new JButton("Revert last action");
        JButton simplifyButton = new JButton("Simplify debts");
        JButton addExpenseButton = new JButton("Add expense");
        JButton payoutButton = new JButton("Record payout");

        revertButton.addActionListener(e -> revertLastAction());
        simplifyButton.addActionListener(e -> simplifyDebts());
        addExpenseButton.addActionListener(e -> openAddExpenseDialog());
        payoutButton.addActionListener(e -> openPayoffDialog());

        panel.add(revertButton);
        panel.add(simplifyButton);
        panel.add(addExpenseButton);
        panel.add(payoutButton);
        return panel;
    }

    private void addMember() {
        String name = memberNameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a member name first.");
            return;
        }
        for (Person member : group.getMembers()) {
            if (member.getName().equalsIgnoreCase(name)) {
                JOptionPane.showMessageDialog(this, "That member already exists.");
                return;
            }
        }
        Person newMember = new Person(name);
        group.addMember(newMember);
        memberNameField.setText("");
        refreshMemberControls();
        membersList.setSelectedValue(newMember, true);
        refreshDetailsPanel();
    }

    private void openAddExpenseDialog() {
        if (group.getMembers().size() < 2) {
            JOptionPane.showMessageDialog(this, "Add at least two members before creating an expense.");
            return;
        }
        AddExpenseDialog dialog = new AddExpenseDialog(this, group, spend -> {
            group.addAction(spend);
            refreshDetailsPanel();
            refreshMemberControls();
        });
        dialog.setVisible(true);
    }

    private void openPayoffDialog() {
        if (group.getMembers().size() < 2) {
            JOptionPane.showMessageDialog(this, "Add at least two members before recording a payout.");
            return;
        }
        PayoffDialog dialog = new PayoffDialog(this, group, (payer, receiver, amount) -> {
            payer.addDetail(receiver, -amount);
            receiver.addDetail(payer, amount);
            refreshDetailsPanel();
        });
        dialog.setVisible(true);
    }

    private void revertLastAction() {
        if (!group.revertLastAction()) {
            JOptionPane.showMessageDialog(this, "No action to revert.");
            return;
        }
        refreshDetailsPanel();
        refreshMemberControls();
    }

    private void simplifyDebts() {
        group.simplifyDebts();
        refreshDetailsPanel();
        refreshMemberControls();
    }

    private void refreshMemberControls() {
        memberModel.clear();
        for (Person member : group.getMembers()) {
            memberModel.addElement(member);
        }
        if (membersList.getSelectedIndex() < 0 && !group.getMembers().isEmpty()) {
            membersList.setSelectedIndex(0);
        }
    }

    private void refreshDetailsPanel() {
        Person selected = membersList.getSelectedValue();
        if (selected == null) {
            balanceArea.setText("Add a member to start tracking balances.");
            detailsArea.setText("Add a member to start tracking IOUs.");
            historyArea.setText("");
            return;
        }

        balanceArea.setText(formatBalanceText(selected));

        StringBuilder detailsBuilder = new StringBuilder();
        detailsBuilder.append(selected.getName()).append("\n\n");
        if (selected.getBalanceDetails().isEmpty()) {
            detailsBuilder.append("No outstanding IOUs.");
        } else {
            for (Map.Entry<Person, Integer> entry : selected.getBalanceDetails().entrySet()) {
                Person other = entry.getKey();
                int amount = entry.getValue();
                if (amount < 0) {
                    detailsBuilder.append(other.getName()).append(" should receive €").append(-amount).append("\n");
                } else {
                    detailsBuilder.append(other.getName()).append(" owes €").append(amount).append("\n");
                }
            }
        }
        detailsArea.setText(detailsBuilder.toString());

        StringBuilder historyBuilder = new StringBuilder();
        for (Action action : group.getHistory()) {
            historyBuilder.append(action.summary()).append("\n");
        }
        historyArea.setText(historyBuilder.toString());
    }

    private String formatBalanceText(Person selected) {
        int balance = selected.getBalance();
        if (balance < 0) {
            return " owes €" + (-balance);
        } else if (balance > 0) {
            return " is owed €" + balance;
        }
        return " is settled";
    }

    private ListCellRenderer<Object> createPersonRenderer() {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Person) {
                    Person person = (Person) value;
                    setText(person.getName() + "  •  " + formatBalanceText(person));
                }
                return this;
            }
        };
    }
}

