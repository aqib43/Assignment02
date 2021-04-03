Aqib Alam 100754170
Jonithan James 100343571
Project file sharing program.

a) Description:
 Clients are assigned an Id on connection. Servers shared direcetory is displayed and Client can select from them to download a file.
 Client can also upload files from Clients Directory to the server.

b)  Improvements:
    UI has been added with an updation feature that refreshes the servers directory upon a files uploading.
    Exceptions if any will also be displayed on the UI in error boxes.
    Navigating to subfolders is possible too.	

c)  How to run:
    Goto the project directory.

    Server:
   
    Run through terminal using command:
    
    @] javac FileServer.java
    
    Note: Name of Servers Shared folder must be given as an argument in command
          port number can also be given as an argument in command (default port number is 1234)     

    @] java FileServer [name of servers directory]  
    
    Client:
   
    @] javac FileClient.java
    
    Note: Name of Clients Shared folder must be given as an argument in command
          port number can also be given as an argument in command (default port number is 1234)
          host name can also be given as an argument in command (default host name is localhost)
      
    @] java FileClient [name of clients directory]  

    After this Client ui will pop up where:

    1.Uploading the file to the Server:
    Enter the file name like ( /Client1.txt ) in the text box and cick the Upload button.
    Connection is Automatically reset.

    2.Downloading the file from the Server:
    Select the file from the Servers shared Folder.  
    Enter the file name like ( /Server1.txt ) in the text box and cick the Download button.
    Connection is Automatically reset.
    
d)  Resources:
     java awt,swing
    


