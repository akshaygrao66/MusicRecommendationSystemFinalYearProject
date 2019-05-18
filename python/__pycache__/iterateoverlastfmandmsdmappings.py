import mysqlfunctions
import obtainSimilarityFromLastFM
import readfunctions
import numpy
import pandas as pd

def iterateoverallmappings():
    mapping=0
    count=0
    # Get all files in LastFM directory
    allfilesinlastfm=readfunctions.getallfilesinlastfmdirectory()
    # For each file in directory do

    datalist=[]
    targetlist=[]
    datatargetlist=[]
    for eachlastfmfile in allfilesinlastfm:
        count=count+1
        print("Root:"+str(eachlastfmfile))
        print(count)
        # if count > 4:
        #     break
        # Removing extensions
        eachlastfmfile=eachlastfmfile.rsplit(".", 1)[0]
        rootsongattributesdict=mysqlfunctions.returntrackattributesasdictionary(eachlastfmfile)
        # if dictionary is not empty(implies lastfm song exists in msd and musixmatch)
        if rootsongattributesdict:
            dataroot=mysqlfunctions.returnchangedatatributesasarray(rootsongattributesdict)
            similarsexistingdictionary = obtainSimilarityFromLastFM.obtainallsimilarityasdictionary(eachlastfmfile)
            if similarsexistingdictionary:
                for eachsimilarsong in similarsexistingdictionary:
                    similarsongattributesdict=mysqlfunctions.returntrackattributesasdictionary(eachsimilarsong)
                    if similarsongattributesdict:
                        mapping=mapping+1
                        datasimilar = mysqlfunctions.returnchangedatatributesasarray(similarsongattributesdict)
                        finaldata=dataroot+datasimilar
                        target=round(similarsexistingdictionary[eachsimilarsong]*10000)
                        datatargetrow=finaldata + [target]
                        print("Similar song:"+str(similarsongattributesdict['track_id']))
                        print("Data:"+str(finaldata))
                        print("Target:"+str(target))
                        datalist.append(finaldata)
                        targetlist.append(target)
                        datatargetlist.append(datatargetrow)
    datatargetnumpyarray = numpy.array(datatargetlist)
    datanumpyarray = numpy.array(datalist)
    targetnumpyarray = numpy.array(targetlist)
    # print("Data numpy"+str(datanumpyarray))
    # print("Target numpy"+str(targetnumpyarray))
    # numpy.savetxt("D:\\projectdatabases\\Numpy results\\data.csv", numpy.asarray(datanumpyarray), delimiter=",")
    pd.DataFrame(datanumpyarray).to_csv("D:\\projectdatabases\\Numpy results\\dataPopulatedByPython(datatargetschanged).csv",index=False)
    pd.DataFrame(targetnumpyarray).to_csv("D:\\projectdatabases\\Numpy results\\targetPopulatedByPython(datatargetschanged).csv", index= False)
    # from numpy import genfromtxt
    # my_data = genfromtxt("D:\\projectdatabases\\Numpy results\\data1.csv", delimiter=',')
    # print("Read array"+str(my_data))
    pd.DataFrame(datatargetnumpyarray).to_csv("D:\\projectdatabases\\Numpy results\\datatargetPopulatedByPython(datatargetschanged).csv", index=False)
    return mapping


print(iterateoverallmappings())