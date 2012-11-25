sub bubbleSort(x)
    when x.length() <= 1
        return x
    for t = 0; t < 25; t++
        for j = 0; j < x.length(); j++
            if j + 1 < x.length()
                if x[j] > x[j + 1]
                    temp = x[j]
                    x[j] = x[j + 1]
                    x[j + 1] = temp
                end
            end
        end
    end
    return x
end

sub partition(x, left, right, pivotIdx)
    pivotValue = x[pivotIdx]
    temp = x[right]
    x[right] = x[pivotIdx]
    x[pivotIdx] = temp
    storeIndex = left
    for i = left; i < right; i++
        if x[i] < pivotValue
            temp = x[i]
            x[i] = x[storeIndex]
            x[storeIndex] = temp
            storeIndex++
        end
    end
    temp = x[right]
    x[right] = x[storeIndex]
    x[storeIndex] = temp
    return [x, storeIndex]
end

sub quickSort(x)
    return doQuickSort(x, 0, x.length() - 1)
end

sub doQuickSort(x, left, right)
    if left < right
        pivotIndex = left
        answers = partition(x, left, right, pivotIndex);
        pivotNewIndex = answers[1]
        x = answers[0]
        x = doQuickSort(x, left, pivotNewIndex - 1)
        x = doQuickSort(x, pivotNewIndex + 1, right)
    end
    return x
end