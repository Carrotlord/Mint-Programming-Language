/* 
 * Each object of this class is designed to represent a
 * single Organya file. If you don't know already, Organya is the file format
 * used by Cave Story's default music playing engine. This format was designed
 * by Daisuke Amaya (aka Studio Pixel).
 */
sub Organya()
    orgVersion = "2"
    tempo = 128
    timeSigBar = 4
    timeSigBeat = 4
    startingMeasure = 0
    endingMeasure = 255
    //startingMeasure and endingMeasure are used for the repeat range. They
    //don't denote the length of the song.
    // If startingMeasure is set to -1, then startingBeat will be used
    // instead.
    // If endingMeasure is set to -1, then endingBeat will be used instead.
    startingBeat = 0
    // 4080 = 255 * 16 = endingMeasure * timeSigBar * timeSigBeat
    endingBeat = 4080
    frequencies = []
    instruments = []
    piValues = []
    tracks = []
    
    repeat 16
        frequencies.append(1000)
        instruments.append(0)
        piValues.append(false)
    end
    
    sub setVersionChar(version)
        orgVersion = version
    end
    
    sub setTempo(t)
        tempo = t
    end
    
    sub setTimeSig(bar, beat)
        timeSigBar = bar
        timeSigBeat = beat
    end
    
    sub setStartingMeasure(start)
        startingMeasure = start
    end
    
    sub setEndingMeasure(e)
        endingMeasure = e
    end
    
    sub setStartingBeat(sBeat)
        startingBeat = sBeat
    end
    
    sub setEndingBeat(eBeat)
        endingBeat = eBeat
    end
    
    sub setFrequency(freq, trackNum)
        frequencies[trackNum] = freq
    end
    
    sub setInstrument(instru, trackNum)
        instruments[trackNum] = instru
    end
    
    sub setPi(p, trackNum)
        piValues[trackNum] = p
    end
    
    sub addTrack(tr)
        tracks.append(tr)
    end
    
    sub toFile(fileName)
        str = "OrgVersion = " + orgVersion + "\r\n"
        t = string(tempo)
        str += "Tempo = " + t + "\r\n"
        str += "TimeSignature = " + string(timeSigBar) + "/" + string(timeSigBeat) + "\r\n"
        str += "StartingMeasure = " + string(startingMeasure) + "\r\n"
        str += "EndingMeasure = " + string(endingMeasure) + "\r\n"
        str += "StartingBeat = " + string(startingBeat) + "\r\n"
        str += "EndingBeat = " + string(endingBeat) + "\r\n"
        for i = 1; i <= 16; i++
            str += "Frequency" + string(i) + " = " + string(frequencies[i - 1]) + "\r\n"
        end
        for i = 1; i <= 16; i++
            str += "Instrument" + string(i) + " = " + string(instruments[i - 1]) + "\r\n"
        end
        for i = 1; i <= 16; i++
            str += "Pi" + string(i) + " = " + string(piValues[i - 1]) + "\r\n"
        end
        str += "\r\n"
        for i = 0; i < 16; i++
            track = tracks[i]
            str += track.toString()
            str += "\r\n"
        end
        strToFile(fileName, str)
    end
    
    sub findKeyVal(key, str)
        str = str.replace("\r", "\n")
        str = str.replace("\n\n", "\n")
        str = str.lower()
        key = key.lower()
        lines = str.split( "\n")
        for each line of lines
            if line.startsWith(key)
                i = line.find("=")
                slice = line.sliceToEnd(i + 1)
                return slice.remove( " ")
            end
        end
        return null
    end
    
    sub readTrack(tr)
        print tr
        when not (":" in tr)
            return Track(0)
        tr = tr.replace("\r", "\n")
        tr = tr.replace("\n\n", "\n")
        tr = tr.lower()
        lines = tr.split( "\n")
        br = false
        track = Track(0)
        z = -1
        for i = 0; i < lines.length(); i++
            show "I:"
            print i
            z++
            when z > 1000 or z >= lines.length()
                break
            line = lines[z]
            when "}" in line
                br = true
            if (not ("track" in line)) and (not ("{" in line))
                if not ("}" in line)
                    line = line.remove( " ")
                    line = line.split(":")
                    len = line.length()
                    if len > 0
                        n = Note()
                        if len == 1
                            if line[0] == "r"
                                track.addRest(1)
                            else
                                track.addNote2(line[0], 1, [200], [6])
                            end
                        else if len == 2
                            if line[0] == "r"
                                track.addRest(int(line[1]))
                            else
                                track.addNote2(line[0], int(line[1]), [200], [6])
                            end
                        else if len == 3
                            track.addNote2(line[0], int(line[1]), eval("[" + line[2] + "]"), [6])
                        else
                            track.addNote2(line[0], int(line[1]), eval("[" + line[2] + "]"), eval("[" + line[3] + "]"))
                        end
                    end
                end
            end
            when br
                break
        end
        return track
    end
    
    /* Converts organya source code into an organya object. */
    sub fromString(str)
        sub toInt(s)
            when s == null
                return null
            return int(s)
        end
        orgVersion = findKeyVal("orgversion", str)
        tempo = toInt(findKeyVal("tempo", str))
        ts = findKeyVal("timesignature", str)
        if ts != null
            tsParts = ts.split("/")
        else
            tsParts = [null, null]
        end
        timeSigBar = toInt(tsParts[0])
        timeSigBeat = toInt(tsParts[1])
        startingMeasure = toInt(findKeyVal("startingmeasure", str))
        endingMeasure = toInt(findKeyVal("endingmeasure", str))
        startingBeat = toInt(findKeyVal("startingbeat", str))
        endingBeat = toInt(findKeyVal("endingbeat", str))
        sub toTruth(s)
            if s == "true"
                return true
            else if s == "false"
                return false
            else if s == "1"
                return true
            else
                return false
            end
        end
        for i = 1; i <= 16; i++
            frequencies[i - 1] = toInt(findKeyVal("frequency" + string(i), str))
            instruments[i - 1] = toInt(findKeyVal("instrument" + string(i), str))
            piValues[i - 1] = toTruth(findKeyVal("pi" + string(i), str))
        end
        // Check for nulls
        when orgVersion == null
            orgVersion = 2
        when tempo == null
            tempo = 128
        when timeSigBar == null
            timeSigBar = 4
        when timeSigBeat == null
            timeSigBeat = 4
        when startingMeasure == null
            startingMeasure = 0
        when endingMeasure == null
            endingMeasure = 255
        when startingBeat == null
            startingBeat = 0
        when endingBeat == null
            endingBeat = 4080
        for i = 0; i < 16; i++
            when frequencies[i] == null
                frequencies[i] = 1000
            when instruments[i] == null
                instruments[i] = 0
            // Pi's default value is false, not null
        end
        i = 0
        j = 0
        z = 0
        while z < 100 and (("{" in str) or i != -1 or j != -1)
            i = str.find("{")
            j = str.find("}")
            print "SHIT!"
            //when (not ("{" in str)) or i == -1 or j == -1
            //    break
            k = i + 1
            print "SOMETHING'S WRONG"
            t = readTrack(str.slice(k, j))
            print "SOMETHING'S MORE WRONG"
            addTrack(t)
            i = j + 1
            str = str.sliceToEnd(i)
            z++
            show "Z:"
            print z
        end
        print "FUCK YOU!"
        repeat 16
            when tracks.length() >= 16
                break
            addTrack(Track(0))
        end
        /* repeat 16
            print "WHAT THE HELL"
            when tracks.length() >= 16
                break
            addTrack(Track(0))
        end */
    end
    
    sub getTrackHex(i)
        when i >= 16
            return ""
        track = tracks[i]
        hex = track.toHex()
        return hex + getTrackHex(i + 1)
    end
    
    sub toHex()
        // 4f72672d30 is "Org-0"
        for i = 0; i < tracks.length(); i++
            t = tracks[i]
            when t.notes.length() > 0
                t.notes.remove(0)
        end
        hex = "4f72672d3032"
        hex += wordToHex(tempo)
        hex += byteToHex(timeSigBar)
        hex += byteToHex(timeSigBeat)
        if startingMeasure != -1
            hex += dwordToHex(startingMeasure * timeSigBar * timeSigBeat)
        else
            hex += dwordToHex(startingBeat)
        end
        if endingMeasure != -1
            hex += dwordToHex(endingMeasure * timeSigBar * timeSigBeat)
        else
            hex += dwordToHex(endingBeat)
        end
        sub truthToInt(t)
            when t
                return 1
            return 0
        end
        for i = 0; i < 16; i++
            hex += wordToHex(frequencies[i])
            hex += byteToHex(instruments[i])
            hex += byteToHex(truthToInt(piValues[i]))
            t = tracks[i]
            ns = t.notes
            hex += wordToHex(ns.length())
        end
        hex += getTrackHex(0)
        return hex
    end
    
    sub toBinaryFile(fileName)
        hex = toHex()
        when hex.length() % 2 != 0
            hex += "0"
        writeHex(fileName, hex, 0)
    end
    
    return this
end 