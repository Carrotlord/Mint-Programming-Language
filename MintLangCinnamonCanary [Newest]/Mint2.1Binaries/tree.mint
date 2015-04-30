sub tree(r)
    root = r
    offspring = []
    
    sub addChild(c)
        offspring.append(c)
    end
    
    sub weight()
        when offspring.length() == 0
            return root
        sum = root
        for each ch of offspring
            sum += ch.weight()
        end
        return sum
    end
    
    sub size()
        when offspring.length() == 0
            return 1
        sum = 1
        for each ch of offspring
            sum += ch.size()
        end
        return sum
    end
    
    return this
end