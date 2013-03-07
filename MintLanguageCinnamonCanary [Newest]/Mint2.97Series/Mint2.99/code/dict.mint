sub Dictionary()
    keys = []
    values = []
    
    sub put(key, val)
        cond = key in keys
        if not cond
            keys.append(key)
            values.append(val)
        else
            z = 0
            while z < keys.length()
                k = keys[z]
                when key == k
                    values[z] = val
                z++
            end
        end
    end
    
    sub get(key)
        t = 0
        while t < keys.length()
            k = keys[t]
            when key == k
                return values[t]
            t++
        end
    end
    
    sub contains(key)
        return key in keys
    end
    
    sub display()
        show "("
        print keys
        show values
        print ")"
    end

    sub size()
        return keys.size()
    end
    
    return this
end