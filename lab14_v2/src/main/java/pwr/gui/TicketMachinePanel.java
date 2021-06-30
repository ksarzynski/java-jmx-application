package pwr.gui;

import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class TicketMachinePanel {

    InformationBoardPanelMXBean proxy;
    JPanel ticketMachinePanel;
    ButtonGroup buttonGroup;
    ButtonGroup slotsGroup;
    JRadioButton ticketA;
    int counterA;
    JRadioButton ticketB;
    int counterB;
    JRadioButton ticketC;
    int counterC;
    JButton submit;
    JButton remove;
    ArrayList<JRadioButton> slotsList;
    ArrayList<String> tickets;

    public TicketMachinePanel(int slots) throws MalformedObjectNameException, InstanceNotFoundException, IOException, ReflectionException, AttributeNotFoundException, MBeanException {
        init();
        initGui(slots);
        initJMX();
        JFrame mainGuiFrame = new JFrame();
        mainGuiFrame.setLayout(new GridLayout(1, 2));
        mainGuiFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainGuiFrame.setSize(400, 400);
        mainGuiFrame.setResizable(false);
        mainGuiFrame.add(ticketMachinePanel);
        mainGuiFrame.setVisible(true);
        addingTickets();
    }

    private void init(){
        tickets = new ArrayList<>();
        counterA = 0;
        counterB = 0;
        counterC = 0;
        slotsGroup = new ButtonGroup();
        buttonGroup = new ButtonGroup();
        ticketA = new JRadioButton("A");
        ticketB = new JRadioButton("B");
        ticketC = new JRadioButton("C");
        slotsList = new ArrayList<>();
    }

    private void initGui(int slots){
        ticketMachinePanel = new JPanel();
        ticketMachinePanel.setLayout(new GridLayout(2, 1));
        ticketMachinePanel.setSize(400, 400);
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1));
        JPanel buttons = new JPanel();
        JPanel slotsPanel = new JPanel();

        for(int i = 0; i < slots; i++){
            JRadioButton radioButton = new JRadioButton(String.valueOf(i));
            slotsList.add(radioButton);
            slotsPanel.add(radioButton);
            slotsGroup.add(radioButton);
        }
        buttonGroup.add(ticketA);
        buttonGroup.add(ticketB);
        buttonGroup.add(ticketC);
        buttons.add(ticketA);
        buttons.add(ticketB);
        buttons.add(ticketC);
        JPanel actionPanel = new JPanel(new GridLayout(1, 2));
        panel.add(buttons);
        panel.add(slotsPanel);
        ticketMachinePanel.add(panel);
        submit = new JButton("submit ticket");
        submit.addActionListener(e -> addTicket());
        remove = new JButton("remove ticket");
        remove.addActionListener(e -> removeTicket());
        actionPanel.add(submit);
        actionPanel.add(remove);
        ticketMachinePanel.add(actionPanel);
    }

    private void initJMX() throws IOException, MalformedObjectNameException, AttributeNotFoundException, MBeanException, ReflectionException, InstanceNotFoundException {
        int jmxPort = 8008;
        JMXServiceURL target = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:" + jmxPort + "/jmxrmi");
        JMXConnector connector = JMXConnectorFactory.connect(target);
        MBeanServerConnection mbs = connector.getMBeanServerConnection();
        ObjectName oname = new ObjectName(ManagementFactory.RUNTIME_MXBEAN_NAME);
        String vendor = (String) mbs.getAttribute(oname, "VmVendor");
        proxy = JMX.newMXBeanProxy(mbs, new ObjectName("pwr.gui.ksarzynski:name=" + "InformationBoardPanel"),
                InformationBoardPanelMXBean.class);
    }

    public static void main(String[] args) throws MalformedObjectNameException, ReflectionException, IOException, InstanceNotFoundException, AttributeNotFoundException, MBeanException {
        new TicketMachinePanel(3);
    }

    public void addTicket() {
        StringBuilder stringBuilder = new StringBuilder();
        if(ticketA.isSelected()){
            stringBuilder.append("A").append(counterA);
            counterA++;
        }
        else if(ticketB.isSelected()){
            stringBuilder.append("B").append(counterB);
            counterB++;
        }
        else if(ticketC.isSelected()){
            stringBuilder.append("C").append(counterC);
            counterC++;
        }
        tickets.add(stringBuilder.toString());
        System.out.println("added ticket (tickets: " + tickets.size() + ")");
    }

    public void removeTicket(){
        for(JRadioButton radioButton : slotsList){
            if(radioButton.isSelected()){
                 proxy.clearTextArea(radioButton.getText());
            }
        }
    }

    public void addingTickets(){
        Thread addingTicketsThread = new Thread(() -> {
            while(true){
                System.out.println(tickets.size());
                if(tickets.size() > 0){
                    String ticket = tickets.get(0);
                    if(proxy.addTicket(ticket)){
                        tickets.remove(0);
                        Thread removingTicketsThread = new Thread(() -> {
                            try {
                                TimeUnit.SECONDS.sleep(10);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            proxy.removeTicket(ticket);
                        });
                        removingTicketsThread.start();
                    }
                }
            }
        });
        addingTicketsThread.start();
    }
}
