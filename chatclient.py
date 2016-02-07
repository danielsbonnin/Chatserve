import socket
import argparse
import re
import errno
import signal

MSG_BYTE_SIZE = 50
MSG_END = '<END>'

def exitCleanly():
    print("handler")
    try:
        print("bye")
        s.close()
    except Exception as e:
        print("bye")
        exit(1)



def main():
    signal.signal(signal.SIGTERM, exitCleanly)
    signal.signal(signal.SIGINT, exitCleanly)
    args = cliHandler()

    print("You are requesting access to the chat server at : " + args.hostname + ':' +
          str(args.port))

    #get user handle
    handle = getHandle()

    #Create tcp connection with server
    #Creat a tcp socket
    s = socket.socket(
        socket.AF_INET, socket.SOCK_STREAM)

    initContact(args, s)
    


    processInput(s)
    while 1:
        prepareOutput(s, handle)
        processInput(s)
        
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

def initContact(args, s):

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

def encodeMSG(msg):
    #spaces = MSG_BYTE_SIZE - len(msg)
    #msg += '_' * spaces
    return msg.encode('utf-8')
    
def processInput(s):
    try:
        data = s.recv(1024)
    except socket.error as e:
        if (e.errno == errno.ECONNRESET or
            e.errno == errno.ECONNABORTED):
            print("server disconnected")
            s.close()
            exit(1)
      
    data = data.decode('utf-8')
    if len(data) < 10:
        print("server disconnected")
        s.close()
        exit(1)
    servername = data[:10]
    msg = data[10:data.find(MSG_END)]
    print(servername.replace('_', '') + "> " + msg);

def prepareOutput(s, handle):
    uinput = input(handle.replace('_', '') + '> ')
    if (uinput.find('\quit') != -1):
        s.close()
        exit(1)
    else:
        try:
            s.sendall(encodeMSG(handle + uinput + MSG_END + "\n"))
        except socket.error as e:
            if (e.errno == errno.ECONNRESET or e.errno == errno.ECONNABORTED):
                print("server disconnected")
                s.close()
                exit(1)
    
if __name__ == "__main__":
    main()
    

