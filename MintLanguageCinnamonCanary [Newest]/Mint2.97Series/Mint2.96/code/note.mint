chromaticScale = ["C", "C#", "D", "Eb", "E", "F", "F#", "G", "G#", "A", "Bb", "B"]
noteValueDict = Dictionary()
noteValueDict.keys = ["c", "d", "e", "f", "g", "a", "b"]
noteValueDict.values = [0, 2, 4, 5, 7, 9, 11]

sub Note()
    noteValue = 48    //48 is middle C in Organya (C4)
    noteLength = 1
    volBytes = []
    panBytes = []
    startBeat = 0

    // Constructor #1
    sub makeNote(value, length, volumeBytes, panningBytes, startingBeat)
        noteValue = value
        noteLength = length
        volBytes = volumeBytes
        panBytes = panningBytes
        startBeat = startingBeat
    end
    
    sub countSemitones(sharpsAndFlats)
        i = 0
        for each char of sharpsAndFlats
            when char == "#"
                i++
            when char == "b"
                i--
        end
        return i
    end
    
    // Constructor #2
    sub makeNote2(symbol, length, volumeBytes, panningBytes, startingBeat)
        when symbol.length() == 0
            return
        pitchLetter = symbol[0]
        octaveDigit = symbol[-1]
        if isSimpleDigit(octaveDigit)
            semitonesAdded = 0
            if symbol.length() >= 3
                sharpsAndFlats = symbol.slice(1, -1)
                semitonesAdded = countSemitones(sharpsAndFlats)
            end
            pitchLetter = pitchLetter.lower()
            t = 0
            ks = noteValueDict.keys
            for each letter of ks
                when letter == pitchLetter
                    distanceFromOctaveLine = noteValueDict.values[t]
                t++
            end
            octave = int(octaveDigit)
            noteValue = octave * 12 + distanceFromOctaveLine + semitonesAdded
            noteLength = length
            volBytes = volumeBytes
            panBytes = panningBytes
            startBeat = startingBeat
        else
            error( "The note " + symbol + " has no octave number appended to the end.")
        end
    end
    
    sub getValue()
        return noteValue
    end
    
    sub setValue(value)
        noteValue = value
    end
    
    sub getSymbol()
        noteLetterValue = noteValue % 12
        octave = noteValue -/ 12
        return chromaticScale[noteLetterValue] + string(octave)
    end
    
    return this
end