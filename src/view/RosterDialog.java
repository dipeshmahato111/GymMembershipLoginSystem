package view;

import controller.ClassController;
import database.UserDAO;
import model.Booking;
import model.FitnessClass;
import model.Trainer;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/** Trainer feature: view registered participants for a selected class. */
public class RosterDialog extends JDialog {

    private final ClassController classController = new ClassController();
    private final UserDAO userDAO = new UserDAO();
    private final Trainer trainer;
    private JComboBox<FitnessClass> cmbClass;
    private DefaultTableModel tableModel;

    public RosterDialog(Window owner, Trainer trainer) {
        super(owner, "Class Roster", ModalityType.APPLICATION_MODAL);
        this.trainer = trainer;
        setSize(560, 420);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        cmbClass = new JComboBox<>();
        if (trainer != null) {
            for (FitnessClass fc : classController.listByTrainer(trainer.getTrainerId())) {
                cmbClass.addItem(fc);
            }
        }
        JButton btnLoad = new JButton("Load Roster");
        JPanel top = new JPanel();
        top.add(new JLabel("Class:"));
        top.add(cmbClass);
        top.add(btnLoad);
        add(top, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"Member ID", "Name", "Email", "Booked On"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        add(new JScrollPane(new JTable(tableModel)), BorderLayout.CENTER);

        JButton btnClose = new JButton("Close");
        JPanel bottom = new JPanel();
        bottom.add(btnClose);
        add(bottom, BorderLayout.SOUTH);

        btnLoad.addActionListener(e -> loadRoster());
        btnClose.addActionListener(e -> dispose());
    }

    private void loadRoster() {
        tableModel.setRowCount(0);
        FitnessClass selected = (FitnessClass) cmbClass.getSelectedItem();
        if (selected == null) {
            return;
        }
        for (Booking b : classController.rosterForClass(selected.getClassId())) {
            try {
                User member = userDAO.findById(b.getMemberId());
                tableModel.addRow(new Object[]{b.getMemberId(),
                        member == null ? "Unknown" : member.getFullName(),
                        member == null ? "-" : member.getEmail(), b.getBookingDate()});
            } catch (Exception ex) {
                tableModel.addRow(new Object[]{b.getMemberId(), "(lookup error)", "-", b.getBookingDate()});
            }
        }
    }
}
