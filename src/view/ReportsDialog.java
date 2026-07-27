package view;

import controller.ReportController;

import javax.swing.*;
import java.awt.*;

/** Admin "Generate Reports" feature: operational summary (SRS 2.2, 4.3 performance target 10s). */
public class ReportsDialog extends JDialog {

    private final ReportController reportController = new ReportController();
    private JTextArea textArea;

    public ReportsDialog(Window owner) {
        super(owner, "Reports", ModalityType.APPLICATION_MODAL);
        setSize(520, 420);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        JButton btnRefresh = new JButton("Refresh");
        JButton btnClose = new JButton("Close");
        JPanel buttons = new JPanel();
        buttons.add(btnRefresh);
        buttons.add(btnClose);
        add(buttons, BorderLayout.SOUTH);

        btnRefresh.addActionListener(e -> loadReport());
        btnClose.addActionListener(e -> dispose());

        loadReport();
    }

    private void loadReport() {
        textArea.setText(reportController.summaryReport());
    }
}
