import socket
print("Chat client started")
#create an INET, STREAMing socket
s = socket.socket(
    socket.AF_INET, socket.SOCK_STREAM)
#now connect to the web server on port 80
# - the normal http port
s.connect(("localhost", 4444))
print(s.recv(32))
s.send("hi Server")
raw_input("end?")
