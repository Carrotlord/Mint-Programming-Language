sub OrgLoader(filePath)
    orgObj = Organya()
    orgRdr = null
    
    sub buildOrg()
        vChar = orgRdr.readVersionChar()
        orgObj.orgVersion = vChar
        offset = 6
        tempo = orgRdr.readWord(offset)
        orgObj.tempo = tempo
        offset += 2
        bar = orgRdr.readByte(offset)
        beat = orgRdr.readByte(offset + 1)
        orgObj.timeSigBar = bar
        orgObj.timeSigBeat = beat
        offset += 2
        sBeat = orgRdr.readDword(offset)
        orgObj.startingBeat = sBeat
        offset += 4
        eBeat = orgRdr.readDword(offset)
        orgObj.endingBeat = eBeat
        offset += 4
        orgObj.startingMeasure = -1
        orgObj.endingMeasure = -1
        //numOfNotes = []
        // Load header data for each track.
        headerResults = loadHeaderData()
        numOfNotes = headerResults[0]
        offset = headerResults[1]
        // Load main song data for each song. This is where all the tracks
        // get added to the orgObj.
        offset = loadMainSongData(numOfNotes, offset)
    end
    
    sub loadOrg(filePath)
        lastOffset = fileLength(filePath) - 1
        orgHex = readHex(filePath, 0, lastOffset)
        orgRdr = OrgReader(orgHex)
        buildOrg()
    end
    
    sub loadHeaderData()
        //print "DERP!"
        numOfNotes = []
        for track = 0; track < 16; track++
            freq = orgRdr.readWord(offset)
            orgObj.setFrequency(freq, track)
            offset += 2
            instru = orgRdr.readByte(offset)
            orgObj.setInstrument(instru, track)
            offset++
            sub toTruth(value)
                return value == 1
            end
            p = toTruth(orgRdr.readByte(offset))
            orgObj.setPi(p, track)
            offset++
            nNotes = orgRdr.readWord(offset)
            numOfNotes.append(nNotes)
            offset += 2
        end
        return [numOfNotes, offset]
    end
    
    sub loadMainSongData(numOfNotes, offset)
        emptySongs = 0
        for i = 0; i < 16; i++
            if true
                noteStartingBeats = []
                noteCount = numOfNotes[i]
                print "Reading note starting beats"
                z = 0
                br = false
                for j = 0; true ; j++
                    sBeat = orgRdr.readDword(offset)
                    if shr(sBeat, 23) != 0
                        br = true
                        //offset -= 4
                    end
                    noteStartingBeats.append(sBeat)
                    when sBeat == 0
                        z++
                    when z > 500
                        break
                    when br
                        break
                    offset += 4
                end
                notePitches = []
                notePaddingLength = []
                print "Reading note pitches"
                for j = 0; j < noteCount; j++
                    pitch = orgRdr.readByte(offset)
                    if pitch == 255
                        while notePaddingLength.size() < j + 1
                            notePaddingLength.append(1)
                        end
                        while noteStartingBeats.size() < j + 1
                            noteStartingBeats.append(-1)
                        end
                        notePaddingLength[j] = notePaddingLength[j] + 1
                        noteStartingBeats[j] = -1
                    else
                        notePitches.append(pitch)
                        notePaddingLength.append(1)
                    end
                    offset++
                end
                noteStartingBeats = removeAll(noteStartingBeats, -1)
                noteLengths = []
                print "Reading note lengths"
                for j = 0; j < noteCount; j++
                    length = orgRdr.readByte(offset)
                    noteLengths.append(length)
                    offset += notePaddingLength[j]
                end
                volumeBytesLists = []
                print "Reading note volume"
                for j = 0; j < noteCount; j++
                    volumeBytes = []
                    currentPaddingLength = notePaddingLength[j]
                    for k = 0; k < currentPaddingLength; k++
                        vol = orgRdr.readByte(offset)
                        volumeBytes.append(vol)
                        //show "VOLUME:"
                        //print volumeBytes
                        offset++
                    end
                    volumeBytesLists.append(volumeBytes)
                end
                panningBytesLists = []
                print "Reading note panning"
                for j = 0; j < noteCount; j++
                    panningBytes = []
                    currentPaddingLength = notePaddingLength[j]
                    for k = 0; k < currentPaddingLength; k++
                        pan = orgRdr.readByte(offset)
                        panningBytes.append(pan)
                        //show "PANNING:"
                        //print panningBytes
                        offset++
                    end
                    panningBytesLists.append(panningBytes)
                end
            end
            // Create the track objects for the song, and add them to the orgObj.
            if noteStartingBeats.length() > 0
                currentTrack = Track(noteStartingBeats[0])
                for j = 0; j < noteCount; j++
                    // TODO is this right?
                    if j < notePitches.size() and j < noteStartingBeats.size()
                        currentTrack.addRawNote(notePitches[j], noteLengths[j], volumeBytesLists[j], panningBytesLists[j], noteStartingBeats[j])
                        note = Note()
                        note.makeNote(notePitches[j], noteLengths[j], volumeBytesLists[j], panningBytesLists[j], noteStartingBeats[j])
                        show note.getSymbol() + " "
                    end
                    when not (j < notePitches.size() and j < noteStartingBeats.size())
                        break
                end
                //print currentTrack.toStringConcise()
                orgObj.addTrack(currentTrack)
            else
                currentTrack = Track(0)
                orgObj.addTrack(currentTrack)
                emptySongs++
            end
            print ""
        end
        //return offset
    end
    
    loadOrg(filePath)
    
    return this
end 