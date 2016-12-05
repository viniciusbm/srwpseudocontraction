package srwpseudocontraction.protegeplugin;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

/**
 * The panel of the graphical interface of the plug-in.
 *
 * @author Vinícius B. Matos
 */
public class MainPanel extends JPanel {

    private JPanel topPanel, settingsPanel, panelRemSize;
    private JButton btnPseudoContract;
    private JLabel lblFormula, lblQueueCapacity, lblRemSize;
    private JSpinner spQueueCapacity, spRemSize;
    private JButton btnSettingsToogle;
    @SuppressWarnings("unused")
    private JTextPane axiomInputField;

    private static final long serialVersionUID = 1L;
    private JPanel panel;
    private JPanel panel_2;
    private JPanel panel_3;
    private Component verticalGlue;
    private Component verticalGlue_1;
    private JPanel panel_1;
    private Component verticalGlue_2;
    private Component verticalGlue_3;
    private Component verticalStrut;
    private Component verticalStrut_1;
    private JPanel editorContainer;
    private JPanel panel_4;
    private Component verticalGlue_4;
    private Component verticalStrut_2;
    private Component verticalStrut_3;

    public void addInputField(JTextPane axiomInputField) {
        editorContainer.add(axiomInputField);
        this.axiomInputField = axiomInputField;
    }

    public void addButtonAction(ActionListener action) {
        btnPseudoContract.addActionListener(action);
    }

    public MainPanel() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        panel_2 = new JPanel();
        add(panel_2);
        panel_2.setLayout(new GridLayout(0, 1, 0, 0));

        panel_3 = new JPanel();
        panel_2.add(panel_3);
        panel_3.setLayout(new BorderLayout(0, 0));

        topPanel = new JPanel();
        panel_3.add(topPanel);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        verticalGlue = Box.createVerticalGlue();
        topPanel.add(verticalGlue);

        lblFormula = new JLabel("<html>Formula to be pseudo-contracted</html>");
        lblFormula.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(lblFormula);
        lblFormula.setHorizontalAlignment(SwingConstants.CENTER);
        lblFormula.setFont(new Font("Dialog", Font.BOLD, 12));

        verticalStrut_2 = Box.createVerticalStrut(20);
        verticalStrut_2.setPreferredSize(new Dimension(0, 10));
        verticalStrut_2.setMinimumSize(new Dimension(0, 10));
        topPanel.add(verticalStrut_2);

        panel_4 = new JPanel();
        topPanel.add(panel_4);
        panel_4.setLayout(new GridLayout(0, 1, 0, 0));

        editorContainer = new JPanel();
        panel_4.add(editorContainer);
        editorContainer.setLayout(new BoxLayout(editorContainer, BoxLayout.Y_AXIS));

        verticalGlue_3 = Box.createVerticalGlue();
        topPanel.add(verticalGlue_3);

        verticalGlue_4 = Box.createVerticalGlue();
        topPanel.add(verticalGlue_4);

        btnPseudoContract = new JButton("Pseudo-contract");
        btnPseudoContract.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(btnPseudoContract);

        panel_1 = new JPanel();
        topPanel.add(panel_1);
        panel_1.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));

        btnSettingsToogle = new JButton("Settings ▼");
        panel_1.add(btnSettingsToogle);
        btnSettingsToogle.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSettingsToogle.setBorderPainted(false);
        btnSettingsToogle.setHorizontalAlignment(SwingConstants.RIGHT);
        btnSettingsToogle.setFont(new Font("Dialog", Font.PLAIN, 12));
        btnSettingsToogle.setContentAreaFilled(false);
        btnSettingsToogle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                settingsPanel.setVisible(!settingsPanel.isVisible());
            }
        });

        panel = new JPanel();
        panel_2.add(panel);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        settingsPanel = new JPanel();
        panel.add(settingsPanel);
        settingsPanel.setVisible(false);
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));

        verticalStrut = Box.createVerticalStrut(20);
        verticalStrut.setPreferredSize(new Dimension(0, 10));
        settingsPanel.add(verticalStrut);

        lblRemSize = new JLabel("<html>Maximum size of the remainder set</html>");
        lblRemSize.setAlignmentX(Component.CENTER_ALIGNMENT);
        settingsPanel.add(lblRemSize);
        lblRemSize.setHorizontalAlignment(SwingConstants.CENTER);
        lblRemSize.setFont(new Font("Dialog", Font.BOLD, 12));
        lblRemSize.setPreferredSize(new Dimension(348, 15));
        lblRemSize.setMaximumSize(new Dimension(348, 15));
        lblRemSize.setMinimumSize(new Dimension(348, 15));

        verticalStrut_3 = Box.createVerticalStrut(20);
        settingsPanel.add(verticalStrut_3);

        spRemSize = new JSpinner();
        settingsPanel.add(spRemSize);
        spRemSize.setMaximumSize(new Dimension(32767, 20));
        spRemSize.setModel(new SpinnerNumberModel(new Integer(10), new Integer(1), null, new Integer(1)));

        panelRemSize = new JPanel();
        settingsPanel.add(panelRemSize);
        panelRemSize.setLayout(new BoxLayout(panelRemSize, BoxLayout.Y_AXIS));

        verticalGlue_1 = Box.createVerticalGlue();
        settingsPanel.add(verticalGlue_1);

        lblQueueCapacity = new JLabel("<html>Queue capacity (used to build the remainder set)</html>");
        settingsPanel.add(lblQueueCapacity);
        lblQueueCapacity.setHorizontalAlignment(SwingConstants.CENTER);
        lblQueueCapacity.setFont(new Font("Dialog", Font.BOLD, 12));
        lblQueueCapacity.setAlignmentX(0.5f);

        verticalStrut_1 = Box.createVerticalStrut(20);
        verticalStrut_1.setMinimumSize(new Dimension(0, 10));
        verticalStrut_1.setPreferredSize(new Dimension(0, 10));
        settingsPanel.add(verticalStrut_1);

        spQueueCapacity = new JSpinner();
        settingsPanel.add(spQueueCapacity);
        spQueueCapacity.setMaximumSize(new Dimension(32767, 20));
        spQueueCapacity.setModel(new SpinnerNumberModel(new Integer(10), new Integer(1), null, new Integer(1)));

        verticalGlue_2 = Box.createVerticalGlue();
        settingsPanel.add(verticalGlue_2);

    }

    public int getQueueCapacity() {
        return (Integer) (spQueueCapacity.getValue());
    }

    public int getMaxRemainderSize() {
        return (Integer) (spRemSize.getValue());
    }
}
