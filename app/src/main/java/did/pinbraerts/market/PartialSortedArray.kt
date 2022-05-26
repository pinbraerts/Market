package did.pinbraerts.market

import java.lang.Integer.max
import java.lang.Integer.min

class PartialSortedArray<T>(
    sections_count: Int,
    val order: (T) -> Int,
    initData: ArrayList<T> = arrayListOf(),
    val sections: IntArray = IntArray(sections_count),
    val data: ArrayList<T> = arrayListOf()
) {
    init {
        initData.forEach(::add)
    }

    fun sectionIndex(index: Int): Int {
        var j = sections.binarySearch(index)
        if(j < 0)
            j = -(j + 2)

        if(j >= sections.size)
            j = sections.size - 1

        val sectionStart = sections[j]
        while(j + 1 < sections.size && sections[j + 1] == sectionStart)
            j += 1

        return j
    }

    fun clear() {
        sections.fill(0)
        data.clear()
    }

    fun add(element: T): Int {
        val section = order(element)
        itemMoved(sections.size - 1, section)
        data.add(sections[section], element)
        return sections[section]
    }

    operator fun get(index: Int) = data[index]
    operator fun set(index: Int, value: T) = data.set(index, value)

    val size: Int
        get() = data.size

    fun removeAt(index: Int) {
        val section = sectionIndex(index)
        itemMoved(section, sections.size - 1)
        data.removeAt(index)
    }

    private fun itemMoved(fromSection: Int, toSection: Int) {
        if(fromSection < toSection) {
            for (i in fromSection + 1 .. min(toSection, sections.size - 1)) {
                sections[i] -= 1
            }
        }
        else if(fromSection > toSection) {
            for (i in toSection + 1 .. min(fromSection, sections.size - 1)) {
                sections[i] += 1
            }
        }
    }

    fun move(fromPosition: Int, toPosition: Int) {
        val fromSection = sectionIndex(fromPosition)

        val mySection = order(data[fromPosition])
        if (toPosition == sections[mySection] - 1)
            itemMoved(fromSection, mySection)
        else if (mySection < sections.size - 1 && toPosition == sections[mySection + 1])
            itemMoved(fromSection, mySection)
        else
            itemMoved(fromSection, sectionIndex(toPosition))

        data.add(toPosition, data.removeAt(fromPosition))
    }

    fun orderChanged(fromPosition: Int): Int {
        val toSection = order(data[fromPosition])
        val fromSection = sectionIndex(fromPosition)

        if (toSection == fromSection)
            return fromPosition

        itemMoved(fromSection, toSection)
        val toPosition =
            if (toSection < fromSection)
                max(0, sections[toSection + 1] - 1)
            else
                sections[toSection]
        data.add(sections[toSection], data.removeAt(fromPosition))
        return toPosition
    }
}
