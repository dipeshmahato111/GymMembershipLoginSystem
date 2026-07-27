package view;

import controller.BookingController;
import controller.Result;
import model.Booking;
import model.FitnessClass;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/** Implements the "Book Fitness Class" use case (SRS 3.2.4), member side. */
public class FitnessClassesDialog extends JDialog {

    private final BookingController bookingController = new BookingController();
    private final User member;
    private DefaultTableModel availableModel;
    private DefaultTableModel myBookingsModel;

    public FitnessClassesDialog(Window owner, User member) {
        super(owner, "Fitness Classes", ModalityType.APPLICATION_MODAL);
        this.member = member;
        setSize(700, 500);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        JTabbedPane tabs = new JTabbedPane();

        availableModel = new DefaultTableModel(new Object[]{"Class ID", "Name", "Trainer", "Schedule", "Seats Left"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        JTable availableTable = new JTable(availableModel);
        JPanel availablePanel = new JPanel(new BorderLayout(5, 5));
        availablePanel.add(new JScrollPane(availableTable), BorderLayout.CENTER);
        JButton btnBook = new JButton("Book Selected Class");
        JPanel bookPanel = new JPanel();
        bookPanel.add(btnBook);
        availablePanel.add(bookPanel, BorderLayout.SOUTH);
        tabs.addTab("Available Classes", availablePanel);

        myBookingsModel = new DefaultTableModel(new Object[]{"Booking ID", "Class ID", "Booked On", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        JTable myTable = new JTable(myBookingsModel);
        JPanel myPanel = new JPanel(new BorderLayout(5, 5));
        myPanel.add(new JScrollPane(myTable), BorderLayout.CENTER);
        JButton btnCancel = new JButton("Cancel Selected Booking");
        JPanel cancelPanel = new JPanel();
        cancelPanel.add(btnCancel);
        myPanel.add(cancelPanel, BorderLayout.SOUTH);
        tabs.addTab("My Bookings", myPanel);

        add(tabs, BorderLayout.CENTER);

        JButton btnClose = new JButton("Close");
        JPanel bottom = new JPanel();
        bottom.add(btnClose);
        add(bottom, BorderLayout.SOUTH);

        btnBook.addActionListener(e -> {
            int row = availableTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Select a class first.");
                return;
            }
            int classId = (int) availableModel.getValueAt(row, 0);
            Result r = bookingController.bookClass(member.getUserId(), classId);
            JOptionPane.showMessageDialog(this, r.getMessage());
            refresh();
        });

        btnCancel.addActionListener(e -> {
            int row = myTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Select a booking first.");
                return;
            }
            int bookingId = (int) myBookingsModel.getValueAt(row, 0);
            Result r = bookingController.cancelBooking(bookingId);
            JOptionPane.showMessageDialog(this, r.getMessage());
            refresh();
        });

        btnClose.addActionListener(e -> dispose());

        refresh();
    }

    private void refresh() {
        availableModel.setRowCount(0);
        for (FitnessClass fc : bookingController.listAvailableClasses()) {
            availableModel.addRow(new Object[]{fc.getClassId(), fc.getClassName(), fc.getTrainerName(),
                    fc.getScheduleTime(), fc.getSeatsAvailable()});
        }
        myBookingsModel.setRowCount(0);
        for (Booking b : bookingController.myBookings(member.getUserId())) {
            myBookingsModel.addRow(new Object[]{b.getBookingId(), b.getClassId(), b.getBookingDate(),
                    b.getStatus()});
        }
    }
}
