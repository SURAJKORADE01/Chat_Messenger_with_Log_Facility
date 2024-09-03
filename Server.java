import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class Server extends Frame implements ActionListener 
{
    TextField typeMessage;           
    TextArea displayChat;               
    Button btnSend;                 
    ServerSocket serverSocket;    
    Socket socket;                
    DataInputStream input;          
    DataOutputStream output;      
    BufferedWriter logWriter;      

    public Server() 
    {
        setLayout(new FlowLayout());        

        typeMessage = new TextField(30);
        displayChat = new TextArea(10, 40);
        btnSend = new Button("Send");      

        add(new Label("Server"));
        add(typeMessage);
        add(btnSend);
        add(displayChat);

        btnSend.addActionListener(this);   

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                closeConnection();
                System.exit(0);
            }
        });

        try 
        {
            serverSocket = new ServerSocket(5000);      
            socket = serverSocket.accept();             
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
            logWriter = new BufferedWriter(new FileWriter("ServerLog.txt", true));

            new Thread(() -> {
                try {
                    while (true) 
                    {
                        String message = input.readUTF();   
                        displayChat.append("Client: " + message + "\n");
                        logMessage("Client: " + message);
                        if (message.equalsIgnoreCase("exit")) 
                        {
                            closeConnection();
                            System.exit(0);
                        }
                    }
                } 
                catch (IOException e) 
                {
                    e.printStackTrace();
                }
            }).start();
        } 
        catch (IOException e) {
            e.printStackTrace();
        }

        setSize(500, 300);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        try 
        {
            String message = typeMessage.getText();
            displayChat.append("Server: " + message + "\n");
            output.writeUTF(message);
            logMessage("Server: " + message);
            if (message.equalsIgnoreCase("exit"))   
            {
                closeConnection();
                System.exit(0);
            }
            typeMessage.setText("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void logMessage(String message) {
        try {
            logWriter.write(message + "\n");
            logWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeConnection() {
        try {
            if (logWriter != null) logWriter.close();
            if (socket != null) socket.close();
            if (serverSocket != null) serverSocket.close();
            if (input != null) input.close();
            if (output != null) output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Server();
    }
}
