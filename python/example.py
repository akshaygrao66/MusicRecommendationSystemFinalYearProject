from keras.models import Sequential
from keras.layers import Dense, Activation


# For a single-input model with 2 classes (binary classification):

model = Sequential()
model.add(Dense(32, activation='relu', input_dim=10))
model.add(Dense(1, activation='sigmoid'))
model.compile(optimizer='rmsprop',
              loss='binary_crossentropy',
              metrics=['accuracy'])

# Generate dummy data
import numpy as np
data = np.random.random((100, 10))
labels = np.random.randint(2, size=(100, 1))
predict_input=np.random.random((1,10))
# Train the model, iterating on the data in batches of 32 samples
model.fit(data, labels, epochs=10, batch_size=32)

ret=model.predict(predict_input, batch_size=None, verbose=0, steps=None)
print("Predicted value is:")
print(ret)

