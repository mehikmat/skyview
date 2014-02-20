package org.smallcode.gui;

import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: deerwalk
 * Date: 9/10/13
 * Time: 3:08 PM
 * To change this template use File | Settings | File Templates.
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
