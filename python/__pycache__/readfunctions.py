import os
import csv
lastfmdirectory="D:\\projectdatabases\\LASTFM\\\lastfm_subset\\"

def getallfilesinlastfmdirectory():
    return os.listdir(lastfmdirectory)


def openfileandreturnstring(filename):
    lastfmpath = lastfmdirectory+filename+".json"
    f = open(lastfmpath, "r")
    ret=f.read()
    f.close()
    return ret


def checkiflastfmdirectorycontainsfile(filename):
    if filename+".json" in getallfilesinlastfmdirectory():
        return True
    return False


def readcsvfileandreturnlist(filename):
    data=[]
    with open(filename) as csvfile:
        reader = csv.reader(csvfile, delimiter=',')
        for row in reader:
           data.append(row)
    return data


#print(openfileandreturnstring("TRAAAAW128F429D538"))
# print(getallfilesinlastfmdirectory())