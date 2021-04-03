import java.io.*;
import java.net.*;
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Arrays;
//Main class for Client
class FileClient extends JFrame implements ActionListener, MouseListener {
    
    // All the required variables definition
	JPanel panel;
	JLabel title, subT, msg, error, servFiles;
	Font font,labelfont;
	JTextField txt;
	JButton up, down;
	String dirName;
	Socket clientSocket;
	InputStream inFromServer;
	OutputStream outToServer;
	BufferedInputStream bis;
	PrintWriter pw;
	String name, file, path;
	String hostAddr;
	int portNumber;
	int c;
	int size = 9022386;
	JList<String> filelist;
	String[] names = new String[10000];
	int len; 
        //constructor
	public FileClient(String dir, String host, int port) {
	 //Title of form	
            super("Client UI");

		dirName = dir; //clients directory
                

		hostAddr = host;//clients host

		portNumber = port;//clients port number 

                
                
                //Closure
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                // Panel design and Font
		panel = new JPanel(null);

		font = new Font("Roboto", Font.BOLD, 60);
		title = new JLabel("Client UI");
		title.setFont(font);
		title.setBounds(300, 50, 400, 50);
		panel.add(title);

		labelfont = new Font("Roboto", Font.PLAIN, 20);
		subT = new JLabel("File Name :");
		subT.setFont(labelfont);
		subT.setBounds(100, 450, 200, 50);
		panel.add(subT);

		txt = new JTextField();
		txt.setBounds(400, 450, 500, 50);
		panel.add(txt);

		up = new JButton("Upload");
		up.setBounds(250, 550, 200, 50);
		panel.add(up);

		down = new JButton("Download");
		down.setBounds(550, 550, 200, 50);
		panel.add(down);

		error = new JLabel("");
		error.setFont(labelfont);
		error.setBounds(200, 650, 600, 50);
		panel.add(error);

		up.addActionListener(this);
		down.addActionListener(this);

		try {
                    //Connection establish
			clientSocket = new Socket(hostAddr, portNumber);
			inFromServer = clientSocket.getInputStream();
			pw = new PrintWriter(clientSocket.getOutputStream(), true);
			outToServer = clientSocket.getOutputStream();
			ObjectInputStream oin = new ObjectInputStream(inFromServer);
			String s = (String) oin.readObject();
			System.out.println(s);

			len = Integer.parseInt((String) oin.readObject());
			System.out.println(len);

			String[] temp_names = new String[len];

			for(int i = 0; i < len; i++) {
				String filename = (String) oin.readObject();
				System.out.println(filename);
				names[i] = filename;
				temp_names[i] = filename;
			}

			Arrays.sort(temp_names);
                        //For Displaying the files in the servers Directory
			servFiles = new JLabel("Files in the Server Directory :");
			servFiles.setBounds(350, 125, 400, 50);
			panel.add(servFiles);

			filelist = new JList<>(temp_names);
			JScrollPane scroll = new JScrollPane(filelist);
			scroll.setBounds(300, 200, 400, 200);

			panel.add(scroll);
			filelist.addMouseListener(this);

		} 
                //exception for the wrong or missing directory
		catch (Exception exc) {
			System.out.println("Exception: " + exc.getMessage());
			error.setText("Exception:" + exc.getMessage());
			error.setBounds(300,125,600,50);
			panel.revalidate();
		}

		getContentPane().add(panel);
	}
        //Mouse actions

    public void mouseClicked(MouseEvent click) {
        if (click.getClickCount() == 2) {
           String selectedItem = (String) filelist.getSelectedValue();
           txt.setText(selectedItem);
           panel.revalidate();
         }
    }

