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
from xml.dom import minidom
import codecs
import sys

androidNS = 'http://schemas.android.com/apk/res/android'


def execute(channel, decompileDir, packageName):

	modifyManifest(channel, decompileDir, packageName)

	return 0


def modifyManifest(channel, decompileDir, packageName):


	manifest = decompileDir + '/AndroidManifest.xml'
	ET.register_namespace('android', androidNS)
	name = '{' + androidNS + '}name'	
	authorities = '{' + androidNS + '}authorities'
	tree = ET.parse(manifest)
	root = tree.getroot()

	appNode = root.find('application')
	if appNode is None:
		return 1

	providerNodes = appNode.findall('provider')

	if providerNodes != None and len(providerNodes) > 0:
		for providerNode in providerNodes:
			providerName = providerNode.get(name)
			##authorities = providerNode.get("android:authorities")
			if providerName == 'com.huawei.hms.update.provider.UpdateProvider':
				providerNode.set(authorities,packageName+".hms.update.provider")
				break

	if providerNodes != None and len(providerNodes) > 0:
		for providerNode in providerNodes:
			providerName = providerNode.get(name)
			##authorities = providerNode.get("android:authorities")
			if providerName == 'com.huawei.updatesdk.fileprovider.UpdateSdkFileProvider':
				providerNode.set(authorities,packageName+".updateSdk.fileProvider")
				break

	tree.write(manifest, 'UTF-8')
