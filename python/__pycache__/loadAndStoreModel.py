from keras.models import model_from_json


def save_ANN_model(model, score, model_name):
    """
    Saves Keras model to an h5 file, based on precision_score

    INPUT
        model: Keras model object to be saved
        score: Score to determine if model should be saved.
        model_name: name of model to be saved
    """

    if score <= 0.048:
        # serialize model to JSON
        model_json = model.to_json()
        with open("D:\\projectdatabases\\Numpy results\\models/" + model_name + "score" + str(round(score, 4)) + ".json", "w") as json_file:
            json_file.write(model_json)
        # serialize weights to HDF5
        model.save_weights("D:\\projectdatabases\\Numpy results\\models/" + model_name + "score" + str(round(score, 4)) + ".h5")
        print("Saving Model")
    else:
        print("Model Not Saved.  Score: ", score)


def save_KNN_model(model, score, model_name):
    """
        Saves Keras model to an h5 file, based on precision_score

        INPUT
            model: Keras model object to be saved
            score: Score to determine if model should be saved.
            model_name: name of model to be saved
        """

    if score <= 0.25:
        # serialize weights to HDF5
        model.save_weights("D:\\projectdatabases\\Numpy results\\models/" + model_name + "score" + str(round(score, 4)) + ".h5")
        print("Saving Model")
    else:
        print("Model Not Saved.  Score: ", score)

def loadmodelfromjsonandh5(jsonfilename,h5filename):
    # load json and create model
    print(jsonfilename)
    json_file = open(jsonfilename, 'r')
    loaded_model_json = json_file.read()
    json_file.close()
    loaded_model = model_from_json(loaded_model_json)
    print("Loaded from json")
    # load weights into new model
    loaded_model.load_weights(h5filename)
    print("Loaded model from disk")
    return loaded_model
