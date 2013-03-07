// Organya File To Text File (and vice versa) Program
// By Carrotlord
return

print "Loading..."

import file
import type
import operator
import mint
import math

import "code/dict.mint"
import "code/note.mint"

sub error(msg)
    print msg
    import system
    exit()
end

sub isSimpleDigit(char)
    return char in "0123456789"
end

sub removeAll(list, item)
    for h = 0; h < list.size(); h++
        if list[h] == item
            list.remove(h)
            h--
        end
    end
    return list
end

import "code/orgloader.mint"

sub bToHex(byteList)
    b = Bytes(byteList)
    hex = ""
    for i = 0; i < byteList.length(); i++
        byte = b.get(i)
        hex += byteToHex(byte)
    end
    return hex
end

sub byteToHex(byte)
    //return bToHex([byte])
    when byte < 0
        byte += 256
    highNibble = shr(byte, 4)
    lowNibble = byte - shl(highNibble, 4)
    hexList = ["0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"]
    return hexList[highNibble] + hexList[lowNibble]
end

sub wordToHex(word)
    highByte = shr(word, 8)
    lowByte = word - shl(highByte, 8)
    return bToHex([lowByte, highByte])
end

sub dwordToHex(dword)
    highestByte = shr(dword, 24)
    highByte = shr(dword, 16)
    lowByte = shr(dword, 8)
    lowestByte = dword - shl(highestByte, 24) - shl(highByte, 16) - shl(lowByte, 8)
    return bToHex([lowestByte, lowByte, highByte, highestByte])
end

sub hexToByte(hex)
    when hex.length() == 0
        return 0
    bytes = hexToBytes(hex)
    b = bytes.get(0)
    when b < 0
        return b + 256
    return b
end

sub hexToWord(hex)
    lower = hexToByte(hex.slice(0, 2))
    upper = hexToByte(hex.slice(2, 4))
    when lower < 0
        lower += 256
    when upper < 0
        upper += 256
    upper = shl(upper, 8)
    return upper + lower
end

sub hexToDword(hex)
    lowest = hexToByte(hex.slice(0, 2))
    low = hexToByte(hex.slice(2, 4))
    high = hexToByte(hex.slice(4, 6))
    highest = hexToByte(hex.slice(6, 8))
    when lowest < 0
        lowest += 256
    when low < 0
        low += 256
    when high < 0
        high += 256
    when highest < 0
        high += 256
    highest = shl(highest, 24)
    high = shl(high, 16)
    low = shl(low, 8)
    return highest + high + low + lowest
end

import "code/orgreader.mint"
import "code/organya.mint"
import "code/track.mint"

print "Enter the name of the .org or .txt file you wish to convert:"
fileName = input
if fileName.endsWith(".org")
    newFileName = fileName.slice(0, -4) + ".txt"
    print "Converting " + fileName + " to " + newFileName + "..."
    print "Please wait..."
    ol = OrgLoader(fileName)
    ol.orgObj.toFile(newFileName)
else if fileName.endsWith(".txt")
    newFileName = fileName.slice(0, -4) + ".org"
    print "Converting " + fileName + " to " + newFileName + "..."
    print "Please wait..."
    org = Organya()
    org.fromString(fileToStr(fileName))
    org.toBinaryFile(newFileName)
end
