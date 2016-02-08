file:       README.txt
author:     Daniel Bonnin
username:   bonnind
email:      bonnind@oregonstate.edu
class:      CS372
project:    1

This file describes the running of Chatserve and chatclient for project 1, 
CS372, Winter term, 2016, at Oregon State University.

There are 4 files contained in the archive file: 
    Chatserve.java
    Chatserve.bash
    chatclient.py
    README.txt

In order to run the server, ensure that Chatserve.bash and Chatserve.java
are in the same directory. The following command will compile and run the 
chat server:

    $Chatserve.bash <valid port number>

It may be necessary to add executing permissions to Chatserve.bash 

In order to run the client, ensure that chatclient.py is in the 
working directory, and type the following command:
    
    $./chatclient.py <server host name> <server port number>

In case there are issues due to different environment variables or 
permissions from those in my testing environment, you may need to 
try the full command:
    
    $python chatclient.py <server host name> <server port number>

The special command '\quit' is required to disconnect a client from
the server. This command can be used from either party.

To close the server requires a SIGINT or SIGKILL.  

After the chat client connects to the server, prompts will instruct you on
the further usage of the program. 



The testing environment I used were the flip1, flip2, and flip3 servers 
at access.engr.oregonstate.edu.


