import type

sub sortedList()
    internals = []
    sub add(e)
        internals.add(e)
    end
    
    sub append(e)
        internals.add(e)
    end
    
    sub sort()
        for t = 0; t < 16; t++
            for j = 0; j < internals.length(); j++
                if j + 1 < internals.length()
                    if internals[j] > internals[j + 1]
                        temp = internals[j]
                        internals[j] = internals[j + 1]
                        internals[j + 1] = temp
                    end
                end
            end
        end
    end

    sub toString()
        sort()
        result = "["
        isFirst = true
        for each elem in internals
            if isFirst
                isFirst = false
                result += string(elem)
            else
                result += ", " + string(elem)
            end
        end
        return result + "]"
    end

    sub length()
        return internals.size()
    end

    sub size()
        return internals.size()
    end

    sub pop()
        sort()
        return internals.pop()
    end

    sub get(i)
        sort()
        return internals[i]
    end

    return this
end
