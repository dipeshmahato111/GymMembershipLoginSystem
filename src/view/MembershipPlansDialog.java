package view;

import controller.MembershipController;
import controller.Result;
import model.MembershipTier;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Admin "Membership Plans" feature: view/add/update/delete pricing tiers.
 * Backed by the {@code membership_tiers} table so rates are configurable
 * without a rebuild (SRS 4.4 Maintainability).
 */
public class MembershipPlansDialog extends JDialog {

    private final MembershipController controller = new MembershipController();
    private DefaultTableModel tableModel;
    private JTable table;

    public MembershipPlansDialog(Window owner) {
        super(owner, "Membership Plans", ModalityType.APPLICATION_MODAL);
        setSize(500, 380);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        tableModel = new DefaultTableModel(new Object[]{"ID", "Plan Name", "Price ($)", "Duration (months)"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton btnAdd = new JButton("Add Plan");
        JButton btnEdit = new JButton("Edit Selected");
        JButton btnDelete = new JButton("Delete Selected");
        JButton btnClose = new JButton("Close");

        JPanel buttons = new JPanel();
        buttons.add(btnAdd);
        buttons.add(btnEdit);
        buttons.add(btnDelete);
        buttons.add(btnClose);
        add(buttons, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> editTier(new MembershipTier(0, "", 0, 1)));
        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Select a plan first.");
                return;
            }
            MembershipTier tier = new MembershipTier(
                    (int) tableModel.getValueAt(row, 0),
                    (String) tableModel.getValueAt(row, 1),
                    (double) tableModel.getValueAt(row, 2),
                    (int) tableModel.getValueAt(row, 3));
            editTier(tier);
        });
        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Select a plan first.");
                return;
            }
            int id = (int) tableModel.getValueAt(row, 0);
            Result r = controller.deleteTier(id);
            JOptionPane.showMessageDialog(this, r.getMessage());
            refresh();
        });
        btnClose.addActionListener(e -> dispose());

        refresh();
    }

    private void editTier(MembershipTier tier) {
        JTextField name = new JTextField(tier.getTierName());
        JTextField price = new JTextField(tier.getPrice() > 0 ? String.valueOf(tier.getPrice()) : "");
        JTextField duration = new JTextField(tier.getDurationMonths() > 0 ? String.valueOf(tier.getDurationMonths()) : "1");

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Plan name:"));
        panel.add(name);
        panel.add(new JLabel("Price ($):"));
        panel.add(price);
        panel.add(new JLabel("Duration (months):"));
        panel.add(duration);

        int result = JOptionPane.showConfirmDialog(this, panel, "Membership Plan", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }
        try {
            tier.setTierName(name.getText().trim());
            tier.setPrice(Double.parseDouble(price.getText().trim()));
            tier.setDurationMonths(Integer.parseInt(duration.getText().trim()));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Price and duration must be numeric.", "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        Result r = controller.saveTier(tier);
        JOptionPane.showMessageDialog(this, r.getMessage());
        refresh();
    }

    private void refresh() {
        tableModel.setRowCount(0);
        List<MembershipTier> tiers = controller.listTiers();
        for (MembershipTier t : tiers) {
            tableModel.addRow(new Object[]{t.getTierId(), t.getTierName(), t.getPrice(), t.getDurationMonths()});
        }
    }
}
