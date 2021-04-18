# -*- coding: utf-8 -*-
#Author:xiaohei
#CreateTime:2014-10-25
#
# All apk operations are defined here
#
#

import file_utils
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
import argparse
import shutil
import time
from PIL import Image
import image_utils
import log_utils
import smali_utils


def create_proj(projPath, toolPath, u8serverPath, targetName, classPrefix, selectType):


    # if not targetName.startswith("U8SDK_"):
    #     targetName = "U8SDK_" + targetName

    print("selectType:"+selectType)

    selectType = int(selectType)

    if selectType == 1 or selectType == 2:

        print("generate client...........")

        if not os.path.exists(projPath):
            log_utils.debug("the U8SDK_Projects dir is not exists."+projPath)
            return

        if not os.path.exists(toolPath):
            log_utils.debug("the U8SDKTool-Win-P34 dir is not exists."+toolPath)
            return

        templatePath = os.path.join(projPath, "U8SDK_Template")

        if not os.path.exists(templatePath):
            log_utils.debug("the template project is not exists."+templatePath)
            return

        sdkConfigPath = os.path.join(toolPath, "config/sdk/"+targetName.lower())
        if os.path.exists(sdkConfigPath):
            log_utils.error("sdk with same name is already exists. please change the name."+targetName)
            return         

        projTargetName = "U8SDK_" + targetName

        targetPath = os.path.join(projPath, projTargetName)
        if os.path.exists(targetPath):
            log_utils.debug("the target project is already exists."+targetPath)
            return

        file_utils.copy_files(templatePath, targetPath)

        srcPath = os.path.join(targetPath, "src/com/u8/sdk")

        for f in os.listdir(srcPath):
            fname = os.path.join(srcPath, f)
            file_utils.modifyFileContent(fname, "Temp", classPrefix)

            ftarget = f.replace("Temp", classPrefix)
            os.rename(fname, os.path.join(srcPath, ftarget))

        configPath = os.path.join(targetPath, "config.xml")
        file_utils.modifyFileContent(configPath, "Temp", classPrefix) 

        projFilePath = os.path.join(targetPath, ".project")
        file_utils.modifyFileContent(projFilePath, "U8SDK_Template", projTargetName)

        os.makedirs(sdkConfigPath)

    if selectType == 1 or selectType == 3:

        u8serverSDKPath = os.path.join(u8serverPath, "src/main/java/com/u8/server/sdk/"+targetName.lower())
        if os.path.exists(u8serverSDKPath):
            log_utils.error("sdk with same name is already exists in server. please change the name."+targetName)
            return

        u8serverPayPath = os.path.join(u8serverPath, "src/main/java/com/u8/server/web/pay/sdk/"+targetName+"PayCallbackAction.java")  
        if os.path.exists(u8serverPayPath):
            log_utils.error("sdk with same name is already exists in server callback. please change the name."+targetName)
            return 

        serverDemoSDKPath = os.path.join(u8serverPath, "src/main/java/com/u8/server/sdk/demo")
        file_utils.copy_files(serverDemoSDKPath, u8serverSDKPath)
        sdkName = os.path.join(u8serverSDKPath, 'DemoSDK.java')
        targetSDKName = sdkName.replace("Demo", targetName)
        os.rename(sdkName, targetSDKName)
        file_utils.modifyFileContent(targetSDKName, "Demo", targetName)
        file_utils.modifyFileContent(targetSDKName, "demo", targetName.lower())

        serverDemoPayPath = os.path.join(u8serverPath, 'src/main/java/com/u8/server/web/pay/sdk/DemoPayCallbackAction.java')
        file_utils.copy_file(serverDemoPayPath, u8serverPayPath)

        file_utils.modifyFileContent(u8serverPayPath, "demo", targetName.lower())
        file_utils.modifyFileContent(u8serverPayPath, "Demo", targetName)



if __name__ == "__main__":

    parser = argparse.ArgumentParser(u"U8SDK 渠道工程创建工具")
    parser.add_argument('-t', '--targetName', help=u"指定渠道名，英文或者拼音")
    parser.add_argument('-p', '--classPrefix', help=u"类名前缀")
    parser.add_argument('-s', '--selectType', help=u"1：前后端都生成;2:前端；3：后端")

    args = parser.parse_args()

    projPath = "../../U8SDK_Projects"
    toolPath = "../../U8SDKTool-Win-P34"
    u8serverPath = "../../../U8ServerD/U8Server"

    log_utils.debug("create "+args.targetName+"...")

    create_proj(projPath, toolPath, u8serverPath, args.targetName, args.classPrefix, args.selectType)

    log_utils.debug("create "+args.targetName+" end")  
