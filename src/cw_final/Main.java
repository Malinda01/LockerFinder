package cw_final;

/*
*
* @authors
* COHNDSE242F-015
* COHNDSE242F-016
* COHNDSE242F-017
* COHNDSE242F-018
*
*/

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = MainFrame.AppFactory.createApp();
            frame.setVisible(true);
        });
    }
}
