package view;

import controller.AttendanceController;
import controller.Result;
import model.Attendance;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Implements the "Member Check In" use case (SRS 3.2.3). In place of a
 * physical barcode/RFID scanner (SRS 3.7 Optional Devices), the
 * receptionist types the member's username or ID - the same
 * {@link AttendanceController#checkIn(String)} entry point a real scanner
 * integration would call.
 */
public class CheckInDialog extends JDialog {

    private final AttendanceController attendanceController = new AttendanceController();
    private JTextField txtMemberId;
    private DefaultTableModel tableModel;

    public CheckInDialog(Window owner) {
        super(owner, "Member Check In", ModalityType.APPLICATION_MODAL);
        setSize(560, 420);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        JPanel top = new JPanel();
        txtMemberId = new JTextField(16);
        JButton btnCheckIn = new JButton("Check In");
        top.add(new JLabel("Username or Member ID:"));
        top.add(txtMemberId);
        top.add(btnCheckIn);
        add(top, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"Attendance ID", "Member ID", "Check-in", "Check-out"}, 0) {
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

        btnCheckIn.addActionListener(e -> doCheckIn());
        txtMemberId.addActionListener(e -> doCheckIn());
        btnClose.addActionListener(e -> dispose());

        refreshToday();
    }

    private void doCheckIn() {
        Result result = attendanceController.checkIn(txtMemberId.getText());
        JOptionPane.showMessageDialog(this, result.getMessage(),
                result.isSuccess() ? "Check-In" : "Check-In Failed",
                result.isSuccess() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
        if (result.isSuccess()) {
            txtMemberId.setText("");
        }
        refreshToday();
    }

    private void refreshToday() {
        tableModel.setRowCount(0);
        for (Attendance a : attendanceController.today()) {
            tableModel.addRow(new Object[]{a.getAttendanceId(), a.getMemberId(), a.getCheckInTime(),
                    a.getCheckOutTime() == null ? "-" : a.getCheckOutTime()});
        }
    }
}
