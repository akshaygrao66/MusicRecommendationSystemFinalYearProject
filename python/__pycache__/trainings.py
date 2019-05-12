import readfunctions
import numpy as np
import loadAndStoreModel
from sklearn.preprocessing import MinMaxScaler
from sklearn.model_selection import train_test_split
from sklearn import neighbors

def trainknnandreturnmodel():
    x = readfunctions.readcsvfileandreturnlist('D:\\projectdatabases\\Numpy results\\dataPopulatedByPython.csv')
    y = readfunctions.readcsvfileandreturnlist('D:\\projectdatabases\\Numpy results\\targetPopulatedByPython.csv')
    y = np.reshape(y, (-1, 1))
    scaler = MinMaxScaler()
    scaler.fit(x)
    scaler.fit(y)
    xscale = scaler.transform(x)
    yscale = scaler.transform(y)
    X_train, X_test, y_train, y_test = train_test_split(xscale, yscale, test_size=0.2, shuffle=False)

    model = neighbors.KNeighborsRegressor(n_neighbors=30)

    model.fit(X_train, y_train)  # fit the model

    return model

def loadmodel(jsonfilename,h5filename):
    model = loadAndStoreModel.loadmodelfromjsonandh5(jsonfilename, h5filename)
    model.compile(loss='mse', optimizer='adam', metrics=['mse', 'mae'])
    return model


def loadandpredict(jsonfilename,h5filename,input):
    model = loadAndStoreModel.loadmodelfromjsonandh5(jsonfilename, h5filename)
    model.compile(loss='mse', optimizer='adam', metrics=['mse', 'mae'])
    scaler = MinMaxScaler()
    inputscaled = scaler.fit_transform(input)
    return model.predict(inputscaled)