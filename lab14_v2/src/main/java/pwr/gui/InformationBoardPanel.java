package pwr.gui;

import javax.management.ObjectName;
import javax.swing.*;
import java.awt.*;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;

public class InformationBoardPanel implements InformationBoardPanelMXBean{

    private JPanel informationBoardPanel;
    private ArrayList<JTextArea> slotTextAreas;

    public InformationBoardPanel(int slots) {
        initGui(slots);
        JFrame mainGuiFrame = new JFrame();
        mainGuiFrame.setLayout(new GridLayout(1, 2));
        mainGuiFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainGuiFrame.setSize(400, 400);
        mainGuiFrame.setResizable(false);
        mainGuiFrame.add(informationBoardPanel);
        mainGuiFrame.setVisible(true);
    }

    private void initGui(int slots){
        informationBoardPanel = new JPanel();
        informationBoardPanel.setLayout(new GridLayout(1, slots + 1));
        informationBoardPanel.setSize(400, 400);
        ArrayList<JPanel> slotPanels = new ArrayList<>(slots);
        slotTextAreas = new ArrayList<>(slots);
        for(int i = 0; i < slots; i++){
            JPanel jPanel = new JPanel();
            jPanel.setLayout(new GridLayout(2, 1));
            jPanel.add(new JLabel("slot no. " + i));
            JTextArea textArea = new JTextArea("empty");
            jPanel.add(textArea);
            slotPanels.add(jPanel);
            slotTextAreas.add(textArea);
            informationBoardPanel.add(jPanel);
            informationBoardPanel.setVisible(true);
        }
    }

    public static void main(String[] args) {
        InformationBoardPanel informationBoardPanel = new InformationBoardPanel(3);
        try {
            ManagementFactory.getPlatformMBeanServer().registerMBean(informationBoardPanel, new ObjectName("pwr.gui.ksarzynski:name=" + "InformationBoardPanel"));
            Thread.currentThread().join();
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

    @Override
    public boolean addTicket(String ticket) {
        for(JTextArea textArea : slotTextAreas){
            if(textArea.getText().equals("empty")){
                textArea.setText(ticket);
                return true;
            }
        }
        return false;
    }

    @Override
    public void removeTicket(String ticket) {
        for(JTextArea textArea : slotTextAreas){
            if(textArea.getText().equals(ticket)){
                textArea.setText("empty");
            }
        }
    }

    @Override
    public void clearTextArea(String id) {
        slotTextAreas.get(Integer.parseInt(id)).setText("empty");
    }
}
