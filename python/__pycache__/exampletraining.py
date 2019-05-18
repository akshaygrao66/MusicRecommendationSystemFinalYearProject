import matplotlib.pyplot as plt
import numpy as np
import readfunctions
import loadAndStoreModel
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import MinMaxScaler
from keras.models import Sequential
from keras.layers import Dense,Dropout

def training():
    #Variables
    x = readfunctions.readcsvfileandreturnlist('D:\\projectdatabases\\Numpy results\\dataPopulatedByPython.csv')
    y = readfunctions.readcsvfileandreturnlist('D:\\projectdatabases\\Numpy results\\targetPopulatedByPython.csv')
    # reversex = []
    # for u in x:
    #     temp=[]
    #     for i in range(6,12):
    #         temp.append(u[i])
    #     for i in range(0,6):
    #         temp.append(u[i])
    #     reversex.append(temp)
    # for u in reversex:
    #     x.append(u)
    #
    # reversey=[]
    # for u in y:
    #     reversey.append(u)
    # for u in reversey:
    #     y.append(u)
    y=np.reshape(y, (-1,1))
    scaler = MinMaxScaler()
    print(scaler.fit(x))
    print(scaler.fit(y))
    xscale=scaler.transform(x)
    yscale=scaler.transform(y)
    print(yscale)
    x=np.array(x)
    y=np.array(y)
    X_train, X_test, y_train, y_test = train_test_split(xscale, yscale,test_size=0.2,shuffle=False)
    print(X_test.shape)

    model = Sequential()
    model.add(Dense(12, input_dim=12, kernel_initializer='normal', activation='relu'))
    model.add(Dense(12, activation='relu'))
    model.add(Dense(22, activation='relu'))
    model.add(Dense(12, activation='relu'))
    model.add(Dense(22, activation='relu'))
    model.add(Dense(12, activation='relu'))
    model.add(Dense(8,  activation='relu'))
    model.add(Dense(1, activation='linear'))
    model.summary()

    model.compile(loss='mse', optimizer='adam', metrics=['mse','mae'])

    history = model.fit(X_train, y_train, epochs=200, batch_size=100,  verbose=1)
    # , validation_split=0.2

    ypred=model.predict(X_test)
    print(len(ypred))
    for a in range(0,len(ypred)):
        print("X=%s, Predicted=%s" % (y_test[a], ypred[a]))
    score = model.evaluate(X_test, y_test, verbose=1)
    print('Test accuracy:', score)
    s=[[121.274,0.43,-9.843,0.630630038,0.417499645,5.118691109,142.963,0.505,-11.298,0.620392056,0.390660523,12.01221904]]
    s=scaler.fit_transform(s)
    print(model.predict(s))

    loadAndStoreModel.save_ANN_model(model, score[0], "ANNr12r12r22r12r22r12r8l1mseadam200epoch100batch")
    print(history.history.keys())
    # "Loss"
    plt.plot(history.history['loss'])
    # plt.plot(history.history['val_loss'])
    plt.title('model loss')
    plt.ylabel('loss')
    plt.xlabel('epoch')
    plt.legend(['train', 'validation'], loc='upper left')
    plt.show()


def loadstoredannmodel(jsonfilename,h5filename):
    model = loadAndStoreModel.loadmodelfromjsonandh5(jsonfilename,h5filename)
    model.compile(loss='mse', optimizer='adam', metrics=['mse', 'mae'])

    x = readfunctions.readcsvfileandreturnlist('D:\\projectdatabases\\Numpy results\\dataPopulatedByPython.csv')
    y = readfunctions.readcsvfileandreturnlist('D:\\projectdatabases\\Numpy results\\targetPopulatedByPython.csv')
    print(x)
    y=np.reshape(y, (-1,1))
    scaler = MinMaxScaler()
    print(scaler.fit(x))
    print(scaler.fit(y))
    xscale=scaler.transform(x)
    yscale=scaler.transform(y)
    print(xscale)
    x=np.array(x)
    y=np.array(y)
    X_train, X_test, y_train, y_test = train_test_split(xscale, yscale,test_size=0.2,shuffle=False)
    print(X_test.shape)

    ypred = model.predict(X_test)
    print(len(ypred))
    for a in range(0, len(ypred)):
        print("X=%s, Predicted=%s" % (y_test[a], ypred[a]))
    score = model.evaluate(X_test, y_test, verbose=1)
    print('Test accuracy:', score)
    s = [['121.274', 0.43, -9.843, 0.6306300375898077, 0.4174996449709784, 5.11869110857799, '140.994', 0.7, -2.769, 0.6297490400005165, 0.42356389026271546, 7.067574981880171]]
    s = scaler.transform(s)
    print(s)
    print(model.predict(np.array([[121.274,0.43,-9.843,0.63063004,0.41749964,5.11869111,86.643,0.652,-13.496,0.42666786,0.33227575,6.55133585]])))


def loadandpredict(jsonfilename,h5filename,input):
    model = loadAndStoreModel.loadmodelfromjsonandh5(jsonfilename, h5filename)
    model.compile(loss='mse', optimizer='adam', metrics=['mse', 'mae'])
    scaler = MinMaxScaler()
    inputscaled = scaler.fit_transform(input)
    print(inputscaled)
    return model.predict(inputscaled)


s = [['121.274', 0.43, -9.843, 0.6306300375898077, 0.4174996449709784, 5.11869110857799, '92.222', 0.708, -5.026, 0.5602019118086116, 0.37522396707186795, 7.450923468654035]]
# print(loadandpredict('D:\\projectdatabases\\Numpy results\\models\\ANNr12r12r22r12r22r12r8l1mseadam200epoch100batchscore0.0358.json','D:\\projectdatabases\\Numpy results\\models\\ANNr12r12r22r12r22r12r8l1mseadam200epoch100batchscore0.0358.h5',s))
# loadstoredannmodel('D:\\projectdatabases\\Numpy results\\models\\ANNr12r12r22r12r22r12r8l1mseadam200epoch100batchscore0.0358.json','D:\\projectdatabases\\Numpy results\\models\\ANNr12r12r22r12r22r12r8l1mseadam200epoch100batchscore0.0358.h5')
training()