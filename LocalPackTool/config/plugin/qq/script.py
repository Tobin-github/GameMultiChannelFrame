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


def execute(channel, pluginInfo, decompileDir, packageName):
	
	modifyManifest(channel, decompileDir, packageName)


	return 0


def modifyManifest(channel, decompileDir, packageName):

	appID = ""

	if 'params' in channel:
		params = channel['params']
		for param in params:
			if param['name'] == 'App_ID':
				appID = param['value']


	manifest = decompileDir + '/AndroidManifest.xml'
	ET.register_namespace('android', androidNS)
	name = '{' + androidNS + '}name'	
	scheme = '{' + androidNS + '}scheme'
	tree = ET.parse(manifest)
	root = tree.getroot()

	appNode = root.find('application')
	if appNode is None:
		return 1

	activityNodes = appNode.findall('activity')
	if activityNodes != None and len(activityNodes) > 0:
		for activityNode in activityNodes:
			activityName = activityNode.get(name)
			if activityName == 'com.tencent.tauth.AuthActivity':
				intentFilters = activityNode.findall('intent-filter')
				if intentFilters != None and len(intentFilters) > 0:
					for intentNode in intentFilters:
						dataNode = SubElement(intentNode, 'data')
						dataNode.set(scheme, 'tencent'+appID)
						break


	tree.write(manifest, 'UTF-8')






	


