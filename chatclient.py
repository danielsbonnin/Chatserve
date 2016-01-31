import socket
import argparse
import re

#Help obtained from python docs at
#docs.python.org/3/library/argparse.html



MSG_BYTE_SIZE = 1024
MSG_END = "<END>"
TYPE_MSG = "MESSAGE__"

parser = argparse.ArgumentParser(description='Handle user port input')
parser.add_argument('hostname',
                    type=str,
                    help='The server\'s hostname(default = \'localhost\')')
parser.add_argument('port', type=int, help='The server\'s port number')

args = parser.parse_args()

print("You are requesting access to the chat server at : " + args.hostname + ':' +
      str(args.port))

#get user handle
handle = input("Enter a handle: ")
if (len(handle) > 10):
    handle = handle[:10]
    print("handle cropped to 10 characters: " + handle)
    
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


while 1:
    input = ''
    while (len(input) < MSG_BYTE_SIZE):
        input += s.recv(MSG_BYTE_SIZE - len(input)).decode('utf-8')
    print(processInput(input))
    s.send(bytes(prepareOutput(s), 'UTF-8'))
input("end?")

def encode10Bytes(word):
    spaces = 10 - len(word)
    return word.append('_' * spaces)

def encodeMSG(msg):
    spaces = MSG_BYTE_SIZE - len(msg)
    return msg.append('_' * spaces
    
def processInput(data):
    servername = data[:10]
    action = data[10:20]
    msg = data[20:data.find('<END>')]
    return(servername + "> " + msg);

def prepareOutput(s):
    uinput = input(handle + '> ')
    if (uinput.find('/quit')):
        s.close()
        exit(1)
    else:
        return encodeMSG(encode10Bytes(handle) + TYPE_MESSAGE + uinput + 'MSG_END')
