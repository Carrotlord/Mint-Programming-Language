sub OrgReader(hex)
    orgHexCodes = hex
    
    sub readVersionChar()
        return readChar(5)
    end
    
    sub readChar(offset)
        return char(readByte(offset))
    end
    
    sub readByte(offset)
        endOffset = (offset + 1) * 2
        hexPair = orgHexCodes.slice(offset * 2, endOffset)
        return hexToByte(hexPair)
    end
    
    sub readWord(offset)
        endOffset = (offset + 2) * 2
        hexWord = orgHexCodes.slice(offset * 2, endOffset)
        return hexToWord(hexWord)
    end
    
    sub readDword(offset)
        endOffset = (offset + 4) * 2
        hexDword = orgHexCodes.slice(offset * 2, endOffset)
        return hexToDword(hexDword)
    end
    
    return this
end 