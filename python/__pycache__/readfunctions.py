import os
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

#print(openfileandreturnstring("TRAAAAW128F429D538"))
# print(getallfilesinlastfmdirectory())