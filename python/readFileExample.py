def openFileAndReturnString(fileName):
    lastFmPath="D:\\\\projectdatabases\\\\LASTFM\\\\lastfm_subset\\"+fileName+".json"
    f = open(lastFmPath, "r")
    return f.read()

