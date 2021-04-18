# -*- coding: utf-8 -*-
#CreateTime:2014-10-25

import sys
import file_operate
import os
import os.path
import time
import zipfile


def entry(packageFile):
	sourceApkFile = file_operate.getFullPath(packageFile)
	channelsFile = file_operate.getFullPath("channels.txt")

	if not os.path.exists(channelsFile):
		file_operate.printF("The channels.txt file is not exists.")
		return

	f = open(channelsFile)
	channelLines = f.readlines()
	f.close()

	channels = []
	if channelLines != None and len(channelLines) > 0:

		for line in channelLines:
			targetChannel = line.strip()
			channels.append(targetChannel)

	else:
		file_operate.printF("There is no channel configed in channels.txt")

	modify(channels, sourceApkFile)



def modify(channels, sourceApkFile):

	sourceApkFile = sourceApkFile.replace('\\', '/')
	if not os.path.exists(sourceApkFile):
		file_operate.printF("The source apk file is not exists")
		return	

	tempFolder = file_operate.getFullPath('temp')
	if not os.path.exists(tempFolder):
		os.makedirs(tempFolder)


	empty_file = os.path.join(tempFolder, "temp.txt")
	f = open(empty_file, 'w')
	f.close()

	for channel in channels:
		generateNewChannelApk(sourceApkFile, empty_file, channel)

	file_operate.del_file_folder(tempFolder)


def generateNewChannelApk(sourceApkFile, empty_file, channelID):

	file_operate.printF("Now to generate channel %s", channelID)

	targetFolder = file_operate.getFullPath("channels")
	if not os.path.exists(targetFolder):
		os.makedirs(targetFolder)

	targetApk = os.path.join(targetFolder, "u8-"+channelID+".apk")
	file_operate.copy_file(sourceApkFile, targetApk)

	zipped = zipfile.ZipFile(targetApk, 'a', zipfile.ZIP_DEFLATED)
	emptyChannelFile = "META-INF/u8channel_{channel}".format(channel=channelID)
	zipped.write(empty_file, emptyChannelFile)
	zipped.close()


args = sys.argv
packageFile = args[1]

entry(packageFile)

