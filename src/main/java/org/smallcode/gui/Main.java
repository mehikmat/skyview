package org.smallcode.gui;

import javax.swing.*;

/**
* @author Hikmat Dhamee
* @email me.hemant.available@gmail.com
*/
public class Main {

    public Main(){
        new ESWindow();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               new Main();
            }
        });
    }
}
