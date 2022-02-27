import speech_recognition as spreg
import sounddevice as sd
import numpy as np
import time

with open('stt_out.txt','w') as f:
        f.write('')
        f.close()
time.sleep(2)


def send(val):
    print(val)
    with open('stt_out.txt','a') as f:
        f.write(val+'\n')
        f.close()

def getTime():
    try:
        with open('stt_time.txt', 'r') as reader:
            output = int(reader.read())
            reader.close()
        return output
    except:
        return 0

def setTime(val):
    with open('stt_time.txt', 'w') as f:
        f.write(str(val))
        f.close()

def print_sound(indata, outdata, frames, time, status):
    volume_norm = np.linalg.norm(indata)*10
    print(int(volume_norm),time.inputBufferAdcTime)
    setTime(getTime()+1)

    if(volume_norm > 300):
        time = getTime()

        if(time > 100):
            setTime(0)
            with spreg.Microphone(sample_rate = sample_rate, chunk_size = data_size) as source:
                recog.adjust_for_ambient_noise(source)
                send("LISTENING")
                speech = recog.listen(source)
                try:
                    text = recog.recognize_google(speech)
                    send("STT|"+text)
                except spreg.UnknownValueError:
                    print('Unable to recognize the audio')
                except spreg.RequestError as e:
                    print("Request error from Google Speech Recognition service; {}".format(e))


#Setup the sampling rate and the data size
sample_rate = 48000
data_size = 8192
recog = spreg.Recognizer()

while(True):
        with sd.Stream(callback=print_sound):
            while(True):
                sd.sleep(5000)