/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import javax.swing.JFrame;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 *
 * @author ismayil
 */
public class Server extends JFrame {

    private JTextField txtUser;
    private JTextArea textChatWin;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private ServerSocket sersoc;
    private Socket socket;

    public Server() {
        super("IsiChat");
        txtUser = new JTextField();
        txtUser.setEditable(false);
        txtUser.addActionListener(
                new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage(e.getActionCommand());
                txtUser.setText("");
            }
        });
        add(txtUser, BorderLayout.NORTH);
        textChatWin = new JTextArea();
        add(new JScrollPane(textChatWin));
        setSize(300, 150);
        setVisible(true);

    }

    public void startRunning() {
        try {
            sersoc = new ServerSocket(6789, 100);

            while (true) {
                try {
                    waitForConnection();
                    setupStreams();
                    whileChatting();

                } catch (EOFException eOFException) {
                    showMessage("\n Server ended the connection");
                } finally {
                    closeCrap();
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void waitForConnection() throws IOException {
        showMessage(" Waiting for someone to connect... \n");
        socket = sersoc.accept();
        showMessage(" Now accepted to " + socket.getInetAddress().getHostName());

    }

    private void setupStreams() throws IOException {
        output = new ObjectOutputStream(socket.getOutputStream());
        output.flush();
        input = new ObjectInputStream(socket.getInputStream());
        showMessage("\n Streams are now setup \n");
    }

    private void whileChatting() throws IOException {
        String message = " You are now connected ";
        showMessage(message);
        abletotype(true);
        do {
            try {
                message = (String) input.readObject();
                showMessage("\n" + message);
            } catch (ClassNotFoundException classNotFoundException) {
                showMessage("\n idk that user send");
            }
        } while (!message.equals("CLIENT- END"));
    }

    private void closeCrap() {
        showMessage("\n Closing Connections");
        abletotype(false);
        try {
            output.close();
            input.close();
            socket.close();
        } catch (Exception e) {
        }
    }

    private void sendMessage(String message) {
        try {
            output.writeObject("SERVER - " + message);
            output.flush();
            showMessage("\nSERVER - " + message);
        } catch (IOException e) {
            textChatWin.append("Nese problem oldu asagdakini developere at");
            e.printStackTrace();

        }

    }

    private void showMessage(final String text) {
        SwingUtilities.invokeLater(
                new Runnable() {
            @Override
            public void run() {
                textChatWin.append(text);
            }
        }
        );

    }
    
    private void abletotype(final boolean tof){
    SwingUtilities.invokeLater(
                new Runnable() {
            @Override
            public void run() {
                txtUser.setEditable(tof);
            }
        }
        );
    }
}