    public void mousePressed(MouseEvent e){}
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}
    public void mouseReleased(MouseEvent e){}

    //process for the upload button action
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == up) {
			try {
				name = txt.getText();

				FileInputStream file = null;
				BufferedInputStream bis = null;

				boolean fileExists = true;
				path = dirName + name;

				try {
					file = new FileInputStream(path);
					bis = new BufferedInputStream(file);
				} catch (FileNotFoundException excep) {
					fileExists = false;
					System.out.println("FileNotFoundException:" + excep.getMessage());
					error.setText("FileNotFoundException:" + excep.getMessage());
					panel.revalidate();
				}

				if (fileExists) {
					pw.println(name);

					System.out.println("Upload starts");
					error.setText("Upload starts");
					panel.revalidate();

					sendData(bis, outToServer);
					System.out.println("Completed");
					error.setText("Completed");
					panel.revalidate();

					boolean exists = false;
					for(int i = 0; i < len; i++){
						if(names[i].equals(name)){
							exists = true;
							break;
						}
					}

					if(!exists){
						names[len] = name;
						len++;
					}

					String[] temp_names = new String[len];
					for(int i = 0; i < len; i++){
						temp_names[i] = names[i];
					}

					Arrays.sort(temp_names);

					filelist.setListData(temp_names);
					bis.close();
					file.close();
					outToServer.close();
				}
			} 
                        //exception if the file not found
			catch (Exception exc) {
				System.out.println("Exception: " + exc.getMessage());
				error.setText("Exception:" + exc.getMessage());
				panel.revalidate();
			}
		}
                //process for the download action
		else if (event.getSource() == down) {
			try {
				File directory = new File(dirName);

				if (!directory.exists()) {
					directory.mkdir();
				}
				boolean complete = true;
				byte[] data = new byte[size];
				name = txt.getText();
				file = new String("*" + name + "*");
				pw.println(file); 
				ObjectInputStream oin = new ObjectInputStream(inFromServer);
				String s = (String) oin.readObject();

				if(s.equals("Success")) {
					File f = new File(directory, name);
					FileOutputStream fileOut = new FileOutputStream(f);
					DataOutputStream dataOut = new DataOutputStream(fileOut);

					while (complete) {
						c = inFromServer.read(data, 0, data.length);
						if (c == -1) {
							complete = false;
							System.out.println("Completed");
							error.setText("Completed");
							panel.revalidate();

						} else {
							dataOut.write(data, 0, c);
							dataOut.flush();
						}
					}
					fileOut.close();
				}
                                //file not found
				else {
					System.out.println("Requested file not found on the server.");
					error.setText("Requested file not found on the server.");
					panel.revalidate();
				}
			} 
                         
			catch (Exception exc) {
				System.out.println("Exception: " + exc.getMessage());
				error.setText("Exception:" + exc.getMessage());
				panel.revalidate();
			}
		}
	}
  
         // Sharing of file over the connetion
	private static void sendData(BufferedInputStream in , OutputStream out) throws Exception {
		int size = 9022386;
		byte[] data = new byte[size];
		int bytes = 0;
		int c = in.read(data, 0, data.length);
		out.write(data, 0, c);
		out.flush();
	}

	public static void main(String args[]) {
            //if client gives 3 arguments  1. directory 2. hostname 3. port number
		if(args.length >= 3){
			FileClient tcp = new FileClient(args[0], args[1], Integer.parseInt(args[2]));
			tcp.setSize(1000, 900);
			tcp.setVisible(true);
		}
                //if client gives if direcroy and hostname (deafult port is 1234)
		else if(args.length == 2){
			FileClient tcp = new FileClient(args[0], args[1], 1234);
			tcp.setSize(1000, 900);
			tcp.setVisible(true);
		}
                //if only directory default hostname is localhost and port is 1234
		else if(args.length == 1){
			FileClient tcp = new FileClient(args[0], "localhost", 1234);
			tcp.setSize(1000, 900);
			tcp.setVisible(true);
		}
		else {
                    //if directory not found
			System.out.println("Please enter the client directory address as first argument while running from command line.");
		}
	}
}
