package pwr.gui;

public interface InformationBoardPanelMXBean {
    boolean addTicket(String ticket);
    void removeTicket(String ticket);
    void clearTextArea(String id);
}
