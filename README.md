Jack-The-Cutter
==

Jack-The-Cutter is a FFmpeg frontend to lossless cut audio files.



__Usage in short:__

1. Open an audio file, any format [FFmpeg](https://ffmpeg.org/) can process.
2. Create one or more cut points, either manually or use 'autocut' function.
3. Name each section you want to cut out.
4. Sections with blank name get skipped.
5. Finally process the audio file to create a single file for each named section.



Jack-The-Cutter temporary creates an uncompressed WAV file which is used for the editor only.
The cut points are used to <u>lossless cut the original audio</u> file using [FFmpeg](https://ffmpeg.org/).



__Features:__

* lossless cut any audio file [FFmpeg](https://ffmpeg.org/) is capable to process
* the audio wave form is shown twice, the complete form as well as the 'zoomed section', this should imho make cutting a lot easier
* slider to zoom in/out either to/from the centre of either the selection or cursor position
* 'Auto cut' function to automatically find cut points
* 'Create CUE sheet' function to generate both a CUE and WAV file. Then use a tool like [ImgBurn](https://www.imgburn.com/) to create an Audio-CD.



For more infos please visit [Jack-The-Cutter wiki](https://github.com/Moon70/Jack-The-Cutter/wiki).



![](Jack-The-Cutter_screenshot.jpg)


[![Download Jack-The-Cutter](https://a.fsdn.com/con/app/sf-download-button)](https://sourceforge.net/projects/jack-the-cutter/files/latest/download)


[![Download Jack-The-Cutter](https://img.shields.io/sourceforge/dt/jack-the-cutter.svg)](https://sourceforge.net/projects/jack-the-cutter/files/latest/download)
