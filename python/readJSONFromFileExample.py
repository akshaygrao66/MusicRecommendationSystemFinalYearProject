import readFileExample
import json

track_id="TRAAABD128F429CF47"
lastFmJSON=readFileExample.openFileAndReturnString(track_id)
lastFmPythonJSON = json.loads(lastFmJSON)
print(lastFmPythonJSON["artist"])
