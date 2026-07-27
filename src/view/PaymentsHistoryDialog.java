package view;

import controller.PaymentController;
import model.Payment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/** Admin view of all payment transactions logged by the system. */
public class PaymentsHistoryDialog extends JDialog {

    private final PaymentController paymentController = new PaymentController();

    public PaymentsHistoryDialog(Window owner) {
        super(owner, "Payments History", ModalityType.APPLICATION_MODAL);
        setSize(650, 420);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Payment ID", "Membership ID", "Amount", "Date", "Method", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        for (Payment p : paymentController.allPayments()) {
            model.addRow(new Object[]{p.getPaymentId(), p.getMembershipId(), p.getAmount(), p.getPaymentDate(),
                    p.getPaymentMethod(), p.getStatus()});
        }
        add(new JScrollPane(new JTable(model)), BorderLayout.CENTER);

        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dispose());
        JPanel buttons = new JPanel();
        buttons.add(btnClose);
        add(buttons, BorderLayout.SOUTH);
    }
}
