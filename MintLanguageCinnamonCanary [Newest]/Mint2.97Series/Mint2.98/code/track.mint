sub Track(startingBeat)
    notes = []
    startBeat = startingBeat
    lastNoteVal = 48
    
    sub setStartingBeat(val)
        startBeat = val
    end
    
    sub addNote(pitch, length, volume, panning)
        n = Note()
        n.makeNote(pitch, length, volume, panning, startBeat)
        lastNoteVal = n.getValue()
        notes.append(n)
        startBeat += length
    end
    
    sub addNote2(symbol, length, volume, panning)
        n = Note()
        if symbol.startsWith("+")
            n.makeNote(lastNoteVal + int(symbol.sliceToEnd(1)), length, volume, panning, startBeat)
        else if symbol.startsWith("-")
            n.makeNote(lastNoteVal - int(symbol.sliceToEnd(1)), length, volume, panning, startBeat)
        else
            n.makeNote2(symbol, length, volume, panning, startBeat)
        end
        lastNoteVal = n.getValue()
        notes.append(n)
        startBeat += length
    end
    
    sub addRest(length)
        startBeat += length
    end
    
    sub addRawNote(pitch, length, volume, panning, sBeat)
        n = Note()
        n.makeNote(pitch, length, volume, panning, sBeat)
        notes.append(n)
        startBeat = sBeat + length
    end
    
    sub notesToBytes()
        bytes = []
        for i = 0; i < notes.length(); i++
            note = notes[i]
            val = note.getValue()
            bytes.append(val)
        end
        return bytes
    end
    
    sub toStringConcise()
        str = "["
        for i = 0; i < notes.length(); i++
            note = notes[i]
            str += note.getSymbol()
            when i != notes.length() - 1
                str += ", "
        end
        str += "]"
        return str
    end
    
    sub toString()
        str = "track {\r\n"
        sBeat = 0
        for i = 0; i < notes.length(); i++
            note = notes[i]
            if note.startBeat > sBeat
                str += "R : " + string(note.startBeat - sBeat) + "\r\n"
            end
            if not (note.volBytes.length() == 0 or note.panBytes.length() == 0)
                str += note.getSymbol() + " : "
                str += string(note.noteLength) + " : "
                str += string(note.volBytes).slice(1, -1) + " : "
                str += string(note.panBytes).slice(1, -1) + "\r\n"
            end
            sBeat = note.startBeat + note.noteLength
        end
        str += "}\r\n"
        return str
    end
    
    sub isEmpty()
        return notes.length() == 0
    end
    
    sub sBeatsToHex(i, hex, hex2)
        when i >= notes.length()
            return hex
        note = notes[i]
        sBeat = note.startBeat - 1
        hex += dwordToHex(sBeat)
        padding = min(note.noteLength, max(note.volBytes.length(), note.panBytes.length())) - 1
        if padding > 0
            for null; padding > 0; padding--
                sBeat++
                hex += dwordToHex(sBeat)
            end
        end
        return sBeatsToHex(i + 1, hex, hex2)
    end
    
    sub pitchesToHex(i, hex)
        when i >= notes.length()
            return hex
        note = notes[i]
        pitch = note.getValue()
        hex += byteToHex(pitch)
        padding = min(note.noteLength, max(note.volBytes.length(), note.panBytes.length())) - 1
        if padding > 0
            for null; padding > 0; padding--
                hex += "ff"
            end
        end
        return pitchesToHex(i + 1, hex)
    end
    
    sub lengthsToHex(i, hex)
        when i >= notes.length()
            return hex
        note = notes[i]
        len = note.noteLength
        hex += byteToHex(note.noteLength)
        padding = min(note.noteLength, max(note.volBytes.length(), note.panBytes.length())) - 1
        if padding > 0
            for null; padding > 0; padding--
                hex += "01"
            end
        end
        return lengthsToHex(i + 1, hex)
    end
    
    sub volToHex(j, hex)
        when j >= notes.length()
            return hex
        note = notes[j]
        vol = note.volBytes
        if vol.length() > 0
            for i = 0; i < vol.length(); i++
                byte = vol[i]
                hex += byteToHex(byte)
            end
        end
        padding = min(note.noteLength, max(note.volBytes.length(), note.panBytes.length())) - 1
        if padding > 0
            for null; padding > 0; padding--
                hex += "ff"
            end
        end
        return volToHex(j + 1, hex)
    end
    
    sub panToHex(j, hex)
        when j >= notes.length()
            return hex
        note = notes[j]
        pan = note.panBytes
        if pan.length() > 0
            for i = 0; i < pan.length(); i++
                byte = pan[i]
                hex += byteToHex(byte)
            end
        end
        padding = min(note.noteLength, max(note.volBytes.length(), note.panBytes.length())) - 1
        if padding > 0
            for null; padding > 0; padding--
                hex += "ff"
            end
        end
        return panToHex(j + 1, hex)
    end
    
    sub toHex()
        hex = ""
        hex2 = ""
        hex = sBeatsToHex(0, hex, hex2)
        if hex2.length() > 0
            hex += hex2
        end
        hex = pitchesToHex(0, hex)
        hex = lengthsToHex(0, hex)
        hex = volToHex(0, hex)
        hex = panToHex(0, hex)
        return hex
    end
    
    return this
end 