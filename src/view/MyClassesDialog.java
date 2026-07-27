package view;

import controller.ClassController;
import controller.Result;
import model.FitnessClass;
import model.Trainer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/** Trainer feature: schedule and manage fitness classes. */
public class MyClassesDialog extends JDialog {

    private static final DateTimeFormatter INPUT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final ClassController classController = new ClassController();
    private final Trainer trainer;
    private DefaultTableModel tableModel;

    public MyClassesDialog(Window owner, Trainer trainer) {
        super(owner, "My Classes", ModalityType.APPLICATION_MODAL);
        this.trainer = trainer;
        setSize(680, 420);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        tableModel = new DefaultTableModel(new Object[]{"Class ID", "Name", "Schedule", "Capacity", "Booked"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton btnAdd = new JButton("Schedule New Class");
        JButton btnRefresh = new JButton("Refresh");
        JButton btnClose = new JButton("Close");
        JPanel buttons = new JPanel();
        buttons.add(btnAdd);
        buttons.add(btnRefresh);
        buttons.add(btnClose);
        add(buttons, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> addClass());
        btnRefresh.addActionListener(e -> refresh());
        btnClose.addActionListener(e -> dispose());

        refresh();
    }

    private void addClass() {
        if (trainer == null) {
            JOptionPane.showMessageDialog(this, "No trainer profile linked to this account.");
            return;
        }
        JTextField name = new JTextField();
        JTextField schedule = new JTextField(LocalDateTime.now().plusDays(1).withMinute(0).withSecond(0)
                .format(INPUT_FMT));
        JTextField capacity = new JTextField("20");

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Class name:"));
        panel.add(name);
        panel.add(new JLabel("Schedule (yyyy-MM-dd HH:mm):"));
        panel.add(schedule);
        panel.add(new JLabel("Max capacity:"));
        panel.add(capacity);

        int choice = JOptionPane.showConfirmDialog(this, panel, "Schedule New Class",
                JOptionPane.OK_CANCEL_OPTION);
        if (choice != JOptionPane.OK_OPTION) {
            return;
        }
        try {
            LocalDateTime time = LocalDateTime.parse(schedule.getText().trim(), INPUT_FMT);
            int cap = Integer.parseInt(capacity.getText().trim());
            Result r = classController.addClass(trainer.getTrainerId(), name.getText(), time, cap);
            JOptionPane.showMessageDialog(this, r.getMessage());
            refresh();
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Schedule must be in yyyy-MM-dd HH:mm format.",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Capacity must be a whole number.", "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refresh() {
        tableModel.setRowCount(0);
        if (trainer == null) {
            return;
        }
        for (FitnessClass fc : classController.listByTrainer(trainer.getTrainerId())) {
            tableModel.addRow(new Object[]{fc.getClassId(), fc.getClassName(), fc.getScheduleTime(),
                    fc.getMaxCapacity(), fc.getCurrentBookings()});
        }
    }
}
