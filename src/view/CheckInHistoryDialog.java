package view;

import controller.AttendanceController;
import model.Attendance;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/** Member "view check-in history" feature (SRS 2.4 Member View). */
public class CheckInHistoryDialog extends JDialog {

    public CheckInHistoryDialog(Window owner, User member) {
        super(owner, "Check-In History", ModalityType.APPLICATION_MODAL);
        setSize(500, 400);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        DefaultTableModel model = new DefaultTableModel(new Object[]{"Attendance ID", "Check-in", "Check-out"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        AttendanceController controller = new AttendanceController();
        for (Attendance a : controller.history(member.getUserId())) {
            model.addRow(new Object[]{a.getAttendanceId(), a.getCheckInTime(),
                    a.getCheckOutTime() == null ? "-" : a.getCheckOutTime()});
        }
        add(new JScrollPane(new JTable(model)), BorderLayout.CENTER);

        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dispose());
        JPanel buttons = new JPanel();
        buttons.add(btnClose);
        add(buttons, BorderLayout.SOUTH);
    }
}
