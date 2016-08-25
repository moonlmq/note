import cv2
  
import os  
import sys  
  
out = sys.stdout  
sys.stdout = open("cv2.txt", "w")  
help(cv2)  
sys.stdout.close()  
sys.stdout = out  