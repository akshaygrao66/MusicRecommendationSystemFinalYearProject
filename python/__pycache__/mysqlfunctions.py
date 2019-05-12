import mysql.connector

mydb = mysql.connector.connect(
  host="localhost",
  user="root",
  passwd="root",
  database="musixmatch_new"
)


def returntrackattributesfromdb(track_id):
    mycursor = mydb.cursor()
    querytofetchallatrributes = "SELECT * FROM allattributes where track_id=%s"
    parameters = (track_id,)
    mycursor.execute(querytofetchallatrributes,parameters)
    myresult = mycursor.fetchall()
    mycursor.close()
    return myresult

def returnalltrackidsfromdb():
    mycursor = mydb.cursor()
    querytofetchallatrributes = "SELECT track_id FROM allattributes"
    mycursor.execute(querytofetchallatrributes)
    myresult = mycursor.fetchall()
    res=[]
    for x in myresult:
        res.append(x[0])
    mycursor.close()
    return res



def returntrackattributesasdictionary(track_id):
    returndictionary={}
    rowarrayfromdb=returntrackattributesfromdb(track_id)
    if len(rowarrayfromdb) == 0:
        return returndictionary
    returndictionary['track_id']=rowarrayfromdb[0][0]
    returndictionary['mxm_tid'] = rowarrayfromdb[0][1]
    returndictionary['tempo'] = rowarrayfromdb[0][2]
    returndictionary['mode_confidence'] = rowarrayfromdb[0][3]
    returndictionary['duration'] = rowarrayfromdb[0][4]
    returndictionary['loudness'] = rowarrayfromdb[0][5]
    returndictionary['song_hottness'] = rowarrayfromdb[0][6]
    returndictionary['year'] = rowarrayfromdb[0][7]
    returndictionary['artist_familiarity'] = rowarrayfromdb[0][8]
    returndictionary['artist_name'] = rowarrayfromdb[0][9]
    returndictionary['artist_hottness'] = rowarrayfromdb[0][10]
    returndictionary['song_title'] = rowarrayfromdb[0][11]
    returndictionary['lyrics_score'] = rowarrayfromdb[0][12]
    returndictionary['artist_id'] = rowarrayfromdb[0][13]

    return returndictionary


def returnrequiredattributesasarray(inputdictionary):
    retarray = []
    retarray.append(str(inputdictionary['tempo']))
    retarray.append(inputdictionary["mode_confidence"])
    retarray.append(inputdictionary["loudness"])
    retarray.append(inputdictionary["artist_familiarity"])
    retarray.append(inputdictionary["artist_hottness"])
    retarray.append(inputdictionary["lyrics_score"])
    return retarray


def returnchangedatatributesasarray(inputdictionary):
    retarray = returnrequiredattributesasarray(inputdictionary)
    retarray[0] = round(inputdictionary['tempo'])
    retarray[1] = round(inputdictionary['mode_confidence']*100)
    retarray[2] = round(abs(inputdictionary['loudness']))
    retarray[3] = round(inputdictionary['artist_familiarity']*100)
    retarray[4] = round(inputdictionary['artist_hottness']*100)
    retarray[5] = round(inputdictionary['lyrics_score']*1000)
    return retarray



# print(returntrackattributesfromdb("TRAAABD128F429CF47"))
# print(returntrackattributesasdictionary("TRAAABD128F429CF47"))
