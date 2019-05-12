import numpy as np
import readfunctions
from sklearn.model_selection import train_test_split
from keras.models import Sequential
from keras.layers import Dense, Dropout
from sklearn.metrics import precision_score
from sklearn.metrics import recall_score
from keras.callbacks import TensorBoard
from keras.utils import plot_model
from matplotlib import pyplot

from sklearn.preprocessing import MinMaxScaler
import keras as keras


def split_data(X, y, test_data_size):
    """
    Split data into test and training datasets.

    INPUT
        X: NumPy array of arrays
        y: Pandas series, which are the labels for input array X
        test_data_size: size of test/train split. Value from 0 to 1

    OUPUT
        Four arrays: X_train, X_test, y_train, and y_test
    """
    return train_test_split(X, y, test_size=test_data_size, random_state=42)


if __name__ == '__main__':
    data = readfunctions.readcsvfileandreturnlist('D:\\projectdatabases\\Numpy results\\dataPopulatedByPython.csv')
    target = readfunctions.readcsvfileandreturnlist('D:\\projectdatabases\\Numpy results\\targetPopulatedByPython(datatargetschanged).csv')

    datanumpy=np.array(data)
    targetnumpy=np.array(target)
    print("Splitting data into test/ train datasets")
    X_train, X_test, y_train, y_test = split_data(datanumpy, targetnumpy, 0.2)
    print("Train data length"+str(X_train.shape))
    print("Testing dsta length:"+str(X_test.shape))

    print("Training data is:"+str(X_train))
    print("Testing data is:"+str(X_test))
    print("Training target is:"+str(y_train))
    print("testing target is:"+str(y_test))

    # pyplot.imshow(X_train[0])
    # pyplot.show()
    #
    from sklearn.preprocessing import StandardScaler
    scaler = StandardScaler(copy=True, with_mean=True, with_std=True)
    scaler.fit(X_train)
    # print("Fit"+str(scaler.fit(X_train)))
    # print("Means are:"+str(scaler.mean_))
    X_train=scaler.transform(X_train)
    scaler.fit(X_test)
    X_test=scaler.transform(X_test)

    # from sklearn import preprocessing
    # y_train = preprocessing.normalize(y_train, norm='l1')
    # y_test = preprocessing.normalize(y_test, norm='l1')
    #
    # # scaler2.fit(y_train)
    # # print("Target dmax:"+str(scaler2.data_max_))
    # # y_train=scaler2.transform(y_train)
    # # scaler2.fit(y_test)
    # # y_test=scaler2.transform(y_test)

    print("Normalised Training data is:"+str(X_train))
    print("Normalised Testing data is:"+str(X_test))
    print("Normalised Training target is:" + str(y_train))
    print("Normalized testing target is:" + str(y_test))

    model = Sequential()
    model.add(Dense(64, input_dim=12, activation='relu'))
    model.add(Dropout(0.5))
    model.add(Dense(64, activation='relu'))
    model.add(Dropout(0.5))
    model.add(Dense(1, activation='sigmoid'))

    model.compile(loss='binary_crossentropy',
                  optimizer='adam',
                  metrics=['mae','accuracy'])
    # one_hot_labels=keras.utils.to_categorical(y_train)
    tensor_board = TensorBoard(log_dir='D:\\projectdatabases\\Numpy results\\Graph', histogram_freq=0, write_graph=True,
                               write_images=True)
    history=model.fit(X_train, y_train,
              epochs=30,
              batch_size=2,
              callbacks=[tensor_board])
    print("Predicting")
    y_pred = model.predict(X_test)
    np.set_printoptions(precision=3)
    dummytest=np.array([[90,77,10,68,51,2786,92,77,15,58,44,3214]])
    pred=model.predict(dummytest)
    print(pred[0][0])


    print("Y test"+str(y_test))
    print("Y pred"+str(y_pred))

    score = model.evaluate(X_test, y_test, verbose=1)
    print('Test accuracy:', score)

    # precision = precision_score(y_test, y_pred)
    # recall = recall_score(y_test, y_pred)
    #
    # print("Precision: ", precision)
    # print("Recall: ", recall)

    keras.utils.print_summary(model)

    # plot_model(model, to_file='D:\\projectdatabases\\Numpy results\\graphpic\\Model.png')
    print(model.metrics_names)
    pyplot.plot(history.history['mean_absolute_error'])
    pyplot.show()