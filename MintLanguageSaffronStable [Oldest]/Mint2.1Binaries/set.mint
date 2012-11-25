import type

sub set()
    internals = []
    sub add(e)
        when (not (e in internals))
            internals.append(e)
    end

    sub append(e)
        when (not (e in internals))
            internals.append(e)
    end

    sub toString()
        result = char(123)
        isFirst = true
        for each elem in internals
            if isFirst
                isFirst = false
                result += string(elem)
            else
                result += ", " + string(elem)
            end
        end
        return result + char(125)
    end

    sub length()
        return internals.size()
    end

    sub size()
        return internals.size()
    end

    sub pop()
        return internals.pop()
    end

    sub get(i)
        return internals[i]
    end

    return this
end
