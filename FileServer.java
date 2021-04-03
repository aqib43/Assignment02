import java.io.*;
import java.net.*;
import java.util.*;
//Main class of sSrver
public class FileServer {
	public static void main(String args[]) throws Exception {
		if(args.length == 0) {
                    //if no argument is given
			System.out.println("enter the Servers directory address as first argument while running from command line.");
		}
		else {
			int id = 1;
                        //starting the server
			System.out.println("Server started...");
			System.out.println("Waiting for connections...");

			ServerSocket welcomeSocket;

			if(args.length >= 2){
				welcomeSocket = new ServerSocket(Integer.parseInt(args[1]));
			}
                        //if not given port number then default is 1234
			else{
				welcomeSocket = new ServerSocket(1234);
			}

			while (true) {
                            //displaying the connection details
				Socket connectionSocket = welcomeSocket.accept();
				System.out.println("Client " + id + " connected from " + connectionSocket.getInetAddress().getHostName() + "...");
				Thread ClientConnectionHandler = new ThreadedServer(connectionSocket, id, args[0]);
				id++;
				ClientConnectionHandler.start();
			}
		}
	}
}

//starting the server thread

class ThreadedServer extends Thread {
	int n;
	int m;
	String name, f, ch, fileData;
	String filename;
	Socket connectionSocket;
	int counter;
	String dirName;

	public ThreadedServer(Socket s, int c, String dir) {
	//socket	
            connectionSocket = s;
		counter = c;

		//user directory
		dirName = dir;
	}

	public void run() {
		try {
                    //process WHEN  connection IS MADE
                    BufferedReader in = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			InputStream inFromClient = connectionSocket.getInputStream();
			PrintWriter outPw = new PrintWriter(connectionSocket.getOutputStream());
			OutputStream output = connectionSocket.getOutputStream();

			ObjectOutputStream oout = new ObjectOutputStream(output);
			oout.writeObject("Connected!");

			File ff = new File(dirName);
			ArrayList<String> names = new ArrayList<String>(Arrays.asList(ff.list()));
			int len = names.size();
			oout.writeObject(String.valueOf(names.size()));

			for(String name: names) {
				oout.writeObject(name);
			}

			name = in.readLine();
			ch = name.substring(0, 1);
                        //displaying the request of clients action
			if (ch.equals("*")) {
				n = name.lastIndexOf("*");
				filename = name.substring(1, n);
				FileInputStream file = null;
				BufferedInputStream bis = null;
				boolean fileExists = true;
				System.out.println("Request to download file " + filename + " recieved from " + connectionSocket.getInetAddress().getHostName() + "...");
				filename = dirName + filename;
				try {
					file = new FileInputStream(filename);
					bis = new BufferedInputStream(file);
				} 
                                //exception for file not found for action
				catch (FileNotFoundException excep) {
					fileExists = false;
					System.out.println("FileNotFoundException:" + excep.getMessage());
				}
                                //if file found then perform the action
				if (fileExists) {
					oout = new ObjectOutputStream(output);
					oout.writeObject("Success");
					System.out.println("Download begins");
					sendData(bis, output);
					System.out.println("Completed");
					bis.close();
					file.close();
					oout.close();
					output.close();
				}
				else {
					oout = new ObjectOutputStream(output);
					oout.writeObject("FileNotFound");
					bis.close();
					file.close();
					oout.close();
					output.close();
				}
			} 
                        // request of clients action
			else{
				try {
					boolean complete = true;
					System.out.println("Request to upload file " + name + " recieved from " + connectionSocket.getInetAddress().getHostName() + "...");
					File directory = new File(dirName);
					if (!directory.exists()) {
						System.out.println("Dir made");
						directory.mkdir();
					}

					int size = 9022386;
					byte[] data = new byte[size];
					File fc = new File(directory, name);
					FileOutputStream fileOut = new FileOutputStream(fc);
					DataOutputStream dataOut = new DataOutputStream(fileOut);

					while (complete) {
						m = inFromClient.read(data, 0, data.length);
						if (m == -1) {
							complete = false;
							System.out.println("Completed");
						} else {
							dataOut.write(data, 0, m);
							dataOut.flush();
						}
					}
					fileOut.close();
                                        
				} 
                                
                                //exception of action
                                catch (Exception exc) {
					System.out.println(exc.getMessage());
				}
			}
		} 
		catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
// sending the data over network
	private static void sendData(BufferedInputStream in , OutputStream out) throws Exception {
		int size = 9022386;
		byte[] data = new byte[size];
		int bytes = 0;
		int c = in .read(data, 0, data.length);
		out.write(data, 0, c);
		out.flush();
	}
}
