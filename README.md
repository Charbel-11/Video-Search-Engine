# Video Search Engine

## About
This application is a local search engine for videos which works on any specified directory. 
<br>Given a query, the search engine will not only search for video titles that match the query but it will also consider the content of the videos. 
So, if a sentence *S* is spoken in a video *V* and the query matches *S*, *V* will be in the results. Text documents in the specified directory are also considered by the search engine.
<br>The most relevant results are then displayed to the user.
If the user clicks on a video result, it will open that video at a time around which the relevant terms of the query were mentioned.


## Methodology
### Preprocessing
Given a directory:
* Each video is mapped into multiple text documents, each of which corresponds to a 20s segment of the video
* A cache is used to avoid running the Speech-to-Text algorithm on the same video each time we open the application

The text documents (some of which correspond to videos) are then added to Lucene
### Search
Lucene takes the query as input and provides us with ranked results. The Vector Space Model is used.
Then, we handle text duplicates (if we get multiple texts corresponding to the same video) and display the results to the user.

## Tools
### Python
* Speech Recognition Library
* FFmpeg
### Java
* Apache Lucene
* WindowsBuilder, Eclipse

## Screenshots
