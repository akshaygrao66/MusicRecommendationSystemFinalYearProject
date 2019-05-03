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
        print(eachlastfmfile)
        print(count)
        # Removing extensions
        eachlastfmfile=eachlastfmfile.rsplit(".", 1)[0]
        rootsongattributesdict=mysqlfunctions.returntrackattributesasdictionary(eachlastfmfile)
        # if dictionary is not empty(implies lastfm song exists in msd and musixmatch)
        if rootsongattributesdict:
            similarsexistingdictionary=obtainSimilarityFromLastFM.obtainallsimilarityasdictionary(eachlastfmfile)
            if similarsexistingdictionary:
                for eachsimilarsong in similarsexistingdictionary:
                    similarsongattributesdict=mysqlfunctions.returntrackattributesasdictionary(eachsimilarsong)
                    if similarsongattributesdict:
                        mapping=mapping+1
        print("Mapping is:" + str(mapping))
    return mapping


print(iterateoverallmappings())