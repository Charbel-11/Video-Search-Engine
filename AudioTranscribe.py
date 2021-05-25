import os
import shutil
import subprocess
import sys

import speech_recognition as sr
import soundfile as sf
import math as mth

cachePath = "C:\\Users\\PC\\Documents\\VideoSearchEngineCache"
ffmpegPath = "C:/Program Files/ffmpeg-4.3.2-2021-02-20-full_build/bin/ffmpeg.exe"

def transcribeFile(fpath, fname, idx):
    f = sf.SoundFile(fpath)
    duration = len(f) / f.samplerate
 
    r = sr.Recognizer()                 # initialize recognizer
    audioFile = sr.AudioFile(fpath)

    for i in range(mth.ceil(duration/20)):
        with audioFile as source:     # mention source it will be either Microphone or audio files.
            try:
                #Partition the source into (slightly) overlapping segments of ~20s
                audio = r.record(source, offset=max(0, i * 20 - 2), duration=22)        # listen to the source
                text = r.recognize_google(audio, show_all=False)    # use recognizer to convert our audio into text part.
                
                outfile = open(cachePath + "\\" + str(idx) + "\\" + str(i) + ".txt", "w+", encoding="utf8")
                if i == 0:
                    outfile.write(fname + " " + str(text))
                else:
                    outfile.write(str(text))
                outfile.close()
                
            except Exception as e:
                print(e)    # In case of voice not recognize

#Takes a path to a video file and produces an audio from it, requires ffmpeg
def vidToWav(path, name):
    command = ffmpegPath + " -i \"" + \
              path + "\" -ab 160k -ac 2 -ar 16000 -vn \"" + name + ".wav\""
    subprocess.call(command, shell=True)

#Returns a dictionary {name: idx} of cached files
def getAllDirName():
    seen = {}
    f = open(cachePath + "\\seen.txt", 'r')
    idx = 0
    for line in f:
        seen[line[:-1]] = idx
        idx += 1
    f.close()
    return seen

def executeCommand():
    dir = sys.argv[1]

    seen = getAllDirName()
    mp4Files = []

    f = open(cachePath + "\\seen.txt", 'a')
    idx = len(seen)
    for subdir, dirs, files in os.walk(dir):
        for filename in files:
            filepath = subdir + os.sep + filename
            prevIdx = -1

            if filepath in seen:
                docIdx = seen[filepath]
                dirPath = cachePath + "\\" + str(docIdx)
                #If file was cached after being last modified, skip it
                if os.path.getmtime(filepath) <= os.path.getmtime(dirPath):
                    continue
                else:
                    prevIdx = docIdx

            if filename.endswith(".mp4"):
                filename = filename[:-4]
                mp4Files.append((filepath, filename, prevIdx))

    for filepath, filename, prevIdx in mp4Files:
        curIdx = prevIdx
        if prevIdx == -1:
            curIdx = idx
            idx += 1
        else:
            shutil.rmtree(cachePath + "\\" + str(curIdx))

        f.write(filepath + '\n')
        os.mkdir(cachePath + "\\" + str(curIdx))
        curAudioPath = ".\\" + filename + ".wav"
        if os.path.exists(curAudioPath):
            os.remove(curAudioPath)
        vidToWav(filepath, filename)
        transcribeFile(curAudioPath, filename, curIdx)
        os.remove(curAudioPath)

    f.close()

executeCommand()