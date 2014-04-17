There are files to unpack that exist also to unpack for other generator plugins with an identical name.
When read from classpath, the classloader cannot destinguish between them, if also the path to the files are identical.
To avoid mixing up these files between the plugins, the pluginId is added to the path of these files.
But unpacked, the pluginId is not part of the path!
