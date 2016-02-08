#! /bin/bash
#Chatserve.bash
#Author: Daniel Bonnin
#Email: bonnind@oregonstate.edu
#This file compiles and executes Chatserve.java.
#The command line argument is passed through to Chatserve.
#Refer to Chatserve.java for documentation. 
javac Chatserve.java
java -cp . Chatserve $1
