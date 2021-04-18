import file_utils
import apk_utils
import os
import os.path
import config_utils
from xml.etree import ElementTree as ET
from xml.etree.ElementTree import SubElement
from xml.etree.ElementTree import Element
from xml.etree.ElementTree import ElementTree
import os
import os.path
import zipfile
import re
import subprocess
import platform
import log_utils
from xml.dom import minidom
import codecs
import sys

androidNS = 'http://schemas.android.com/apk/res/android'


def execute(channel, decompileDir, packageName):

	modifyManifest(channel, decompileDir, packageName)


	return 0


def modifyManifest(channel, decompileDir, packageName):

	AppID = ""

	if 'params' in channel:
		params = channel['params']
		for param in params:
			if param['name'] == 'OPENAPPID':
				AppID = param['value']



	manifest = decompileDir + '/AndroidManifest.xml'
	ET.register_namespace('android', androidNS)
	name = '{' + androidNS + '}name'	
	scheme = '{' + androidNS + '}scheme'
	mValue = '{' + androidNS + '}value'
	tree = ET.parse(manifest)
	root = tree.getroot()

	appNode = root.find('application')
	if appNode is None:
		return 1

	metaDataNodes = appNode.findall('meta-data')
	if metaDataNodes != None and len(metaDataNodes) > 0:
		for metaDataNode in metaDataNodes:
			metaDataName = metaDataNode.get(name)
			if metaDataName == 'lenovo.open.appid':
				metaDataNode.set(mValue, AppID)
	# 		if metaDataName == 'lenovo:channel':
	# 			metaDataNode.set(mValue, AppID)
	# 		if metaDataName == 'alipayquick':
	# 			metaDataNode.set(mValue, AppID)


	# receiverNodes = appNode.findall('receiver')
	# if receiverNodes != None and len(receiverNodes) > 0:
	# 	for receiverNode in receiverNodes:
	# 		receiverName = receiverNode.get(name)
	# 		if receiverName == 'com.lenovo.lsf.gamesdk.receiver.GameSdkReceiver':
	# 			intentNode = receiverNode.find('intent-filter')
	# 			actionNode = SubElement(intentNode, 'action')
	# 			actionNode.set(name, AppID)
	# 			categoryNode = SubElement(intentNode, 'category')
	# 			categoryNode.set(name, packageName)
			
	# 		if receiverName == 'com.lenovo.lsf.gamesdk.receiver.GameSdkAndroidLReceiver':
	# 			intentNode = receiverNode.find('intent-filter')
	# 			categoryNode = SubElement(intentNode, 'category')
	# 			categoryNode.set(name, packageName)
	
	# activityNodes = appNode.findall('activity')
	# if activityNodes != None and len(activityNodes) > 0:
	# 	for activityNode in activityNodes:
	# 		activityName = activityNode.get(name)
	# 		if activityName == 'com.lenovo.lsf.pay.ui.TempVBTypeChooseActivity':
	# 			intentNode = activityNode.find('intent-filter')
	# 			dataNode = SubElement(intentNode, 'data')
	# 			dataNode.set(scheme, AppID)

	tree.write(manifest, 'UTF-8')