import mysqlfunctions
import  trainings
from sklearn.preprocessing import MinMaxScaler
import pandas as pd
import numpy as np
import readfunctions



def predictsongbetweentwosongs(songid1,songid2,knnmodel,annmodel):
    x=getattributesoftwosongs(songid1,songid2)
    # print("input is "+str(x))
    return predictusingannandknn(x,knnmodel,annmodel)


def initialiseandfitscaler():
    x = readfunctions.readcsvfileandreturnlist(
        'D:\\projectdatabases\\Numpy results\\dataPopulatedByPython(data changed).csv')
    y = readfunctions.readcsvfileandreturnlist('D:\\projectdatabases\\Numpy results\\targetPopulatedByPython.csv')
    y = np.reshape(y, (-1, 1))
    scaler = MinMaxScaler()
    scaler.fit(x)
    scaler.fit(y)
    return scaler

#
# def predictusingannandknnwithoutscaling(scaledinput,knnmodel,annmodel):
#     # predictionfromann = annmodel.predict(scaledinput)
#     predictionfromknn = knnmodel.predict(scaledinput)
#     # print("Input:"+str(scaledinput)+"ANN:"+str(predictionfromann)+"KNN:"+str(predictionfromknn))
#     finalprediction = predictionfromknn
#
#     return finalprediction


def predictusingannandknn(input,knnmodel,annmodel):
    # scaler=initialiseandfitscaler()
    # scaledinput = scaler.transform(input)
    input=np.array(input)
    input=input.astype(np.float64)
    # predictionfromann = annmodel.predict(input)
    predictionfromknn = knnmodel.predict(input)
    # print("Input:"+str(input)+"ANN:"+str(predictionfromann)+"KNN:"+str(predictionfromknn))
    finalprediction=predictionfromknn

    return finalprediction


def initializeknnmodel():
    knnmodel = trainings.trainknnandreturnmodel()
    return knnmodel


def getattributesoftwosongs(songid1,songid2):
    song1dict = mysqlfunctions.returntrackattributesasdictionary(songid1)
    song2dict = mysqlfunctions.returntrackattributesasdictionary(songid2)
    if song1dict:
        song1attributesarray = mysqlfunctions.returnrequiredattributesasarray(song1dict)
        if song1attributesarray:
            song2attributesarray = mysqlfunctions.returnrequiredattributesasarray(song2dict)
            if song2attributesarray:
                inputarray = song1attributesarray + song2attributesarray
                returnarray = []
                returnarray.append(inputarray)
                return returnarray


def predictsimilarityscoreinarray(inputsongarray):
    knnmodel = initializeknnmodel()
    annmodel = trainings.loadmodel('D:\\projectdatabases\\Numpy results\\models\\ANNr12r12r22r12r22r12r8l1mseadam200epoch100batchscore0.0358.json','D:\\projectdatabases\\Numpy results\\models\\ANNr12r12r22r12r22r12r8l1mseadam200epoch100batchscore0.0358.h5')
    resulttocsv=[]
    inp=[""]+inputsongarray
    resulttocsv.append(inp)

    # inputsarray=[]
    # for i in range(0, len(inputsongarray)):
    #     print("Taking inputs at row "+str(i))
    #     s1 = inputsongarray[i]
    #     for j in range(0, len(inputsongarray)):
    #         s2 = inputsongarray[j]
    #         inputpersong=getattributesoftwosongs(s1,s2)
    #         inputsarray.append(inputpersong[0])
    #
    # print("Starting scaling")
    # scaler=initialiseandfitscaler()
    # scaledinputarray = scaler.transform(inputsarray)
    # print("Finished scaling")

    for i in range(0,len(inputsongarray)):
        # print("=======================================================================================")
        print("==============================>   "+str(i)+"   <======================================")
        # print("=======================================================================================")
        song1 = inputsongarray[i]
        scoremappingpersong=[song1]
        for j in range(0,len(inputsongarray)):
            if i != j :
                # print("???????????????????????????>   " + str(j) + "    <????????????????????????????????????")
                song2=inputsongarray[j]
                # inp=np.array([scaledinputarray[2215*i+j]])
                scoresong1song2=predictsongbetweentwosongs(song1,song2,knnmodel,annmodel)
                # scoresong1song2 = predictusingannandknnwithoutscaling(inp,knnmodel,annmodel)
                # print("Song1:"+str(song1)+"Song2:"+str(song2)+"Score:"+str(scoresong1song2))
                scoremappingpersong.append(scoresong1song2[0][0])
            else:
                scoremappingpersong.append(0)
        resulttocsv.append(scoremappingpersong)
    print("Scoring is:"+str(resulttocsv))
    rnumpy=np.array(resulttocsv)
    pd.DataFrame(rnumpy).to_csv("D:\\projectdatabases\\Numpy results\\predictedsimilarityscorings(onlyknnbased).csv", index=False)


allsongsarray=mysqlfunctions.returnalltrackidsfromdb()
predictsimilarityscoreinarray(allsongsarray)


# dummyinput=["TRAAABD128F429CF47","TRAAAEF128F4273421","TRAAAFD128F92F423A"]
# predictsimilarityscoreinarray(dummyinput)

# print(getattributesoftwosongs("TRAAABD128F429CF47","TRAAAEF128F4273421"))