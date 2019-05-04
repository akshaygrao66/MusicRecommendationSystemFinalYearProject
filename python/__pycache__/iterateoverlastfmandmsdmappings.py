import mysqlfunctions
import obtainSimilarityFromLastFM
import readfunctions


def iterateoverallmappings():
    mapping=0
    count=0
    # Get all files in LastFM directory
    allfilesinlastfm=readfunctions.getallfilesinlastfmdirectory()
    # For each file in directory do
    for eachlastfmfile in allfilesinlastfm:
        count=count+1
        print("Root:"+str(eachlastfmfile))
        print(count)
        if count > 2:
            break
        # Removing extensions
        eachlastfmfile=eachlastfmfile.rsplit(".", 1)[0]
        rootsongattributesdict=mysqlfunctions.returntrackattributesasdictionary(eachlastfmfile)
        # if dictionary is not empty(implies lastfm song exists in msd and musixmatch)
        if rootsongattributesdict:
            dataroot=mysqlfunctions.returnrequiredattributesasarray(rootsongattributesdict)
            similarsexistingdictionary = obtainSimilarityFromLastFM.obtainallsimilarityasdictionary(eachlastfmfile)
            if similarsexistingdictionary:
                for eachsimilarsong in similarsexistingdictionary:
                    similarsongattributesdict=mysqlfunctions.returntrackattributesasdictionary(eachsimilarsong)
                    if similarsongattributesdict:
                        datasimilar = mysqlfunctions.returnrequiredattributesasarray(similarsongattributesdict)
                        finaldata=dataroot+datasimilar
                        target=similarsexistingdictionary[eachsimilarsong]
                        print("Similar song:"+str(similarsongattributesdict['track_id']))
                        print("Data:"+str(finaldata))
                        print("Target:"+str(target))
                        mapping=mapping+1
    return mapping


print(iterateoverallmappings())