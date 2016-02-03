import socket
import argparse
import re

MSG_BYTE_SIZE = 50
MSG_END = "<END>"

def main():
    args = cliHandler()

    print("You are requesting access to the chat server at : " + args.hostname + ':' +
          str(args.port))

    #get user handle
    handle = getHandle()

    #Create tcp connection with server
    s = initContact(args)

    while 1:
        s.send(prepareOutput(s, handle))
        print(processInput(s.recv(), s))
        
    exit(1)
    
def cliHandler():
    #Help obtained from python docs at
    #docs.python.org/3/library/argparse.html
    parser = argparse.ArgumentParser(description='Handle user port input')
    parser.add_argument('hostname',
                        type=str,
                        help='The server\'s hostname(default = \'localhost\')')
    parser.add_argument('port', type=int, help='The server\'s port number')
    return parser.parse_args()

def getHandle():
    name = input("Enter a handle: ")
    #ensure handle is 10 characters long
    if (len(name) > 10):
        name = name[:10]
        print("handle cropped to 10 characters: " + name)
    elif (len(name) < 10):
        pad = 10 - len(name)
        name += '_' * pad
    return name

def initContact(args):
    #create an INET, STREAMing socket
    s = socket.socket(
        socket.AF_INET, socket.SOCK_STREAM)

    #This error handling code heavily influenced by the accepted answer here:
    #stackoverflow.com/questions/177389/testing-socket-connection-in-python
    try:
        s.connect((args.hostname, args.port))
    except Exception as e:
        print(args.hostname + ':' +
              str(args.port) +
              ' does not seem to be responding.')
        #print(e)
        s.close()
        exit(1)
    return s

def encodeMSG(msg):
    spaces = MSG_BYTE_SIZE - len(msg)
    return msg.append('_' * spaces)
    
def processInput(data, s):              
    servername = data[:10]
    msg = data[10:data.find(MSG_END)]
    if (msg.find('/quit')):
        s.close()
        return(servername + ' has quit the conversation')
    return(servername.replace('_', '') + "> " + msg);

def prepareOutput(s, handle):
    uinput = input(handle.replace('_', '') + '> ')
    if (uinput.find('/quit')):
        s.close()
        exit(1)
    else:
        return encodeMSG(handle + uinput + MSG_END)

    
if __name__ == "__main__":
    main()
    

