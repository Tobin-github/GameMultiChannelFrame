import file_utils
import apk_utils
import os
import os.path
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
import log_utils

androidNS = 'http://schemas.android.com/apk/res/android'
def execute(channel, pluginInfo, decompileDir, packageName):
    log_utils.debug("plugin execute="+str(channel)+"pluginInfo="+str(pluginInfo)+" decompileDir="+str(decompileDir)+" packageName="+str(packageName));
    modifyManifest(channel, decompileDir, packageName)
    return 0
# def execute(channel, decompileDir, packageName):
#     log_utils.dug("plugin execute="+channel+" decompileDir="+decompileDir+" packageName="+packageName);
#     modifyManifest(channel, decompileDir, packageName)
#     return 0

def modifyManifest(channel, decompileDir, packageName):
    manifestFile = decompileDir + "/AndroidManifest.xml"
    manifestFile = file_utils.getFullPath(manifestFile)
    log_utils.debug("providerName manifestFile="+manifestFile);
    ET.register_namespace('android', androidNS)
    tree = ET.parse(manifestFile)
    root = tree.getroot()
    authorities = '{' + androidNS + '}authorities'
    appNode = root.find('application')
    if appNode is None:
        return 1

    providerNodes = appNode.findall('provider')
    if providerNodes != None and len(providerNodes) > 0:
        for providerNode in providerNodes:
            authoritiesValue = providerNode.get(authorities)
            log_utils.debug("providerName="+authoritiesValue);
            if authoritiesValue == '.unicorn.fileprovider':
                providerNode.set(authorities, packageName + authoritiesValue)

    tree.write(manifestFile, 'UTF-8')