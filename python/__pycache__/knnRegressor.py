import readfunctions
import matplotlib.pyplot as plt
import numpy as np
import loadAndStoreModel
from sklearn.preprocessing import MinMaxScaler
from sklearn.model_selection import train_test_split
from sklearn import neighbors
from sklearn.metrics import mean_squared_error
from math import sqrt
import matplotlib.pyplot as plt


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
print(scaler)
print(scaler.fit(x))
print(scaler.fit(y))
xscale=scaler.transform(x)
yscale=scaler.transform(y)
print(xscale)
x=np.array(x)
y=np.array(y).astype(np.float)
X_train, X_test, y_train, y_test = train_test_split(xscale, yscale,test_size=0.2,shuffle=False)
print(X_test.shape)

# rmse_val = [] #to store rmse values for different k
# for K in range(30):
#     K = K+1
#     model = neighbors.KNeighborsRegressor(n_neighbors = K)
#
#     model.fit(X_train, y_train)  #fit the model
#     pred = model.predict(X_test) #make prediction on test set
#     #
#     # for a in range(0, len(pred)):
#     #     print("X=%s, Predicted=%s" % (y_test[a], pred[a]))
#     error = mean_squared_error(y_test,pred) #calculate rmse
#     rmse_val.append(error) #store rmse values
#     print('MSE value for k= ' , K , 'is:', error)


model = neighbors.KNeighborsRegressor(n_neighbors = 30)

model.fit(X_train, y_train)  #fit the model

ypred = model.predict(X_test) #make prediction on test set


for a in range(0, len(ypred)):
   print("X=%s, Predicted=%s" % (y_test[a], ypred[a]))

error = mean_squared_error(y_test,ypred) #calculate rmse
print('MSE value for k= ' , 30 , 'is:', error)

s= [['121.274', 0.43, -9.843, 0.6306300375898077, 0.4174996449709784, 5.11869110857799, '140.994', 0.7, -2.769, 0.6297490400005165, 0.42356389026271546, 7.067574981880171]]
s=scaler.transform(s)
print(s)
print(model.predict(np.array([[121.274,0.43,-9.843,0.63063004,0.41749964,5.11869111,86.643,0.652,-13.496,0.42666786,0.33227575,6.55133585]])))

