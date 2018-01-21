package chat;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Client extends JFrame {

    private JTextField userText;
    private JTextArea chatWindow;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private String message = "";
    private String serverIP;
    private Socket connection;

    public Client(String host, String nname) {
        super("Isi Chat Client");
        serverIP = host;
        userText = new JTextField();
        userText.setEditable(false);
        userText.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage(e.getActionCommand() , nname);
                userText.setText("");
            }
        });
        add(userText, BorderLayout.NORTH);
        chatWindow = new JTextArea();
        add(new JScrollPane(chatWindow), BorderLayout.CENTER);
        setSize(300, 150);
        setVisible(true);
    }

    public void startRunning() {
        try {
            connectToServer();
            setupStreams();
            whileChatting();

        } catch (EOFException eOFException) {
            showMessage("\n Client terminated connection");
        } catch (IOException exception) {
            exception.printStackTrace();
        } finally {
            closeCrap();
        }
    }

    private void connectToServer() throws IOException {
        showMessage("Attempting connection... \n");
        connection = new Socket(InetAddress.getByName(serverIP), 6789);
        showMessage("Connected to: " + connection.getInetAddress().getHostName());

    }

    private void setupStreams() throws IOException {
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        input = new ObjectInputStream(connection.getInputStream());
        showMessage("\nDude your streams are now good to go! \n");

    }

    private void whileChatting() throws IOException {
        ableToType(true);
        do {
            try {
                message = (String) input.readObject();
                showMessage("\n" + message);
            } catch (ClassNotFoundException e) {
                showMessage("\n I dont know that object type");
            }
        } while (!message.equalsIgnoreCase("Server - END"));
    }

    private void closeCrap() {
        showMessage("\n closing app down....");
        ableToType(false);
        try {
            output.close();
            input.close();
            connection.close();
        } catch (Exception e) {
        }
    }

    private void sendMessage(String message,String nname) {
        try {
            output.writeObject(nname + " - " + message);
            output.flush();
            showMessage("\n"+nname+"- " + message);

        } catch (IOException e) {
            chatWindow.append("\n something messed up sending message ");
        }
    }

    private void showMessage(final String m) {
        SwingUtilities.invokeLater(
                new Runnable() {
            @Override
            public void run() {
                chatWindow.append(m);

            }
        }
        );

    }

    private void ableToType(final boolean tof) {
        SwingUtilities.invokeLater(
                new Runnable() {
            @Override
            public void run() {
                userText.setEditable(tof);
            }
        }
        );
    }
}
