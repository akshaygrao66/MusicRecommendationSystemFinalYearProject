import readfunctions
import json

def obtainsimilarsarrayfromlastfm(track_id):
    lastFmJSON=readfunctions.openfileandreturnstring(track_id)
    lastFmPythonJSON = json.loads(lastFmJSON)
    return lastFmPythonJSON["similars"]


def obtainallsimilarityasdictionary(track_id):
    similarsdictionary={}
    similararray=obtainsimilarsarrayfromlastfm(track_id)
    for x in similararray:
        for i in range(len(x)):
            if i==0:
                similarsong=x[i]
            if i==1:
                similarity_score=x[i]
        similarsdictionary[similarsong] = similarity_score
    return similarsdictionary


def obtainonlyexistingsimilarsasdictionary(track_id):
    similarsdictionary = {}
    similararray = obtainsimilarsarrayfromlastfm(track_id)
    for x in similararray:
        for i in range(len(x)):
            if i == 0:
                similarsong = x[i]
            if i == 1:
                similarity_score = x[i]
        if readfunctions.checkiflastfmdirectorycontainsfile(similarsong):
            similarsdictionary[similarsong] = similarity_score
    return similarsdictionary


# print(obtainallsimilarityasdictionary("TRAAAAW128F429D538"))
# print(obtainonlyexistingsimilarsasdictionary("TRAAAAW128F429D538"))