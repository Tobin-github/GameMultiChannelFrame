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
	
	compileWXEntryActivity(channel, decompileDir, packageName)

	compileWXPayActivity(channel, decompileDir, packageName)

	modifyManifest(channel, decompileDir, packageName)


	return 0


def modifyManifest(channel, decompileDir, packageName):

	wxAppID = ""

	if 'params' in channel:
		params = channel['params']
		for param in params:
			if param['name'] == 'WX_APP_ID':
				wxAppID = param['value']


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
			if activityName == '.wxapi.WXEntryActivity':
				activityNode.set(name, packageName+activityName)
				intentFilters = activityNode.findall('intent-filter')
				if intentFilters != None and len(intentFilters) > 0:
					for intentNode in intentFilters:
						dataNode = SubElement(intentNode, 'data')
						dataNode.set(scheme, wxAppID)
						break
			elif activityName == '.wxapi.WXPayEntryActivity':
				activityNode.set(name, packageName+activityName)


	tree.write(manifest, 'UTF-8')




def compileWXEntryActivity(channel, decompileDir, packageName):

	sdkDir = decompileDir + '/../sdk/' + channel['sdk']
	if not os.path.exists(sdkDir):
		file_utils.printF("The sdk temp folder is not exists. path:"+sdkDir)
		return 1

	extraFilesPath = sdkDir + '/extraFiles'
	relatedJar = os.path.join(extraFilesPath, 'wechat.jar')
	relatedJar2 = os.path.join(extraFilesPath, 'u8sdk.jar')
	WXEntryActivity = os.path.join(extraFilesPath, 'WXEntryActivity.java')
	file_utils.modifyFileContent(WXEntryActivity, 'com.u8.sdk.wxapi', packageName+".wxapi")

	splitdot = ';'
	if platform.system() == 'Darwin':
		splitdot = ':'

	cmd = '"%sjavac" -source 1.7 -target 1.7 "%s" -classpath "%s"%s"%s"%s"%s"' % (file_utils.getJavaBinDir(), WXEntryActivity, relatedJar, splitdot,relatedJar2, splitdot, file_utils.getFullToolPath('android.jar'))


	ret = file_utils.execFormatCmd(cmd)
	if ret:
		return 1

	packageDir = packageName.replace('.', '/')
	srcDir = sdkDir + '/tempDex'
	classDir = srcDir + '/' + packageDir + '/wxapi'

	if not os.path.exists(classDir):
		os.makedirs(classDir)

	sourceClassFilePath = os.path.join(extraFilesPath, 'WXEntryActivity.class')
	targetClassFilePath = classDir + '/WXEntryActivity.class'

	file_utils.copy_file(sourceClassFilePath, targetClassFilePath)

	targetDexPath = os.path.join(sdkDir, 'WXEntryActivity.dex')

	dxTool = file_utils.getFullToolPath("/lib/dx.jar")

	cmd = file_utils.getJavaCMD() + ' -jar -Xmx512m -Xms512m "%s" --dex --output="%s" "%s"' % (dxTool, targetDexPath, srcDir)

	ret = file_utils.execFormatCmd(cmd)

	if ret:
		return 1

	ret = apk_utils.dex2smali(targetDexPath, decompileDir+'/smali', "baksmali.jar")

	if ret:
		return 1


def compileWXPayActivity(channel, decompileDir, packageName):

	sdkDir = decompileDir + '/../sdk/' + channel['sdk']
	if not os.path.exists(sdkDir):
		file_utils.printF("The sdk temp folder is not exists. path:"+sdkDir)
		return 1

	extraFilesPath = sdkDir + '/extraFiles'
	relatedJar = os.path.join(extraFilesPath, 'wechat.jar')
	relatedJar2 = os.path.join(extraFilesPath, 'u8sdk.jar')
	WXPayEntryActivity = os.path.join(extraFilesPath, 'WXPayEntryActivity.java')
	file_utils.modifyFileContent(WXPayEntryActivity, 'com.u8.sdk.wxapi', packageName+".wxapi")

	splitdot = ';'
	if platform.system() == 'Darwin':
		splitdot = ':'

	cmd = '"%sjavac" -source 1.7 -target 1.7 "%s" -classpath "%s"%s"%s"%s"%s"' % (file_utils.getJavaBinDir(), WXPayEntryActivity, relatedJar, splitdot,relatedJar2, splitdot, file_utils.getFullToolPath('android.jar'))

	ret = file_utils.execFormatCmd(cmd)
	if ret:
		return 1

	packageDir = packageName.replace('.', '/')
	srcDir = sdkDir + '/tempDex'
	classDir = srcDir + '/' + packageDir + '/wxapi'

	if not os.path.exists(classDir):
		os.makedirs(classDir)

	sourceClassFilePath = os.path.join(extraFilesPath, 'WXPayEntryActivity.class')
	targetClassFilePath = classDir + '/WXPayEntryActivity.class'

	file_utils.copy_file(sourceClassFilePath, targetClassFilePath)

	targetDexPath = os.path.join(sdkDir, 'WXPayEntryActivity.dex')

	dxTool = file_utils.getFullToolPath("/lib/dx.jar")

	cmd = file_utils.getJavaCMD() + ' -jar -Xmx512m -Xms512m "%s" --dex --output="%s" "%s"' % (dxTool, targetDexPath, srcDir)

	ret = file_utils.execFormatCmd(cmd)

	if ret:
		return 1

	ret = apk_utils.dex2smali(targetDexPath, decompileDir+'/smali', "baksmali.jar")

	if ret:
		return 1



	


