# Video Search Engine

## About
This application is a local search engine for videos which works on any specified directory.\
Given a query, the search engine will not only search for video titles that match the query but it will also consider the content of the videos. 
So, if a sentence *S* is spoken in a video *V* and the query matches *S*, *V* will be in the results. Text documents in the specified directory are also considered by the search engine.\
The most relevant results are then displayed to the user.
If the user clicks on a video result, it will open that video at a time around which the relevant terms of the query were mentioned.


## Methodology
### Preprocessing
Given a directory:
* Each video is mapped to multiple text documents using the Speech-to-Text library; each document corresponds to 20s of the video
* A cache is used to avoid running the Speech-to-Text algorithm on the same video each time we open the application

The text documents (some of which correspond to videos) are then added to Lucene
### Search
Lucene takes a query as input and provides us with ranked results. The Vector Space Model is used.\
Then, we handle text duplicates (if we get multiple texts corresponding to the same video) and display the results to the user.

## Tools Used
### Python
* Speech Recognition Library
* FFmpeg
### Java
* Apache Lucene
* WindowsBuilder, Eclipse

## Setup
#### Step 1 : Clone the repository
In order to clone the frontend repository : 
1. Create a new folder, anywhere in your PC
2. Open Command Prompt and change the directory into the folder created
3. Enter : git clone https://github.com/Charbel-11/Video-Search-Engine.git
#### Step 2 : Install needed tools
Download the following tools:
* Python with the Speech Recognition Library
* FFMPEG
* Lucene Java Library
* Eclipse and the Windows Builder Extension
* VLC Media Player
#### Step 3 : Set the required paths
Follow these steps:
* Add the Lucene library to your Eclipse solution
* Set Python in your environment variables
* In Helper.java, set the paths to:  
  *  A directory that will be used as cache 
  *  The python file of the project 
  * The VLC executable  
* In AudioTranscribe.py, set the paths to: 
  *  The cache directory as above 
  *  The FFMPEG executable  

## Screenshots
### Choose Directory
<img src="https://user-images.githubusercontent.com/61922252/119517499-571e5000-bd80-11eb-8909-e8c086be0c68.png" width="450" height="350"/>

### Search
<img src="https://user-images.githubusercontent.com/61922252/119517366-3950eb00-bd80-11eb-9ee9-78295099a3bd.png" width="500" height="400">
